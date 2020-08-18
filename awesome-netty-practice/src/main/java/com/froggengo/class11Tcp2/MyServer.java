package com.froggengo.class11Tcp2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.time.LocalDateTime;

public class MyServer {
    /**
     * 与class10的区别是，这里添加了解码器。每次只读取指定长度的信息。
     * <code>
     *         int length = in.readInt();
     *         byte[] bytes = new byte[length];
     *         ByteBuf byteBuf = in.readBytes(bytes);
     *
     * </code>
     * 而就版本是读取所有读取的数据
     * <code>
     *      byte[] byteBuf = new byte[msg.readableBytes()];
     * </code>
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workGroup)
                    .handler(new LoggingHandler())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyProtocolDecoder());
                            pipeline.addLast(new MyProtocolEncoder());
                            pipeline.addLast(new MyServerChannelHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8080).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("完成端口注册" + LocalDateTime.now());
                }
            }).sync();
            channelFuture.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("关闭channel"+LocalDateTime.now());
                }
            }).sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
