package com.froggengo.class6Nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest {

    public static void main(String[] args) throws IOException {

        FileOutputStream outputStream = new FileOutputStream("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt");
        FileChannel channel = outputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] bytes = "Hello world".getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byteBuffer.put(bytes[i]);
        }
        byteBuffer.flip(); //
        channel.write(byteBuffer); //new  FileInputStream调用write导致 java.nio.channels.NonWritableChannelException

        outputStream.close();
    }
}
