package com.awesomeJdk.myNetty.exec.v1;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * 版本1：实现单线程的复用
 * 即一个线程中不断的执行任务，而不是每个任务使用单独的线程，使用完就丢弃线程
 */
public class SerialExecutor1 implements Executor {

    final Queue<Runnable> tasks = new ArrayDeque<>();
    private final Worker worker;
    private final Thread thread;
    boolean started = false;

    public SerialExecutor1() {
        this.worker = new Worker();
        this.thread = new Thread(worker);
    }

    @Override
    public synchronized void execute(Runnable r) {
        if (started) {
            this.worker.addTask(r);
        } else {
            this.started = true;
            this.worker.addTask(r);
            this.thread.start();
        }

    }

    class Worker implements Runnable {

        void addTask(Runnable runnable) {
            tasks.add(runnable);
        }

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
