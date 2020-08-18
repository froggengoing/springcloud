package com.froggengo.flink;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerSocketMain {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        InetSocketAddress address = new InetSocketAddress(9999);
        serverSocket.bind(address);
        System.out.println("等待连接");
        Socket socket = serverSocket.accept();
        if(socket !=null){
            System.out.println(socket.getInetAddress()+"已连接");
            OutputStream out = socket.getOutputStream();
            InputStream in ;
            while((in = System.in) !=null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String str = reader.readLine()+"\n";
                out.write(str.getBytes());
                out.flush();
                System.out.println("发送消息："+str);
            }
            in.close();
            out.close();
        }

        socket.close();
    }
}
