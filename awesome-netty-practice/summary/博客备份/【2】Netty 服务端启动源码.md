# Netty 服务端启动源码

 Posted on 2019-09-26 | In [Netty](https://jimmy2angel.github.io/categories/Netty/)

Netty 服务端启动代码基本如下所示，服务端启动时 Netty 内部为我们做了什么？

- 服务端的线程模型是如何设置的？
- 服务端的 channel 是如何创建并初始化的？
- 具体又是怎么绑定端口后监听客户端连接请求的？

带着以上几个问题，下面我们来一探究竟。

```
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup();
try {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
            // 指定线程模型
            .group(bossGroup, workerGroup)
            // 指定IO模型
            .channel(NioServerSocketChannel.class)
            // 设置 TCP 参数
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.TCP_NODELAY, true)
            // 设置业务处理 handler
            .handler(new MyHandler())
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MyChildHandler());
                }
            });
    // 绑定端口
    ChannelFuture future = serverBootstrap.bind(port).sync();
    future.channel().closeFuture().sync();
} finally {
    // 优雅停机
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
}
```

## 核心概念

- ServerBootstrap

  > 作为抽象辅助类 AbstractBootstrap 的子类，ServerBootstrap 主要提供给服务端使用；
  >
  > 基于此进行一些 group、channel、option、handler、childOption 和 childHandler 的配置，其中 option 和 handler 是针对自身的，而 childOption 和 childHandler 是针对后面接收的每个连接的；

- NioServerSocketChannel

  > 封装了 jdk 的 ServerSocketChannel，且存在一个属性 readInterestOp 记录对什么事件感兴趣，初始化时为 `SelectionKey.OP_ACCEPT`；
  > 以及继承自父类 AbstractChannel 的三大属性 id（全局唯一标识）、unsafe（依附于 channel，负责处理操作） 和 pipeline（责任链模式处理请求）；

- Unsafe

  > Unsafe 附属于 Channel，大部分情况下仅在内部使用；

- ChannelPipeline

  > 初始化 DefaultChannelPipeline 时，设置 HeadContext、TailContext 两个节点组成双向链表结构，两个节点均继承自 AbstractChannelHandlerContext；
  >
  > 换句话说 ChannelPipeline 就是一个双向链表 （head ⇄ handlerA ⇄ handlerB ⇄ tail），链表的每个节点是一个 AbstractChannelHandlerContext；

- NioEventLoopGroup

  > 有个属性 EventExecutor[]，而下面要说的 NioEventLoop 就是 EventExecutor；
  >
  > 按 Reactor (主从)多线程模型来说，服务端存在两个 NioEventLoopGroup，一个 NioEventLoopGroup（bossGroup） 用来接收连接，然后将连接丢给另一个 NioEventLoopGroup（workerGroup），
  > 以后该连接的读写事件均有这个 workerGroup 的一个 NioEventLoop 来执行；

- NioEventLoop

  > 核心线程（其实看源码的话，好像理解成一个 task 更合理），含有重要属性 executor、taskQueue、selector 等；

## 创建 NioEventLoopGroup

创建 NioEventLoopGroup 的时候最终调用其父类 MultithreadEventExecutorGroup 的构造方法，主要代码如下：

```
protected MultithreadEventExecutorGroup(int nThreads, Executor executor,
                                            EventExecutorChooserFactory chooserFactory, Object... args) {
        if (executor == null) {
            // 后面会将 NioEventLoop run 方法包装成一个 task，然后交给这个 executor 执行，
            // 而这个 executor 仅仅启动一个新线程去执行该 task，这个执行线程就是IO核心线程
            executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
        }
        // 初始化 EventExecutor[]
        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                // 调用 NioEventLoopGroup 的 newChild 方法创建 NioEventLoop，此时尚未创建核心线程
                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {
                // TODO: Think about if this is a good exception type
                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {
                if (!success) {
                    ...
                }
            }
        }
        // chooser 主要是用来从 EventExecutor[] 中选取一个 NioEventLoop
        chooser = chooserFactory.newChooser(children);
    }
```

创建 NioEventLoopGroup 主要做了以下几件事：

1. **没有 executor 则创建一个 executor**
2. **初始化一个 EventExecutor[]，为创建 NioEventLoop 做准备**
3. **循环调用 newChild 方法（传入 executor ）去创建 NioEventLoop 对象，置于 EventExecutor[] 数组当中**
4. **创建一个 chooser，用来从 EventExecutor[] 选取处一个 NioEventLoop**

## 创建 NioEventLoop

创建 NioEventLoop 的时候会调用其父类 SingleThreadEventExecutor 的构造方法，主要代码如下：

```
protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor,
                                    boolean addTaskWakesUp, int maxPendingTasks,
                                    RejectedExecutionHandler rejectedHandler) {
    super(parent);
    this.addTaskWakesUp = addTaskWakesUp;
    this.maxPendingTasks = Math.max(16, maxPendingTasks);
    this.executor = ObjectUtil.checkNotNull(executor, "executor");
    // 任务队列
    taskQueue = newTaskQueue(this.maxPendingTasks);
    rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
}
 
NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider,
             SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler) {
    super(parent, executor, false, DEFAULT_MAX_PENDING_TASKS, rejectedExecutionHandler);
    if (selectorProvider == null) {
        throw new NullPointerException("selectorProvider");
    }
    if (strategy == null) {
        throw new NullPointerException("selectStrategy");
    }
    provider = selectorProvider;
    final SelectorTuple selectorTuple = openSelector();
    // 之后要把 channel 注册在 selector 上
    selector = selectorTuple.selector;
    unwrappedSelector = selectorTuple.unwrappedSelector;
    selectStrategy = strategy;
}
```

创建 NioEventLoop 主要做了以下几件事：

1. **创建任务队列，以后调用 NioEventLoop.execute(Runnable task) 的时候均是把 task 放入该任务队列**
2. **创建一个 selector，之后要把 channel 注册到该 selector 上**

## 设置 ServerBootstrap

### 设置 Reactor 线程模型

```
// ServerBootstrap.group(group, group)
public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
    // 这里的 parentGroup 就是我们创建的 NioEventLoopGroup（bossGroup）
    super.group(parentGroup);
    if (childGroup == null) {
        throw new NullPointerException("childGroup");
    }
    if (this.childGroup != null) {
        throw new IllegalStateException("childGroup set already");
    }
    // 这里的 childGroup 就是我们创建的 NioEventLoopGroup（workerGroup）
    this.childGroup = childGroup;
    return this;
}
```

### 设置 IO 模型

```
// AbstractBootstrap.channel(channelClass)
public B channel(Class<? extends C> channelClass) {
    if (channelClass == null) {
        throw new NullPointerException("channelClass");
    }
    // channelClass 就是上面传进来的 NioServerSocketChannel.class
    // 创建一个持有 channelClass 的 channelFactory 并保存，后面用来反射创建 NioServerSocketChannel 对象
    return channelFactory(new ReflectiveChannelFactory<C>(channelClass));
}
```

### 设置 TCP 参数

```
// AbstractBootstrap.option(option, value)
public <T> B option(ChannelOption<T> option, T value) {
    if (option == null) {
        throw new NullPointerException("option");
    }
    if (value == null) {
        synchronized (options) {
            options.remove(option);
        }
    } else {
        synchronized (options) {
            options.put(option, value);
        }
    }
    return self();
}

// ServerBootstrap.childOption(option, value)
public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
    if (childOption == null) {
        throw new NullPointerException("childOption");
    }
    if (value == null) {
        synchronized (childOptions) {
            childOptions.remove(childOption);
        }
    } else {
        synchronized (childOptions) {
            childOptions.put(childOption, value);
        }
    }
    return this;
}
```

### 设置业务处理 handler

```
// AbstractBootstrap.handler(handler)
public B handler(ChannelHandler handler) {
    if (handler == null) {
        throw new NullPointerException("handler");
    }
    this.handler = handler;
    return self();
}

// ServerBootstrap.childHandler(childHandler)
public ServerBootstrap childHandler(ChannelHandler childHandler) {
    if (childHandler == null) {
        throw new NullPointerException("childHandler");
    }
    this.childHandler = childHandler;
    return this;
}
```

## 绑定端口

首先根据端口构造 SocketAddress 对象，然后调用 doBind(localAddress) 方法：

```
// AbstractBootstrap.doBind(localAddress)
private ChannelFuture doBind(final SocketAddress localAddress) {
    // 初始化一个 channel，然后注册到 selector 上
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }
    // 如果注册完成的话，接下来就要绑定端口了
    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successful.
        ChannelPromise promise = channel.newPromise();
        // 绑定端口的核心方法
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } else {
        // Registration future is almost always fulfilled already, but just in case it's not.
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        // 给 regFuture 添加个监听器，一旦注册完成则执行该监听方法，注册成功的话依然调用 doBind0 绑定端口
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                    // IllegalStateException once we try to access the EventLoop of the Channel.
                    promise.setFailure(cause);
                } else {
                    // Registration was successful, so set the correct executor to use.
                    // See https://github.com/netty/netty/issues/2586
                    promise.registered();

                    doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }
}
```

### 初始化 channel（NioServerSocketChannel）

```
// AbstractBootstrap.initAndRegister()
final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
        // 首先通过 channelFactory 用反射创建一个 NioServerSocketChannel 对象（封装了 jdk 的 ServerSocketChannel 和 SelectionKey.OP_ACCEPT）
        channel = channelFactory.newChannel();
        // 初始化该 channel 对象
        init(channel);
    } catch (Throwable t) {
        if (channel != null) {
            // channel can be null if newChannel crashed (eg SocketException("too many open files"))
            channel.unsafe().closeForcibly();
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
        return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
    }
    // 初始化后将 channel 注册到 selector 上，核心方法
    // 这里的 group() 方法返回的就是 bossGroup，所以它的 register 方法肯定是从数组中选出一个 NioEventLoop，然后将 channel 注册到它对应的 selector 上
    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null) {
        if (channel.isRegistered()) {
            channel.close();
        } else {
            channel.unsafe().closeForcibly();
        }
    }
    return regFuture;
}

// ServerBootstrap.init(channel)
void init(Channel channel) throws Exception {
    final Map<ChannelOption<?>, Object> options = options0();
    synchronized (options) {
        setChannelOptions(channel, options, logger);
    }

    final Map<AttributeKey<?>, Object> attrs = attrs0();
    synchronized (attrs) {
        for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
            @SuppressWarnings("unchecked")
            AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
            channel.attr(key).set(e.getValue());
        }
    }

    // 获取 channel 对应的 pipeline，也就是个双向链表
    ChannelPipeline p = channel.pipeline();

    final EventLoopGroup currentChildGroup = childGroup;
    final ChannelHandler currentChildHandler = childHandler;
    final Entry<ChannelOption<?>, Object>[] currentChildOptions;
    final Entry<AttributeKey<?>, Object>[] currentChildAttrs;
    synchronized (childOptions) {
        currentChildOptions = childOptions.entrySet().toArray(newOptionArray(0));
    }
    synchronized (childAttrs) {
        currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));
    }

    // 在链表尾部（实际上是 tail 节点前面）添加一个 ChannelHandlerContext 节点；
    // 该节点依据当前这个 pipeline 和 handle（ChannelInitializer）创建的。其中 pipeline 又关联着 channel，方便后面可以获取到 channel；
    p.addLast(new ChannelInitializer<Channel>() {
        // 目前来看，正常情况下该方法只会在其 handlerAdded 方法中调用，并且调用后立即将自身从 pipeline 中移除
        @Override
        public void initChannel(final Channel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            // 这个 handler() 就是设置业务处理 handler 时设置的，注意区分 handler 和 childHandler
            ChannelHandler handler = config.handler();
            // 如果通过 serverBootstrap.handler(new MyHandler()) 设置了 handler，
            // 同上面一样，构造一个 ChannelHandlerContext 节点添加到 pipeline 的 tail 节点前面
            if (handler != null) {
                pipeline.addLast(handler);
            }
            // eventLoop() 方法返回一个 NioEventLoop，这个会在 channel 注册到 selector 的时候设置到 channel 中
            // 这个 execute 方法也是核心，这里往 NioEventLoop 里扔一个 task
            ch.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    // 同上面一样，构造一个 ChannelHandlerContext 节点添加到 pipeline 的 tail 节点前面
                    pipeline.addLast(new ServerBootstrapAcceptor(
                            ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                }
            });
        }
    });
}
```

### 注册 channel 到 selector

回到 initAndRegister() 方法中，在创建 channel 并调用 init(channel) 方法后，开始把 channel 注册到 selector 上，通过 EventLoopGroup 来注册；
首先我们看下 EventLoopGroup 接口中有哪些方法：

```
public interface EventLoopGroup extends EventExecutorGroup {
    /**
     * Return the next {@link EventLoop} to use
     */
    @Override
    EventLoop next();

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The returned {@link ChannelFuture}
     * will get notified once the registration was complete.
     */
    ChannelFuture register(Channel channel);

    /**
     * Register a {@link Channel} with this {@link EventLoop} using a {@link ChannelFuture}. The passed
     * {@link ChannelFuture} will get notified once the registration was complete and also will get returned.
     */
    ChannelFuture register(ChannelPromise promise);
}
```

NioEventLoopGroup 的 register(channel) 方法继承自父类 MultithreadEventLoopGroup

```
// NioEventLoopGroup.register(channel)
@Override
public ChannelFuture register(Channel channel) {
    // 所以这里变成了调用 NioEventLoop 的 register(channel) 方法
    return next().register(channel);
}

@Override
public EventLoop next() {
    // 父类则是通过 chooser.next() 来选取一个 NioEventLoop
    return (EventLoop) super.next();
}
```

NioEventLoop 的 register(channel) 方法继承自父类 SingleThreadEventLoop

```
// NioEventLoop.register(channel)
@Override
public ChannelFuture register(Channel channel) {
    // 这里根据 channel 和 NioEventLoop 自身创建了一个 DefaultChannelPromise
    return register(new DefaultChannelPromise(channel, this));
}

@Override
public ChannelFuture register(final ChannelPromise promise) {
    ObjectUtil.checkNotNull(promise, "promise");
    // unsafe() 方法返回 channel 对应的 AbstractUnsafe 对象，转为调用 AbstractUnsafe 的 register(eventLoop, promise) 方法
    promise.channel().unsafe().register(this, promise);
    return promise;
}
```

AbstractUnsafe 的 register(eventLoop, promise) 相关方法主要代码如下：

```
// AbstractUnsafe.register(eventLoop, promise)
@Override
public final void register(EventLoop eventLoop, final ChannelPromise promise) {
    // 给 channel 的 eventLoop 属性设值了
    AbstractChannel.this.eventLoop = eventLoop;
    // 如果当前是 eventLoop 线程调用的话则直接调用 register0(promise)，否则封装成一个 task 丢给 eventLoop 去 execute
    // 正常来说，这里 eventLoop 线程还没有启动，第一次调用 eventLoop.execute(task) 的时候才会启动用 execute 启动 eventLoop 线程
    if (eventLoop.inEventLoop()) {
        register0(promise);
    } else {
        try {
            // 目前看来，这里是启动 eventLoop 线程的地方，下篇文章会对 eventLoop 线程的启动及执行做介绍，先不做深究
            // 把注册任务封装成 task 丢给 eventLoop 线程去执行
            eventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(promise);
                }
            });
        } catch (Throwable t) {
            logger.warn(
                    "Force-closing a channel whose registration task was not accepted by an event loop: {}",
                    AbstractChannel.this, t);
            closeForcibly();
            closeFuture.setClosed();
            safeSetFailure(promise, t);
        }
    }
}

// AbstractUnsafe.register0(promise)
private void register0(ChannelPromise promise) {
    try {
        // check if the channel is still open as it could be closed in the mean time when the register
        // call was outside of the eventLoop
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
        boolean firstRegistration = neverRegistered;
        // 最终注册的方法，在 AbstractNioChannel 中实现，其实也就是调用 jdk 的那个 ServerSocketChannel 的 register 方法
        doRegister();
        neverRegistered = false;
        registered = true;

        // 注册已经算是成功了，是时候进行我们设置的 handlerAdded 方法了；
        // 然后此时会找到初始化 channel 的时候往 pipeline 中添加的那个 ChannelInitializer 的 handlerAdded 方法；
        // 接着会调用它重写的的 initChannel(ch) 方法，具体内容在上面初始化 channel 的时候说过了
        pipeline.invokeHandlerAddedIfNeeded();
        // 设置 promise 值
        safeSetSuccess(promise);
        // 开始在 pipeline 上一次调用 fireChannelRegistered
        pipeline.fireChannelRegistered();
        // Only fire a channelActive if the channel has never been registered. This prevents firing
        // multiple channel actives if the channel is deregistered and re-registered.
        // 这里尚未绑定上端口，所以不会执行
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                // This channel was registered before and autoRead() is set. This means we need to begin read
                // again so that we process inbound data.
                //
                // See https://github.com/netty/netty/issues/4805
                beginRead();
            }
        }
    } catch (Throwable t) {
        // Close the channel directly to avoid FD leak.
        closeForcibly();
        closeFuture.setClosed();
        safeSetFailure(promise, t);
    }
}
```

### ChannelRegistered 事件在 pipeline 上的传播

然后我们来看注册成功后，fireChannelRegistered 事件在 pipeline 上怎么传播：

```
// DefaultChannelPipeline.fireChannelRegistered()
@Override
public final ChannelPipeline fireChannelRegistered() {
    // 从 pipeline 的 head 节点开始传播
    AbstractChannelHandlerContext.invokeChannelRegistered(head);
    return this;
}

// AbstractChannelHandlerContext.invokeChannelRegistered(next)
static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
    // 这个 executor 实际上就是 NioEventLoop
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        // HeadContext 的方法继承自 AbstractChannelHandlerContext
        next.invokeChannelRegistered();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRegistered();
            }
        });
    }
}

// AbstractChannelHandlerContext.invokeChannelRegistered() 
private void invokeChannelRegistered() {
    if (invokeHandler()) {
        try {
            // 如果节点是 head 或者 tail，handler() 方法则返回 this
            // 如果节点是 DefaultChannelHandlerContext，handler() 方法则返回创建时传入的 handler
            ((ChannelInboundHandler) handler()).channelRegistered(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        fireChannelRegistered();
    }
}
```

如果我们添加了 handlerA、handlerB，那么内部会将其封装成两个 DefaultChannelHandlerContext 并添加到 pipeline 中，此时链表结构为：
`head ⇄ handlerA ⇄ handlerB ⇄ tail`，ChannelRegistered 事件从 head 节点开始往后传播，在经历了自定义的 ChannelInboundHandler 后，最终会到达 tail 节点，tail 节点对该事件不作处理。

```
// HeadContext.channelRegistered(ctx)
@Override
public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    // 这个方法我们上面看过了
    invokeHandlerAddedIfNeeded();
    // 该方法 AbstractChannelHandlerContext 的实现如下
    ctx.fireChannelRegistered();
}

// AbstractChannelHandlerContext.fireChannelRegistered()
@Override
public ChannelHandlerContext fireChannelRegistered() {
    invokeChannelRegistered(findContextInbound());
    return this;
}

// AbstractChannelHandlerContext.findContextInbound()
// 查找下一个 inbound 节点
private AbstractChannelHandlerContext findContextInbound() {
    // AbstractChannelHandlerContext 拥有两个属性 inbound 和 outbound
    // inbound 代表 handler instanceof ChannelInboundHandler
    // outbound 代表 handler instanceof ChannelOutboundHandler
    AbstractChannelHandlerContext ctx = this;
    do {
        ctx = ctx.next;
    } while (!ctx.inbound);
    return ctx;
}

// AbstractChannelHandlerContext.invokeChannelRegistered(next)
// 正常情况下，在经历我们自定义的 handlerA、handlerB 后，总是到达 tail 节点，因为 TailContext 实现了 ChannelInboundHandler
static void invokeChannelRegistered(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelRegistered();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRegistered();
            }
        });
    }
}
```

### 绑定端口

回到 AbstractBootstrap.doBind(localAddress) 方法中，可以看到在初始化 channel 并将其注册到 NioEventLoop 的 selector 上后，才开始调用 doBind0 执行真正的绑定动作。

```
// AbstractBootstrap.doBind0(future, channel, localAddress, promise)
private static void doBind0(
        final ChannelFuture regFuture, final Channel channel,
        final SocketAddress localAddress, final ChannelPromise promise) {

    // 把绑定端口封装成一个 task 丢给 NioEventLoop 去执行
    channel.eventLoop().execute(new Runnable() {
        @Override
        public void run() {
            if (regFuture.isSuccess()) {
                // 调用 channel bind 
                channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                promise.setFailure(regFuture.cause());
            }
        }
    });
}

// AbstractChannel.bind(localAddress, promise)
@Override
public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
    // 交给 pipeline 去处理 bind 操作
    return pipeline.bind(localAddress, promise);
}

// DefaultChannelPipeline.bind(localAddress, promise)
@Override
public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
    // 交给 tail 节点去处理
    return tail.bind(localAddress, promise);
}

// AbstractChannelHandlerContext.bind(localAddress, promise)
@Override
public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
    if (localAddress == null) {
        throw new NullPointerException("localAddress");
    }
    if (isNotValidPromise(promise, false)) {
        // cancelled
        return promise;
    }
    // 从 tail 节点往前查找 ChannelOutboundHandler，直到 head 节点
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeBind(localAddress, promise);
    } else {
        safeExecute(executor, new Runnable() {
            @Override
            public void run() {
                next.invokeBind(localAddress, promise);
            }
        }, promise, null);
    }
    return promise;
}

// AbstractChannelHandlerContext.invokeBind(localAddress, promise)
private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
    if (invokeHandler()) {
        try {
            // 最终到达 head 节点，handler() 返回 this，即调用 HeadContext.bind(ctx, localAddress, promise)
            ((ChannelOutboundHandler) handler()).bind(this, localAddress, promise);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }
    } else {
        bind(localAddress, promise);
    }
}

// HeadContext.bind(ctx, localAddress, promise)
@Override
public void bind(
        ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
        throws Exception {
    // 调用 unsafe 执行 bind 操作
    unsafe.bind(localAddress, promise);
}

// AbstractUnsafe.bind(localAddress, promise) 主要代码如下：
@Override
public final void bind(final SocketAddress localAddress, final ChannelPromise promise) {
    assertEventLoop();
    boolean wasActive = isActive();
    try {
        // 最终还是调用 NioServerSocketChannel.doBind(localAddress) 方法
        doBind(localAddress);
    } catch (Throwable t) {
        safeSetFailure(promise, t);
        closeIfClosed();
        return;
    }

    if (!wasActive && isActive()) {
        // 把 pipeline.fireChannelActive() 封装成 task，丢给 NioEventLoop 线程去执行
        invokeLater(new Runnable() {
            @Override
            public void run() {
                pipeline.fireChannelActive();
            }
        });
    }

    safeSetSuccess(promise);
}

// NioServerSocketChannel.doBind(localAddress)
@Override
protected void doBind(SocketAddress localAddress) throws Exception {
    // 依旧是通过 jdk 的 ServerSocketChannel 去 bind 的
    if (PlatformDependent.javaVersion() >= 7) {
        javaChannel().bind(localAddress, config.getBacklog());
    } else {
        javaChannel().socket().bind(localAddress, config.getBacklog());
    }
}
```

### ChannelActive 事件在 pipeline 上的传播

上面看到在调用 jdk 的 ServerSocketChannel 执行 bind 操作后，将 ChannelActive 事件在 pipeline 上的传播封装成 task 丢给 NioEventLoop 线程去执行了，那么我们接着看 ChannelActive 事件在 pipeline 上的传播是否像 ChannelRegistered 的传播一样呢。

```
// DefaultChannelPipeline.fireChannelActive()
@Override
public final ChannelPipeline fireChannelActive() {
    // 依旧是从 head 节点开始传播
    AbstractChannelHandlerContext.invokeChannelActive(head);
    return this;
}

// AbstractChannelHandlerContext.invokeChannelActive(next)
static void invokeChannelActive(final AbstractChannelHandlerContext next) {
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelActive();
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelActive();
            }
        });
    }
}

// AbstractChannelHandlerContext.invokeChannelActive()
private void invokeChannelActive() {
    if (invokeHandler()) {
        try {
            // head 节点的 handler() 返回的是 this
            ((ChannelInboundHandler) handler()).channelActive(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        fireChannelActive();
    }
}

// HeadContext.channelActive(ctx)
@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
    // 这里是将事件传递到下一个 ChannelInboundHandler，可以跟踪下看看是不是跟之前 ChannelRegistered 的传播有什么不一样
    // 多了个之前添加的 ServerBootstrapAcceptor，虽然它并没有重写 channelActive()，所以这里只是通过它简单的往下传递而已
    ctx.fireChannelActive();

    // 下面来看看这行代码背后做了哪些工作
    readIfIsAutoRead();
}

// HeadContext.readIfIsAutoRead()
private void readIfIsAutoRead() {
    // isAutoRead() 方法默认就返回 true
    if (channel.config().isAutoRead()) {
        channel.read();
    }
}

// AbstractChannel.read()
@Override
public Channel read() {
    pipeline.read();
    return this;
}

// DefaultChannelPipeline.read()
@Override
public final ChannelPipeline read() {
    // tail 节点的 read() 继承自 AbstractChannelHandlerContext
    tail.read();
    return this;
}

// AbstractChannelHandlerContext.read()
@Override
public ChannelHandlerContext read() {
    // 从 tail 节点往前查找 ChannelOutboundHandler，直到 head 节点
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        // 我们是在 NioEventLoop 线程中执行的，所以直接调用 invokeRead() 方法
        next.invokeRead();
    } else {
        Runnable task = next.invokeReadTask;
        if (task == null) {
            next.invokeReadTask = task = new Runnable() {
                @Override
                public void run() {
                    next.invokeRead();
                }
            };
        }
        executor.execute(task);
    }

    return this;
}

// AbstractChannelHandlerContext.invokeRead()
private void invokeRead() {
    if (invokeHandler()) {
        try {
            // head 节点的 handler() 方法返回 this，所以最终调用 HeadContext.read(ctx)
            ((ChannelOutboundHandler) handler()).read(this);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        read();
    }
}

// HeadContext.read(ctx)
@Override
public void read(ChannelHandlerContext ctx) {
    unsafe.beginRead();
}

// AbstractUnsafe.beginRead()
@Override
public final void beginRead() {
    assertEventLoop();

    if (!isActive()) {
        return;
    }

    try {
        // 最终调用 AbstractNioChannel.doBeginRead()
        doBeginRead();
    } catch (final Exception e) {
        invokeLater(new Runnable() {
            @Override
            public void run() {
                pipeline.fireExceptionCaught(e);
            }
        });
        close(voidPromise());
    }
}

// AbstractNioChannel.doBeginRead()
@Override
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    // 我们之前把 channel 注册到 selector 的时候，注册的 ops 为 0，
    // 这里我们通过 selectionKey.interestOps 方法将其修改为 SelectionKey.OP_ACCEPT，以便用来监听客户端的连接
    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## 服务端启动总结

1. 首先创建两个 NioEventLoopGroup，一个 bossGroup 用于接收客户端的连接请求；一个 workerGroup 用于处理连接的 IO 读写操作；每个 NioEventLoopGroup 持有一个 EventExecutor[] 数组，用来保存 NioEventLoop
2. 创建 NioEventLoop 对象填充 NioEventLoopGroup 的 EventExecutor[] 数组；每个 NioEventLoop 都有自己的 executor、taskQueue、selector 等属性
3. 创建辅助类 ServerBootstrap，并设置 Reactor 线程模型、IO 模型、TCP 参数以及业务处理 handler 等
4. 调用 ServerBootstrap 的 bind 方法进行端口绑定
   1. 反射创建一个 NioServerSocketChannel 对象，并进行初始化（设置一些属性后，在 pipeline 后添加一个 ChannelInitializer，重写的 initChannel 方法在 handlerAdded 方法中调用）
   2. 从 NioEventLoopGroup 选出一个 NioEventLoop，将 channel 注册到它的 selector 上，最终调用的是 jdk 的 ServerSocketChannel 的注册方法，但是 ops 为 0
   3. 注册成功后，进行 handlerAdded、ChannelRegistered 等事件的触发
   4. 然后开始真正的绑定端口，最终还是调用 jdk 的 ServerSocketChannel 的 bind 方法
   5. 进行 ChannelActive 事件的传播，其中会修改 channel 注册到 selector 上返回的 selectionKey 的 ops 为 SelectionKey.OP_ACCEPT，以便用来监听客户端的连接

- **一句话总结：初始化 channel 后，注册到 selector 上，触发 handlerAdded、ChannelRegistered 事件后绑定端口，接着触发 ChannelActive 事件，其中又会修改 selectionKey 的 ops 以便监听客户端的连接**