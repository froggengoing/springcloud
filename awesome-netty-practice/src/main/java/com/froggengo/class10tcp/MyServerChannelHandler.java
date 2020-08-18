package com.froggengo.class10tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;
import java.util.UUID;

public class MyServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] byteBuf = new byte[msg.readableBytes()];
        msg.readBytes(byteBuf);
        System.out.println("服务器接收到消息 "+ new String(byteBuf, Charset.defaultCharset()));
        System.out.println("服务器计数：" +(++this.count));
        ByteBuf byteBuf1 = Unpooled.copiedBuffer(UUID.randomUUID().toString(), Charset.forName("utf-8"));
        ctx.writeAndFlush(byteBuf1);
    }
}
