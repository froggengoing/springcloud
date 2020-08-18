# Netty Reactor线程启动及执行

 Posted on 2019-09-27 | In [Netty](https://jimmy2angel.github.io/categories/Netty/)

前面通过源码分析了服务端启动时做了以下这些事：

- 初始化 channel 注册到 eventLoop 的 selector 上
- 触发 handlerAdded、ChannelRegistered 事件
- 进行端口绑定
- 触发 ChannelActive 事件，修改 selectionKey 的 interestOps 为 OP_ACCEPT

客户端启动时做了以下这些事：

- 初始化 channel 注册到 eventLoop 的 selector 上
- 触发 handlerAdded、ChannelRegistered 事件
- 发送连接请求，修改 selectionKey 的 interestOps 为 OP_CONNECT
- 连接建立后，触发 ChannelActive 事件，修改 selectionKey 的 interestOps 为 OP_ACCEPT

但是并没有去研究 Netty 的核心 Reactor 线程模型是什么样的，以及服务端如何接收一个客户端新连接的，现在我们通过跟踪源码来了解一下。

## Reactor 线程启动及执行

Netty 中的 Reactor 模型可以自由配置成 `单线程模型`、`多线程模型` 和 `主从多线程模型`，这里不做过多解释；

服务端通常启用两个线程组 NioEventLoopGroup，一个用来接收客户端的连接请求，也就是本文的重点，我们称之为 bossGroup；另一个用来处理连接建立后的IO读写操作，我们称之为 workerGroup；其中每个 group 又包括一组 NioEventLoop；

一开始 NioEventLoop 线程并没有启动，直到第一次往 NioEventLoop 上注册 channel 的时候，会把注册任务封装成一个 task 丢到 NioEventLoop 的 taskQueue 中等待 NioEventLoop 线程启动后执行，然后将 NioEventLoop 中的 run() 方法封装成 task 交给 execute 去执行，execute 会单独起个线程去执行 NioEventLoop 的 run() 方法，这个核心线程才是我们前面说的 NioEventLoop 线程。

NioEventLoop 的 execute() 方法继承自 SingleThreadEventExecutor：

```
// SingleThreadEventExecutor.execute(task)
@Override
public void execute(Runnable task) {
    if (task == null) {
        throw new NullPointerException("task");
    }
    // 这里就是看当前线程是不是 NioEventLoop 线程，第一次的时候肯定返回 false
    boolean inEventLoop = inEventLoop();
    // 添加 task 到 taskQueue 中去
    addTask(task);
    if (!inEventLoop) {
        startThread();
        if (isShutdown() && removeTask(task)) {
            reject();
        }
    }

    if (!addTaskWakesUp && wakesUpForTask(task)) {
        wakeup(inEventLoop);
    }
}

// SingleThreadEventExecutor.startThread()
private void startThread() {
	// 当前未启动才启动
    if (state == ST_NOT_STARTED) {
        if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
            try {
            	// CAS 修改状态成功才启动
                doStartThread();
            } catch (Throwable cause) {
                STATE_UPDATER.set(this, ST_NOT_STARTED);
                PlatformDependent.throwException(cause);
            }
        }
    }
}

// SingleThreadEventExecutor.doStartThread()
private void doStartThread() {
    assert thread == null;
    // 这里的 executor 一般是 ThreadPerTaskExecutor，其 execute 方法就是简单的 newThread(task).start();
    executor.execute(new Runnable() {
        @Override
        public void run() {
        	// 修改这个属性 thread 为当前线程，也就是 NioEventLoop 线程
        	// 上面 inEventLoop() 方法就是看执行 `inEventLoop()` 代码的线程是不是 NioEventLoop 线程
            thread = Thread.currentThread();
            if (interrupted) {
                thread.interrupt();
            }

            boolean success = false;
            updateLastExecutionTime();
            try {
            	// 核心方法，在 NioEventLoop 有实现
                SingleThreadEventExecutor.this.run();
                success = true;
            } catch (Throwable t) {
                logger.warn("Unexpected exception from an event executor: ", t);
            } finally {
                ...
            }
        }
    });
}
```



上面代码已经启动了 NioEventLoop 线程，并开始执行 NioEventLoop 的 run() 方法：

```
@Override
protected void run() {
    for (;;) {
        try {
        	// hasTasks 为 true 则返回调用 selectNow() 的返回值，否则返回 SelectStrategy.SELECT
            switch (selectStrategy.calculateStrategy(selectNowSupplier, hasTasks())) {
                case SelectStrategy.CONTINUE:
                    continue;
                case SelectStrategy.SELECT:
                	// select 操作
                    select(wakenUp.getAndSet(false));
                    if (wakenUp.get()) {
                        selector.wakeup();
                    }
                    // fall through
                default:
            }

            cancelledKeys = 0;
            needsToSelectAgain = false;
            // io 操作的比率默认 50，也就是和 task 执行耗时尽量各占一半
            final int ioRatio = this.ioRatio;
            if (ioRatio == 100) {
                try {
                    processSelectedKeys();
                } finally {
                    // Ensure we always run tasks.
                    runAllTasks();
                }
            } else {
                final long ioStartTime = System.nanoTime();
                try {
                	// 处理轮询到的 SelectedKeys
                    processSelectedKeys();
                } finally {
                    // Ensure we always run tasks.
                    final long ioTime = System.nanoTime() - ioStartTime;
                    // 在超时时间内执行 taskQueue 中的任务
                    // ioRatio 为 50 的话，runAllTasks 的超时时间即为刚刚的IO耗时 ioTime
                    runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                }
            }
        } catch (Throwable t) {
            handleLoopException(t);
        }
        // Always handle shutdown even if the loop processing threw an exception.
        try {
            if (isShuttingDown()) {
                closeAll();
                if (confirmShutdown()) {
                    return;
                }
            }
        } catch (Throwable t) {
            handleLoopException(t);
        }
    }
}
```

所以 NioEventLoop 线程主要做了以下几件事：

- **在 selector 上轮询事件**
- **处理轮询到的事件**
- **在超时时间内执行 taskQueue 里的 task**

### select 操作

```
// oldWakenUp 表示是否唤醒正在阻塞的 select 操作
private void select(boolean oldWakenUp) throws IOException {
    Selector selector = this.selector;
    try {
        int selectCnt = 0;
        long currentTimeNanos = System.nanoTime();
        // delayNanos() 找到定时任务队列里等下最先要执行的任务，返回还要多久这个任务会执行
        long selectDeadLineNanos = currentTimeNanos + delayNanos(currentTimeNanos);

        for (;;) {
        	// 计算定时任务队列里等下最先要执行的任务是不是马上就要执行了(<= 0.5ms)
            long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
            if (timeoutMillis <= 0) {
            	// 如果之前还没有 select 过，就 selectNow() 一次，并置 selectCnt 为 1
                if (selectCnt == 0) {
                    selector.selectNow();
                    selectCnt = 1;
                }
                // 跳出循环
                break;
            }

        	// 如果任务队列有任务，且 wakenUp.compareAndSet(false, true) 成功的话，就 selectNow() 一次，并置 selectCnt 为 1
            if (hasTasks() && wakenUp.compareAndSet(false, true)) {
                selector.selectNow();
                selectCnt = 1;
                break;
            }

        	// 阻塞式 select 操作
        	// 在外部线程添加任务的时候，会调用 wakeup 方法来唤醒它
            int selectedKeys = selector.select(timeoutMillis);
            selectCnt ++;

        	// 这几种情况的话，直接跳出循环
            if (selectedKeys != 0 || oldWakenUp || wakenUp.get() || hasTasks() || hasScheduledTasks()) {
                // - Selected something,
                // - waken up by user, or
                // - the task queue has a pending task.
                // - a scheduled task is ready for processing
                break;
            }
            if (Thread.interrupted()) {
                // Thread was interrupted so reset selected keys and break so we not run into a busy loop.
                // As this is most likely a bug in the handler of the user or it's client library we will
                // also log it.
                //
                // See https://github.com/netty/netty/issues/2426
                if (logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely because " +
                            "Thread.currentThread().interrupt() was called. Use " +
                            "NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
                }
                selectCnt = 1;
                break;
            }

            long time = System.nanoTime();
            // 判断 select 操作是否至少持续了 timeoutMillis
            if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                // timeoutMillis elapsed without anything selected.
                selectCnt = 1;
            } 
        	// 没有持续 timeoutMillis 则表明是一次空轮询，如果超过阈值（默认512），则重建 selector 用以避免 jdk 空轮询导致 cpu 100% 的 bug
            else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 &&
                    selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                // The selector returned prematurely many times in a row.
                // Rebuild the selector to work around the problem.
                logger.warn(
                        "Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.",
                        selectCnt, selector);

                rebuildSelector();
                selector = this.selector;

                // Select again to populate selectedKeys.
                selector.selectNow();
                selectCnt = 1;
                break;
            }

            currentTimeNanos = time;
        }

        if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS) {
            if (logger.isDebugEnabled()) {
                logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
                        selectCnt - 1, selector);
            }
        }
    } catch (CancelledKeyException e) {
        if (logger.isDebugEnabled()) {
            logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?",
                    selector, e);
        }
        // Harmless exception - log anyway
    }
}
```

select 操作可以总结如下：

1. **检查是否有马上就要执行（delay<=0.5ms）的定时任务，有的话跳出循环；跳出前检查是否是第一次 select，是的话进行一次 selectNow() ，并置 selectCnt 为 1；**
2. **如果任务队列中有任务，且 wakenUp.compareAndSet(false, true) 成功的话，就 selectNow() 一次，并置 selectCnt 为 1，然后跳出循环；**
3. **调用 selector.select(timeoutMillis) 进行阻塞式 select；在此期间，外部线程添加任务可以唤醒阻塞；**
4. **如果存在以下情况，则直接跳出循环**

- 轮询到了某些事件
- 被用户线程唤醒
- 任务队列里存在待处理的任务
- 存在已经准备好的待执行的定时任务

1. **判断 select 操作是否至少持续了 timeoutMillis，持续了 timeoutMillis 的话则视为有效轮询，否则视为空轮询；当空轮询次数超过阈值（512）后，进行 selector 重建，避免 jdk 空轮询导致 cpu 100% 的 bug；**

#### 重建 selector

```
// NioEventLoop.rebuildSelector()
public void rebuildSelector() {
	// 当前线程不是 NioEventLoop 线程的话，则将重建任务封装成 task 丢到 NioEventLoop 的任务队列
    if (!inEventLoop()) {
        execute(new Runnable() {
            @Override
            public void run() {
                rebuildSelector0();
            }
        });
        return;
    }
    // 当前线程是 NioEventLoop 线程的话，直接调用重建方法
    rebuildSelector0();
}

// NioEventLoop.rebuildSelector0()
private void rebuildSelector0() {
    final Selector oldSelector = selector;
    final SelectorTuple newSelectorTuple;

    if (oldSelector == null) {
        return;
    }

    try {
    	// 新建一个 SelectorTuple
        newSelectorTuple = openSelector();
    } catch (Exception e) {
        logger.warn("Failed to create a new Selector.", e);
        return;
    }

    // Register all channels to the new Selector.
    int nChannels = 0;
    for (SelectionKey key: oldSelector.keys()) {
        Object a = key.attachment();
        try {
            if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) {
                continue;
            }

            int interestOps = key.interestOps();
            // 取消在旧 selector 上的注册
            key.cancel();
            // 将 channel 注册到新的 selector 上
            SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
            // 维护 channel 和新 selectionKey 的关系
            if (a instanceof AbstractNioChannel) {
                // Update SelectionKey
                ((AbstractNioChannel) a).selectionKey = newKey;
            }
            nChannels ++;
        } catch (Exception e) {
            logger.warn("Failed to re-register a Channel to the new Selector.", e);
            if (a instanceof AbstractNioChannel) {
                AbstractNioChannel ch = (AbstractNioChannel) a;
                ch.unsafe().close(ch.unsafe().voidPromise());
            } else {
                @SuppressWarnings("unchecked")
                NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
                invokeChannelUnregistered(task, key, e);
            }
        }
    }

    selector = newSelectorTuple.selector;
    unwrappedSelector = newSelectorTuple.unwrappedSelector;

    try {
        // time to close the old selector as everything else is registered to the new one
        oldSelector.close();
    } catch (Throwable t) {
        if (logger.isWarnEnabled()) {
            logger.warn("Failed to close the old Selector.", t);
        }
    }

    if (logger.isInfoEnabled()) {
        logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
    }
}
```

总结 Netty 解决 JDK 空轮询 BUG 的方式如下：

1. **新建一个 selector**
2. **取消 channel 在旧 selector 上的注册**
3. **将 channel 注册到新的 selector 上**
4. **维护 channel 和新 selectionKey 的关系**

### 处理轮询到的 SelectedKeys

```
// NioEventLoop.processSelectedKeysOptimized()
private void processSelectedKeysOptimized() {
    for (int i = 0; i < selectedKeys.size; ++i) {
        final SelectionKey k = selectedKeys.keys[i];
        // null out entry in the array to allow to have it GC'ed once the Channel close
        // See https://github.com/netty/netty/issues/2363
        selectedKeys.keys[i] = null;

        final Object a = k.attachment();
 		// 通过 SelectionKey 获取到 channel 后进行处理
        if (a instanceof AbstractNioChannel) {
            processSelectedKey(k, (AbstractNioChannel) a);
        } else {
            @SuppressWarnings("unchecked")
            NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
            processSelectedKey(k, task);
        }
    	// 判断是否需要再次 select，暂不关注
        if (needsToSelectAgain) {
            // null out entries in the array to allow to have it GC'ed once the Channel close
            // See https://github.com/netty/netty/issues/2363
            selectedKeys.reset(i + 1);

            selectAgain();
            i = -1;
        }
    }
}

// NioEventLoop.processSelectedKey(k, ch)
private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
    final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
    ...
    try {
		// 准备就绪的 ops
        int readyOps = k.readyOps();
        // 连接建立成功，在 read/write 前客户端总是首先调用 finishConnect
        if ((readyOps & SelectionKey.OP_CONNECT) != 0) {
            // remove OP_CONNECT as otherwise Selector.select(..) will always return without blocking
            // See https://github.com/netty/netty/issues/924
            int ops = k.interestOps();
            ops &= ~SelectionKey.OP_CONNECT;
            k.interestOps(ops);

            unsafe.finishConnect();
        }

        // 处理 write 事件
        if ((readyOps & SelectionKey.OP_WRITE) != 0) {
            // Call forceFlush which will also take care of clear the OP_WRITE once there is nothing left to write
            ch.unsafe().forceFlush();
        }

        // 处理 read 事件或者 accept 事件
        // 当客户端进行连接请求后，服务端需要在这里 accept 连接
        if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
            unsafe.read();
        }
    } catch (CancelledKeyException ignored) {
        unsafe.close(unsafe.voidPromise());
    }
}
```

### 执行任务 runAllTasks

在进行 select 时间并处理后，需要进行 taskQueue 里的任务处理，NioEventLoop 的 runAllTasks(timeoutNanos) 继承自 SingleThreadEventExecutor

```
// SingleThreadEventExecutor.runAllTasks(timeoutNanos)
protected boolean runAllTasks(long timeoutNanos) {
	// 将定时任务队列里已经准备就绪待执行的任务添加到 taskQueue 中
    fetchFromScheduledTaskQueue();
    // 从 taskQueue 中取一个 task
    Runnable task = pollTask();
    if (task == null) {
        afterRunningAllTasks();
        return false;
    }

    // 根据超时时间计算截止时间
    final long deadline = ScheduledFutureTask.nanoTime() + timeoutNanos;
    long runTasks = 0;
    long lastExecutionTime;
    // 循环执行任务，并关注是否超过截止时间
    for (;;) {
    	// 执行 task
        safeExecute(task);

        runTasks ++;

        // 每执行 64 个任务检查一次是否超过截止时间
        if ((runTasks & 0x3F) == 0) {
            lastExecutionTime = ScheduledFutureTask.nanoTime();
            if (lastExecutionTime >= deadline) {
                break;
            }
        }

        // 从 taskQueue 中取出下一个 task
        task = pollTask();
        if (task == null) {
            lastExecutionTime = ScheduledFutureTask.nanoTime();
            break;
        }
    }

    afterRunningAllTasks();
    this.lastExecutionTime = lastExecutionTime;
    return true;
}
```

## 服务端接收新连接

前面说到 NioEventLoop 线程轮询到事件后调用 NioEventLoop.processSelectedKey(k, ch) 进行处理，其中就包括接收客户端新连接的处理。

### 轮询到 OP_ACCEPT 事件

NioEventLoop.processSelectedKey(k, ch) 中轮询到 OP_ACCEPT 事件的相关代码如下：

```
int readyOps = k.readyOps();

...

if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
    unsafe.read();
}
```

这里服务端轮询到 OP_ACCEPT 事件后，交给 unsafe 来处理。前面介绍过，unsafe 依附于 channel，channel 的许多操作最终均交付于 unsafe 来处理。

### 将客户端新连接注册到 workerGroup

NioMessageUnsafe 的 read() 方法相关代码如下：

```
private final List<Object> readBuf = new ArrayList<Object>();

// NioMessageUnsafe.read()
@Override
public void read() {
    assert eventLoop().inEventLoop();
    final ChannelConfig config = config();
    final ChannelPipeline pipeline = pipeline();
    // 缓冲区内存分配处理器
    final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config);

    boolean closed = false;
    Throwable exception = null;
    try {
        try {
            do {
            	// 不断读取消息并放入 readBuf 中
                int localRead = doReadMessages(readBuf);
                if (localRead == 0) {
                    break;
                }
                if (localRead < 0) {
                    closed = true;
                    break;
                }

                allocHandle.incMessagesRead(localRead);
            } while (allocHandle.continueReading());
        } catch (Throwable t) {
            exception = t;
        }

        int size = readBuf.size();
        // 循环处理 readBuf 中的元素，实际上就是一个个连接，交给 pipeline 去处理
        for (int i = 0; i < size; i ++) {
            readPending = false;
            // 在 pipeline 上传播 ChannelRead 事件
            pipeline.fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
        allocHandle.readComplete();
        // 在 pipeline 上传播 ChannelReadComplete 事件
        pipeline.fireChannelReadComplete();

        if (exception != null) {
            closed = closeOnReadError(exception);
			// 在 pipeline 上传播 ExceptionCaught 事件
            pipeline.fireExceptionCaught(exception);
        }

        if (closed) {
            inputShutdown = true;
            if (isOpen()) {
                close(voidPromise());
            }
        }
    } finally {
        if (!readPending && !config.isAutoRead()) {
            removeReadOp();
        }
    }
}

// NioServerSocketChannel.doReadMessages(list)
@Override
protected int doReadMessages(List<Object> buf) throws Exception {
	// 根据 jdk 的 ServerSocketChannel 去 accept 客户端连接
    SocketChannel ch = SocketUtils.accept(javaChannel());
    try {
        if (ch != null) {
        	// 将 NioServerSocketChannel 和 SocketChannel 封装成 NioSocketChannel 对象，放入到 list 中
            buf.add(new NioSocketChannel(this, ch));
            return 1;
        }
    } catch (Throwable t) {
        ...
    }
    return 0;
}
```

在 accept 到客户端连接，并封装成 NioSocketChannel 对象后，首先需要在 pipeline 上从 head 节点开始传播 ChannelRead 事件

```
// DefaultChannelPipeline.fireChannelRead(msg)
@Override
public final ChannelPipeline fireChannelRead(Object msg) {
	// 将 ChannelRead 在 pipeline 上从 head 节点开始传播
    AbstractChannelHandlerContext.invokeChannelRead(head, msg);
    return this;
}

// AbstractChannelHandlerContext.invokeChannelRead(next, msg)
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    EventExecutor executor = next.executor();
    if (executor.inEventLoop()) {
        next.invokeChannelRead(m);
    } else {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRead(m);
            }
        });
    }
}

// AbstractChannelHandlerContext.invokeChannelRead(msg)
private void invokeChannelRead(Object msg) {
    if (invokeHandler()) {
        try {
            ((ChannelInboundHandler) handler()).channelRead(this, msg);
        } catch (Throwable t) {
            notifyHandlerException(t);
        }
    } else {
        fireChannelRead(msg);
    }
}
```

然后 ChannelRead 事件从 head 节点经过业务 handler 会传递到 ServerBootstrapAcceptor 上。对服务端启动过程还有印象的话，ServerBootstrapAcceptor 添加时机相关流程如下：

1. **初始化 NioServerSocketChannel 时在 pipeline 中添加 ChannelInitializer**
2. **在将 NioServerSocketChannel 注册到 selector 上后，触发 handlerAdded 事件**
3. **ChannelInitializer 的 handlerAdded() 方法调用重写的 initChannel(ch) 方法**
4. **initChannel(ch) 方法会在 pipeline 上添加自定义的业务 handler，然后给 eventLoop 添加一个异步任务，任务内容为将 ServerBootstrapAcceptor 添加到 pipeline 中**

```
// ServerBootstrapAcceptor.channelRead(ctx, msg)
@Override
@SuppressWarnings("unchecked")
public void channelRead(ChannelHandlerContext ctx, Object msg) {

	// 这个 child 就是上面 accept 客户端连接后封装的 NioSocketChannel 对象
    final Channel child = (Channel) msg;
    // 在 NioSocketChannel 对象的 pipeline 上添加 childHandler
    child.pipeline().addLast(childHandler);
    // 将 childOptions 属性设置给 NioSocketChannel 对象
    setChannelOptions(child, childOptions, logger);

    for (Entry<AttributeKey<?>, Object> e: childAttrs) {
        child.attr((AttributeKey<Object>) e.getKey()).set(e.getValue());
    }

    try {
    	// 将 NioSocketChannel 对象注册到 childGroup 上，也就是我们说的 workerGroup
        childGroup.register(child).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    forceClose(child, future.cause());
                }
            }
        });
    } catch (Throwable t) {
        forceClose(child, t);
    }
}
```

NioSocketChannel 对象在 childGroup 上的注册过程和之前的注册分析类似，最终调用到 AbstractUnsafe 的 register0(promise) 方法

```
// AbstractUnsafe.register0(promise)
private void register0(ChannelPromise promise) {
    try {
        // check if the channel is still open as it could be closed in the mean time when the register
        // call was outside of the eventLoop
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
        boolean firstRegistration = neverRegistered;
        // 调用注册方法
        doRegister();
        neverRegistered = false;
        registered = true;

        // 触发 HandlerAdded 事件
        pipeline.invokeHandlerAddedIfNeeded();

        safeSetSuccess(promise);

        // 触发 ChannelRegistered 事件
        pipeline.fireChannelRegistered();
        
        // 建立连接后 isActive() 为 true
        if (isActive()) {
        	// 第一次注册的话，head 节点处理 ChannelActive 时候会通过 readIfIsAutoRead(); 修改 ops 注册读事件
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                // 否则需要在 read 之前注册 read 事件
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

// AbstractNioChannel.doRegister()
@Override
protected void doRegister() throws Exception {
    boolean selected = false;
    for (;;) {
        try {
        	// 将 SocketChannel 注册到 selector 上，ops 为 0，并将 NioSocketChannel 作为附属对象
            selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);
            return;
        } catch (CancelledKeyException e) {
            if (!selected) {
                // Force the Selector to select now as the "canceled" SelectionKey may still be
                // cached and not removed because no Select.select(..) operation was called yet.
                eventLoop().selectNow();
                selected = true;
            } else {
                // We forced a select operation on the selector before but the SelectionKey is still cached
                // for whatever reason. JDK bug ?
                throw e;
            }
        }
    }
}
```

### 注册 read 事件

上面我们看到个条件 isActive()，它对于服务端和客户端的含义是不一样的：

- 服务端 NioServerSocketChannel 注册到 bossGroup 的 selector 的时候，它代表是否绑定好了端口
- 客户端 NioSocketChannel 注册到 group 的 selector 的时候，它代表是否已经建立了连接（此时尚未与服务端建立连接）
- 服务端 accept 客户端连接后，将 NioSocketChannel 注册到 workerGroup 的 selector 的时候，它代表是否已经建立了连接（此时已经建立了连接）

又由于 doRegister() 在将 SocketChannel 注册到 selector 上的时候 ops 为 0，所以我们需要在 read 之前注册一下 read 事件；
如果是第一次注册的话，在 pipeline 上传播 ChannelActive 事件时，head 节点会通过 readIfIsAutoRead() 修改 ops 注册读事件；
否则的话需要调用 beginRead() 方法去修改 ops 注册 read 事件；

```
// AbstractNioChannel.beginRead()
@Override
public final void beginRead() {
    assertEventLoop();

    if (!isActive()) {
        return;
    }

    try {
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
    // 跟前面说的 readIfIsAutoRead() 最终调用都是这个地方
    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
```

## 总结

### Reactor 线程启动执行总结

在第一次调用 eventLoop.execute(task) 方法的时候，将 NioEventLoop 的 run() 方法封装成一个 task，然后交给 executor 去执行，ThreadPerTaskExecutor 只是简单的 new Thread(task).start() 去启动一个线程，即我们全篇所说的 NioEventLoop 线程。

NioEventLoop 的 run() 方法主要做了以下几件事：

1. 轮询 selector 上事件，通过重建 selector 规避了 jdk 的空轮询导致的 cpu 100% 的 bug
2. 处理轮询到的事件，包括 connect、accept、read、write 等事件
3. 执行任务队列的任务，会根据上面处理事件的 ioTime 和设置的 ioRatio 计算一个超时时间

### 服务端接收新连接过程总结

1. 轮询到 OP_ACCEPT 事件
2. 根据 accept 到的 SocketChannel 构造一个 NioSocketChannel
3. 将 NioSocketChannel 注册到 workerGroup 中一个 NioEventLoop 的 selector 上
4. 进行 handlerAdded、ChannelRegistered 等一些事件的传播
5. 在 read 之前需要通过修改 ops 去注册 read 事件