package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;

public class Thread8_lockMain {
    public static void main(String[] args) throws InterruptedException {
        Thread8_lock lock = new Thread8_lock();
        Runnable runnable = () -> {
            lock.lock();
            try {
                System.out.println("进入线程" + Thread.currentThread().getName());
                lock.getAqs().getQueuedThreads().forEach(n -> System.out.println("   等待队列中的线程：" + n.getName()));
                TimeUnit.SECONDS.sleep(10);
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
        Thread.sleep(100);
        lock.getAqs().getQueuedThreads().forEach(n-> System.out.println("   等待队列中的线程："+n.getName()));
    }
}
