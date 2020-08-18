package com.awesomeJdk.practise.bthread;

import java.lang.reflect.Field;
import java.util.concurrent.locks.LockSupport;

public class Thread11_LockSupport4{
    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        Thread thread1 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("进入线程：" + thread.getName());
            System.out.println("    执行park：");
            LockSupport.park("我是blocker");
            System.out.println("    由于其他线程先调用updark，当前线程调用park也么没有阻塞");
        });
        thread1.start();
        LockSupport.unpark(thread1);
        System.out.println("    线程"+thread1.getName()+", 先调用unpark");
        Thread.sleep(4_000);
        System.out.println("    线程"+thread1.getName()+", 线程状态"+thread1.getState());

    }
}

