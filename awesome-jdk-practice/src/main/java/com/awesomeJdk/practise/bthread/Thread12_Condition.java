package com.awesomeJdk.practise.bthread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Thread12_Condition{
    ReentrantLock lock=new ReentrantLock();
    Condition condition = lock.newCondition();
    public void conditionWait(){
        lock.lock();
        try{
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void conditionNotify(){
        lock.lock();
        try{
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
