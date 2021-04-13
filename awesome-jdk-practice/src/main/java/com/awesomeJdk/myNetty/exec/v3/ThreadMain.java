package com.awesomeJdk.myNetty.exec.v3;

/**
 * @author froggengo@qq.com
 * @date 2021/2/20 8:42.
 */
public class ThreadMain {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            int count = 0;
            Thread thread = Thread.currentThread();
            while (!thread.isInterrupted()) {
                System.out.println(count);
                try {
                    synchronized (ThreadMain.class) {
                        ThreadMain.class.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("被中断");
                }
            }
        });
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        Thread.sleep(1000);
        System.out.println("完成");
    }

}
