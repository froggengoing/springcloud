# Vertx

## Vertx接口

![image-20210206235607720](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323130344.png)

主要功能

* 创建TCP客户端和服务器
* 创建HTTP客户端和服务器
* 创建DNS客户端
* 创建数据包套接字socket
* 设置或取消定时或一次的任务
* 获取Eventbus
* 获取file styem
* 获取共享数据
* 部署或者撤销Verticle

```java
interface Vertx extends Measured {
    //创建vertx实例
    Vertx vertx();
    Vertx vertx(VertxOptions);
    //创建集群vertx
    void clusteredVertx(VertxOptions, Handler<AsyncResult<Vertx>>);
    boolean isClustered();
    //获取当前上下文
	Context currentContext() ;
    //获取或者创建上下文
    Context getOrCreateContext();
    //当前上下文中执行指定的handler,此方法非常重要,提交任务的EventLoopGroup
    void runOnContext(Handler<Void> action);
    
    //########IO相关############
    //创建TCP/SSL server和创建TCP/SSL client
    NetServer createNetServer(NetServerOptions);
    NetServer createNetServer();
    NetClient createNetClient(NetClientOptions);
    NetClient createNetClient();
    //创建HTTP/HTTPS server和HTTP/HTTPS client
    HttpServer createHttpServer(HttpServerOptions);
    HttpServer createHttpServer();
    HttpClient createHttpClient(HttpClientOptions);
    HttpClient createHttpClient();
    //创建datagram socket
    DatagramSocket createDatagramSocket(DatagramSocketOptions);
    DatagramSocket createDatagramSocket();
    //创建DNS client
    DnsClient createDnsClient(int port, String host);
    DnsClient createDnsClient();
    DnsClient createDnsClient(DnsClientOptions);
    //处理文件系统
    FileSystem fileSystem();
    
    
    //部署相关
    void deployVerticle(Verticle verticle);
    void deployVerticle(Verticle verticle, Handler<AsyncResult<String>>);
    void deployVerticle(Verticle verticle, DeploymentOptions);
    void deployVerticle(Class<? extends Verticle>, DeploymentOptions);
    void deployVerticle(Supplier<Verticle>, DeploymentOptions);
    void deployVerticle(Verticle, DeploymentOptions, Handler<AsyncResult<String>>);
    void deployVerticle(Class<? extends Verticle>, DeploymentOptions,Handler);
    void deployVerticle(Supplier<Verticle>, DeploymentOptions, Handler);
    void deployVerticle(String name);
    void deployVerticle(String name, Handler<AsyncResult<String>>);
    void deployVerticle(String name, DeploymentOptions);
    void deployVerticle(String name, DeploymentOptions, Handler);
    void undeploy(String deploymentID);
    void undeploy(String deploymentID, Handler<AsyncResult<Void>> completionHandler);
    //返回所有部署的id
    Set<String> deploymentIDs();
    //VertxFactor用于部署Vertx
    void registerVerticleFactory(VerticleFactory factory);
    void unregisterVerticleFactory(VerticleFactory factory);
    Set<VerticleFactory> verticleFactories();
    //设置线程池
    WorkerExecutor createSharedWorkerExecutor(String name, int poolSize, long maxExecuteTime, TimeUnit maxExecuteTimeUnit);
    
    //####其他##
    //获取 EventBus,每个Vertx实例只有一个 EventBus
    EventBus eventBus();
    //获取共享数据
    SharedData sharedData();
    //####任务##
    //设置一个执行一次的任务,定时器
    long setTimer(long delay, Handler<Long>);
    //一次性的读取流
    TimeoutStream timerStream(long delay);
    //设置一个定期执行的任务,定时任务
    long setPeriodic(long delay, Handler<Long> handler);
    //周期性的读取流
    TimeoutStream periodicStream(long delay);
    //取消定时任务
    boolean cancelTimer(long id);
    //关闭Vertx并释放资源
    void close();
    void close(Handler<AsyncResult<Void>> completionHandler);
    //异常Handler
    Vertx exceptionHandler(@Nullable Handler<Throwable> handler);
    Handler<Throwable> exceptionHandler();
}
```



## VertxImpl

构造方法

```java
  private VertxImpl(VertxOptions options, Transport transport) {
    closeHooks = new CloseHooks(log);
      //检查线程阻塞,当任务执行超过一定时间报异常
    checker = new BlockedThreadChecker(options.getBlockedThreadCheckInterval(), options.getBlockedThreadCheckIntervalUnit(), options.getWarningExceptionTime(), options.getWarningExceptionTimeUnit());
    maxEventLoopExTime = options.getMaxEventLoopExecuteTime();
    maxEventLoopExecTimeUnit = options.getMaxEventLoopExecuteTimeUnit();
      //线程工厂,实现JDK的ThreadFactory接口
    eventLoopThreadFactory = new VertxThreadFactory("vert.x-eventloop-thread-", checker, false, maxEventLoopExTime, maxEventLoopExecTimeUnit);
      //创建Netty的 NioEventLoopGroup
    eventLoopGroup = transport.eventLoopGroup(Transport.IO_EVENT_LOOP_GROUP, options.getEventLoopPoolSize(), eventLoopThreadFactory, NETTY_IO_RATIO);
      //Acceptor的线程工程,接口socket请求
    ThreadFactory acceptorEventLoopThreadFactory = new VertxThreadFactory("vert.x-acceptor-thread-", checker, false, options.getMaxEventLoopExecuteTime(), options.getMaxEventLoopExecuteTimeUnit());
      //acceptor的NioEventLoopGroup
    acceptorEventLoopGroup = transport.eventLoopGroup(Transport.ACCEPTOR_EVENT_LOOP_GROUP, 1, acceptorEventLoopThreadFactory, 100);

    metrics = initialiseMetrics(options);

    //创建线程池,ThreadPoolExecutor为JDK类
    int workerPoolSize = options.getWorkerPoolSize();
    ExecutorService workerExec = new ThreadPoolExecutor(workerPoolSize, workerPoolSize,
      0L, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>(),
      new VertxThreadFactory("vert.x-worker-thread-", checker, true, options.getMaxWorkerExecuteTime(), options.getMaxWorkerExecuteTimeUnit()));
      
    PoolMetrics workerPoolMetrics = metrics != null ? metrics.createPoolMetrics("worker", "vert.x-worker-thread", options.getWorkerPoolSize()) : null;
      //创建阻塞线程池
    ExecutorService internalBlockingExec = Executors.newFixedThreadPool(options.getInternalBlockingPoolSize(),
        new VertxThreadFactory("vert.x-internal-blocking-", checker, true, options.getMaxWorkerExecuteTime(), options.getMaxWorkerExecuteTimeUnit()));
    PoolMetrics internalBlockingPoolMetrics = metrics != null ? metrics.createPoolMetrics("worker", "vert.x-internal-blocking", options.getInternalBlockingPoolSize()) : null;
      
    internalBlockingPool = new WorkerPool(internalBlockingExec, internalBlockingPoolMetrics);
    namedWorkerPools = new HashMap<>();
      //工作线程池
    workerPool = new WorkerPool(workerExec, workerPoolMetrics);
    defaultWorkerPoolSize = options.getWorkerPoolSize();
    maxWorkerExecTime = options.getMaxWorkerExecuteTime();
    maxWorkerExecTimeUnit = options.getMaxWorkerExecuteTimeUnit();

    this.transport = transport;
      //文件Io相关的处理
    this.fileResolver = new FileResolver(options.getFileSystemOptions());
    this.addressResolverOptions = options.getAddressResolverOptions();
    this.addressResolver = new AddressResolver(this, options.getAddressResolverOptions());
      //部署管理器,用户Verticle的部署
    this.deploymentManager = new DeploymentManager(this);
    if (options.getEventBusOptions().isClustered()) {
        //集群管理器和集群的eventBus
      this.clusterManager = getClusterManager(options);
      this.eventBus = new ClusteredEventBus(this, options, clusterManager);
    } else {
        //本地的eventBus和集群EventBus
      this.clusterManager = null;
      this.eventBus = new EventBusImpl(this);
    }
      //共享数据,可以在集群或者本地范围共享数据
    this.sharedData = new SharedDataImpl(this, clusterManager);
  }
```



## Context

![image-20210206222818138](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323130345.png)



### 接口

```java
public interface Context {    
	//同一个context中异步执行action
  void runOnContext(Handler<Void> action);
  /**
   * 使用worker pool执行阻塞代码，执行完成会回调结果给原来的上下文
   * 超过10秒，blocked-thread-checker会打印一个消息
   * 长阻塞的操作，应使用vertx管理的专用线程
   */
  <T> void executeBlocking(Handler<Promise<T>> blockingCodeHandler, boolean ordered, Handler<AsyncResult<@Nullable T>> resultHandler);
  //执行注释代码， order = true，即同一个上下文中，按顺序执行
  <T> void executeBlocking(Handler<Promise<T>> blockingCodeHandler, Handler<AsyncResult<@Nullable T>> resultHandler);

  //返回创建当前context的Vertx instance
  Vertx owner();  
  //获取上线文中的数据
  <T> T get(String key);
  //在contxt中设置数据，在不同handlers中分享数据
  void put(String key, Object value);
  //移除数据
  boolean remove(String key);  
  //返回关联的Verticle deploymentID
  String deploymentID();
  //返回关联的 Verticle deployment configuration。
  @Nullable JsonObject config();
  List<String> processArgs();
  //verticle实例的数量
  int getInstanceCount();

  //uncaught throwable的处理器
  Context exceptionHandler(@Nullable Handler<Throwable> handler);
  //返回当前current exception handler
  Handler<Throwable> exceptionHandler();
    
  //类型判断
  static boolean isOnWorkerThread() {return ContextInternal.isOnWorkerThread();}
  static boolean isOnEventLoopThread() {return ContextInternal.isOnEventLoopThread();}
  static boolean isOnVertxThread() {return ContextInternal.isOnVertxThread();}
  boolean isEventLoopContext();
  boolean isWorkerContext();
  boolean isMultiThreadedWorkerContext();  
  
  //关闭的钩子
  void addCloseHook(Closeable hook);
  boolean removeCloseHook(Closeable hook);

}
```











## EventBus

![image-20210206222645249](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323130346.png)

## Consumer

![image-20210206222417587](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323130347.png)

## Verticle

![image-20210207000316765](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323130348.png)

```java
//提供action风格的部署和并发模型
//一个vertx可以部署多个Verticle
//
public interface Verticle {

  //获取Vert.x instance
  Vertx getVertx();

  //使用Vert.x实例和上下文初始化 verticle  with the Vert.x instance and the context.
  void init(Vertx vertx, Context context);

  //启动Verticle
  default void start(Promise<Void> startPromise) throws Exception {
    start((Future<Void>) startPromise);
  }
  //停用verticle
  default void stop(Promise<Void> stopPromise) throws Exception {
    stop((Future<Void>) stopPromise);
  }
}

```



## EventLoopGroup









## DeploymentManager





## BlockedThreadChecker