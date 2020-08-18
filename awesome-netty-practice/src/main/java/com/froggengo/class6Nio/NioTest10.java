package com.froggengo.class6Nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class NioTest10 {

    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt","rw");
        FileChannel channel = randomAccessFile.getChannel();

        FileLock lock = channel.lock(0, 5, true);

        System.out.println(lock.isShared());
        System.out.println(lock.isValid());

        lock.release();
        randomAccessFile.close();

    }
}
