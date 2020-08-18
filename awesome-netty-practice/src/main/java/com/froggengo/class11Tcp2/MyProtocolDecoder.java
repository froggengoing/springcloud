package com.froggengo.class11Tcp2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyProtocolDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyProtocolDecoder decode() invoke");
        int length = in.readInt();
        byte[] bytes = new byte[length];
        ByteBuf byteBuf = in.readBytes(bytes);
        MyProtocol myProtocol = new MyProtocol();
        myProtocol.setContext(bytes);
        myProtocol.setLength(length);
        out.add(myProtocol);
    }
}
