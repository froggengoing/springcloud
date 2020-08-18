package com.awesomeJdk.practise.bthread;

import java.time.LocalDateTime;

/**
 * notify线程执行notifyAll()线程后，wait线程不会马上唤醒。
 * 而是需要notify线程释放锁后，才能被唤醒。
 */
public class Thread4_notify {
    static boolean flag=true;
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            synchronized (Thread4_notify.class){
                while(flag){
                    System.out.println("1、wait线程-当前flag为"+flag +", 时间："+ LocalDateTime.now());
                    try {
                        Thread4_notify.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("4、wait线程-当前flag为"+flag +", 时间："+ LocalDateTime.now());
            }
        },"t1").start();
        Thread.sleep(5_000);
        new Thread(()->{
            synchronized (Thread4_notify.class){
                System.out.println("2、notify线程-当前flag为"+flag +", 时间："+ LocalDateTime.now());
                Thread4_notify.class.notifyAll();
                flag=false;
                try {
                    Thread.currentThread().sleep(5_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (Thread4_notify.class){
                    System.out.println("3、notify线程-当前flag为"+flag +", 时间："+ LocalDateTime.now());
                    try {
                        Thread.currentThread().sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        },"t2").start();

    }
}
