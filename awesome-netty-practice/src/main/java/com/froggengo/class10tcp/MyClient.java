package com.froggengo.class10tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.Charset;
import java.util.List;

public class MyClient  {

    /**
     * 粘包
     * 客户端循环发送10条数据给服务器，但服务器只接受到一条数据（把10条数据整合为1条）
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap().group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                private  int count;
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("SimpleChannelInboundHandler channelRead0() invoke");
                                    byte[] bytes = new byte[msg.readableBytes()];
                                    msg.readBytes(bytes);
                                    System.out.println("客户端接收到数据：" + new String (bytes,Charset.defaultCharset()));
                                    System.out.println("客户端计数"+(++count));
                                }
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("SimpleChannelInboundHandler channelActive() invoke");
                                    for (int i = 0; i < 100; i++) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        for (int j = 0; j < 1000; j++) {
                                            stringBuilder.append(i + "-hello-" +j +"  ");
                                        }
                                        ByteBuf byteBuf = Unpooled.copiedBuffer(stringBuilder.toString(), Charset.forName("UTF-8"));
                                        ctx.writeAndFlush(byteBuf);
                                    }
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

}
