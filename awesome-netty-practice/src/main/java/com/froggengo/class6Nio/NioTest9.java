package com.froggengo.class6Nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest9 {
    public static void main(String[] args) throws IOException {

        RandomAccessFile fileInputStream = new RandomAccessFile("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt","rw");
        FileChannel channel = fileInputStream.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE,0,5);
        map.put(0,(byte)'a');
        map.put(4, ((byte) 'b'));
        fileInputStream.close();
    }
}
