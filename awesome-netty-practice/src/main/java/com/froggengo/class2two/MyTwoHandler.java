package com.froggengo.class2two;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyTwoHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("服务器："+ctx.channel().remoteAddress()+"-"+msg);
        ctx.channel().writeAndFlush("服务器："+msg);
    }
}
