package com.awesomeJdk.myNetty.exec.t1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author froggengo@qq.com
 * @date 2021/2/21 9:19.
 */
public class ThreadInterrupt3 {

    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean isStop=new AtomicBoolean(false);
        Runnable runnable=()->{
            String path = "D:\\[4]project\\springcloud\\awesome-jdk-practice\\summary\\hello.txt";
            try {
                FileOutputStream outputStream = new FileOutputStream(path);
                int count=0;
                while(true){
                    outputStream.write(count++);
                    if(isStop.get()){
                        break;
                    }
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread t1 = new Thread(runnable);
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        Thread.sleep(1000);
        System.out.println("完成");
    }
}
