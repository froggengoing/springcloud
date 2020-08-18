package com.froggengo.class3third;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyChatClient {
    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        BufferedReader bufferedReader=null;
        Channel channel =null;
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class).group(workerGroup)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            // 自定义handler
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println(msg);
                                }
                            });
                        }
                    });
            //future = bootstrap.connect("localhost", 8001).sync();
            channel = bootstrap.connect("localhost", 8001).sync().channel();
            //future.channel().closeFuture().sync();
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            for(;;){
                if (bufferedReader.readLine()!=null) {
                    channel.writeAndFlush(bufferedReader.readLine() + "\r\n");
                }
            }
        }finally {
            bufferedReader.close();
            channel.close();
            workerGroup.shutdownGracefully();
        }

    }
}
