package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;

public class Thread18_join {
    /**
     * join等待线程结束，原理是对thread调用wait(0)等待，当thread结束后再jvm内部会调用noftifyAll()
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->{
            try {
                System.out.println("进入线程:"+Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(10);
                System.out.println("退出线程:"+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
        Thread.sleep(2_000);
        System.out.println("主线程");
//        进入线程:Thread-0
//        退出线程:Thread-0
//        主线程
    }
}
