package com.awesomeJdk.practise.athread;

public class Thread9_deadlock {
    /**
     * 线程1持有p1锁，等待p2锁
     * 线程2持有p2锁，等待p1锁
     * 导致死锁
     * 诊断：参考Thread7
     * @param args
     */
    public static void main(String[] args) {
        DeadLock deadLock = new DeadLock();
        new Thread(deadLock::read,"t1").start();
        new Thread(deadLock::write,"t2").start();
    }
}
class DeadLock{
    private Object p1=new Object();
    private Object p2=new Object();

    public void write()  {
        synchronized (p1){
            System.out.println("执行写操作，已对p1加锁,等待p2锁");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (p2){
                System.out.println("成功获得p1/p2做，执行操作");
            }
        }
    }
    public void read() {
        synchronized (p2){
            System.out.println("执行写操作，已对p2加锁,等待p1锁");
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (p1){
                System.out.println("成功获得p1/p2做，执行操作");
            }
        }
    }
}
