package com.froggengo.class1one;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Hello world!
 *
 */
public class App 
{
    private boolean registered ,neverRegistered;

    public static void main(String[] args ) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup =new NioEventLoopGroup();
        try {


            ServerBootstrap serverBootstrap = new ServerBootstrap()
                                        .group(bossGroup, workerGroup)
                                        .channel(NioServerSocketChannel.class).handler(new LoggingHandler())
                                        .childHandler(new MyServerInitializer());
            //绑定端口,开始接收进来的连接
            ChannelFuture future = serverBootstrap.bind(8001).sync();
            System.out.println("future1");
            ChannelFuture future1 = serverBootstrap.bind(8002).sync();
            System.out.println("future2");
            //阻塞到这里
            //等待服务器socket关闭
            //在本例子中不会发生,这时可以关闭服务器了
            future.channel().closeFuture().sync();
            //是在sync中阻塞了
            // io.netty.util.concurrent.DefaultPromise.sync中调用了await()
            //object.wait()直到isDone()返回true
            System.out.println("完成1");
            future1.channel().closeFuture().sync();
            System.out.println("完成2");
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }

    }
/*
private void register0(ChannelPromise promise) {
    try {
        boolean firstRegistration = neverRegistered;
 //1.注册channel
 // selectionKey = javaChannel().register(eventLoop().unwrappedSelector(),
        //                                      0, this);
        doRegister();//如上，selector中注册channel
        neverRegistered = false;
        registered = true;
 //2.执行pipeline中的任务链，实际是所有已经注册的handlerAdded()方法
 //即ServerBootstrap.init(channel)中为channel添加ChannelInitializer实现类
     //1.pipeline添加主程序中配置的主handler
     //2.添加ServerBootstrapAcceptor(也是handler)
        pipeline.invokeHandlerAddedIfNeeded();
 //3.设置ChannelPromise完成状态，进而调用promise注册的listener
        safeSetSuccess(promise);
//4.调用Pipeline中ChannelContext链
//进而调用handler.channelRegistered()方法
        pipeline.fireChannelRegistered();
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                // See https://github.com/netty/netty/issues/4805
                beginRead();
            }
        }
    }
}
*/

    private void safeSetSuccess(ChannelPromise promise) {
    }

    DefaultChannelPipeline  pipeline;
    private ChannelConfig config() {
        return null;
    }

    private void beginRead() {

    }

    private void doRegister() {
    }

    private boolean isActive() {
        return false;
    }
}
