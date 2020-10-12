# Netty 客户端启动源码

 Posted on 2019-09-27 | In [Netty](https://jimmy2angel.github.io/categories/Netty/)

Netty 客户端启动代码基本如下所示，客户端启动时 Netty 内部为我们做了什么？

- 客户端的线程模型是如何设置的？
- 客户端的 channel 是如何创建并初始化的？
- 具体又是怎么去 connect 服务端的？

带着以上几个问题，下面我们来一探究竟。

```
EventLoopGroup group = new NioEventLoopGroup();
try {
    Bootstrap b = new Bootstrap();
    //配置客户端 NIO 线程组
    b.group(group)
            // 设置 IO 模型
            .channel(NioSocketChannel.class)
            // 设置 TCP 参数
            .option(ChannelOption.TCP_NODELAY, true)
            // 设置业务处理 handler
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new MyClientHandler());
                }
            });
    //发起异步连接请求
    ChannelFuture future = b.connect(host, port).sync();
    future.channel().closeFuture().sync();
} finally {
    group.shutdownGracefully();
}
```

## 核心概念

- Bootstrap

  > 作为抽象辅助类 AbstractBootstrap 的子类，Bootstrap 主要提供给客户端使用；
  >
  > 基于此进行一些 group、channel、option 和 handler 的配置

- NioSocketChannel

  > 封装了 jdk 的 SocketChannel，且存在一个属性 readInterestOp 记录对什么事件感兴趣，初始化时为 `SelectionKey.OP_READ`；
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
  > 客户端只存在一个 NioEventLoopGroup；

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

## 设置 Bootstrap

这一步对 Bootstrap 进行配置，无论是 group、channel 还是 option、handler，均是继承的 AbstractBootstrap 的属性；
对其的配置目前也仅仅是在 AbstractBootstrap 中配置属性，尚未做过多的事情；

### 设置 NIO 线程组

```
// AbstractBootstrap.group(group)
public B group(EventLoopGroup group) {
    if (group == null) {
        throw new NullPointerException("group");
    }
    if (this.group != null) {
        throw new IllegalStateException("group set already");
    }
    this.group = group;
    return self();
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
```

## 发起连接请求

封装好 SocketAddress 后调用 doResolveAndConnect 方法，代码如下：

```
// Bootstrap.doResolveAndConnect(remoteAddress, localAddress)
private ChannelFuture doResolveAndConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
    // 同服务端一样，先初始化一个 channel 然后注册到 selector 上
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();

    // 因为上面注册是 封装 task 丢给 eventLoop 去执行的，也就是说它是异步的；
    // 这里根据 future 判断是否注册完成了
    if (regFuture.isDone()) {
        if (!regFuture.isSuccess()) {
            return regFuture;
        }
        // 发起连接请求
        return doResolveAndConnect0(channel, remoteAddress, localAddress, channel.newPromise());
    } else {
        // Registration future is almost always fulfilled already, but just in case it's not.
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // Directly obtain the cause and do a null check so we only need one volatile read in case of a
                // failure.
                Throwable cause = future.cause();
                if (cause != null) {
                    // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                    // IllegalStateException once we try to access the EventLoop of the Channel.
                    promise.setFailure(cause);
                } else {
                    // Registration was successful, so set the correct executor to use.
                    // See https://github.com/netty/netty/issues/2586
                    promise.registered();
                    // 发起连接请求
                    doResolveAndConnect0(channel, remoteAddress, localAddress, promise);
                }
            }
        });
        return promise;
    }
}
```

### 初始化 channel（NioSocketChannel）

```
// AbstractBootstrap.initAndRegister()
final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
        // 首先通过 channelFactory 用反射创建一个 NioSocketChannel 对象（封装了 jdk 的 SocketChannel 和 SelectionKey.OP_READ）
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
    // 这里的 group() 方法返回 NioEventLoopGroup，它的 register 方法从数组中选出一个 NioEventLoop，然后将 channel 注册到它对应的 selector 上
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

// Bootstrap.init(channel)
@Override
@SuppressWarnings("unchecked")
void init(Channel channel) throws Exception {
    ChannelPipeline p = channel.pipeline();
    // 将设置的业务 handler（demo 中是一个 ChannelInitializer）封装成 ChannelHandlerContext，添加到 pipeline 的 tail 节点前面
    p.addLast(config.handler());
    // 设置 option
    final Map<ChannelOption<?>, Object> options = options0();
    synchronized (options) {
        setChannelOptions(channel, options, logger);
    }
    // 设置 attr
    final Map<AttributeKey<?>, Object> attrs = attrs0();
    synchronized (attrs) {
        for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
            channel.attr((AttributeKey<Object>) e.getKey()).set(e.getValue());
        }
    }
}
```

### 注册 channel 到 selector

回到 initAndRegister() 方法中，在创建 channel 并调用 init(channel) 方法后，开始把 channel 注册到 selector 上，通过 EventLoopGroup 来注册；
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
        // 最终注册的方法，在 AbstractNioChannel 中实现，其实也就是调用 jdk 的那个 SocketChannel 的 register 方法
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
        // 这里还没有连接上服务端，所以不会执行
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

### 触发 handlerAdded、ChannelRegistered 事件

在将 channel 注册到 selector 上后，进行 handlerAdded 事件的传播，如果客户端启动代码配置了 .handler(ChannelInitializer）的话，ChannelInitializer 的 handlerAdded 方法会调用重写 initChannel(socketChannel) 方法，该方法又会将我们实际的客户端处理 handler 按序添加到 pipeline 中。然后再进行 ChannelRegistered 在 pipeline 中的传播。

> 再次声明下，pipeline 是双向链表，节点是 ChannelHandlerContext，持有 handler 属性及 inbound、outbound 标识；
>
> head 节点既是 inbound 节点也是 outbound 节点，tail 节点只是 inbound 节点；
>
> 像 ChannelRegistered 这种 inbound 事件，会从 pipeline 的 head 节点开始处理，然后按照链表顺序查找下一个 inbound 节点，依次处理直到 tail 节点

### 发起连接请求

在将 channel 注册到 selector 上后，对 remoteAddress 进行解析，解析完成后开始发起连接请求；
发起请求这个操作，也是封装成 task 丢给 eventLoop 线程去执行

```
// Bootstrap.doResolveAndConnect0(channel, remoteAddress, localAddress, promise)
private ChannelFuture doResolveAndConnect0(final Channel channel, SocketAddress remoteAddress,
                                           final SocketAddress localAddress, final ChannelPromise promise) {
    try {
        final EventLoop eventLoop = channel.eventLoop();
        final AddressResolver<SocketAddress> resolver = this.resolver.getResolver(eventLoop);

        if (!resolver.isSupported(remoteAddress) || resolver.isResolved(remoteAddress)) {
            // Resolver has no idea about what to do with the specified remote address or it's resolved already.
            doConnect(remoteAddress, localAddress, promise);
            return promise;
        }

        final Future<SocketAddress> resolveFuture = resolver.resolve(remoteAddress);

        if (resolveFuture.isDone()) {
            final Throwable resolveFailureCause = resolveFuture.cause();

            if (resolveFailureCause != null) {
                // Failed to resolve immediately
                channel.close();
                promise.setFailure(resolveFailureCause);
            } else {
                // Succeeded to resolve immediately; cached? (or did a blocking lookup)
                // 在成功解析地址后，进行连接
                doConnect(resolveFuture.getNow(), localAddress, promise);
            }
            return promise;
        }

        // Wait until the name resolution is finished.
        resolveFuture.addListener(new FutureListener<SocketAddress>() {
            @Override
            public void operationComplete(Future<SocketAddress> future) throws Exception {
                if (future.cause() != null) {
                    channel.close();
                    promise.setFailure(future.cause());
                } else {
                    doConnect(future.getNow(), localAddress, promise);
                }
            }
        });
    } catch (Throwable cause) {
        promise.tryFailure(cause);
    }
    return promise;
}

// Bootstrap.doConnect(remoteAddress, localAddress, connectPromise)
private static void doConnect(
        final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise connectPromise) {

    // This method is invoked before channelRegistered() is triggered.  Give user handlers a chance to set up
    // the pipeline in its channelRegistered() implementation.
    final Channel channel = connectPromise.channel();
    // 将发起连接请求这个操作封装成 task 丢给 eventLoop 线程去执行
    channel.eventLoop().execute(new Runnable() {
        @Override
        public void run() {
            if (localAddress == null) {
                // 当 eventLoop 线程执行到这个 task 的时候，通过 channel 进行发起连接
                channel.connect(remoteAddress, connectPromise);
            } else {
                channel.connect(remoteAddress, localAddress, connectPromise);
            }
            connectPromise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    });
}

// AbstractChannel.connect(remoteAddress, promise)
@Override
public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
    // 交给 pipeline 去执行连接操作
    return pipeline.connect(remoteAddress, promise);
}

// DefaultChannelPipeline.connect(remoteAddress, promise)
@Override
public final ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
    // 交给 tail 节点去执行连接操作，tail 节点的 connect 方法继承自 AbstractChannelHandlerContext
    return tail.connect(remoteAddress, promise);
}

// AbstractChannelHandlerContext.connect(remoteAddress, promise)
@Override
public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
    return connect(remoteAddress, null, promise);
}

// AbstractChannelHandlerContext.connect(remoteAddress, localAddress, promise)
@Override
public ChannelFuture connect(
        final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {

    if (remoteAddress == null) {
        throw new NullPointerException("remoteAddress");
    }
    if (isNotValidPromise(promise, false)) {
        // cancelled
        return promise;
    }
    // 从 tail 节点开始查找下一个 outbound 节点，直到 head 节点
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        // 因为当前发起连接请求的任务是由 eventLoop 线程执行的，所以会直接走这里
        next.invokeConnect(remoteAddress, localAddress, promise);
    } else {
        safeExecute(executor, new Runnable() {
            @Override
            public void run() {
                next.invokeConnect(remoteAddress, localAddress, promise);
            }
        }, promise, null);
    }
    return promise;
}

// AbstractChannelHandlerContext.invokeConnect(remoteAddress, localAddress, promise)
private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
    if (invokeHandler()) {
        try {
            // 调用节点封装的 handler 的 connect 方法，最终调用的是 head 节点的 connect 方法
            ((ChannelOutboundHandler) handler()).connect(this, remoteAddress, localAddress, promise);
        } catch (Throwable t) {
            notifyOutboundHandlerException(t, promise);
        }
    } else {
        connect(remoteAddress, localAddress, promise);
    }
}

// HeadContext.connect(ctx, remoteAddress, localAddress, promise)
@Override
public void connect(
        ChannelHandlerContext ctx,
        SocketAddress remoteAddress, SocketAddress localAddress,
        ChannelPromise promise) throws Exception {
    // 交给 unsafe 去执行
    unsafe.connect(remoteAddress, localAddress, promise);
}

// AbstractNioUnsafe.connect(remoteAddress, localAddress, promise)
@Override
public final void connect(
        final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
    if (!promise.setUncancellable() || !ensureOpen(promise)) {
        return;
    }

    try {
        if (connectPromise != null) {
            // Already a connect in process.
            throw new ConnectionPendingException();
        }

        boolean wasActive = isActive();
        // 发起连接请求
        if (doConnect(remoteAddress, localAddress)) {
            fulfillConnectPromise(promise, wasActive);
        } else {
            connectPromise = promise;
            requestedRemoteAddress = remoteAddress;

            // Schedule connect timeout.
            int connectTimeoutMillis = config().getConnectTimeoutMillis();
            if (connectTimeoutMillis > 0) {
                connectTimeoutFuture = eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                        ConnectTimeoutException cause =
                                new ConnectTimeoutException("connection timed out: " + remoteAddress);
                        if (connectPromise != null && connectPromise.tryFailure(cause)) {
                            close(voidPromise());
                        }
                    }
                }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
            }

            promise.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isCancelled()) {
                        if (connectTimeoutFuture != null) {
                            connectTimeoutFuture.cancel(false);
                        }
                        connectPromise = null;
                        close(voidPromise());
                    }
                }
            });
        }
    } catch (Throwable t) {
        promise.tryFailure(annotateConnectException(t, remoteAddress));
        closeIfClosed();
    }
}

// NioSocketChannel.doConnect(remoteAddress, localAddress)
@Override
protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
    if (localAddress != null) {
        doBind0(localAddress);
    }

    boolean success = false;
    try {
        // 最终调用的还是 jdk 的 SocketChannel 的 connect 方法
        // 目前看来，这里始终返回的是 false，所以设置 ops 为 SelectionKey.OP_CONNECT
        boolean connected = SocketUtils.connect(javaChannel(), remoteAddress);
        if (!connected) {
            selectionKey().interestOps(SelectionKey.OP_CONNECT);
        }
        success = true;
        return connected;
    } finally {
        if (!success) {
            doClose();
        }
    }
}
```

跟踪代码到 NioSocketChannel.doConnect(remoteAddress, localAddress) 方法为止，请求算是发出去了，但是连接算建立成功了么？当然没有，一个连接的建立当然还需要服务端的接收。

当服务端接收请求即建立连接后，客户端如何知晓呢？上面我们在 selector 上注册了 SelectionKey.OP_CONNECT 事件，selector 轮询到连接事件后，
最终会调用到 AbstractNioUnsafe 的 finishConnect() 方法。

```
// NioSocketChannel.finishConnect()
@Override
public final void finishConnect() {
    // Note this method is invoked by the event loop only if the connection attempt was
    // neither cancelled nor timed out.

    assert eventLoop().inEventLoop();

    try {
        boolean wasActive = isActive();
        // NioSocketChannel 仅仅判断下 SocketChannel 是否连接上了，没有的话则直接抛个 Error
        doFinishConnect();
        // 填充 connectPromise，就是往 connectPromise 里写个 success
        fulfillConnectPromise(connectPromise, wasActive);
    } catch (Throwable t) {
        fulfillConnectPromise(connectPromise, annotateConnectException(t, requestedRemoteAddress));
    } finally {
        // Check for null as the connectTimeoutFuture is only created if a connectTimeoutMillis > 0 is used
        // See https://github.com/netty/netty/issues/1770
        if (connectTimeoutFuture != null) {
            connectTimeoutFuture.cancel(false);
        }
        connectPromise = null;
    }
}

// NioSocketChannel.fulfillConnectPromise(promise, wasActive)
private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
    if (promise == null) {
        // Closed via cancellation and the promise has been notified already.
        return;
    }

    // Get the state as trySuccess() may trigger an ChannelFutureListener that will close the Channel.
    // We still need to ensure we call fireChannelActive() in this case.
    boolean active = isActive();

    // trySuccess() will return false if a user cancelled the connection attempt.
    boolean promiseSet = promise.trySuccess();

    // Regardless if the connection attempt was cancelled, channelActive() event should be triggered,
    // because what happened is what happened.
    if (!wasActive && active) {
        // 正常情况下连接建立上了的话，开始通过 pipeline 传播 ChannelActive 事件
        pipeline().fireChannelActive();
    }

    // If a user cancelled the connection attempt, close the channel, which is followed by channelInactive().
    if (!promiseSet) {
        close(voidPromise());
    }
}
```

### ChannelActive 事件在 pipeline 上的传播

```
// DefaultChannelPipeline.fireChannelActive()
@Override
public final ChannelPipeline fireChannelActive() {
    // 从 head 节点开始传播
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
    // 这里是将事件传递到下一个 ChannelInboundHandler
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
    // 从 tail 节点往前查找 ChannelOutboundHandler
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

    // 这里我们通过 selectionKey.interestOps 方法将其修改为 SelectionKey.OP_READ
    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## 客户端启动总结

1. 创建 NioEventLoopGroup，然后创建 NioEventLoop 来填充 NioEventLoopGroup 的 EventExecutor[] 数组
2. 创建辅助类 Bootstrap，并设置 NIO 线程组、IO 模型、TCP 参数以及业务处理 handler 等
3. 调用 Bootstrap 的 connect 方法进行连接
   1. 反射创建一个 NioSocketChannel 对象，并进行初始化（封装 handler 添加到 pipeline 的 tail 节点前）
   2. 从 NioEventLoopGroup 选出一个 NioEventLoop，将 channel 注册到它的 selector 上，最终调用的是 jdk 的 ServerSocketChannel 的注册方法，但是 ops 为 0
   3. 注册成功后，进行 handlerAdded、ChannelRegistered 等事件的触发
   4. 然后开始真正的请求连接，最终还是调用 jdk 的 SocketChannel 的 connect 方法，然后设置 ops 为 SelectionKey.OP_CONNECT
   5. 在连接建立后，进行 ChannelActive 事件的传播，其中会修改 channel 注册到 selector 上返回的 selectionKey 的 ops 为 SelectionKey.OP_READ

- **一句话总结：初始化 channel 后注册到 selector 上，触发 handlerAdded、ChannelRegistered 事件后发送连接请求，在连接建立（服务端 accept）以后，接着触发 ChannelActive 事件，其中又会修改 selectionKey 的 ops 以便 selector 轮询到 服务端发来的消息**