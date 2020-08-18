package com.awesomeJdk.practise.MyThreadPool;

public interface MyThreadFactory{
    Thread CreateThread(Runnable runnable);
}
