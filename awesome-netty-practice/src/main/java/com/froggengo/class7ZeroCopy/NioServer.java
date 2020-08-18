package com.froggengo.class7ZeroCopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(8888));
        channel.socket().setReuseAddress(true); // 46043068 262142
        int total=0;
        while (true){
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(true);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int read=0;
            while( -1!=read){
                read = socketChannel.read(byteBuffer);
                total +=read;
                byteBuffer.rewind();
            }
/*            while( (read = socketChannel.read(byteBuffer)) > 0 ){
                total +=read;
                byteBuffer.clear();
            }*/
            System.out.println(total);
        }
    }
}
