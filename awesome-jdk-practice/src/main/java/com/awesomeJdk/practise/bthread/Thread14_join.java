package com.awesomeJdk.practise.bthread;

public class Thread14_join{
    /**
     *
     * join只会等待调用线程结束，而不会等调用线程内新创建线程结束。
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("进入线程:" + Thread.currentThread().getName());
            new Thread(() -> {
                try {
                    Thread thread1 = Thread.currentThread();
                    System.out.println("进入线程:" + thread1.getName()+",线程是否为守护："+thread1.isDaemon());
                    Thread.currentThread().sleep(10_000);
                    System.out.println("线程休眠结束:" + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            try {
                Thread.currentThread().sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程休眠结束:" + Thread.currentThread().getName());
        });
        thread.start();
        thread.join();
        System.out.println("测试主线程的打印位置");
    }
}
