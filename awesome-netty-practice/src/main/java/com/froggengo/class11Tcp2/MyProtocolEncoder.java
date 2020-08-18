package com.froggengo.class11Tcp2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyProtocolEncoder extends MessageToByteEncoder<MyProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MyProtocolEncoder encode() invoke");

        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContext());
    }
}
