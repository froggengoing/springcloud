package com.froggengo.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class GuavaTestMain {
    /**
     * 10个线程，执行10个任务（任务耗时5秒），限流规则只允许每秒执行2个
     * @param args
     */
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(2);// QPS
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable runnable = () -> {
            rateLimiter.acquire();
            System.out.println(Thread.currentThread().getId() + "=>" + LocalDateTime.now() + "=>" + "执行任务");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {

            }
            System.out.println(Thread.currentThread().getId() + "执行结束");
        };
        IntStream.range(0,10).forEach(n->executorService.execute(runnable));
        executorService.shutdown();
    }
}
