package com.awesomeJdk.practise.athread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Thread6_close {
    /**
     * 通过interrupt标志,判断是否退出运行
     * @throws InterruptedException
     */
    @Test
    public void testThreadClose () throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("开始工作，比如接收报文/心跳检查");
                while (!isInterrupted()) {
                    //do something
                }
                System.out.println("安全退出工作");
            }
        };
        thread.start();
        Thread.sleep(10_000);
        //TimeUnit.MINUTES.sleep(1);
        System.out.println("即将退出运行");
        thread.interrupt();
    }
    @Test
    public void testThreadClose2 () throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("开始工作，比如接收报文/心跳检查");
                while (!isInterrupted()) {
                    //do something
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        System.out.println("安全退出工作");
                        break;
                    }
                }
            }
        };
        thread.start();
        Thread.sleep(10_000);
        //TimeUnit.MINUTES.sleep(1);
        System.out.println("即将退出运行");
        thread.interrupt();
    }

    /**
     * 修改volatile变量，控制线程退出
     * 感觉示例不太正确
     */
    @Test
    public void testThreadClose3 () throws InterruptedException {
        ANewThread thread = new ANewThread();
        thread.start();
        Thread.sleep(10_000);
        System.out.println("即将退出运行");
        thread.close();
    }
    class ANewThread extends Thread{
        volatile boolean  closed=false;
        @Override
        public void run() {
            System.out.println("开始工作，比如接收报文/心跳检查");
            while (!closed && !isInterrupted()) {
            }
            System.out.println("安全退出工作");
        }
        public void close(){
            this.closed=true;
            this.interrupt();
        }
    }


}
