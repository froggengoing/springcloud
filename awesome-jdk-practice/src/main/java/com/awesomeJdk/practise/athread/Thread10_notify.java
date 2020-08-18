package com.awesomeJdk.practise.athread;

import java.util.LinkedList;
import java.util.UUID;

public class Thread10_notify {
    /**
     * 单一生产者/消费这模型
     * 生产者线程，为队列添加元素，数量达10个后，线程阻塞。唤醒等待线程
     * 消费者线程，移除队列元素，队列为空后，线程阻塞。唤醒等待线程
     * 注意：这里只能是单生产者，单消费者。否则连续唤醒生产者/或者消费者会导致多生产/多消费
     * @param args
     */
    public static void main(String[] args) {
        Thead10 thread = new Thead10();
        new Thread(()->{
            while (true){
                thread.add(UUID.randomUUID().toString());
            }
        },"t1").start();
        new Thread(()->{
            while (true){
                thread.get();
            }
        },"t2").start();
        /**
         * 多个生产者导致超生产
         */
/*      new Thread(()->{
            while (true){
                thread.add(UUID.randomUUID().toString());
            }
        },"t3").start();*/
        new Thread(()->{
            while (true){
                thread.get();
            }
        },"t4").start();
        new Thread(()->{
            while (true){
                thread.get();
            }
        },"t5").start();
    }
}
class Thead10{
    private final LinkedList<String> que=new LinkedList<String>();
    public int size=10;
    public  void add(String value) {
        synchronized(que){
            if(que.size() >=size){
                System.out.println("队列已满");
                try {
                    que.wait();
                    System.out.println("add线程被唤醒: "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            que.add(value);
            System.out.println("添加元素："+value);
            que.notifyAll();

        }
    }
    public  void get()  {
        synchronized(que){
            if(que.isEmpty()){
                System.out.println("队列已空");
                try {
                    que.wait();
                    System.out.println("get线程被唤醒:"+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String value = que.remove();
            System.out.println("移除元素："+value);
            que.notifyAll();

        }
    }
}