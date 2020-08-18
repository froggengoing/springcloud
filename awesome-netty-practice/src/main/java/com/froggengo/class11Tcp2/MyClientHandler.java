package com.froggengo.class11Tcp2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class MyClientHandler extends SimpleChannelInboundHandler<MyProtocol> {
    int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnsupportedEncodingException {
        for (int i = 0; i < 10; i++) {
            String message =  "客服端信息" + i;
            byte[] content = message.getBytes("UTF-8");
            MyProtocol myProtocol = new MyProtocol();
            myProtocol.setLength(content.length);
            myProtocol.setContext(content);
            ctx.writeAndFlush(myProtocol);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocol msg) throws Exception {
        System.out.println("MyClientHandler channelRead0 invoke");
        int length = msg.getLength();
        byte[] context = msg.getContext();
        System.out.println("客户端接收到服务器信息:"+"长度："+length+",内容：" +new String(context, Charset.defaultCharset()));

        System.out.println("计数"+(++this.count));
    }
}
