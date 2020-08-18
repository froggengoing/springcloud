package com.awesomeJdk.practise.bthread;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Thread10_ReadWriteLock {
    public static void main(String[] args) {
        HashMap<String,String> map=new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        Runnable readRun=()->{
            readLock.lock();
            String name = Thread.currentThread().getName();
            try{
                System.out.println("获取读锁"+ name);
                Thread.currentThread().sleep(200);
                map.forEach((k,v)->System.out.println("    MAP:"+k+" : "+v));
                System.out.println("    当前线程读锁次数readHold："+lock.getReadHoldCount());
                System.out.println("    持有读锁readLock："+lock.getReadLockCount());
                System.out.println("    持有写锁writeHold："+lock.getWriteHoldCount());
                System.out.println("    等待队列长度："+lock.getQueueLength());

                TimeUnit.SECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        };
        Runnable writeRun=()->{
            writeLock.lock();
            String name = Thread.currentThread().getName();
            try{
                System.out.println("获取写锁"+ name);
                map.put(name, LocalDateTime.now().toString());
                Thread.currentThread().sleep(200);
                System.out.println("    当前线程读锁次数readHold："+lock.getReadHoldCount());
                System.out.println("    持有读锁readLock："+lock.getReadLockCount());
                System.out.println("    持有写锁writeHold："+lock.getWriteHoldCount());
                System.out.println("    等待队列长度："+lock.getQueueLength());
                TimeUnit.SECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        };
        new Thread(writeRun,"T1").start();
        new Thread(writeRun,"T2").start();
        new Thread(readRun,"T3").start();
        new Thread(readRun,"T4").start();
        new Thread(readRun,"T5").start();


    }

}
