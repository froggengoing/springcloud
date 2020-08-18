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