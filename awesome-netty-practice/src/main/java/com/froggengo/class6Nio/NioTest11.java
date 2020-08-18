package com.froggengo.class6Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class NioTest11 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel=ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(8888));
       //channel.bind(new InetSocketAddress(8888)); // 区别是啥
        ByteBuffer[] bf= new ByteBuffer[3];
        bf[0] = ByteBuffer.allocate(2);
        bf[1] = ByteBuffer.allocate(3);
        bf[2] = ByteBuffer.allocate(4);
        SocketChannel socketChannel = channel.accept();
        System.out.println("有连接进来"+socketChannel.getRemoteAddress());
        int messageLength=9;
        while (true){
            //读取直到buf满
            long readed=0;
            while (readed <messageLength){
                long read = socketChannel.read(bf);
                readed += read;
                Arrays.stream(bf).forEach(n-> System.out.println("position:"+n.position()+"-"+"limit:"+n.limit()));
            }
            //翻转
            Arrays.stream(bf).forEach(n->n.flip());
            //写到sockechannel中
            socketChannel.write(bf);
            //清空
            Arrays.stream(bf).forEach(n->n.clear());
        }
    }
}
