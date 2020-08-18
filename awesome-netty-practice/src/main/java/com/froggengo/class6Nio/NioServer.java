package com.froggengo.class6Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectionKey;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8899));

        Selector selector = Selector.open();
        Object attachment=new Object();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT,attachment);

        while (true){
            int number = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.stream().forEach(n->{
                if(n.isAcceptable()){
                    try {
                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) n.channel();
                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        socketChannel.configureBlocking(false);
                        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println(socketChannel.getRemoteAddress()+"已连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(n.isReadable()){
                    try {
                        SocketChannel socketChannel = (SocketChannel) n.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        while(true){
                            byteBuffer.clear();
                            int read = socketChannel.read(byteBuffer);
                            if(read <=0){
                                break;
                            }
                            byteBuffer.flip();
                            while(byteBuffer.hasRemaining()){
                                System.out.println(((char) byteBuffer.get()));
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            selectionKeys.clear();
        }
    }
}
