package com.awesomeJdk.myNetty.exec.t1;

import java.awt.font.TextHitInfo;

/**
 * @author froggengo@qq.com
 * @date 2021/2/21 9:49.
 */
public class ThreadInterrupt4 {

    public static void main(String[] args) {
        Runnable runnable=()->{
            Thread t = Thread.currentThread();
            t.interrupt();
            System.out.println("继续执行");
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
