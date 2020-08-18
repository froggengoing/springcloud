package com.froggengo.class2two;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MyServer {
    /**
     * 重要类：
     * @see EventLoopGroup
     *  @see NioEventLoopGroup
     * @see ServerBootstrap
     * @see io.netty.channel.ChannelInitializer
     * @see io.netty.channel.SimpleChannelInboundHandler
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup bossGroup =new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyTwoInitializer());
            ChannelFuture future = serverBootstrap.bind(8001).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
