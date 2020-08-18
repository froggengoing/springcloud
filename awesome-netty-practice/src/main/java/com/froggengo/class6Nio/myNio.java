package com.froggengo.class6Nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class myNio {
    public static void main(String[] args) throws IOException {
        SelectorProvider selectorProvider = SelectorProvider.provider();
        ServerSocketChannel serverSocketChannel = selectorProvider.openServerSocketChannel();

    }
}
