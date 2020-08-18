package com.froggengo.class11Tcp2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;

public class MyServerChannelHandler extends SimpleChannelInboundHandler<MyProtocol> {
    private  int  count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocol msg) throws Exception {
        int length = msg.getLength();
        byte[] context = msg.getContext();
        System.out.println("服务器接受到信息长度："+ length);
        System.out.println("服务器接受到信息："+ new String (msg.getContext(), Charset.defaultCharset()));
        System.out.println("服务器接受信息计数：" +(++count));
        String message = UUID.randomUUID().toString();
        byte[] bytes = message.getBytes(Charset.defaultCharset());
        MyProtocol myProtocol = new MyProtocol();
        myProtocol.setLength(bytes.length);
        myProtocol.setContext(bytes);
        ctx.writeAndFlush(myProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
