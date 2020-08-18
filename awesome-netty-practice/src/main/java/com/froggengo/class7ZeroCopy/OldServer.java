package com.froggengo.class7ZeroCopy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8888));

        int total=0;
        while (true){
            byte[] buf=new byte[1024];
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            while (true){
                // int read = inputStream.read(buf,0,inputStream.available()); //导致无法读取
                int read = inputStream.read(buf);
                if (read<=0) {
                    break;
                }
                total+=read;
            }
            System.out.println("总计："+ total);
        }
    }
}
