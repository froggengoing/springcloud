package com.awesomeJdk.practise.bthread;

import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

public class ConcurrentPutHashMap {

    public static void main(String[] args) throws InterruptedException {
        final HashMap<String, String> map = new HashMap<String, String>(2);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            map.put(UUID.randomUUID().toString(), "");
                        }
                    }, "ftf" + i).start();
                }
            }
        }, "ftf");
        t.start();
        t.join();
        System.out.println(map.size());
        Thread.sleep(10_000);
        System.out.println(map.size());
    }



}
