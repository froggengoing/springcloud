## 1. SpringBoot启动

### 1. SpringApplication初始化 
   1. 初始化
      
      ```java
          public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
              this.sources = new LinkedHashSet();
              this.bannerMode = Mode.CONSOLE;
              this.logStartupInfo = true;
              this.addCommandLineProperties = true;
              this.headless = true;
              this.registerShutdownHook = true;
              this.additionalProfiles = new HashSet();
              this.isCustomEnvironment = false;
              this.resourceLoader = resourceLoader;
              Assert.notNull(primarySources, "PrimarySources must not be null");
              this.primarySources = new LinkedHashSet(Arrays.asList(primarySources));
              this.webApplicationType = this.deduceWebApplicationType();
              this.setInitializers(this.getSpringFactoriesInstances(ApplicationContextInitializer.class));
              this.setListeners(this.getSpringFactoriesInstances(ApplicationListener.class));
              this.mainApplicationClass = this.deduceMainApplicationClass();
          }
      ```
      
      通过`this.getSpringFactoriesInstances`获取`META-INF/spring.factories`中配置的自定义对应的接口类
      
   2. 涉及接口 

      1. ApplicationContextInitializier
         
         ApplicationContext执行refresh之前，调用ApplicationContextInitializer的initialize()方法，对ApplicationContext做进一步的设置和处理。
         
         ```java
         public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
             void initialize(C var1);
         }
         ```
         
      2. ApplicationListener
      
         事件监听器，其作用可以理解为在SpringApplicationRunListener发布通知事件时，由ApplicationListener负责接收。
      
         ```java
         @FunctionalInterface
         public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
             void onApplicationEvent(E var1);
         }
         ```

3. SpringBoot默认Listener和ContextInitializier

   ```properties
   # Run Listeners
   org.springframework.boot.SpringApplicationRunListener=\
   org.springframework.boot.context.event.EventPublishingRunListener
   
   # Error Reporters
   org.springframework.boot.SpringBootExceptionReporter=\
   org.springframework.boot.diagnostics.FailureAnalyzers
   
   # Application Context Initializers
   org.springframework.context.ApplicationContextInitializer=\
   org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
   org.springframework.boot.context.ContextIdApplicationContextInitializer,\
   org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
   org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer
   
   # Application Listeners
   org.springframework.context.ApplicationListener=\
   org.springframework.boot.ClearCachesApplicationListener,\
   org.springframework.boot.builder.ParentContextCloserApplicationListener,\
   org.springframework.boot.context.FileEncodingApplicationListener,\
   org.springframework.boot.context.config.AnsiOutputApplicationListener,\
   org.springframework.boot.context.config.ConfigFileApplicationListener,\
   org.springframework.boot.context.config.DelegatingApplicationListener,\
   org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
   org.springframework.boot.context.logging.LoggingApplicationListener,\
   org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
   
   ```
# Environment Post Processors
   #解析 spring.application.json 或 SPRING_APPLICATION_JSON 配置的 json 字符串。--spring.application.json='{"foo":"bar"}'
   #将环境编辑添加至environment中
   org.springframework.boot.env.EnvironmentPostProcessor=\
   org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
   org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
   org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor

1. SpringApplicationRunListener接口，可以理解为执行ApplicationListener接口
### 2. run()方法

```java
    public interface SpringApplicationRunListener {
    ····//通知监听器，SpringBoot开始执行
        void starting();
        ////通知监听器，Environment准备完成
        void environmentPrepared(ConfigurableEnvironment environment);
        //通知监听器，ApplicationContext已经创建并初始化完成
        void contextPrepared(ConfigurableApplicationContext context);
        //通知监听器，ApplicationContext已经完成IoC配置加载
        void contextLoaded(ConfigurableApplicationContext context);
        //通知监听器，SpringBoot启动完成
        void started(ConfigurableApplicationContext context);
        void running(ConfigurableApplicationContext context);
        void failed(ConfigurableApplicationContext context, Throwable exception);
    }
```



springboot默认提供`EventPublishingRunListener`,注册`applicationListener`，并调用`onApplicationEvent(E var1)`对相应事件做处理

2. Springboot默认事件

    ```java
    //ApplicationEnvironmentPreparedEvent
    //ApplicationPreparedEvent
    //ApplicationStartedEvent
    //ApplicationReadyEvent
    //ApplicationFailedEvent
    //ApplicationStartingEvent
    ```

    

3. SpringBootExceptionReporter接口

    ```java
    @FunctionalInterface
    public interface SpringBootExceptionReporter {
    boolean reportException(Throwable failure);
    }
    ```

4. RUN方法具体做什么

    ```java
    public ConfigurableApplicationContext run(String... args) {
        //1、开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
    	//设置系统属性『java.awt.headless』，为true则启用headless模式支持
        this.configureHeadlessProperty();
        //2、获取SpringApplicationRunListener(spring.factories中配置)，并启动starting()
        //默认为EventPublishingRunListener
        SpringApplicationRunListeners listeners = this.getRunListeners(args);
        listeners.starting();
    
        Collection exceptionReporters;
        try {
            //3、获取启动commanline参数，比如--server.port=9090
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
           // 4、设置environment中propertySource（即启动参数），并--spring.profiles.active的值，如有则设置environment为对应的环境参数ActiveProfiles
            //调用ConfigFileApplicationListener完成yml或properties文件解析
            ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
            //5、获取System.getProperty("spring.beaninfo.ignore")的值，一般没空未知用途
            this.configureIgnoreBeanInfo(environment);
            //6、打印banner， 默认spring，可以通过environment.getProperty("spring.banner.location", "banner.txt")设置
            Banner printedBanner = this.printBanner(environment);
            //
            context = this.createApplicationContext();
            exceptionReporters = this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
            //7、
            this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            //8、
            this.refreshContext(context);
            this.afterRefresh(context, applicationArguments);
            stopWatch.stop();
            if (this.logStartupInfo) {
                (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
            }
    
            listeners.started(context);
            this.callRunners(context, applicationArguments);
        } catch (Throwable var10) {
            this.handleRunFailure(context, var10, exceptionReporters, listeners);
            throw new IllegalStateException(var10);
        }
    
        try {
            listeners.running(context);
            return context;
        } catch (Throwable var9) {
            this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
            throw new IllegalStateException(var9);
        }
    }
    ```


​         

## 2.  DefaultApplicationArguments  

 解析启动参数，分为 *option arguments* and *non-option arguments*  。

```properties
 java -jar qs.jar --spring.profile.active=dev
 其余格式如下均正确：
 --foo
 --foo=
 --foo=""
 --foo=bar
 --foo="bar then baz"
 --foo=bar,baz,biz
 如没有--则为non-option arguments
```

## 3.StandardServletEnvironment



## 3. BeanUtils

单独

## 【问题】

1. 怎么解析yml文件，怎么在一个文件中区分环境。

答：由`YamlPropertySourceLoader`解析。对一个文件，会以---分组解析到`List<PropertySource<?>>`中一个`PropertySource<?>`(可以看作是【key1,map(key2,value)】结构)

```properties
applicationConfig: [classpath:/application.yml] (document #0)
server.port=8080
spring.profiles.active=test
applicationConfig: [classpath:/application.yml] (document #1)
server.port=9999
spring.profiles.active=nihao
```

```JAVA
    @Test
    public void testParseYml() throws IOException {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        ClassPathResource resource = new ClassPathResource("application.yml");
        String name = "applicationConfig: [classpath:/application.yml]";
        List<PropertySource<?>> load = yamlPropertySourceLoader.load(name, resource);
        load.forEach(n->{
            System.out.println(n.getName());
            Map<String,Object> source = (Map<String, Object>) n.getSource();
            source.forEach((k,v)-> System.out.println(k+"="+v));
        });
    }
```

2. 配置文件加载优先顺序？properties？yml
3. 如何获取key、value，重复怎么处理





## 3. SpringMVC组件

   先思考请求到返回的流程应该有什么。

+ 客户端：发出请求，我想要什么。 

+ 服务器：接收请求，谁来处理，执行处理，返回结果， 

   1. 路由：即找到那个Servlet处理。但对于Spring，统一将所有的请求交给Dispatcher处理。Dispatcher再将拦截下的请求找到相应的controller处理。处理又细分为：

      1. 路由：处理器映射器 HandlerMappings，即**找**到是哪个处理器来处理请求

      2. 处理：处理器适配器HandlerAdapters，即通过适配器**调**用具体的处理器

      3. 返回：视图解析器ViewSolvers，返回不同格式的文件需要不同的解析器。

         | JSP           | InternalResourceViewResolver |
         | ------------- | ---------------------------- |
         | **Thymeleaf** | **ThymeleafViewResolver**    |

   其他就是在这个基础上修补了，

      1. RequestToViewTranslator ：请求视图解析器，即处理器没有返回相应的视图时，根据 Request请求路径获取名称相同的视图。
   2. HandlerExceptionResolvers：处理器异常解析器，即处理器处理过程发生异常时，交给他处理
   3. MultiPartResolver：上传文件解析器
   4. LocaleResolver ：本地化解析器
   5. ThemeResolver：主题解析器

 