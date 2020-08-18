package com.froggengo.class6Nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class NioText13 {


    public static void main(String[] args) throws IOException {
        String path = "F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\niotext3.txt";
        FileInputStream fileInputStream = new FileInputStream(path);
        FileOutputStream fileOutputStream = new FileOutputStream("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\niotext13.txt");

        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileChannel outputStreamChannel = fileOutputStream.getChannel();
        MappedByteBuffer byteBuffer = inputStreamChannel.map(FileChannel.MapMode.READ_ONLY, 0, new File(path).length());

        Charset charset = Charset.forName("gbk");
/*        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder charsetEncoder = charset.newEncoder();*/
        CharBuffer decode = charset.decode(byteBuffer);
        ByteBuffer encode = charset.encode(decode);

        outputStreamChannel.write(encode);

        fileInputStream.close();
        fileOutputStream.close();
    }
}
