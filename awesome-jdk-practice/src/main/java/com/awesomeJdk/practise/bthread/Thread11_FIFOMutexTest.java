package com.awesomeJdk.practise.bthread;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 很神奇的一件事情，lock.unlock使用LockSupport.unpark(lock.getThread());居然不能生效。
 * 目前的结论是，park和unpark必须封装在同一个对象中才能生效。
 * 以上是错误的思想。
 * 一致忽略了unpark后会由于Thread11_FIFOMutex中locked值为false，而导致unpark后重新被阻塞。
 */
public class Thread11_FIFOMutexTest {

    public static void main(String[] args) throws InterruptedException {
        Thread11_FIFOMutex lock = new Thread11_FIFOMutex();
        Runnable runnable = () -> {
            lock.lock();
            System.out.println("进入线程:" + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("子线程直接unpark");
                System.out.println("等待线程:" + lock.getThread());
                /*LockSupport.unpark(lock.getThread());*/
                TimeUnit.SECONDS.sleep(10);
                System.out.println("退出线程");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //lock.setMyLock(); //一直以来忽略的问题，一直奇怪为什么unpark后线程没有继续往下跑。查了一天
                LockSupport.unpark(lock.getThread());
                //lock.unlock();
            }

        };
        Thread thread1 = new Thread(runnable, "t1");
        Thread thread2 = new Thread(runnable, "t2");
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread2.start();
        TimeUnit.SECONDS.sleep(2);
        System.out.println(thread2.getState());
        /*System.out.println("主线程,unpark被阻塞的线程");
        LockSupport.unpark(lock.getThread());*/
    }
}
