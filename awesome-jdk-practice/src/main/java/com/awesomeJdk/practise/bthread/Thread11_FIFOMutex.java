package com.awesomeJdk.practise.bthread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class Thread11_FIFOMutex {
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue waiters
            = new ConcurrentLinkedQueue();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // Block while not first in queue or cannot acquire lock
        while (waiters.peek() != current ||
                !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
            if (Thread.interrupted()) // ignore interrupts while waiting
                wasInterrupted = true;
        }

        waiters.remove();
        if (wasInterrupted)          // reassert interrupt status on exit
            current.interrupt();
    }

    public void unlock() {
        locked.set(false);
        LockSupport.unpark((Thread) waiters.peek());
    }
    public Thread getThread(){
        return (Thread) waiters.peek();
    }
    public void setMyLock(){
        locked.set(false);
    }

}
