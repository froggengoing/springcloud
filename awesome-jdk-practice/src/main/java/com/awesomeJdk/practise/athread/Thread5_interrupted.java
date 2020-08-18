package com.awesomeJdk.practise.athread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Thread5_interrupted {
    /**
     * interrupt()方法打断 blocked/waiting/timed_waiting 的线程状态
     * sleep/wait/join方法会捕捉InterruptedException异常。
     * @throws InterruptedException
     */
    @Test
    public void testInterrupt () throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("我在休眠，但被打断了");
            }
        });
        thread.start();
        Thread.sleep(100);
        System.out.println("准备打断线程休眠");
        thread.interrupt();
        Thread.sleep(100);
        System.out.println("结束");
    }

    /**
     * 对线程执行interrupt操作，会使线程保存一个interrupt flag
     * 但是如果如果线程正在执行可中断方法，即抛出InterruptedException异常，则标志会被清除
     * 本例子则中断标志会保留
     * @throws InterruptedException
     */
    @Test
    public  void test() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true){

            }
        });
        thread.start();
        Thread.sleep(100);
        System.out.println("准备打断线程休眠，线程状态isInterrupted： "+thread.isInterrupted());
        thread.interrupt();
        Thread.sleep(100);
        System.out.println("结束，线程状态isInterrupted："+thread.isInterrupted());
    }

    /**
     * 与例子2的区别是，interrupted()方法会获取中断标志并清除状态
     * @throws InterruptedException
     */
    @Test
    public  void testInterruptBoolean() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true){
                System.out.println("当前线程中断状态，并清算中断标识 "+Thread.currentThread().interrupted());
            }
        });
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(100);
        System.out.println("准备打断线程休眠，线程状态isInterrupted： "+thread.isInterrupted());
        thread.interrupt();
        Thread.sleep(100);
        System.out.println("结束，线程状态isInterrupted："+thread.isInterrupted());
    }
}
