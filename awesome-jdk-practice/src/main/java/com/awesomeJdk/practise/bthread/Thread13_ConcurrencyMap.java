package com.awesomeJdk.practise.bthread;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Thread13_ConcurrencyMap {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        AtomicInteger count = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(10000);
        IntStream.range(0,10000).asLongStream().forEach(n->{
            new Thread(()->{
                count.incrementAndGet();
                //System.out.println(Thread.currentThread().getName());
                map.put("t"+n, UUID.randomUUID().toString());
                countDownLatch.countDown();
            },"t"+n).start();
        });
        countDownLatch.await();
        System.out.println("map:"+map.size());
        System.out.println("count:"+count.get());
        TimeUnit.SECONDS.sleep(5);
        System.out.println(map.size());
        System.out.println("判断值重复");
        LinkedHashSet<String> set=new LinkedHashSet<String>();
        map.forEach((k,v)->{
            set.add((String) v);
            map.forEach((m,n)->{
                if(v.equals(n) && !k.equals(m)){
                    System.out.println(k+"=="+v+" ; "+m+"=="+n);
                }
            });
        });
        System.out.println("set:"+set.size());
    }
}
