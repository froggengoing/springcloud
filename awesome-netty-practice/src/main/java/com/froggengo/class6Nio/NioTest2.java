package com.froggengo.class6Nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest2 {

    public static void main(String[] args) throws IOException {
        FileInputStream inputStream = new FileInputStream("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt");
        FileChannel channel = inputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);

        byteBuffer.flip();
/*        for (int i = 0; i < byteBuffer.array().length; i++) {
            System.out.println(((char) byteBuffer.array()[i]));
        }*/
        while (byteBuffer.hasRemaining()){
            System.out.println(((char) byteBuffer.get()));
        }
        inputStream.close();
    }

}
