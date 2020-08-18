package com.awesomeJdk.practise.athread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Thread7_syncronized {
    /**
     * method1/2/3均为对象锁，所以只有一个线程能获得锁。
     * 期望：其中一个线程处于TIMED_WAITING、其余两个线程处于BLOCKED
     * 命令：jps -l查询pid值
     * 命令：jstack pid查看线程状态
     * @param args
     */
    public static void main(String[] args) {
        NewThread7 newThread7 = new NewThread7();
        new Thread(newThread7::method1, "t1").start();
        new Thread(newThread7::method2, "t2").start();
        new Thread(newThread7::method3, "t3").start();
    }
}
class NewThread7{

    /**
     * 方法上加synchronized关键字，实际上是对this对象加锁。
     */
    public  synchronized void method1(){
        System.out.println(Thread.currentThread().getName());
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public synchronized void method2(){
        System.out.println(Thread.currentThread().getName());
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void method3(){
        System.out.println(Thread.currentThread().getName());
        synchronized (this){
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

