package com.froggengo.class6Nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest3 {

    public static void main(String[] args) throws IOException {

        FileInputStream inputStream = new FileInputStream("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt");
        FileOutputStream outputStream = new FileOutputStream("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\niotext3");

        FileChannel channel = inputStream.getChannel();
        FileChannel outputStreamChannel = outputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int read = channel.read(buffer);
        while(read != -1){
            buffer.flip();
            outputStreamChannel.write(buffer);
            //buffer.flip();
            buffer.clear();
            read=channel.read(buffer);
        }
        inputStream.close();
        outputStream.close();
    }
}
