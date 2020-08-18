package com.awesomeJdk.practise.bthread;

import java.sql.Timestamp;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 类似于twinsLock，控制能同时执行的线程数量
 */
public class Thread15_semaphore {
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(2);
        Runnable runnable=()->{
            try {
                semaphore.acquire();
                System.out.println("线程获得锁："+Thread.currentThread().getName());
                System.out.println("    等待队列的长度"+semaphore.getQueueLength());
                TimeUnit.SECONDS.sleep(10);
                System.out.println("    线程即将退出："+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        };
        new Thread(runnable,"t1").start();
        new Thread(runnable,"t2").start();
        new Thread(runnable,"t3").start();
        new Thread(runnable,"t4").start();

    }
}
