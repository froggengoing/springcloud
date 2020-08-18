package com.awesomeJdk.practise.MyThreadPool;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public interface MyThreadPool {
    void execute(Runnable runnable);
    void shutdown();
    int getInitialSize();
    int getMaxSize();
    int getCoreSize();
    int getActiveCount();
    int getQueueSize();
    boolean isShutdown();
}
