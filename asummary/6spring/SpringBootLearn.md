#  springboot启动

## 接口：ImportSelector

### 接口定义

```java
public interface ImportSelector {
   //可以根据importingClassMetadata的属性，注入beandefinition
	String[] selectImports(AnnotationMetadata importingClassMetadata);
}
```

> 说明：接口返回希望注入到Spring容器中的类的全路径名称。

### 自定义ImportSelector

```java
public class MyHelloServiceImportSelectorImpl implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{ HelloServiceImpl.class.getName()};
    }
}
```

### 通过@Import注解导入

```java
@SpringBootApplication
@Import(MyHelloServiceImportSelectorImpl.class)
public class SpringMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class,args);
    }
}
```

### 测试类

```java
@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class p1_importselector {
    @Autowired
    BeanFactory beanFactory;
    @Test
    public void test (){
        HelloServiceImpl bean = beanFactory.getBean(HelloServiceImpl.class);
        bean.setName("frog");
        bean.setWorld("hello");
        bean.say();
    }
}
//frog : hello
```

### 开源项目的实例

#### @EnableTransactionManagement

> 在springboot中，其实已经由自动配置注解@EnableAutoConfiguration，完成了该注解的引入，实际项目中不需要在添加该注解

##### 1、引入注解

**事务管理器DataSourceTransactionManager**

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ JdbcTemplate.class, PlatformTransactionManager.class })
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceTransactionManagerAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnSingleCandidate(DataSource.class)
	static class DataSourceTransactionManagerConfiguration {

		@Bean
		@ConditionalOnMissingBean(PlatformTransactionManager.class)
		DataSourceTransactionManager transactionManager(DataSource dataSource,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
			transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
			return transactionManager;
		}

	}

}
```

**自动配置：@EnableTransactionManagement**

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PlatformTransactionManager.class)
@AutoConfigureAfter({ JtaAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
      DataSourceTransactionManagerAutoConfiguration.class, Neo4jDataAutoConfiguration.class })
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {
    //删减部分Bean注册
    
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnBean(TransactionManager.class)
   @ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
   public static class EnableTransactionManagementConfiguration {
      @Configuration(proxyBeanMethods = false)
      @EnableTransactionManagement(proxyTargetClass = false)
      @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
            matchIfMissing = false)
      public static class JdkDynamicAutoProxyConfiguration {

      }
      @Configuration(proxyBeanMethods = false)
      @EnableTransactionManagement(proxyTargetClass = true)
      @ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
            matchIfMissing = true)
      public static class CglibAutoProxyConfiguration {

      }

   }

}
```



##### 2、引入ImportSelector

```java
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {
	boolean proxyTargetClass() default false;
	AdviceMode mode() default AdviceMode.PROXY;
	int order() default Ordered.LOWEST_PRECEDENCE;

}
```

##### 3、通过ImportSelector导入配置类

> * **AutoProxyRegistrar：**
> * **ProxyTransactionManagementConfiguration：**

```java
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {
	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {AutoProxyRegistrar.class.getName(),
						ProxyTransactionManagementConfiguration.class.getName()};
			case ASPECTJ:
                //为了简洁，有所改动
				return new String[] {AspectJTransactionManagementConfiguration.class.getName()};
			default:
				return null;
		}
	}
}
```







## 接口：DeferredImportSelector

### 接口定义

```java
public interface DeferredImportSelector extends ImportSelector {
    
    String[] selectImports(AnnotationMetadata importingClassMetadata);
	@Nullable
	default Class<? extends Group> getImportGroup() {
		return null;
	}
	interface Group {
		void process(AnnotationMetadata metadata, DeferredImportSelector selector);
		Iterable<Entry> selectImports();

		class Entry {
			private final AnnotationMetadata metadata;
			private final String importClassName;

			public Entry(AnnotationMetadata metadata, String importClassName) {
				this.metadata = metadata;
				this.importClassName = importClassName;
			}
			public AnnotationMetadata getMetadata() {
				return this.metadata;
			}
			public String getImportClassName() {
				return this.importClassName;
			}
		}
	}
}
```



> 注意：DeferredImportSelector虽然继承了ImportSelector，但实际在启动过程中没有调用`selectImports(AnnotationMetadata)`,
>
> 而是通过实现`Group`接口，调用`process()和selectImports()`方法

### 开源项目实例

#### @EnableAutoConfiguration

##### 1、引入AutoConfigurationImportSelector和AutoConfigurationPackages.Registra

```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
	Class<?>[] exclude() default {};
	String[] excludeName() default {};
}
//
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
}

```

##### 2、AutoConfigurationImportSelector

```java
public class AutoConfigurationImportSelector implements DeferredImportSelector {
	private static class AutoConfigurationGroup implements DeferredImportSelector.Group{
		public void process(AnnotationMetadata metadata, DeferredImportSelector importSelector) {
            ///最重要的调用
			AutoConfigurationEntry entry = ((AutoConfigurationImportSelector) importSelector).getAutoConfigurationEntry(getAutoConfigurationMetadata(), metadata);
			this.autoConfigurationEntries.add(entry);
			for (String importClassName : entry.getConfigurations()) {
				this.entries.putIfAbsent(importClassName, metadata);
			}
		}

		@Override
		public Iterable<Entry> selectImports() {
			if (this.autoConfigurationEntries.isEmpty()) {
				return Collections.emptyList();
			}
			Set<String> allExclusions = this.autoConfigurationEntries.stream()
       			.map(AutoConfigurationEntry::getExclusions)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
			Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
					.map(AutoConfigurationEntry::getConfigurations)
                	.flatMap(Collection::stream)
					.collect(Collectors.toCollection(LinkedHashSet::new));
			processedConfigurations.removeAll(allExclusions);
			return sortAutoConfigurations(processedConfigurations, 
                                          getAutoConfigurationMetadata()).stream()
                .map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
					.collect(Collectors.toList());
		}
    }

}
```

##### 最重要的方法是

```java
protected AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata, AnnotationMetadata annotationMetadata) {
    if (!isEnabled(annotationMetadata)) {
        return EMPTY_ENTRY;
    }
    AnnotationAttributes attributes = getAttributes(annotationMetadata);
    List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    configurations = removeDuplicates(configurations);
    Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    checkExcludedClasses(configurations, exclusions);
    configurations.removeAll(exclusions);
    configurations = filter(configurations, autoConfigurationMetadata);
    fireAutoConfigurationImportEvents(configurations, exclusions);
    return new AutoConfigurationEntry(configurations, exclusions);
}
//----------------------------------------------------
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,
                                                                         getBeanClassLoader());
    return configurations;
}
```



##### SpringFactoriesLoader

> 负责查找每个jar包的，**/META-INF/spring.factories**里面对应的接口的值

![image-20200801000304341](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131138.png)

```java
@Test
public void testDeferredImportSelector (){
    List<String> factories = SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.getClass().getClassLoader());
    factories.stream().forEach(System.out::println);
}
//常见的比如
//org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
//org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
//org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
//org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

##### spring-boot-autoconfigure-2.2.2.RELEASE.jar!\META-INF\spring.factories

```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration,\
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration,\
org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
```





## 接口：ImportBeanDefinitionRegistrar

### 接口定义

```java
public interface ImportBeanDefinitionRegistrar {
	default void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry,
			BeanNameGenerator generator) {
		registerBeanDefinitions(importingClassMetadata, registry);
	}
	default void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
	}
}
```

> 使用`ImportBeanDefinitionRegistrar`可能更自由的组装`BeanDefinition`

### 自定义ImportBeanDefinitionRegistrar

#### MyImplImportBeanDefinitionRegistar2

> <font color=red>new RuntimeBeanReference(Teacher.class)</font> 动态

```java
public class MyImplImportBeanDefinitionRegistar2 implements ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Student.class);
        beanDefinition.getPropertyValues().addPropertyValue("teacher",new RuntimeBeanReference(Teacher.class));
        registry.registerBeanDefinition("student",beanDefinition);
    }
}
```

#### Student && Teacher

```java
public class Student {
    String name;
    String className;
    //注意这里故意名称与set/get不一致
    Teacher t1;
    public Teacher getTeacher() {
        return t1;
    }
    public void setTeacher(Teacher teacher) {
        this.t1 = teacher;
    }
}
@Service
public class Teacher {
    @Value("${com.teacher.name:fly}")
    String name;
    @Value("${com.teacher.age:20}")
    int age;
}
```

#### 启动类

```java
@SpringBootApplication
@Import({MyImplImportBeanDefinitionRegistar2.class})
public class SpringMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class,args);
    }
}
```



#### 测试类

```java
@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class p2_ImportBeanDefinitionRegistrar {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testSelfImpl (){
        Student bean = beanFactory.getBean(Student.class);
        Teacher teacher = bean.getTeacher();
        System.out.println(teacher.getName());
        System.out.println(teacher.getAge());
    }
}
//fly
//20
```





### 开源示例

#### @MapperScan

> 注册Mybatis的Mapper接口，有两种方式，一种在mapper接口上使用`@Mapper`注解，一种是使用`@MapperScan(basePackages={""})`,其中basePackages必不可少

##### 1、引入MapperScannerRegistrar

```java
@Import(MapperScannerRegistrar.class)
public @interface MapperScan {
    //省略注解的属性
}
```

##### 2、ImportBeanDefinitionRegistrar

```java
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

  private ResourceLoader resourceLoader;

  public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
      
    AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(MapperScan.class.getName()));
    ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
      //省略了，其他注解属性的获取
    for (String pkg : annoAttrs.getStringArray("basePackages")) {
      if (StringUtils.hasText(pkg)) {
        basePackages.add(pkg);
      }
    }
    for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
      basePackages.add(ClassUtils.getPackageName(clazz));
    }
    scanner.registerFilters();
      //向容器注册Beandefinition
    scanner.doScan(StringUtils.toStringArray(basePackages));
  }
```

> ClassPathMapperScanner使用`org.springframework.context.annotation.ClassPathBeanDefinitionScanner`扫描包路径下所有class,
>
> 重写以下方法：找出所有接口
>
> ```java
> public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner { 
> 	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
>     return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
>   }
> }
> ```
>
> 重写以下方法：
>
> * ClassPathBeanDefinitionScanner找到对应的class后就直接注册
> * ClassPathMapperScanner则找到class后，对beandefinition做一些修改
>
> ```java
> public Set<BeanDefinitionHolder> doScan(String... basePackages) {
>     Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
>     if (beanDefinitions.isEmpty()) {
>     } else {
>         processBeanDefinitions(beanDefinitions);
>     }
>     return beanDefinitions;
> }
> ```
>
> * 修改beandefinition的属性
>
> ```java
>   private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
>     GenericBeanDefinition definition;
>     for (BeanDefinitionHolder holder : beanDefinitions) {
>       definition = (GenericBeanDefinition) holder.getBeanDefinition();
> 
>       definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); 		// issue #59,修改beanclass，使用factoryBean
>       definition.setBeanClass(this.mapperFactoryBean.getClass());
> 		//修改属性
>       definition.getPropertyValues().add("addToConfig", this.addToConfig);
> 
>       boolean explicitFactoryUsed = false;
>       if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
>         definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
>         explicitFactoryUsed = true;
>       } else if (this.sqlSessionFactory != null) {
>         definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
>         explicitFactoryUsed = true;
>       }
> 
>       if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
>         if (explicitFactoryUsed) {
>         }
>         definition.getPropertyValues().add("sqlSessionTemplate", new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
>         explicitFactoryUsed = true;
>       } else if (this.sqlSessionTemplate != null) {
>         if (explicitFactoryUsed) {
>         }
>         definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
>         explicitFactoryUsed = true;
>       }
>       if (!explicitFactoryUsed) {
>         definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
>       }
>     }
>   }
> ```
>
> 注意：这里使用 `new RuntimeBeanReference(this.sqlSessionFactoryBeanName)`，动态 获取容器的相应名称/类型的Bean



#### @EnableAspectJAutoProxy

> 1. 在启动类上添加@EnableAspectJAutoProxy
>
> 2. 其实已经通过自动配置类引入了该注解
>
> 3. @EnableAspectJAutoProxy和@EnableTransactionManagement的区别
>
>    > * 两者都会注册beanName为`org.springframework.aop.config.internalAutoProxyCreator`的Bean
>    >
>    > * EnableAspectJAutoProxy将注册`AnnotationAwareAspectJAutoProxyCreator.class`
>    >
>    > * EnableTransactionManagement将注册`InfrastructureAdvisorAutoProxyCreator.class`
>    >
>    > * BeanFactory中beanName是不能重复的，所以只能注册其中一个。这时取List中的索引值大的
>    >
>    >   ```java
>    >   static {
>    >   	// Set up the escalation list...
>    >   	APC_PRIORITY_LIST.add(InfrastructureAdvisorAutoProxyCreator.class);
>    >   	APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);
>    >   	APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);
>    >   }
>    >   ```
>    >
>    >   

##### 1、自动配置： 引入注解

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(Advice.class)
	static class AspectJAutoProxyingConfiguration {

		@Configuration(proxyBeanMethods = false)
		@EnableAspectJAutoProxy(proxyTargetClass = false)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
				matchIfMissing = false)
		static class JdkDynamicAutoProxyConfiguration {

		}

		@Configuration(proxyBeanMethods = false)
		@EnableAspectJAutoProxy(proxyTargetClass = true)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
				matchIfMissing = true)
        //matchIfMissing表示即使 没有配置spring.aop.proxy-target-class也会创建
		static class CglibAutoProxyConfiguration {

		}

	}
}
```



##### 2、引入AspectJAutoProxyRegistrar

```java
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {
	boolean proxyTargetClass() default false;
	boolean exposeProxy() default false;
}
```

##### 3、AspectJAutoProxyRegistrar

```java
class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	public void registerBeanDefinitions(
			AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		//注册AnnotationAwareAspectJAutoProxyCreator
		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

		AnnotationAttributes enableAspectJAutoProxy =
				AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
		if (enableAspectJAutoProxy != null) {
			if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
			if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
				AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
			}
		}
	}

}
```

**4、测试用例**

```java
//工具类自动完成 AnnotationAwareAspectJAutoProxyCreator 的注册
@Test
public void testAopConfigUtils (){
    //AspectJAutoProxyRegistrar
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
    Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(System.out::println);
    AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(beanFactory);
    System.out.println("设置proxyTargetClass属性");
    bean.getPropertyValues().stream().forEach(n-> System.out.println(n.getValue()+"=="+n.getName()));
}
```





## 接口：BeanDefinitionRegistryPostProcessor

> spring中的BeanDefinitionRegistryPostProcessor是BeanFactoryPostProcessor的子接口，BeanFactoryPostProcessor的作用是在bean的定义信息已经加载但还没有初始化的时候执行方法postProcessBeanFactory(）方法，而BeanDefinitionRegistryPostProcessor是在BeanFactoryPostProcessor的前面执行。

### 接口定义

```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * All regular bean definitions will have been loaded,but no beans will have been instantiated yet. 
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
```

### 自定义BeanDefinitionRegistryPostProcessor

#### BeanDefinitionRegistryPostProcessor实现类

```java
public class MyimplBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("我是BeanDefinitionRegistryPostProcessor，postProcessBeanDefinitionRegistry()");
        Arrays.stream(registry.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("#################### 开始修改  ####################");
        BeanDefinition definition = registry.getBeanDefinition("student");
        definition.getPropertyValues().addPropertyValue("className","初三一班");
        System.out.println("#################### 结束修改  ####################");
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("我是BeanFactoryPostProcessor");
    }
}
```

#### 启动类

```java
@SpringBootApplication
@Import({MyHelloServiceImportSelectorImpl.class, MyImplImportBeanDefinitionRegistar2.class, MyimplBeanDefinitionRegistryPostProcessor.class})
//@EnableTransactionManagement //已通过自动配置引入
@MapperScan(basePackages = {"com.froggengo.practise.*.mapper"})
public class SpringMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class,args);
    }
}
```



#### 测试类

```java
@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P4_BeanDefinitionRegistryPostPocessor {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testBeanDefinitionRegistryPostPocessor (){
        Student bean = beanFactory.getBean(Student.class);
        Teacher teacher = bean.getTeacher();
        System.out.println(teacher.getName());
        System.out.println(teacher.getAge());
        //MyimplBeanDefinitionRegistryPostProcessor中再次修属性值
        System.out.println(bean.getClassName());
    }

}
//我是 ImportSelector，selectImports()
//我是 ImportBeanDefinitionRegistrar，registerBeanDefinitions()
//我是BeanDefinitionRegistryPostProcessor，postProcessBeanDefinitionRegistry()
//#################### 开始修改  ####################
//#################### 结束修改  ####################
//我是BeanFactoryPostProcessor
//fly
//20
//初三一班
```











### 开源实例

#### ConfigurationClassPostProcessor

springboot启动最重要的的注册BeanDefinition类

##### 注册

```properties
##1、启动过程中在
SpringApplication#prepareContext
  SpringApplication#load
 	SpringApplication#createBeanDefinitionLoader
 		返回new BeanDefinitionLoader(registry, sources);//sources为启动类run方法传参
```

**BeanDefinitionLoader**

> 这里可以看到以下默认的解析BeanDefinition的类，关注`AnnotatedBeanDefinitionReader`

```java
BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
    Assert.notNull(registry, "Registry must not be null");
    Assert.notEmpty(sources, "Sources must not be empty");
    this.sources = sources;
    this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
    this.xmlReader = new XmlBeanDefinitionReader(registry);
    if (isGroovyPresent()) {
        this.groovyReader = new GroovyBeanDefinitionReader(registry);
    }
    this.scanner = new ClassPathBeanDefinitionScanner(registry);
    this.scanner.addExcludeFilter(new ClassExcludeFilter(sources));
}
```

**AnnotatedBeanDefinitionReader**

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    //使用工具类注册ConfigurationClassPostProcessor等
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

**AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, null)**

> <font color=red>这里注册了非常重要的**BeanDefinitionRegistryPostProcessor**实现类ConfigurationClassPostProcessor</font>

```properties
####简化为以下，key-value形式
org.springframework.context.annotation.internalConfigurationAnnotationProcessor=ConfigurationClassPostProcessor.class
org.springframework.context.annotation.internalAutowiredAnnotationProcessor=AutowiredAnnotationBeanPostProcessor.class
org.springframework.context.annotation.internalCommonAnnotationProcessor=CommonAnnotationBeanPostProcessor.class
org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor=PersistenceAnnotationBeanPostProcessor.class
org.springframework.context.event.internalEventListenerProcessor=EventListenerMethodProcessor.class
org.springframework.context.event.internalEventListenerFactory=DefaultEventListenerFactory.class
```



## 接口：BeanFactoryPostProcessor

### 接口定义

```java
public interface BeanFactoryPostProcessor {
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
```







### 开源示例

#### spring启动默认BeanFactoryPostProcess

```java
org.springframework.context.annotation.ConfigurationClassPostProcessor
org.springframework.context.event.EventListenerMethodProcessor
org.springframework.context.support.PropertySourcesPlaceholderConfigurer
org.springframework.boot.context.properties.ConfigurationPropertiesBeanDefinitionValidator
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$PreserveErrorControllerTargetClassPostProcessor
```





#### PropertyPlaceholderAutoConfiguration

##### 自动配置：PropertyPlaceholderAutoConfiguration

```properties
##spring-boot-autoconfigure-2.0.5.RELEASE.jar!/META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\

```

##### 配置类：PropertyPlaceholderAutoConfiguration

```java
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class PropertyPlaceholderAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
```

##### PropertySourcesPlaceholderConfigurer

![30173810_SVn2](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131139.png)

> 调用过程较为复杂，最后调用到如下逻辑。
>
> 需要注意的是，**PropertySourcesPlaceholderConfigurer** 是BeanFactoryPostProcess的实现类，那么他只能在BeanDefinition的层面进行改动。并不是负责解析@Value()注解，而是有**AutowiredAnnotationBeanPostProcessor**负责解析

```java
public class BeanDefinitionVisitor {
	public void visitBeanDefinition(BeanDefinition beanDefinition) {
		visitParentName(beanDefinition);
		visitBeanClassName(beanDefinition);
		visitFactoryBeanName(beanDefinition);
		visitFactoryMethodName(beanDefinition);
		visitScope(beanDefinition);
		if (beanDefinition.hasPropertyValues()) {
			visitPropertyValues(beanDefinition.getPropertyValues());
		}
		if (beanDefinition.hasConstructorArgumentValues()) {
			ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
			visitIndexedArgumentValues(cas.getIndexedArgumentValues());
			visitGenericArgumentValues(cas.getGenericArgumentValues());
		}
	}
}
```



##### 测试

注册BeanDefinition类：

> 这个类沿用了前面的测试类，但增加了name属性的赋值，使用`${com.froggengo.test}`占位符，由
>
> PropertySourcesPlaceholderConfigurer进行解析@Autowired和@Value

```java
public class MyimplBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("我是BeanDefinitionRegistryPostProcessor，postProcessBeanDefinitionRegistry()");
        Arrays.stream(registry.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("#################### 开始修改  ####################");
        BeanDefinition definition = registry.getBeanDefinition("student");
        definition.getPropertyValues().addPropertyValue("className","初三一班");
        //使用占位符测试 PropertySourcesPlaceholderConfigurer
        definition.getPropertyValues().addPropertyValue("name","${com.froggengo.test}");
        System.out.println("#################### 结束修改  ####################");
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("我是BeanFactoryPostProcessor");
    }
}
```



测试类：

```java
@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P5_BeanFactoryPostProcess   {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    /**
     * @see PropertySourcesPlaceholderConfigurer
     */
    @Test
    public void testPropertySourcesPlaceholderConfigurer (){
        Student bean = beanFactory.getBean(Student.class);
        System.out.println(bean.getName());
        System.out.println(bean.getClassName());
    }
}
```







## 接口：InstantiationAwareBeanPostProcessor

### 接口定义

```java
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) 
        throws BeansException {
		return null;
	}

	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Nullable
	default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {

		return null;
	}
	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, 
        Object bean, String beanName) throws BeansException {

		return pvs;
	}

}

```







### 开源示例

#### spring启动默认实现

```java
org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
```









## 接口：MergedBeanDefinitionPostProcessor

### 接口定义

```java
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

	default void resetBeanDefinition(String beanName) {
	}

}
```





## 接口：BeanPostProcessor

### 接口定义

```java
public interface BeanPostProcessor {
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
```



### 开源示例

#### spring启动默认实现

```java
org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor
org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor
org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor
org.springframework.validation.beanvalidation.MethodValidationPostProcessor
org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerPostProcessor
//AOP
org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer$PropertyMappingCheckBeanPostProcessor
```





## 接口：ApplicationContextInitializer

### 接口定义

> 在容器刷新 `refresh()`之前被调用，一般用于容器上下文中的初始化 ，比如property sources

```java
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
	void initialize(C applicationContext);
}
```



### 开源示例

#### ConfigFileApplicationContextInitializer

```java
public class ConfigFileApplicationContextInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
   @Override
   public void initialize(ConfigurableApplicationContext applicationContext) {
      new ConfigFileApplicationListener() {
         public void apply() {
            addPropertySources(applicationContext.getEnvironment(), applicationContext);
            addPostProcessors(applicationContext);
         }
      }.apply();
   }
}
//为environment添加new RandomValuePropertySource(RANDOM_PROPERTY_SOURCE_NAME)
//为context.addBeanFactoryPostProcessor(new PropertySourceOrderingPostProcessor(context));
```



## 依赖注入相关

### 接口：RuntimeBeanReference





### 接口：ObjectProvider





#### 接口：NamedContextFactory

> 此接口在：org.springframework.cloud.context.named.NamedContextFactory







## 接口：BeanDefinition











##  问题

1. 为什么是三级缓存解决循环依赖问题，而不是两级

   

2. 

# 笔记

### AOP的三种方式

1. 编译期（complie）织入
2. 加载期（LoadTime）织入
   * 需要java.lang.instruments包支持
   * Spring Load Time Weaving
3. 运行时（RunTime）织入
   * Spring一般使用的就是运行时织入

> AspectJ 与spring aop是两套不同的东西，虽然 spring aop 使用了 @Aspectj注解，但是其实和Aspectj一点关系都没有。spring aop只是借用了Aspectj里面比如切点、增强等概念，实际底层是jdkproxy和cglib
>
> Aspectj（[安装](http://www.eclipse.org/aspectj/downloads.php)）：spring aop是在代码运行时动态的生成class文件达到动态代理的目的，那我们现在回到静态代理，静态代理唯一的缺点就是我们需要对每一个方法编写我们的代理逻辑，造成了工作的繁琐和复杂。AspectJ就是为了解决这个问题，在编译成class字节码的时候在方法周围加上业务逻辑。复杂的工作由特定的编译器帮我们做。

---

### Spring的支持

三要素

* 切点poincut

* 增强advice
* 切面advisor
* 织入weaving

#### 解析

> 1. **@EnableAspectJAutoProxy**：负责improt导入**AspectJAutoProxyRegistrar**
>
> 2. **ImportBeanDefinitionRegistrar**：实现了**ImportBeanDefinitionRegistrar**
>
> ```java
> AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
> // 注册 AnnotationAwareAspectJAutoProxyCreator.class
> ```
>
> 2. **AnnotationAwareAspectJAutoProxyCreator**:实现了**BeanPostProcessor**及**InstantiationAwareBeanPostProcess**，从而具备Bean在初始化及实例化过程中修改Bean的能力
>
> ```java
> // BeanPostProcessor
> @Nullable
> default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
>     return bean;
> }
> @Nullable
> default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
>     return bean;
> }
> // InstantiationAwareBeanPostProcessor
> @Nullable
> default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
>    return null;
> }
> default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
>     return true;
> }
> @Nullable
> default PropertyValues postProcessPropertyValues(
>     PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
> 
>     return pvs;
> }
> ```
>
> 2. **AnnotationAwareAspectJAutoProxyCreator** 封装了 **ReflectiveAspectJAdvisorFactory**及**BeanFactoryAspectInstanceFactory**，用于查询BeanFactory中注解了**@ApectJ**的类，并获取其中注解了`Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class`的方法，并创建出相应的**Advisor类**，
>
>    ```java
>    org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation // spring启动中会调用的方法
>    org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors
>    org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors
>      org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#isAspect //判断BeanFactory中@Aspectj的类
>      org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors //取出@Aspectj类中的方法，并封装成Advisor类
>    ```
>
>    
>
> 3. 最后实例化
>
>    ```java
>    org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization
>    org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#createProxy
>    ProxyFactory proxyFactory = new ProxyFactory();// 负责最后完成代理创建
>    DefaultAopProxyFactory //实际创建代理的工厂类
>    ObjenesisCglibAopProxy  // Cglib
>    JdkDynamicAopProxy  //jdk动态代理
>    ```
>
>    
>
> 4. 底层则是：
>
>    1. CGLIB
>    2. JDKProxy
>
> 5. 奇怪的地方是，
>
>    1. postProcessBeforeInstantiation及postProcessAfterInitialization都会创建代理
>
> 

### JdkProxy

1. 代码实例

   * ClassLoader
   * 类接口
   * `InvocationHandler`接口的实现类，就是真实代理类执行的地方

   ```java
       @Test
   public void test(){
           InvocationHandler invocationHandler = new InvocationHandler() {
               // 这里写死了注入对象
               Annimal annimal = new Annimal();//实现InvocationHandler负责注入希望代理的对象
               @Override
               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                   System.out.println("测试jdkReflect");
                   return method.invoke(annimal, args);
               }
           };
           Object proxyInstance = Proxy.newProxyInstance(
                                               Annimal.class.getClassLoader(),
                                               Annimal.class.getInterfaces(),
                                               invocationHandler );
           int sale = ((MyItem) proxyInstance).sale();
           int i = ((MyItem) proxyInstance).hashCode();
   }
   // Annimal类
   public class Annimal implements MyItem {
       @Override
       public int sale(){
           System.out.println("annimal销售");
           return 1000;
       }
   }
   // 父类接口
   public interface MyItem {
       public int sale();
   }
   //输出
   测试jdkReflect
   annimal销售
   测试jdkReflect
   ```

2. 将动态生成的代理类输出至文件

   ```java
   byte[] cls = ProxyGenerator.generateProxyClass("$Proxy0", 
                                              proxyInstance.getClass().getInterfaces());
   File file = new File("D:/$Proxy0.class");
   try (FileOutputStream outputStream = new FileOutputStream(file);){
       outputStream.write(cls);
       outputStream.flush();
   } catch (FileNotFoundException e) {
       e.printStackTrace();
   } catch (IOException e) {
       e.printStackTrace();
   }
   ```

   得到如下的代理类

   ```java
   public final class $Proxy0 extends Proxy implements MyItem {
       private static Method m1;
       private static Method m2;
       private static Method m3;
       private static Method m0;
   	// 需要传入InvocationHandler
       public $Proxy0(InvocationHandler var1)   {
           super(var1);
       }
       public final int sale()   {
           try {
               return (Integer)super.h.invoke(this, m3, (Object[])null);
           } catch (RuntimeException | Error var2) {
               throw var2;
           } catch (Throwable var3) {
               throw new UndeclaredThrowableException(var3);
           }
       }
   
       public final int hashCode()   {
           try {
               return (Integer)super.h.invoke(this, m0, (Object[])null);
           } catch (RuntimeException | Error var2) {
               throw var2;
           } catch (Throwable var3) {
               throw new UndeclaredThrowableException(var3);
           }
       }
   
       static {
           try {
               m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
               m2 = Class.forName("java.lang.Object").getMethod("toString");
               m3 = Class.forName("Context.aop.MyItem").getMethod("sale");
               m0 = Class.forName("java.lang.Object").getMethod("hashCode");
           } catch (NoSuchMethodException var2) {
               throw new NoSuchMethodError(var2.getMessage());
           } catch (ClassNotFoundException var3) {
               throw new NoClassDefFoundError(var3.getMessage());
           }
       }
   }
   
   ```

   

3. 原理分析

   * Proxy.newProxyInstance()，将创建代理类 ,该类继承`Proxy`类，并实现我们想要的接口 `MyItem`

     > **java类只能继承一个类，可实现多个接口，这就是为什么JDK动态代理必须有接口了**

     ```java
     public final class $Proxy0 extends Proxy implements MyItem{
         
     }
     ```

   * 比如调用代理对象的方法时，如下

     > 事实上所有的方法 ，都转换为了对`InvocationHandler`的调用，method在静态代码块中生成。
     >
     > 所以对代理对象调用`((MyItem) proxyInstance).hashCode();`也会打印 ：**测试jdkReflect**

     ```java
     int sale = ((MyItem) proxyInstance).sale(); 
     //实际是调用，如下代码，h为传入的InvocationHandler
     public final int sale()   {
         try {
             return (Integer)super.h.invoke(this, m3, (Object[])null);
         } catch (RuntimeException | Error var2) {
             throw var2;
         } catch (Throwable var3) {
             throw new UndeclaredThrowableException(var3);
         }
     }
     ```

   4. 思考为啥非得继承proxy或者说必须有接口

      > 代理，说明存在两个对象，一个是原始对象一个是代理对象。而代理对象其实只做转发，不做实际的操作。如果使用cglib继承原始类的方法，则原始类的字段也会继承，这导致内存资源的浪费。
      >
      > 最致命的是，原始类的final方法不能被继承及覆写。
      >
      > 通过接口实现代理，即使是final方法也能代理

   #### Cglib

   1. 与JDKProxy类似，不过是继承代理类，进而调用callback继承类

   

#### 思考问题

1. 怎么解析@Aspectj以及@PointCut、及@Before等
   1. 必须配合@Component使用，被@Component-scan扫描注入BeanFactory
   2. @EnableAspectjAutoProxy,导入配置类，注入AnnotationAwareAspectJAutoProxyCreator，负责解析@Aspectj类，以及完成代理创建
2. 怎么织入weaving

### 资料

[Load_Time_weaving](https://www.cnblogs.com/grey-wolf/p/12228958.html) b