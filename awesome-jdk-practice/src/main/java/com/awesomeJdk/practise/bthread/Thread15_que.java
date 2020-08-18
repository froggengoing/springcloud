package com.awesomeJdk.practise.bthread;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class Thread15_que {
    public static void main(String[] args) {
        LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(5);
/*        Runnable runnable=()->{
            blockingQueue.add(UUID.randomUUID().toString())
        }*/
        blockingQueue.add(UUID.randomUUID().toString());
        blockingQueue.add(UUID.randomUUID().toString());
        blockingQueue.add(UUID.randomUUID().toString());
        blockingQueue.add(UUID.randomUUID().toString());
        blockingQueue.add(UUID.randomUUID().toString());
        System.out.println("使用offer添加数据，返回false");
        System.out.println(blockingQueue.offer(UUID.randomUUID().toString()));
        System.out.println("使用add添加数据将，报错");
        blockingQueue.add(UUID.randomUUID().toString());


    }
}
