package com.awesomeJdk.myNetty.exec.v2;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现：核心线程数coreSize、最大线程数：maximumPoolSize、阻塞队列
 */
public class SerialExecutor2 implements Executor {

    final int taskMaxSize;
    //    volatile 保证线程安全
    volatile int corePoolSize;
    volatile int maximumPoolSize;
    //涉及符合操作，不能使用volatile
    AtomicInteger workerCount = new AtomicInteger();
    //阻塞队列
    LinkedBlockingDeque<Runnable> tasks;

    public SerialExecutor2(int corePoolSize, int maximumPoolSize, int taskMaxSize) {
        this.corePoolSize    = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.taskMaxSize     = taskMaxSize;
        this.tasks           = new LinkedBlockingDeque<>(taskMaxSize);
    }

    @Override
    public void execute(Runnable command) {
        if (workerCount.get() < corePoolSize) {
            addWorker(command);
        } else if (!tasks.add(command)) {//队列满了添加不成功
            if (workerCount.get() < maximumPoolSize) {
                addWorker(command);
            } else {
                //reject
            }
        }
    }

    private boolean addWorker(Runnable command) {
        //判断是否为空，空则不添加线程，只添加至任务
        //不为空则添加线程
        tasks.add(command);
        if (tasks.isEmpty() && workerCount.get() > 0) {
            return false;
        }
        new Thread(new Worker(), "Test").start();
        workerCount.incrementAndGet();
        return true;
    }

    class Worker implements Runnable {

        @Override
        public void run() {
            Runnable active;
            while (true) {
                if ((active = tasks.poll()) != null) {
                    active.run();
                }
            }
        }
    }
}
