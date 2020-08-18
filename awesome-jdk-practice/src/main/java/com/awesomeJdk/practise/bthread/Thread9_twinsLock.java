package com.awesomeJdk.practise.bthread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Thread9_twinsLock implements Lock {
    class MyTwinsAqs extends AbstractQueuedSynchronizer{
        MyTwinsAqs(int count ){
            if(count <=0) throw new IllegalThreadStateException();
            setState(count);
        }

        /**
         * 这里实例中使用死循环，导致线程一直在运行。不知道为何这样设计。
         * 我这里修改为if
         * @param arg
         * @return
         */
        @Override
        protected int tryAcquireShared(int arg) {
            //while(true){
                System.out.println("    尝试获得锁"+Thread.currentThread().getName());
                int current = getState();
                int newCount = current - arg;
                if(newCount>=0 && compareAndSetState(current,newCount)){
                    return newCount;
                }
            //}
            return -1;
        }

        /**
         * 这里实例中使用死循环，导致线程一直在运行。不知道为何这样设计。
         * 我这里修改为if
         * @param arg
         * @return
         */
        @Override
        protected boolean tryReleaseShared(int arg) {
            //while(true){
                int current = getState();
                int newCount = current + arg;
                if(newCount<=2 && compareAndSetState(current,newCount))
                    return true;
            //}
            return false;
        }
        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }
    MyTwinsAqs aqs=new MyTwinsAqs(2);

    public MyTwinsAqs getAqs() {
        return aqs;
    }

    @Override
    public void lock() {
        aqs.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        aqs.acquireSharedInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return aqs.tryAcquireShared(1)>=0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return aqs.tryAcquireSharedNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        aqs.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return aqs.newCondition();
    }
}
