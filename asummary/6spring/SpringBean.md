## 知识脉络
---
### 理解BeanWrapper

### 理解BeanDefinition



---



## JDK

### JAVABean规范

---

1. 类必须使用public修饰

2. 必须提供无参构造函数

3. 属性值必须为private

4. 提供public的getter和 setter方法设置属性值

5. 实现serializable接口

#### java.beans.*

#####     重要接口及类

| 类或接口名称          | 类型      | 作用                                                         |
| --------------------- | --------- | ------------------------------------------------------------ |
| Introspector          | class     | 通过**反射**封装Bean的信息到BeanInfo                         |
| BeanInfo              | class     | 封装属性名及其getter/setter名称，方法等等信息                |
| PropertyDescriptor    | class     | 属性描述符                                                   |
| **PropertyEditor**    | interface | 设置属性值时，需要将字符串转为相应的数据类型。JDK默认只提供基础类型比如boolean、long，string等 |
| PropertyEditorManager | class     | 管理/注册/提供PropertyEditor。                               |

#### 代码示例

```java
  BeanInfo beanInfo = Introspector.getBeanInfo(Bean_User.class);
 //获取属性描述符
  PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
  //bean实例
	Bean_User bean_user =new Bean_User();
	//调用PropertyEditor设置属性值
Arrays.stream(propertyDescriptors).forEach(descriptor->{
            if(descriptor.getName().equals("age")){//注意basename和name区别
                System.out.println("修改 age");
                Method writeMethod = descriptor.getWriteMethod();
                //注意只有基础类型比如int、long、boolean、string等，没有date
                PropertyEditor editor = PropertyEditorManager.findEditor(
                    descriptor.getPropertyType());
                editor.setAsText("55");
                try {
                    writeMethod.invoke(bean_user,editor.getValue());
                } catch (Exception e) {
                }
			}
}
```

#### 示意图

![设置属性值流程图](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131156.svg)

#### 问题

1. 原生JAVA的PropertyEditor不足之处：

   - 只能用于字符串到Java对象的转换，不能进行任意两个Java类型之间的转换。
   - 对上下文不敏感（如注解、类结构），因此不能利用上下文信息进行高级的转换逻辑。

   * spring希望**org.springframework.core.convert**可以替换原生JAVA体系，但历史原因保留两种支持，后文在具体分析。

   * Spring 3后，Spring提供了统一的ConversionService API和强类型的Converter SPI，以实现转换逻辑。

   - 通用的core.convert Converter SPI不能直接完成格式化需求。基于此，Spring 3 引入了 Formatter SPI，相比PropertyEditors简单直接。

   * ConversionService 为Converter SPI和Formatter SPI提供了统一的 API。

### BeanWrapper

---



![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131157.png)

根据第一部分的理解也能只能，beanwrapper其实是对上述**设置属性值**的封装：

1. Bean实例包裹器；

2. 属性访问器；

3. 属性编辑器注册表。

beanwrapper处理复杂的设置属性的逻辑，如属性名的嵌套、list、set、map、array等
	`getPropertyValue`和`setPropertyValue`是分别用于获取和设置bean的属性值的。这里的propertyName支持表达式：

| 表达式                 | 说明                                                         |
| ---------------------- | ------------------------------------------------------------ |
| `name`                 | 指向属性name，与getName() 或 isName() 和 setName()相对应。   |
| `account.name`         | 指向属性account的嵌套属性name，与之对应的是getAccount().setName()和getAccount().getName() |
| `account[2]`           | 指向索引属性account的第三个元素，索引属性可能是一个数组（array），列表（list）或其它天然有序的容器。 |
| `account[COMPANYNAME]` | 指向一个Map实体account中以COMPANYNAME作为键值（key）所对应的值 |

#### 示例

```java
    @Test
    public void testNestableProperty(){
        Bean_User bean_user = Bean_User.getDefaultBean_user();
        System.out.println("修改前："+ReflectionToStringBuilder.toString(bean_user));
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean_user);
        beanWrapper.setPropertyValue("bean_book.name","嵌套属性");
        beanWrapper.setPropertyValue("list[0].name","list嵌套属性");
        System.out.println("修改后："+ReflectionToStringBuilder.toString(bean_user));
    }
    //-------------------------Bean属性-------------------------------------//
    public static Bean_User getDefaultBean_user(){
        ArrayList<Bean_Book> bean_bookList = new ArrayList<>();
        Bean_Book bean_book = new Bean_Book();
        bean_book.setAuth("beanbook");
        bean_book.setName("helloworld");
        bean_bookList.add(bean_book);
        Bean_Book bean_book2 = new Bean_Book();
        bean_book2.setAuth("beanbook2");
        bean_book2.setName("helloworld2");
        //-----------
        Bean_User bean_user = new Bean_User();
        bean_user.setAge(20);
        bean_user.setBirth(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        bean_user.setId("froggengo");
        bean_user.setList(bean_bookList);
        bean_user.setName("froggenname");
        bean_user.setPassword("123456");
        bean_user.setScore(100);
        bean_user.setBean_book(bean_book2);
        return bean_user;
    }
```

1. **找**：如`property1.property2.property3`结构的数据，依赖`AbstractNestablePropertyAccessor`循环截取点符号“.”前的属性名，通过getmethod反射获取属性值value，循环获取当前对象对应下一个点符号“.”前的属性名对应的值，直到没有下一个，则设置value值。复杂在循环解析属性名，还要区分set、list、map等类型。即截取获得`property1`=>`getmethod获取属性值`=>`获得下一个属性property2`=>`getmethod获取属性值`=>`获得下一个属性property3`=>`setmethod设置属性值`
2. **设置**：解析得到最后一个propertyDescript后，需要判断时说明属性类型类型，找相应的属性类型转换器，propertEditor、ConversionService

#### 流程图

![BeanWrapper](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131158.png)

#### 数据转换

##### 接口图

![数据转换](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131159.svg)

##### 注册



1. @InitBinder

2. ConversionService

3. WebBindingInitializer

4. https://www.javatt.com/p/33050

### 资源

---

1. [Spring框架的技术内幕](https://www.iteye.com/topic/1123081)
2. 

## SpringCore：[官网](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#)

---

### 生命周期

---

* 实现接口`InitializingBean`和`DisposableBean`，对应`afterPropertiesSet()`和`destory()`
  * 接口产生耦合，不推荐，但Spring框架多处使用到
* 方法上声明JSR250注解`@PostConstruct`和`@PreDestory`

* @Bean中属性显示给出`init-method`和`destory-method`
* 默认的方法名称：`init(), initialize(), dispose()  `

1. `initialization callback`  在完成所有依赖注入后马上执行，即AOP前
2. `initialization callback`调用顺序
   1. Methods annotated with `@PostConstruct`
   2.  afterPropertiesSet() as defined by the`InitializingBean` callback interface
   3. A custom configured `init()` method  
   4. 

3. `Destruction callbacks  `调用顺序
   1. Methods annotated with `@PreDestroy`
   2. destroy() as defined by the `DisposableBean` callback interface
   3.  A custom configured `destroy()` method  

#### 相关Aware接口

1. ApplicationContextAware 
2. BeanNameAware 
3. BeanFactoryAware
4. MessageSourceAware
5. ResourceLoaderAware
6. ServletConfigAware
7. ServletContextAware
8. 等  

### 注解开发

**`@Autowired, @Inject, @Resource, and @Value  `由`Spring的BeanPostProcessor  `处理**

#### @Require： 

* `bean property setter methods`
* 必须完成设置的property值，否则NullPointerException

#### @Autowired： 

* `field、method with aribitary  name and arguements`

* `type-driven injection`  
* 推荐使用属性`required=true`，代替`@Require`
* 可以为Bean注入已知的Bean：`BeanFactory, ApplicationContext, Environment, ResourceLoader,ApplicationEventPublisher, and MessageSource`

#### @Order or  @Priority  

* `@Autowired`首先注入标记`@Priority`的Bean

#### @Qualifier  

* 配合`@Autowired`,注入`@Qualifier`指定名称的Bean`创建时声明name`，

#### @Resource   

* 只能使用在变量、property setter methods
* `by-name  injection`
* 没有指定name属性，则`by-type`

#### @PostConstruct and @PreDestroy  

#### @Scope

#### @Lazy  

#### @Value 

* `@Value("#{privateInstance.age}")`为参数注入privateInstance的age值

#### **@Description**

#### @Profile

#### @PropertySource 



### component管理

#### @Configuration, 

#### @Component、@ComponentScan、@ImportResource 、@Import  

* **@ImportResource**:引入XML配置文件

#### @Repository, @Service, and @Controller  

#### @Bean(value、initMethod ,destroyMethod )

* Bean注解在方法上，入参会自动注入



#### Meta-annotations  将一个注解标注在另一个注解上

```java
@Component // Spring will see this and treat @Service in the same way as @Component
public @interface Service {
}

@Scope(WebApplicationContext.SCOPE_SESSION)
public @interface SessionScope {
    /**
    * Alias for {@link Scope#proxyMode}.
    * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
    */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}    
```

#### @Component,@Repository, @Service,@Controller  

```java
@Configuration
@ComponentScan(basePackages = "org.example",
    includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
    excludeFilters = @Filter(Repository.class))
public class AppConfig {
...
}
```

#### @Component和@Configuration区别

[参考Spring @Configuration和@Component的区别](https://www.jb51.net/article/153430.htm)

```java
 @Component
public @interface Configuration {
	@AliasFor(annotation = Component.class)
	String value() default "";
}
```

从定义来看，`@Configuration `注解本质上还是`@Component`， `@ComponentScan`都能处理`@Configuration`注解的类。

##### 示例

1. 配置类

```java
@Configuration//@Component
public class myConfiguration {

    @Bean
    public Bean_Book getBean_Book(){
        System.out.println("创建一个Book");
        return new Bean_Book();
    }
    @Bean(name="user1")
    public Bean_User getBean_User(){
        Bean_User bean_user = new Bean_User();
        bean_user.setBean_book(getBean_Book());
        return bean_user;
    }
}
```

2. 启动类

   ```java
   public class BeanDefinetionLearn {
       @Test
       public void DifBetweenConfigurationAndComent(){
           AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
           applicationContext.scan("com.froggengo.springlearn.apractice.a_bean.a_beandefinition.other.testConfiguration");
           applicationContext.refresh();
           Object user1 = applicationContext.getBean("user1");
           Object getBean_book = applicationContext.getBean("getBean_Book");
           Object getBean_book1 = applicationContext.getBean("getBean_Book");
           if (getBean_book ==getBean_book1){
               System.out.println("相同");
           }else{
               System.out.println("不相同");
           }
       }
   }
   ```

3. 输出：

   ```
   //@Configuration
   创建一个Book
   相同
   //@Component
   创建一个Book
   创建一个Book
   相同
   ```

4. 分析 

   ***@Configuration 中所有带 @Bean 注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例。***

   ***@Component中的@Bean并不被CGLIB代理，但并不是每次都返回新的对象，有些文章写每次都返回新对象是不正确的***。应该是`bean_user.setBean_book(getBean_Book());`，在解析的过程中，``getBean_Book()``会作为java原义解析，而不会注入当前已经实例的Bean

   ```markdown
   The `@Bean` methods in a regular Spring component are processed differently than their counterparts inside a Spring `@Configuration` class. The difference is that `@Component` classes are not enhanced with CGLIB to intercept the invocation of methods and fields. CGLIB proxying is the means by which invoking methods or fields within `@Bean` methods in @Configuration classes creates bean metadata
   references to collaborating objects; **such methods are not invoked with normal Java semantics but rather go through the container in order to provide the usual lifecycle management and proxying of Spring beans even when referring to other beans via programmatic calls to `@Bean` methods.** In contrast, invoking a method or field in an `@Bean` method within a plain `@Component` class has standard Java semantics, with no special CGLIB processing or other constraints applying. 
   ```

   [Spring Reference](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-factorybeans-annotations) 单独一章说明**Defining bean metadata within components **,从上文也可以看出，**@Component **中@Bean如果调用了其他方法，如示例中`bean_user.setBean_book(getBean_Book())`并不会再**container**中找相应的Bean，而是作为`standard Java semantics`就像正常的java代码一样执行

5. Debug，查看applicationcontext中Beanfactory的SingleBean

   **@Configuration**下，会在容器中查找`getBean_Book()`所代表的Bean，而**@Component **只会作为java方法指向

   ![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131200.png)

### BeanFactory和ApplicationContext区别

| Feature                                                     | `BeanFactory` | `ApplicationContext` |
| :---------------------------------------------------------- | :------------ | :------------------- |
| **Bean instantiation/wiring**                               | Yes           | Yes                  |
| Integrated lifecycle management                             | No            | Yes                  |
| Automatic **`BeanPostProcessor`** registration              | No            | Yes                  |
| Automatic **`BeanFactoryPostProcessor`** registration       | No            | Yes                  |
| Convenient **`MessageSource`** access (for internalization) | No            | Yes                  |
| Built-in **`ApplicationEvent`** publication mechanism       | No            | Yes                  |

### 相关接口和注解

#### BeanFactoryPostProcessor  配合Ordered  ：

* customing configuration metadata，即修改实例化前的元信息
  * PropertyOverrideConfigurer  
  * PropertyPlaceholderConfigurer  

#### BeanPostProcessor 

* customing Beans Instances，即修改实例化后Bean的信息
* `CommonAnnotationBeanPostProcessor ` :识别`@Resource`和`JSR-250 lifecycle annotations`
* `AutowiredAnnotationBeanPostProcessor`

#### FactoryBean\<T\>

* 一般复杂的Bean初始化使用该方式，ioc容器调用`getObject()`返回实例对象
* `applicationContext.getBean('&factoryBeanName')`返回factory本身



----

## Bean从class到最后实例Bean的过程

---

### 流程示意图

![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131201.svg)

### 思考问题

1. 如何加载BeanDefinition
2. 如何实例化、做了什么
3. 怎么处理${}站位符、怎么处理autoworied注入等
4. 如何初始化、做了什么
5. 有哪些BeanFactoryPostProcess、做了什么
6. 有哪些BeanPostProcess、做了什么
7. 怎么自定义及注册BeanFactoryPostProcess、BeanPostProcess

### BeanDefinition

---

```java
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    int ROLE_APPLICATION = 0;
    int ROLE_SUPPORT = 1;
    int ROLE_INFRASTRUCTURE = 2;

    void setParentName(@Nullable String var1);

    @Nullable
    String getParentName();

    void setBeanClassName(@Nullable String var1);

    @Nullable
    String getBeanClassName();

    void setScope(@Nullable String var1);

    @Nullable
    String getScope();

    void setLazyInit(boolean var1);

    boolean isLazyInit();

    void setDependsOn(@Nullable String... var1);

    @Nullable
    String[] getDependsOn();

    void setAutowireCandidate(boolean var1);

    boolean isAutowireCandidate();

    void setPrimary(boolean var1);

    boolean isPrimary();

    void setFactoryBeanName(@Nullable String var1);

    @Nullable
    String getFactoryBeanName();

    void setFactoryMethodName(@Nullable String var1);

    @Nullable
    String getFactoryMethodName();

    ConstructorArgumentValues getConstructorArgumentValues();

    default boolean hasConstructorArgumentValues() {
        return !this.getConstructorArgumentValues().isEmpty();
    }

    MutablePropertyValues getPropertyValues();

    default boolean hasPropertyValues() {
        return !this.getPropertyValues().isEmpty();
    }

    boolean isSingleton();

    boolean isPrototype();

    boolean isAbstract();

    int getRole();

    @Nullable
    String getDescription();

    @Nullable
    String getResourceDescription();

    @Nullable
    BeanDefinition getOriginatingBeanDefinition();
}
```

BeanDefinition接口说明：

*  可以参考注解开发中配置的注解：比如`@Scope、@Description、@Primary`等
* 个别注解说明

#### 继承图

![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131202.png)

#### BeanDefinition示例

```java
public class BeanLearn {
    /**
     * BeanDefinition 解析
     * @see AnnotatedBeanDefinitionReader#register(java.lang.Class[])
     * BeanDefinition 实际由 BeanDefinitionReader 解析并注入BeanFactory中
     *
     */
    @Test
    public void BeanfinitionLearn() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        /**{@link org.springframework.context.annotation.AnnotatedBeanDefinitionReader.registerBean(java.lang.Class<?>, java.lang.String, java.lang.Class<? extends java.lang.annotation.Annotation>...)}*/
        //1、获得注解BenDefinition
        System.out.println("1、获得注解BenDefinition");
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(Bean_Book.class);
        System.out.println("初始化："+ReflectionToStringBuilder.toString(definition));
        System.out.println("    注解："+ReflectionToStringBuilder.toString(definition.getMetadata()));
        //AnnotatedGenericBeanDefinition只有 AnnotationMetadata,其余为空
        //2、解析@Conditional
        //略
        //3、解析scope         //设置scope，注意没有proxyMode
        AnnotationScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(definition);
        definition.setScope(scopeMetadata.getScopeName());
        System.out.println("初始化："+ReflectionToStringBuilder.toString(definition));
        //4、解析BeanName,由于方法为protected，所以使用反射调用
       /** @see  AnnotationBeanNameGenerator.buildDefaultBeanName(org.springframework.beans.factory.config.BeanDefinition)*/
        Class<?> aClass = Class.forName("org.springframework.context.annotation.AnnotationBeanNameGenerator");
        AnnotationBeanNameGenerator nameGenerator = (AnnotationBeanNameGenerator)aClass.newInstance();
       // Arrays.stream(aClass.getDeclaredMethods()).forEach(n-> System.out.println(n));
        Method method = aClass.getDeclaredMethod("buildDefaultBeanName",BeanDefinition.class);
        method.setAccessible(true);
        /** @see ClassUtils#getShortName(java.lang.String)
         *  @see Introspector#decapitalize(java.lang.String)
         * buildDefaultBeanName会调用此方法 */
        String beanName =(String) method.invoke(nameGenerator, definition);
        System.out.println(beanName);//bean_Book
        //4、解析普通注解
        /**
         * @see Lazy
         * @see Primary
         * @see DependsOn
         * @see Role
         * @see Description
         */
        AnnotationConfigUtils.processCommonDefinitionAnnotations(definition);
        System.out.println("解析普通注释："+ReflectionToStringBuilder.toString(definition));
        //5、封装为DefinitionHolder
        BeanDefinitionHolder definitionHolder=new BeanDefinitionHolder(definition,beanName);
        //6、解析@Scope.proxyMode的值，非ScopedProxyMode.NO下设置代理模式，是Cglib方式，还是jdk代理，
        /**
         * @see org.springframework.context.annotation.ScopedProxyMode#INTERFACES
         * @see org.springframework.context.annotation.ScopedProxyMode#TARGET_CLASS
         */
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        boolean proxyTargetClass=scopeMetadata.getScopedProxyMode().equals(ScopedProxyMode.TARGET_CLASS);
        /**
         * 实际工作：
         * @see AnnotationConfigUtils#applyScopedProxyMode(org.springframework.context.annotation.ScopeMetadata, org.springframework.beans.factory.config.BeanDefinitionHolder, org.springframework.beans.factory.support.BeanDefinitionRegistry)
         * 1、生产新的 ProxyBeanDefinition (BeanDefinitionHolder),
         * 2、添加PropertyValues：proxyTargetClass(true,false) 及 targetBeanName(scopedTarget.原beanName)
         * 3、注册原来的beanDefinition到BeanFactory，即 targeBeanDefinition,beanName为scopedTarget.beanName
         * 4、设置原BeanDefinition的setAutowireCandidate及setPrimary为false
         * 5、注册ProxyBeanDefinition，此时beanName为原beanName,即
         * scopedTarget.beanName 对应原 BeanDefinition
         * beanName对应ProxyBeanDefinition，但ScopedProxyMode.NO时，也是原BeanDefinition
         */
        BeanDefinitionHolder definitionHolderProxy = ScopedProxyUtils.createScopedProxy(definitionHolder, beanFactory, proxyTargetClass);
        //注册
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolderProxy, beanFactory);
        Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(n-> System.out.println(n));
    }
}
```

#### 单一Class注册至BeanFactory流程图

![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131203.svg)

示例只表明了单一class怎么生产BeanDefinition的过程，不同的环境BeanDefinition来源不一样，比如XML、注解等

下面以springBoot启动为例，讨论怎么获取这些Class

#### SpringBootBeanDefinition扫描及注册

![](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131204.svg)

**长话短说：**

1. `SpringApplication#prepareContext()`会将启动类的BeanDefinition注入BeanFactory

2. `SpringApplication#refreshContext()`会刷新ApplicationContext，其中会调用`AbstractApplicationContext#invokeBeanFactoryPostProcessors()`。

3. 通过`BeanFactoryPostProcessor`接口的实现完成BeanDefinition的扫描机注册

4. 重点关注：

   ```
   ConfigurationClassPostProcessor
   ConfigurationClassParser
   	ClassPathBeanDefinitionScanner
   ConfigurationClassBeanDefinitionReader
       AnnotatedBeanDefinitionReader
   AutoConfigurationImportSelector
   ```

#### 重点分析@ComponentScan

> 坑

#### 分析@Import

​		@Import注解来注册bean的时候，Import注解的值可以是`ImportSelector`或者`DeferredImportSelector`的实现类，[官网原文 4.3.19.RELEASE](https://docs.spring.io/spring/docs/4.3.19.RELEASE/javadoc-api/)

> A variation of ImportSelector that runs after all @Configuration beans have been processed. This type of selector can be particularly useful when the selected imports are @Conditional.
> Implementations can also extend the Ordered interface or use the Order annotation to indicate a precedence against other DeferredImportSelectors.

Spring 源码

```java
org.springframework.context.annotation.ConfigurationClassParser#processImports
    private void processImports() {
					if (candidate.isAssignable(ImportSelector.class)) {
						if (this.deferredImportSelectors != null && selector instanceof DeferredImportSelector) {
							this.deferredImportSelectors.add(
									new DeferredImportSelectorHolder(configClass, (DeferredImportSelector) selector));///////////////////
						}
						else {
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());////////////////
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames);
							processImports(configClass, currentSourceClass, importSourceClasses, false);
						}
					}
					else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());///////////////
					}
					else {
						processConfigurationClass(candidate.asConfigClass(configClass));/////
					}
				}

}
    
```

代码只包含关键代码，对import的不同处理：

* 实现`ImportSelector.class`接口，直接执行`selector.selectImports()`
* 实现`DeferredImportSelector`接口，添加至`List<DeferredImportSelectorHolder> `,最后处理
* 实现`ImportBeanDefinitionRegistrar`接口，添加至`Map<ImportBeanDefinitionRegistrar, AnnotationMetadata>`，下一个循环处理
* 其余作为 `@Configuration`类处理

参考

* https://www.javadoop.com/post/spring-ioc
* https://www.cnblogs.com/wyq178/p/11415877.html
* [Full @Configuration vs “lite” @Bean mode?](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#beans-java-basic-concepts)
* [官网spring-framework-reference]()

### Instantiation

准备

* RuntimeBeanReference
* 

### 问题

**spring如何解决循环依赖问题**

https://juejin.im/post/5c98a7b4f265da60ee12e9b2

### 工具类

```java
org.springframework.context.annotation.ConfigurationClassUtils
org.springframework.context.annotation.AnnotationConfigUtils
org.springframework.aop.scope.ScopedProxyUtils
org.springframework.core.annotation.AnnotatedElementUtils
org.springframework.util.ClassUtils
org.springframework.util.ReflectionUtils.accessibleConstructor(waiClass).newInstance());
BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
AnnotationAwareOrderComparator.sort(this.handlerMappings);
MethodIntrospector.selectMethods()
```

### 实战问题

1. springboot整合shiro时，报

   ```java
   The bean 'qsFtpDownloadDataServiceImpl' could not be injected as a 'com.qssystem.qsftp.qsftpdownloaddata.service.QsFtpDownloadDataServiceImpl' because it is a JDK dynamic proxy that implements:
   ```

   说明：

   * `qsFtpDownloadDataServiceImpl`没有父级接口，且类方法中有注解`@Transactional`，但启动没没有添加`@EnableTransactionManagement`注解
   * 这时整合shiro报错
   * 如果去掉shiro后，不报错。以下方法都没用

   ```java
   @EnableTransactionManagement(proxyTargetClass = true)
   spring.aop.auto=true spring.aop.proxy-target-class: true
   @EnableAspectJAutoProxy(proxyTargetClass = true)
   @EnableCaching(proxyTargetClass = true)
   ```

   解决：

   ```java
   @Bean
   @DependsOn("lifecycleBeanPostProcessor")
   public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
       DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
       //这一句比较重要
       creator.setProxyTargetClass(true);
       return creator;
   }
   ```

   

   分析：

   * 大概知道是`@Transactional`和shiro导致该类多重代理，导致使用了JDK代理，而不是CGLIB。具体后面再了解

2. 