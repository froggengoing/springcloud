package com.awesomeJdk.practise.athread.test;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class TestThread  {

    @Test
    public  void test1(String[] args) {
        Thread t;
        while (true) (new ThreadCount()).start();
    }
}
class ThreadCount extends Thread{
    private static final AtomicInteger count = new AtomicInteger();
    @Override
    public void run() {
        System.out.println(count.incrementAndGet());
        while (true)
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                break;
            }
    }
}