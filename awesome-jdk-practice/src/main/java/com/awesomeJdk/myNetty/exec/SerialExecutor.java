package com.awesomeJdk.myNetty.exec;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SerialExecutor implements Executor {

    final Queue<Runnable> tasks = new ArrayDeque<>();
    final Executor executor;
    Runnable active;

    public SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 最简单的实现
     */
    public static void main(String[] args) {
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
        SerialExecutor serialExecutor = new SerialExecutor(executor);
        serialExecutor.execute(() -> {
            System.out.println(1);
        });
        serialExecutor.execute(() -> {
            System.out.println(2);
        });
        serialExecutor.execute(() -> {
            System.out.println(3);
        });
    }

    @Override
    public synchronized void execute(Runnable r) {
        tasks.add(() -> {
            try {
                r.run();
            } finally {
                scheduleNext();
            }
        });
        if (active == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            executor.execute(active);
        }
    }
}
