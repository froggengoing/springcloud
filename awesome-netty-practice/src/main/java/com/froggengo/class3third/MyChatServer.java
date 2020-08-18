package com.froggengo.class3third;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 两个问题，客户端如果关闭时如果点disconnect，会一直发null数据到其余客户端，cpu拉满，电脑卡死。
 * 如果直接关闭，会报java.io.IOException: 远程主机强迫关闭了一个现有的连接。
 */
public class MyChatServer {

    private static ChannelGroup channelGroup =new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //自定义Initializer
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    //自定义handler
                    /**
                     * 上线保存至列表
                     * 客户端发消息所有上线客户端都能收到
                     */
                    pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("进入channelRead0");
                            Channel channel = ctx.channel();
                            channelGroup.forEach(ch->{
                                if(channel !=ch){
                                    ch.writeAndFlush(channel.remoteAddress() + "发生消息：" + msg +"\n");
                                }else{
                                    ch.writeAndFlush("自己：" + msg + "\n");
                                }
                            });
                        }

                        /**
                         * 这里的\n必须加，否则消息发不出去，应该与DelimiterBasedFrameDecoder有关
                         * @param ctx
                         * @throws Exception
                         */
                        @Override
                        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                            //super.handlerAdded(ctx);
                            Channel channel = ctx.channel();
                            System.out.println("handlerAdded:" + channelGroup.size());
                            channelGroup.writeAndFlush("服务器消息："+ channel.remoteAddress() +"已经加入\n");
                            //添加至列表
                            channelGroup.add(channel);
                        }

                        /**
                         * DefaultChannelGroup会自动调用remove移除
                         * @param ctx
                         * @throws Exception
                         */
                        @Override
                        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                            //super.handlerRemoved(ctx);
                            Channel channel = ctx.channel();
                            // channelGroup.remove(channel);
                            channelGroup.writeAndFlush("服务器消息："+ channel.remoteAddress() +"已经移除\n");
                            System.out.println("handlerRemoved:" + channelGroup.size());
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            super.channelActive(ctx);
                            System.out.println("服务器消息："+ctx.channel().remoteAddress() +"上线");
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            super.channelInactive(ctx);
                            System.out.println("服务器消息："+ctx.channel().remoteAddress() +"下线");
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            // super.exceptionCaught(ctx, cause);
                            cause.printStackTrace();
                            ctx.close();

                        }
                    });
                }
            });

            ChannelFuture future = serverBootstrap.bind(8001).sync();
            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
