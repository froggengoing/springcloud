package com.awesomeJdk.practise.athread;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Thread4_priortity {
    public static void main(String[] args) throws InterruptedException {
        //ArrayList<String> list = new ArrayList<>();
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger();
        int size=100000;
        ThreadPriority t1 = new ThreadPriority("t1", list, atomicInteger, size, 1);
        ThreadPriority t2 = new ThreadPriority("t2", list, atomicInteger, size, 5);
        ThreadPriority t3 = new ThreadPriority("t3", list, atomicInteger, size, 10);
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println(atomicInteger.get());
        System.out.println("T1执行次数："+list.stream().filter(n -> n.equals("t1")).count());
        System.out.println("T2执行次数："+list.stream().filter(n -> n.equals("t2")).count());
        System.out.println("T3执行次数："+list.stream().filter(n -> n.equals("t3")).count());
    }
}

class ThreadPriority extends Thread{
    private final int size;
    private final AtomicInteger atomicInteger;
    private final String name;
    private  CopyOnWriteArrayList<String> list;
    public ThreadPriority(String name,CopyOnWriteArrayList<String> list, AtomicInteger atomicInteger,
                          int size, int priority){
        this.list=list;
        this.size=size;
        this.atomicInteger=atomicInteger;
        this.name=name;
        this.setName(name);
        this.setPriority(priority);
    }
    @Override
    public void run() {
        while (atomicInteger.incrementAndGet() <size){
            list.add(name);
        }
    }
}

