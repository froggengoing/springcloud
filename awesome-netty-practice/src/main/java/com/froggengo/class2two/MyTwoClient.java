package com.froggengo.class2two;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class MyTwoClient {


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /**
             * 注意与服务器不同的地方
             * 1.使用Bootstrap
             * 2.只使用一个EventLoopGroup
             * 3.handler对应bossGrroup，childerHandler对应workerGroup。所以client使用handler
             * 4.使用connect而不是bind
             * 5.使用NioSocketChannel而不是NioServerSocketChannel
             */
            Bootstrap bootstrap = new Bootstrap().group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("客户端"+ctx.channel().remoteAddress()+"-"+msg);
                                    ctx.channel().writeAndFlush("客户端");
                                }
                                /**
                                 * 如果没有这个方法，Client并不会主动发消息给Server
                                 * 那么Server的channelRead0无法触发，导致Client的channelRead0也无法触发
                                 * 这个channelActive可以让Client连接后，发送一条消息
                                 * @param ctx
                                 * @throws Exception
                                 */
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.channel().writeAndFlush("channelActive");
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    super.exceptionCaught(ctx, cause);
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8001).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
