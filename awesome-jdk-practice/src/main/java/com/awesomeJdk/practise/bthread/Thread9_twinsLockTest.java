package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;

/**
 * 两个线程同时在运行
 */
public class Thread9_twinsLockTest {
    public static void main(String[] args) throws InterruptedException {
        Thread9_twinsLock lock = new Thread9_twinsLock();
        Runnable runnable = () -> {
            lock.lock();
            try {
                System.out.println("进入线程" + Thread.currentThread().getName());
                lock.getAqs().getQueuedThreads().forEach(n -> System.out.println("    等待队列中的线程：" + n.getName()+" ,线程状态:"+n.getState()));
                TimeUnit.SECONDS.sleep(20);
                System.out.println("退出线程" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
        new Thread(runnable,"t1").start();
        new Thread(runnable,"t2").start();
        new Thread(runnable,"t3").start();
        new Thread(runnable,"t4").start();
        Thread.sleep(100);
        System.out.println("    主线程");
        //getQueuedThreads()无输出
        //getSharedQueuedThreads()无 输出
        lock.getAqs().getQueuedThreads().forEach(n -> System.out.println("    等待队列中的线程：" + n.getName()));

    }
}
