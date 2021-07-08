package com.awesomeJdk.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author froggengo@qq.com
 * @date 2021/7/8 16:24.
 */
public class T20210708_1001_completablefuture {

    public static void main(String[] args) {
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            final Thread thread = Thread.currentThread();
            System.out.println(thread.getName());
            System.out.println("是否守护线程" + thread.isDaemon());
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "abc";
        });
        int i = 0;
        while (i < 200000) {
            i++;
        }
        System.out.println(i);
        future.whenComplete((str, ex) -> {
            System.out.println("结果：" + str);
        });
        //CompletableFuture是守护线程，所以主线程需要阻塞等待
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
