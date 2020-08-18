package com.awesomeJdk.practise.bthread;

import org.assertj.core.util.Streams;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 1.报错：at java.util.HashMap.putVal(HashMap.java:643)，java.lang.ClassCastException: java.util.HashMap$Node cannot be cast to java.util.HashMap$TreeNode
 * 2.数据丢失：map的大小不等于 10000
 * 3.数据重复：map的value转为set后，不等于map.size()
 */
public class Thread13_Map {
    /**
     * map丢失数据
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        HashMap map = new HashMap<String,String>();
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
