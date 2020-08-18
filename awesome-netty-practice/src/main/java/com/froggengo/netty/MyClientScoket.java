package com.froggengo.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyClientScoket {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(8080));
        while (true){
            int num = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.stream().forEach(n->{
                    if(n.isConnectable()){
                        SocketChannel channel = (SocketChannel)n.channel();
                        try {
                            channel.finishConnect();
                            ScheduledExecutorService executorService1 = Executors.newScheduledThreadPool(1);
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            executorService1.scheduleAtFixedRate(()->{
                                buffer.clear();
                                buffer.put("hello world".getBytes());
                                buffer.flip();
                                try {
                                    channel.write(buffer);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            },0,10, TimeUnit.SECONDS);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            });
        }
    }
}
