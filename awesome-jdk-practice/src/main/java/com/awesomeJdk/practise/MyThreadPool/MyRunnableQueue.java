package com.awesomeJdk.practise.MyThreadPool;

public interface MyRunnableQueue{
    void off(Runnable runnable);
    Runnable take() throws  InterruptedException;
    int size();
}
