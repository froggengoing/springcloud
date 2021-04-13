package com.awesomeJdk.myNetty.exec.t1;

/**
 * @author froggengo@qq.com
 * @date 2021/2/21 0:35.
 */
public class ThreadInterrupt2 {

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable=()-> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000); // 延迟1秒
                    System.out.println(" 中断状态："+Thread.currentThread().isInterrupted());
                } catch (InterruptedException e) {
                    System.out.println(" 被中断："+e.getMessage());
                    System.out.println(" 异常后，中断状态："+Thread.currentThread().isInterrupted());
                    //interrupt();
                    //System.out.println(getName()+" 再次中断，状态："+isInterrupted());
                }
            }
            System.out.println("任务退出！");
        };
        Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(50);
        thread.interrupt();
        System.out.println("完成");
    }

}
