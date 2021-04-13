package com.awesomeJdk.myNetty.exec.t1;

/**
 * @author froggengo@qq.com
 * @date 2021/2/20 9:25.
 */
public class ThreadInterrupt1 {

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            Thread t1 = Thread.currentThread();
            int count = 0;
            while (!t1.isInterrupted()) {
                System.out.println(count++);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(1);
        thread.interrupt();
        System.out.println("完成");
    }
}
