package com.awesomeJdk.practise.athread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Thread11_threadGroup {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1");
        System.out.println("主线程threadGroup："+Thread.currentThread().getName());
        System.out.println("默认threadGroup："+t1.getThreadGroup().getName());
        ThreadGroup tg1 = new ThreadGroup("TG1");
        Thread thread = new Thread(tg1,()->{},"t2");
        System.out.println("指定threadGroup："+thread.getThreadGroup().getName());
    }

    @Test
    public void test (){
        ThreadGroup tg = new ThreadGroup("tg1");
        Thread t1 = new Thread(tg,()->{
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1");
        Thread t2 = new Thread(tg,()->{
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2");
        t1.start();
        t2.start();
        Thread[] tga=new Thread[tg.activeCount()];
        int enumerate = tg.enumerate(tga);
        for (Thread thread : tga) {
            System.out.println("新建线程组:"+thread.getName());
        }
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        //threadGroup.activeCount()返回所active的线程，包括子线程组的线程
        Thread[] tgm=new Thread[threadGroup.activeCount()];
        //是否递归获取子线程组的线程
        //因为activeCount返回是包含了子线程组的活动线程数的
        //设置recurse为false不递归获取，导致数组部分为null
        int enumerate1 = threadGroup.enumerate(tgm,false);
        for (Thread thread : tgm) {
            if(thread!=null){
                System.out.println("主线程组："+thread.getName());
            }
        }


    }

}
