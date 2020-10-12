# Netty 入门使用总结

 Posted on 2019-02-19 | In [Netty](https://jimmy2angel.github.io/categories/Netty/)

# Netty 是什么? 为什么要用 Netty?

Netty 是一个异步事件驱动的网络应用框架，用于快速开发可维护的高性能服务器和客户端。

使用 Netty 而不使用 Java 原生 NIO 的原因如下：

1. 使用 Java NIO 需要了解太多概念，编程复杂
2. Netty 底层 IO 模型切换简单，改改参数，直接从 NIO 模型变为 IO 模型
3. Netty 自带的拆包解包、异常检测等机制，让你只需要关注业务逻辑
4. Netty 解决了 JDK 很多包括空轮询在内的 BUG
5. Netty 底层对线程，selector 做了很多细小的优化，精心设计的 reactor 线程模型做到非常高效的并发处理
6. 自带各种协议栈让你处理任何一种通用协议都几乎不用亲自动手
7. Netty 已经历各大 RPC 框架，消息中间件，分布式通信中间件线上的广泛验证，健壮性无比强大

# 启动流程

## 服务端

```
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap
                // 指定线程模型
                .group(boss, worker)
                // 指定 IO 模型
                .channel(NioServerSocketChannel.class)
                // tcp 相关设置
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 指定业务处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new FirstServerHandler());
                    }
                })
                .bind(8888);
    }
}
```

1. 创建一个服务端启动辅助类 ServerBootstrap。
2. 通过 ServerBootstrap 配置线程模型、IO模型、tcp相关参数和 channelHandler 等。
3. 绑定端口。

其中 boss 线程组负责创建新连接，worker 线程组负责读取数据以及业务处理。

## 客户端

```
public static void main(String[] args) {
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    Bootstrap bootstrap = new Bootstrap();
    bootstrap
            // 1.指定线程模型
            .group(workerGroup)
            // 2.指定 IO 类型为 NIO
            .channel(NioSocketChannel.class)
            // 3.IO 处理逻辑
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new FirstClientHandler());
                }
            });
    // 4.建立连接
    bootstrap.connect("127.0.0.1", 8888).addListener(future -> {
        if (future.isSuccess()) {
            System.out.println("连接成功!");
        } else {
            System.err.println("连接失败!");
        }

    });
}
```

1. 和服务端一样，创建一个辅助类 Bootstrap，然后配置线程模型、IO模型和 handler 等。
2. 调用 connect 方法进行连接，可以看到 connect 方法有两个参数，第一个参数可以填写 IP 或者域名，第二个参数填写的是端口号，
   由于 connect 方法返回的是一个 Future，也就是说这个方是异步的，我们通过 addListener 方法可以监听到连接是否成功，进而打印出连接信息。

# 客户端和服务端双向通信

## 客户端发送数据到服务端

上面客户端启动代码中向 pipeline 添加了一个 FirstClientHandler。pipeline 是和这条连接相关的逻辑处理链，采用了责任链模式。
然后编写 FirstClientHandler 代码如下：

```
public class FirstClientHandle extends ChannelHandlerAdapter {

    /**
     * 这个方法会在客户端连接建立成功之后被调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端写出数据");

        ByteBuf buffer = getByteBuf(ctx);
        ctx.channel().writeAndFlush(buffer);
    }

    /**
     * 获取一个 netty 对二进制数据的抽象 ByteBuf
     * @param ctx
     * @return
     */
    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1. 获取二进制抽象 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();
        // 2. 准备数据，指定字符串的字符集为 utf-8
        byte[] bytes = "Hello Lollipop, This is my netty demo!".getBytes(Charset.forName("UTF-8"));
        // 3. 填充数据到 ByteBuf
        buffer.writeBytes(bytes);
        return buffer;
    }
}
```



写数据的逻辑分为两步：首先我们需要获取一个 netty 对二进制数据的抽象 ByteBuf，上面代码中, ctx.alloc() 获取到一个 ByteBuf 的内存管理器，
这个内存管理器的作用就是分配一个 ByteBuf，然后我们把字符串的二进制数据填充到 ByteBuf，这样我们就获取到了 Netty 需要的一个数据格式，
最后我们调用 ctx.channel().writeAndFlush() 把数据写到服务端。

## 服务端读取客户端发送的数据

在服务端启动代码中向 pipeline 中添加一个 FirstServerHandler。与 FirstClientHandler 类似：

```
public class FirstServerHandler extends ChannelHandlerAdapter {

    /**
     * 这个方法在接收到客户端发来的数据之后被回调。
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
    }
}
```



这里的 msg 就是我们客户端发送的 ByteBuf，需要我们强转一下。然后我们直接打印。
之后启动服务端、客户端，可以看到服务端确实打印了客户端发送的数据。

## 服务端回复数据给客户端

与客户端发送数据类似，同样准备一个 ByteBuf 然后发送即可。修改 FirstServerHandler 代码如下：

```
/**
 * 这个方法在接收到客户端发来的数据之后被回调。
 * @param ctx
 * @param msg
 */
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));

    // 服务端向客户端发送数据
    System.out.println(new Date() + ": 服务端写出数据");
    ByteBuf buf = getByteBuf(ctx);
    ctx.channel().writeAndFlush(buf);
}

private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
    byte[] bytes = "你好，我已经收到了你的消息!".getBytes(Charset.forName("utf-8"));
    ByteBuf buffer = ctx.alloc().buffer();
    buffer.writeBytes(bytes);
    return buffer;
}
```



## 客户端读取服务端发送的数据

修改 FirstClientHandle 代码如下：

```
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;

    System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
}
```



## 总结

- 客户端和服务端的逻辑处理是均是在启动的时候，通过给逻辑处理链 pipeline 添加逻辑处理器，来编写数据的读写逻辑
- 在客户端连接成功之后会回调到逻辑处理器的 channelActive() 方法，而不管是服务端还是客户端，收到数据之后都会调用到 channelRead 方法
- 写数据调用writeAndFlush方法，客户端与服务端交互的二进制数据载体为 ByteBuf，ByteBuf 通过连接的内存管理器创建，
  字节数据填充到 ByteBuf 之后才能写到对端

# 拆包粘包理论与解决方案

## ByteBuf

- ByteBuf 是 Netty 中客户端与服务端交互的二进制数据载体
- ByteBuf 通过 readerIndex 和 writerIndex 两个指针划分可读字节和可写字节
- 从 ByteBuf 中每读取一个字节，readerIndex 自增1，ByteBuf 里面总共有 writerIndex-readerIndex 个字节可读,
  由此可以推论出当 readerIndex 与 writerIndex 相等的时候，ByteBuf 不可读
- 写数据是从 writerIndex 指向的部分开始写，每写一个字节，writerIndex 自增1，直到增到 capacity，这个时候，表示 ByteBuf 已经不可写了
- ByteBuf 里面其实还有一个参数 maxCapacity，当向 ByteBuf 写数据的时候，如果容量不足，那么这个时候可以进行扩容，
  直到 capacity 扩容到 maxCapacity，超过 maxCapacity 就会报错

## 为什么会有粘包半包现象

我们需要知道，尽管我们在应用层面使用了 Netty，但是对于操作系统来说，只认 TCP 协议，尽管我们的应用层是按照 ByteBuf 为 单位来发送数据，
但是到了底层操作系统仍然是按照字节流发送数据，因此，数据到了服务端，也是按照字节流的方式读入，然后到了 Netty 应用层面，重新拼装成 ByteBuf，
而这里的 ByteBuf 与客户端按顺序发送的 ByteBuf 可能是不对等的。因此，我们需要在客户端根据自定义协议来组装我们应用层的数据包，
然后在服务端根据我们的应用层的协议来组装数据包，这个过程通常在服务端称为拆包，而在客户端称为粘包。

拆包和粘包是相对的，一端粘了包，另外一端就需要将粘过的包拆开，举个栗子，发送端将三个数据包粘成两个 TCP 数据包发送到接收端，
接收端就需要根据应用协议将两个数据包重新组装成三个数据包。

## 拆包的原理

在没有 Netty 的情况下，用户如果自己需要拆包，基本原理就是不断从 TCP 缓冲区中读取数据，每次读取完都需要判断是否是一个完整的数据包

- 如果当前读取的数据不足以拼接成一个完整的业务数据包，那就保留该数据，继续从 TCP 缓冲区中读取，直到得到一个完整的数据包。
- 如果当前读到的数据加上已经读取的数据足够拼接成一个数据包，那就将已经读取的数据拼接上本次读取的数据，构成一个完整的业务数据包传递到业务逻辑，
  多余的数据仍然保留，以便和下次读到的数据尝试拼接。

如果我们自己实现拆包，这个过程将会非常麻烦，我们的每一种自定义协议，都需要自己实现，还需要考虑各种异常，而 Netty 自带的一些开箱即用的拆包器已经完全满足我们的需求了

## Netty 自带的拆包器

1. 固定长度的拆包器 FixedLengthFrameDecoder

   如果你的应用层协议非常简单，每个数据包的长度都是固定的，比如 100，那么只需要把这个拆包器加到 pipeline 中，Netty 会把一个个长度为 100 的数据包 (ByteBuf) 传递到下一个 channelHandler。

2. 行拆包器 LineBasedFrameDecoder

   从字面意思来看，发送端发送数据包的时候，每个数据包之间以换行符作为分隔，接收端通过 LineBasedFrameDecoder 将粘过的 ByteBuf 拆分成一个个完整的应用层数据包。

3. 分隔符拆包器 DelimiterBasedFrameDecoder

   DelimiterBasedFrameDecoder 是行拆包器的通用版本，只不过我们可以自定义分隔符。

4. 基于长度域拆包器 LengthFieldBasedFrameDecoder

   最后一种拆包器是最通用的一种拆包器，只要你的自定义协议中包含长度域字段，均可以使用这个拆包器来实现应用层拆包。

# Netty 性能优化

## 共享 handler

调用 pipeline().addLast() 方法的时候，都直接使用单例，不需要每次有新连接的时候都 new 一个 handler，提高效率，也避免了创建很多小的对象。

```
// 1. 加上注解标识，表明该 handler 是可以多个 channel 共享的
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    // 2. 构造单例
    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    protected LoginRequestHandler() {
    }

}
```



## 压缩 handler - 合并编解码器

Netty 内部提供了一个类，叫做 MessageToMessageCodec，使用它可以让我们的编解码操作放到一个类里面去实现。我们也可以同时使用单例。

```
@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    private PacketCodecHandler() {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        out.add(PacketCodec.INSTANCE.decode(byteBuf));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        PacketCodec.INSTANCE.encode(byteBuf, packet);
        out.add(byteBuf);
    }
}
```



## 缩短事件传播路径

### 压缩 handler - 合并平行 handler

在有多个平行 handler（即一个指令只会有一个对应 handler 执行）的时候，我们可以合并成一个 handler，然后调用指定对应的 handler 处理即可。

### 更改事件传播源

在某个 inBound 类型的 handler 处理完逻辑之后，调用 ctx.writeAndFlush() 可以直接一口气把对象送到 codec 中编码，然后写出去。

在某个 inBound 类型的 handler 处理完逻辑之后，调用 ctx.channel().writeAndFlush()，对象会从最后一个 outBound 类型的 handler 开始，
逐个往前进行传播，路径是要比 ctx.writeAndFlush() 要长的。

在能够使用 ctx.writeAndFlush() 的地方用其代替 ctx.channel().writeAndFlush()。

## 减少阻塞主线程的操作

通常我们的应用程序会涉及到数据库和网络，例如：

```
protected void channelRead(ChannelHandlerContext ctx, T packet) {
    // 1. balabala 一些逻辑
    // 2. 数据库或者网络等一些耗时的操作
    // 3. writeAndFlush()
    // 4. balabala 其他的逻辑
}
```



由于单个 NIO 线程上绑定了多个 channel，一个 channel 上绑定了多个 handler，所以只要有一个 handler 的该方法阻塞了 NIO 线程，
最终都会拖慢绑定在该 NIO 线程上的其他所有的 channel。

所以对于耗时的操作，我们需要把这些耗时的操作丢到我们的业务线程池中去处理，下面是解决方案的伪代码：

```
ThreadPool threadPool = xxx;

protected void channelRead(ChannelHandlerContext ctx, T packet) {
    threadPool.submit(new Runnable() {
        // 1. balabala 一些逻辑
        // 2. 数据库或者网络等一些耗时的操作
        // 3. writeAndFlush()
        // 4. balabala 其他的逻辑
    })
}
```



这样，就可以避免一些耗时的操作影响 Netty 的 NIO 线程，从而影响其他的 channel。

## 准确统计处理时长

```
protected void channelRead0(ChannelHandlerContext ctx, T packet) {
    threadPool.submit(new Runnable() {
        long begin = System.currentTimeMillis();
        // 1. balabala 一些逻辑
        // 2. 数据库或者网络等一些耗时的操作
        
        // 3. writeAndFlush
        xxx.writeAndFlush().addListener(future -> {
            if (future.isDone()) {
                // 4. balabala 其他的逻辑
                long time =  System.currentTimeMillis() - begin;
            }
        });
    })
}
```

writeAndFlush() 方法会返回一个 ChannelFuture 对象，我们给这个对象添加一个监听器，然后在回调方法里面，我们可以监听这个方法执行的结果，
进而再执行其他逻辑，最后统计耗时，这样统计出来的耗时才是最准确的。

[# Netty](https://jimmy2angel.github.io/tags/Netty/) [# NIO](https://jimmy2angel.github.io/tags/NIO/)