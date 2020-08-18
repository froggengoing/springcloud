package com.awesomeJdk.practise.athread;

import org.junit.Test;

public class Thread1_State {
    @Test
    public  void testThreadState() throws InterruptedException {
        System.out.println("线程状态： new -》runnable -》terminate");
        Thread thread = new Thread(() -> {
            System.out.println("   执行中的线程状态："+Thread.currentThread().getState().toString());
            System.out.println("   线程执行结束");
        });
        System.out.println("   执行前的线程状态："+thread.getState().toString());
        thread.start();
        Thread.sleep(1000);
        System.out.println("   执行结束的线程状态："+thread.getState().toString());
    }
    @Test
    public  void testThreadStateSecond() throws InterruptedException {
        System.out.println("线程状态： new -》runnable -》timed_waiting -》terminate");
        Thread thread = new Thread(() -> {
            System.out.println("   执行中的线程状态："+Thread.currentThread().getState().toString());
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("   线程执行结束");
        });
        System.out.println("   执行前的线程状态："+thread.getState().toString());
        thread.start();
        Thread.sleep(200);
        System.out.println("   执行中然后sleep()的线程状态："+thread.getState().toString());
        Thread.sleep(2000);
        System.out.println("   执行结束的线程状态："+thread.getState().toString());
    }
    @Test
    public  void testThreadStateThird() throws InterruptedException {
        System.out.println("线程状态： new -》runnable -》waiting -》runnable -》terminate");
        Thread1_State state = new Thread1_State();
        Thread thread = new Thread(() -> {
            System.out.println("   执行中的线程状态："+Thread.currentThread().getState().toString());
            try {
                synchronized (state){
                    state.wait();
                    System.out.println("   wait()被notify()的线程状态："+Thread.currentThread().getState().toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("   线程执行结束");
        });
        System.out.println("   执行前的线程状态："+thread.getState().toString());
        thread.start();
        Thread.sleep(200);
        System.out.println("   执行中然后wait()的线程状态："+thread.getState().toString());
        /*
        这里必须加synchronized，表明要拿到state的对象锁，否则报IllegalMonitorStateException
        当前的线程不是此对象监视器的所有者。也就是要在当前线程锁定对象，才能用锁定的对象此行这些方法，
        需要用到synchronized锁定对象, 锁定什么对象就用什么对象来执行notify(), notifyAll(),wait(), wait(long), wait(long, int)操作，
        否则就会报IllegalMonitorStateException异常。
         */
        synchronized (state){
            state.notify();
        }
        Thread.sleep(200);//没有这个，偶发会出现：执行结束的线程状态：BLOCKED
        System.out.println("   执行结束的线程状态："+thread.getState().toString());
    }

    @Test
    public  void testThreadStateFourth() throws InterruptedException {
        System.out.println("线程状态： new -》runnable -》blocked -》runnable -》terminate");
        Thread thread = new Thread(() -> {
            System.out.println("   执行中的线程状态："+Thread.currentThread().getState().toString());
            synchronized (Thread1_State.class){
                System.out.println("   执行中的线程状态："+Thread.currentThread().getState().toString());
            }
            System.out.println("   线程执行结束");
        });
        System.out.println("   执行前的线程状态："+thread.getState().toString());
        synchronized (Thread1_State.class){
            thread.start();
            Thread.sleep(200);
            System.out.println("   执行中要进入同步块的线程状态："+thread.getState().toString());
        }
        Thread.sleep(2000);
        System.out.println("   执行结束的线程状态："+thread.getState().toString());
    }
}
