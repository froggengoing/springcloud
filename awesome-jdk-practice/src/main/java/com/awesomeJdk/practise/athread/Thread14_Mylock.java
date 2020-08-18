package com.awesomeJdk.practise.athread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.currentThread;

/**
 * 自定义实现类似 ReentranLock 的锁。
 * 原理是对调用 BooleanLock 的lock方法的线程，使用wait方法阻塞。
 */
public class Thread14_Mylock {
    public static void main(String[] args) {
        BooleanLock lock = new BooleanLock();
        Runnable runnable = () -> {
            try {
                lock.lock();
                System.out.println(currentThread().getName() + "正在工作");
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
        Runnable runnable2 = () -> {
            try {
                lock.lock(10_000);
                System.out.println(currentThread().getName() + "正在工作");
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
        new Thread(runnable,"t1").start();
        new Thread(runnable,"t2").start();
        new Thread(runnable,"t3").start();
        new Thread(runnable2,"t4").start();
    }
}
interface Lock
{
    void lock() throws InterruptedException;
    void lock(long mills) throws InterruptedException, TimeoutException;
    void unlock();
    List<Thread> getBlockThreads();
}
class BooleanLock implements  Lock{
    boolean locked = false;
    Thread currentThread;
    List<Thread> blockedList = new ArrayList<>();


    @Override
    public void lock() throws InterruptedException {
        synchronized (this){
            while(locked){
                try{
                    blockedList.add(currentThread());
                    this.wait();
                } catch (InterruptedException e) {
                    blockedList.remove(currentThread());
                    throw e;
                }

            }
            blockedList.remove(currentThread());
            this.locked = true;
            this.currentThread = currentThread();
        }
    }

    @Override
    public void lock(long mills) throws InterruptedException, TimeoutException {
        synchronized (this){
            if (mills<=0) {
                this.lock();
            }else{
                long remainMills = mills;
                long endMills = System.currentTimeMillis() + remainMills;
                while(locked){
                    if(remainMills<=0){
                       throw new TimeoutException("指定时间内无法获取锁："+mills);
                    }
                    //改进，如果正在wait的线程被中断，回到值blcoklist残留了线程
                    try{
                        if(!blockedList.contains(currentThread())){
                            blockedList.add(currentThread());
                            this.wait(remainMills);
                            remainMills = endMills - System.currentTimeMillis();
                        }
                    }catch (InterruptedException e){
                        blockedList.remove(currentThread());
                        throw e;
                    }

                }
                blockedList.remove(currentThread());
                this.locked = true;
                this.currentThread = currentThread();
            }
        }
    }

    @Override
    public void unlock() {
        synchronized (this){
            if(currentThread == currentThread()){
                this.locked=false;
                this.notifyAll();
            }
        }
    }

    @Override
    public List<Thread> getBlockThreads() {
        return blockedList;
    }
}
