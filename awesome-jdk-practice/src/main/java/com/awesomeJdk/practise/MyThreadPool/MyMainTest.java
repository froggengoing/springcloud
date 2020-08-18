package com.awesomeJdk.practise.MyThreadPool;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MyMainTest {

    public static void main(String[] args) {
        MynewThreadFactory threadFactory = new MynewThreadFactory();
        MyThreadPoolImpl threadPool = new MyThreadPoolImpl(2, 10, 5,
                threadFactory, 1000);
        IntStream.range(1,10).asLongStream().forEach(m->{
            threadPool.execute(()->{
                try {
                    System.out.println(m);
                    System.out.println(Thread.currentThread().getName()+"正在工作");
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });

    }
}
