package com.froggengo.class9codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.List;

public class MyClient  {



    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap().group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyLongDecode());
                            pipeline.addLast(new MyLongEncode());
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
                                    System.out.println("SimpleChannelInboundHandler channelRead0() invoke");
                                    System.out.println("服务器数据：" + msg);
                                }
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("SimpleChannelInboundHandler channelActive() invoke");
                                    ctx.writeAndFlush(99999L);
                                   // ctx.writeAndFlush(99999); //因为encode只接受long，所以数据没发出去
                                }

                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端完成注册");
                    }else {
                        System.out.println("客户端注册失败");
                    }

                }
            }).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
    }

    }

    private static class MyLongDecode extends ByteToMessageDecoder {
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
