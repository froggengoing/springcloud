package com.awesomeJdk.practise.bthread;

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 通过CyclicBarrier等待4个线程的业务处理完成在执行汇总
 */
public class Thread20_CyclicBarrier {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        ConcurrentHashMap<String, Integer> hashMap = new ConcurrentHashMap<>();
        CyclicBarrier barrier = new CyclicBarrier(4, () -> {
            System.out.println("银行今日存款总额" + hashMap.entrySet().stream().mapToInt(n -> n.getValue()).sum());
            System.out.println("reduce求和："+hashMap.reduceValues(0, Integer::sum));
        });
        Runnable runnable=()->{
            String name = Thread.currentThread().getName();
            int res = new Random().nextInt(10);
            System.out.println("进入线程："+ name+"， 营业额："+res);
            try {
                //模拟业务消耗2秒
                TimeUnit.SECONDS.sleep(2);
                hashMap.put(name,res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    System.out.println("统计结束:"+name);
                    barrier.await();
                    System.out.println("await()统计结束:"+name);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };
        IntStream.range(1,5).forEach(n->executorService.submit(runnable));
        executorService.shutdown();
    }
}

