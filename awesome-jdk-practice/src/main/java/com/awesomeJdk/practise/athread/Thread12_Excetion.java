package com.awesomeJdk.practise.athread;

public class Thread12_Excetion {
    /**
     * 设置unchecked异常的处理器。
     * @see ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
     * @param args
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t,e)->{
            System.out.println(e.getMessage());
            System.out.println(Thread.currentThread().getName()+"线程抛出异常。");
        });
        new Thread(()->{
            System.out.println("线程正在工作："+Thread.currentThread().getName());
            System.out.println(1/0);
        },"t1").start();
    }
}
