package com.awesomeJdk.practise.bthread;



import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Thread8_lock implements Lock {
    class MyAqs extends AbstractQueuedSynchronizer{

        @Override
        protected boolean isHeldExclusively() {
            return getState()==1;
        }
        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if(getState() ==0) {
                throw new IllegalMonitorStateException();
            }
            if(getExclusiveOwnerThread() !=Thread.currentThread()) {
                throw  new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        Condition newCondition(){
            return new ConditionObject();
        }
    }

    MyAqs aqs=new MyAqs();
    public MyAqs getAqs(){
        return aqs;
    }
    @Override
    public void lock() {
        aqs.acquire(1);
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {
        aqs.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return aqs.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return aqs.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        aqs.release(1);
    }

    @Override
    public Condition newCondition() {
        return aqs.newCondition();
    }

}
