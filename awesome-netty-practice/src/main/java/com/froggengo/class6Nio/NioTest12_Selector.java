package com.froggengo.class6Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NioTest12_Selector {
    public static void main(String[] args) throws IOException {
        int[] port=new int[5];
        port[0]=5000;
        port[1]=5001;
        port[2]=5002;
        port[3]=5003;
        port[4]=5004;
        Selector selector = Selector.open();

        Arrays.stream(port).forEach(n-> {
            try {
                ServerSocketChannel socketChannel = ServerSocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.socket().bind(new InetSocketAddress(n));
                socketChannel.register(selector, SelectionKey.OP_ACCEPT);//返回selectionKey
                System.out.println("监听端口:"+n);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        while(true){
            int numbers = selector.select();
            System.out.println("准备就绪的channel数量："+numbers);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selectionKeys:"+selectionKeys);
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey n = iterator.next();
                if (n.isAcceptable()) {
                    ServerSocketChannel channel = ((ServerSocketChannel) n.channel());
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    iterator.remove();
                    System.out.println("获取客户端连接"+socketChannel);
                }else if (n.isReadable()){
                    SocketChannel channel = ((SocketChannel) n.channel());
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int byteRead=0;
                    while(true) {
                        byteBuffer.clear();
                        int read = channel.read(byteBuffer);
                        if (read <= 0) {
                            break;
                        }
                        byteBuffer.flip();
                        channel.write(byteBuffer);
                        byteRead += read;
                    }
                    System.out.println("读取"+byteRead+"来自于："+channel);
                    iterator.remove();
                }

            }
        }

    }
}
