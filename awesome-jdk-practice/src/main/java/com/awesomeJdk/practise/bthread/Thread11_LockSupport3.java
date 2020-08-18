package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一直非常疑惑为什么LockSupport.unpark(thread2);不能释放线程资源，这样T1和T2能同时执行。
 * LockSupport.unpark(thread2);只是唤醒线程，线程会再次由于tryAcquire失败而导致再次调用park而进入wait状态。
 * unlock除了唤醒线程，还归还了资源（state的值）
 */
public class Thread11_LockSupport3 {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Runnable runnable = () -> {
            lock.lock();
            System.out.println("进入线程");
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println("退出线程");
        };
        Thread thread1 = new Thread(runnable,"t1");
        Thread thread2 = new Thread(runnable,"t2");
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread2.start();
        TimeUnit.SECONDS.sleep(10);
        System.out.println(thread2.getState());
        System.out.println("主线程,unpark被阻塞的线程");
        //线程会lock()中由于调用park()而处于wait状态，在unpark会唤醒线程后
        // 会再次由于tryacquire失败而导致再次调用park而进入wait状态。
        LockSupport.unpark(thread2);

    }


}
