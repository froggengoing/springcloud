package com.froggengo.zookeeper.curator.self;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

//@SpringBootTest(classes = myCuratorExample.class)
@RunWith(SpringRunner.class)
public class MyLockTest {
    @Autowired
    CuratorFramework client;

    @Test
    public void test() throws InterruptedException {
        InterProcessMutex processMutex = new InterProcessMutex(client, "/zklock");
        CountDownLatch latch = new CountDownLatch(5);
        Runnable runnable = () -> {
            System.out.println("线程：" + Thread.currentThread().getName());
            System.out.println("    准备获取锁");
            try {
                processMutex.acquire();
                System.out.println("    线程：" + Thread.currentThread().getName() + "已获取锁");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("    完成");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    processMutex.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        ExecutorService executor = Executors.newFixedThreadPool(5);
        IntStream.range(1, 6).forEach(n -> executor.execute(runnable));
        latch.await();
        System.out.println("结束");
    }
}
