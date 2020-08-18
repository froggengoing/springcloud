package com.awesomeJdk.practise.bthread;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Thread11_LockSupport {

    public static void main(String[] args) throws InterruptedException, Exception {
        Thread thread1 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            System.out.println("进入线程：" + thread.getName());
            System.out.println("    执行park：");
            myPark();
            System.out.println("    其他线程执行unpark()后");
        });
        thread1.start();
        Thread.sleep(2_000);
        System.out.println("    线程"+thread1.getName()+", 线程状态"+thread1.getState());
        Thread.sleep(10_000);
        Field parkBlocker = Thread.class.getDeclaredField("parkBlocker");
        parkBlocker.setAccessible(true);
        System.out.println("parkBlocker:"+parkBlocker.get(thread1));
        LockSupport.unpark(thread1);
    }

    /**
     * 对reentrantLock debug测试，追踪lock()源码发现在调用LockSupport.park("我是blocker");后任然继续执行
     * 所以疑惑调用park()后到底是否阻塞。
     */
    private static void myPark(){
        System.out.println("准备park");
        LockSupport.park("我是blocker");
        System.out.println("park之后");
    }

}
