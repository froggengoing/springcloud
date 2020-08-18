package com.awesomeJdk.practise.bthread;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Thread5_ReentrantLock {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();//公平锁
        System.out.println("nihao");
        lock.unlock();
        ReentrantLock unfairlock = new ReentrantLock(false);
        //tryLock()// 尝试获取锁,立即返回获取结果 轮询锁
        //tryLock(long timeout, TimeUnit unit)//尝试获取锁,最多等待 timeout 时长 超时锁
        lock.tryLock();
        System.out.println("nihao");
        lock.unlock();
        Thread.currentThread().join();
    }
}

