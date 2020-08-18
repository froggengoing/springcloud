package com.awesomeJdk.practise.MyThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

public class MynewThreadFactory implements MyThreadFactory{
    private static final String PRE_FIX="mythread";
    private AtomicInteger threadCount=new AtomicInteger(1);
    @Override
    public Thread CreateThread(Runnable runnable){
        return new Thread(runnable,PRE_FIX+"-"+threadCount.incrementAndGet());
    }
}
