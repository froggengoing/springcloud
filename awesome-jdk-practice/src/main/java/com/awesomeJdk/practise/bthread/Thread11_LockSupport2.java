package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Thread11_LockSupport2 {
    /**
     * park()与unpark()事实上就是中断与恢复的现场
     * 本案例研究wait()能否通过unpark()唤醒
     * 因为wait()和park()底层好像是一一样的
     * 结论：不能通过unpark唤醒wait线程。
     * 分析：LockSupport.park()中还要设置
     */

    public static void main (String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("进入线程");
            synchronized (Thread11_LockSupport2.class) {
                try {
                    Thread11_LockSupport2.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("退出线程");
        });
        thread.start();
        TimeUnit.SECONDS.sleep(10);
        System.out.println("主线程");
        LockSupport.unpark(thread);

    }
}
