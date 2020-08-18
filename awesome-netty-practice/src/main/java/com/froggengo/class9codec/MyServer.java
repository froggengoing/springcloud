package com.froggengo.class9codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.time.LocalDateTime;
import java.util.List;

public class MyServer {


    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workGroup)
                    .handler(new LoggingHandler())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        //ChannelInitializer将会被移除，所以添加channelActive()方法无效
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyLongDecode());
                            pipeline.addLast(new MyLongEncode());
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
                                    System.out.println("SimpleChannelInboundHandler channelRead0() invoke");
                                    System.out.println("客户端信息："+msg);
                                    ctx.writeAndFlush(123456L);
                                }
                            });
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

    private static class MyLongDecode extends ByteToMessageDecoder{
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println("MyLongDecode decode() invoke");
            if (in.readableBytes()< 8) {
                return ;
            }
            out.add(in.readLong());
        }
    }

    private static class MyLongEncode extends MessageToByteEncoder<Long> {
        @Override
        protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
            System.out.println("MyLongEncode encode() invoke");
            out.writeLong(msg);
        }
    }
}
