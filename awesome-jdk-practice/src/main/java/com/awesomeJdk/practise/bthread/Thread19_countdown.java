package com.awesomeJdk.practise.bthread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Thread19_countdown {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        Runnable runnable=()->{
            try {
                System.out.println("进入线程:"+Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(5);
                System.out.println("退出线程:"+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                latch.countDown();
            }
        };
        new Thread(runnable,"T1").start();
        new Thread(runnable,"T2").start();
        //当getState()为0时获得锁
        latch.await();
        System.out.println("主线程");
    }
}
