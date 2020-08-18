package com.froggengo.class7ZeroCopy;

import javax.xml.ws.Endpoint;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class OldClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(8888));
        OutputStream outputStream = socket.getOutputStream();
        String path="F:\\资料\\【1】电子书\\让Oracle跑得更快：Oracle 10g性能分析与优化思路.pdf";
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] buf= new byte[1024];
        long bef = System.currentTimeMillis();
        int total=0;
        System.out.println("开始读取");
        while (true){
            int read = fileInputStream.read(buf);
            if(read <=0){
                break;
            }
            total +=read;
            outputStream.write(buf);
        }
        fileInputStream.close();
        socket.close();
        System.out.println("");
        System.out.println("总计："+total+"，旧io用时："+(System.currentTimeMillis()-bef));
        //1800 1500  1643
    }
}
