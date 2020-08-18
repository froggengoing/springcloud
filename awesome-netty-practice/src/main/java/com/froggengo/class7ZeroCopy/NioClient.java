package com.froggengo.class7ZeroCopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NioClient {
    /**
     * 遗留问题，传输与接收的字节数不一致
     * 最后未填满transferTo中lenth比实际文件长度长怎么处理？
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        boolean isConnect = socketChannel.connect(new InetSocketAddress(8888));
        String path="F:\\资料\\【1】电子书\\让Oracle跑得更快：Oracle 10g性能分析与优化思路.pdf";
        socketChannel.configureBlocking(false);
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel = fileInputStream.getChannel();
        long bef = System.currentTimeMillis();
        System.out.println("文件总字节："+file.length());
        // magic number for Windows, (64Mb - 32Kb)
        int maxCount = (64 * 1024 * 1024) - (32 * 1024);
        int pos=0;
        while(pos < channel.size()){

            pos += channel.transferTo(0, maxCount, socketChannel);//零拷贝：file的内存地址直接传至socket中
        }
        System.out.println("传输总字节:"+pos);
        System.out.println(System.currentTimeMillis()-bef);
        //46043068  46043136
        channel.close();
        socketChannel.close();
    }
}
