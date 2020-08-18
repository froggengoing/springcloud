package com.awesomeJdk.practise.bthread;

import java.util.concurrent.locks.LockSupport;

public class Thread11_park{
    public static void main(String[] args) throws InterruptedException {
        Thread11_Mypark mypark = new Thread11_Mypark();
        Thread thread1 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            System.out.println("进入线程：" + thread.getName());
            System.out.println("    执行park：");
            mypark.park();
            System.out.println("    其他线程执行unpark()后");
        });
/*        Thread thread2 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            System.out.println("进入线程：" + thread.getName());
            System.out.println("    执行park：");
            mypark.park();
            System.out.println("    其他线程执行unpark()后");
        });*/
        thread1.start();
        //thread2.start();
        Thread.sleep(1_000);
        System.out.println(thread1.getState());
        LockSupport.unpark(thread1);
    }


}
