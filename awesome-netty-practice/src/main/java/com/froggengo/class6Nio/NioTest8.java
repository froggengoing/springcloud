package com.froggengo.class6Nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Test;

public class NioTest8 {


    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        FileInputStream fileInputStream = new FileInputStream(
            "F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\nioText.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(
            "F:\\dev\\nettylearn\\netty-first\\src\\main\\java\\com\\froggengo\\class6Nio\\niotext3");

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);//与NioTest3唯一区别

        FileChannel inputStreamChannel = fileInputStream.getChannel();
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        while (true) {
            byteBuffer.clear();
            int read = inputStreamChannel.read(byteBuffer);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            outputStreamChannel.write(byteBuffer);
        }
        fileInputStream.close();
        fileOutputStream.close();

    }

//    @Test
//    public void test(){
//        Unsafe unsafe = null;
//        try {
//            unsafe = getUnsafe();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        System.out.println(unsafe.pageSize());
//
//    }

    @Test
    public void testaddr() throws NoSuchFieldException, IllegalAccessException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);//与NioTest3唯一区别
        Field address = Buffer.class.getDeclaredField("address");
        address.setAccessible(true);
        Long o = ((Long) address.get(byteBuffer));
        System.out.println("内存地址" + o);
    }
//    public Unsafe getUnsafe() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
//        Class<?> aClass = Class.forName("sun.misc.Unsafe");
//        Field theUnsafe = aClass.getDeclaredField("theUnsafe");
//        theUnsafe.setAccessible(true);
//        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
//        return unsafe;
//    }
}
