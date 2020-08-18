package com.awesomeJdk.practise.athread;


import java.util.concurrent.TimeUnit;

public class Thread8_syncronized2 {
    /**
     * class monitor.
     * 对class进行加锁，期望1/2/3只有一个能获得锁
     * 4由于是对象锁不会被阻塞.
     * 结果："t1"、"t4"：TIMED_WAITING
     *"t2"、"t3" ：BLOCKED
     * @param args
     */
    public static void main(String[] args) {
        NewThread8 thread8 = new NewThread8();
        new Thread(NewThread8::method1, "t1").start();
        new Thread(NewThread8::method2, "t2").start();
        new Thread(thread8::method3, "t3").start();
        new Thread(thread8::method4, "t4").start();
    }


}
class NewThread8{
    /**
     * 方法上加synchronized关键字，实际上是对this对象加锁。
     */
    public  synchronized static void method1(){
        System.out.println(Thread.currentThread().getName());
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public synchronized static void method2(){
        System.out.println(Thread.currentThread().getName());
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void method3(){
        synchronized (NewThread8.class){
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void method4(){
        System.out.println(Thread.currentThread().getName());
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}