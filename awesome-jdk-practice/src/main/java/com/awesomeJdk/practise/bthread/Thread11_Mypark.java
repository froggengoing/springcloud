package com.awesomeJdk.practise.bthread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

public class Thread11_Mypark {

    private final Queue waiters
            = new ConcurrentLinkedQueue();
    public  void park(){
        LockSupport.park(this);
    }
    public void add(){
        Thread current = Thread.currentThread();
        waiters.add(current);
    }
}
