package com.awesomeJdk.practise.bthread;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通过Mbean获取JVM线程的信息，jconsole其实也是通过Mbean获取信息的
 */
public class Thread1_Default {
    public static void main(String[] args) {
        AtomicLong atomicLong = new AtomicLong(1);
        new Thread(()->{
            synchronized (Thread1_Default.class){
                atomicLong.getAndIncrement();
                try {
                    TimeUnit.MINUTES.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1").start();

        new Thread(()->{
            synchronized (Thread1_Default.class){
                atomicLong.getAndIncrement();
                try {
                    TimeUnit.MINUTES.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t2").start();
        new Thread(()->{
            synchronized (Thread1_Default.class){
                atomicLong.getAndIncrement();
                try {
                    TimeUnit.MINUTES.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t3").start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);
        int count=1;
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("序号["+(count++) +"] 线程名称: "+threadInfo.getThreadName());
            System.out.println("       锁名称："+ threadInfo.getLockName());
            MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
            for (MonitorInfo monitor : lockedMonitors) {
                System.out.println("        monitor"+monitor.getLockedStackFrame().getFileName());
            }
            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            System.out.println("       栈长度"+stackTrace.length);
            System.out.println(".......等待锁时长"+threadInfo.getWaitedTime());
            System.out.println(".......锁拥有者"+threadInfo.getLockOwnerName());
            System.out.println("       阻塞计数"+threadInfo.getBlockedCount());
            System.out.println("       阻塞时长"+threadInfo.getBlockedTime());
            System.out.println("       线程状态"+threadInfo.getThreadState());
        }

    }
}
