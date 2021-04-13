package com.awesomeJdk.myNetty.exec.v2;


import com.awesomeJdk.myNetty.exec.v1.SerialExecutor1;
import java.util.concurrent.TimeUnit;

public class SerialExecutor2Main {

    public static void main(String[] args) {
        SerialExecutor2 serialExecutor = new SerialExecutor2(2,5,10);
        serialExecutor.execute(()->{
            try {
                TimeUnit.MINUTES.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        });
        serialExecutor.execute(()->{
            try {
                TimeUnit.MINUTES.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(2);
        });
        serialExecutor.execute(()->{
            try {
                TimeUnit.MINUTES.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(3);
        });
    }
}
