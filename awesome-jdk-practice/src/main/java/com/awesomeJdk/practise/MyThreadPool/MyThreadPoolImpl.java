package com.awesomeJdk.practise.MyThreadPool;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolImpl extends Thread implements MyThreadPool{
    final int initialSize;
    final int maxSize;

    @Override
    public void execute(Runnable runnable) {
        if (this.isShutdown) {
            throw new RuntimeException("线程池已关闭");
        }
        this.myRunnableQueue.off(runnable);
    }

    @Override
    public void shutdown() {

    }



    final int coreSize;
    int activeCount;
    final MyThreadFactory myThreadFactory;
    final MyRunnableQueue myRunnableQueue;
    volatile boolean isShutdown =false;
    final Queue<MyInternelTask> threaQueue =new ArrayDeque<>();
    final static MyDenypolicy mydenypolicy = new MyDenypolicy.MyDiscardDenyPolicy();
    final long keepAliveTime;
    final TimeUnit timeUnit;

    public MyThreadPoolImpl(int initialSize, int maxSize, int coreSize,
                            MyThreadFactory myThreadFactory,int queueSize){
        this.initialSize = initialSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.myThreadFactory = myThreadFactory;
        this.myRunnableQueue = new MyLinkedRunnableQueue(queueSize,mydenypolicy,this);
        this.keepAliveTime = 1000;
        this.timeUnit =TimeUnit.MINUTES;
        this.init();
    }
    public MyThreadPoolImpl(int initialSize, int maxSize, int coreSize,
                            MyThreadFactory myThreadFactory,int queueSize,
                            long keepAliveTime, TimeUnit timeUnit) {
        this.initialSize = initialSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.myThreadFactory = myThreadFactory;
        this.myRunnableQueue = new MyLinkedRunnableQueue(queueSize,mydenypolicy,this);
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.init();
    }

    private void init() {
        start();
        for (int i = 0; i < initialSize; i++) {
            newThread();
        }
    }

    private void newThread() {
        MyInternelTask task = new MyInternelTask(myRunnableQueue);
        Thread thread = myThreadFactory.CreateThread(task);
        threaQueue.offer(task);
        this.activeCount++;
        thread.start();
    }

    @Override
    public void run() {
        super.run();
        //回收线程、扩容等工作
    }
    //#####################################

    @Override
    public int getInitialSize() {
        return initialSize;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public int getCoreSize() {
        return coreSize;
    }

    @Override
    public int getActiveCount() {
        return activeCount;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    public MyThreadFactory getMyThreadFactory() {
        return myThreadFactory;
    }

    public MyRunnableQueue getMyRunnableQueue() {
        return myRunnableQueue;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    public Queue<MyInternelTask> getThreaQueue() {
        return threaQueue;
    }

    public static MyDenypolicy getMydenypolicy() {
        return mydenypolicy;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }


}
