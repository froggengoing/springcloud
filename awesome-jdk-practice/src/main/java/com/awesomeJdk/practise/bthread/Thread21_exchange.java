package com.awesomeJdk.practise.bthread;

import org.junit.Test;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class Thread21_exchange {
    private static final int BUFFER_SIZE = 10;

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

    }

    //https://www.baeldung.com/java-exchanger
    @Test
    public void givenThreads_whenMessageExchanged_thenCorrect() {
        Exchanger<String> exchanger = new Exchanger<>();

        Runnable taskA = () -> {
            try {
                String message = exchanger.exchange("from A");
                assertEquals("from B", message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        Runnable taskB = () -> {
            try {
                String message = exchanger.exchange("from B");
                assertEquals("from A", message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };
        CompletableFuture.allOf(
                CompletableFuture.runAsync(taskA), CompletableFuture.runAsync(taskB)).join();
    }
    @Test
    public void givenThread_WhenExchangedMessage_thenCorrect() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();

        Runnable runner = () -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                String message = exchanger.exchange("from runner");
                //assertEquals("to runner", message);
                System.out.println(Thread.currentThread().getName()+"获取exchanger的信息："+message);
            } catch (InterruptedException e) {
                //避免interrupted标志被擦除
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };
        Thread thread = new Thread(runner,"t1");
        thread.start();
        /*CompletableFuture<Void> result
                = CompletableFuture.runAsync(runner);*/
        String msg = exchanger.exchange("to runner");
        System.out.println(Thread.currentThread().getName()+"获取exchanger的信息："+msg);
        //assertEquals("from runner", msg);
        /*result.join();*/
    }
    @Test
    public void givenData_whenPassedThrough_thenCorrect() throws InterruptedException {

        Exchanger<Queue<String>> readerExchanger = new Exchanger<>();
        Exchanger<Queue<String>> writerExchanger = new Exchanger<>();

        Runnable reader = () -> {
            try {
                Queue<String> readerBuffer = new ConcurrentLinkedQueue<>();
                while (true) {
                    readerBuffer.add(UUID.randomUUID().toString());
                    if (readerBuffer.size() >= BUFFER_SIZE) {
                        readerBuffer = readerExchanger.exchange(readerBuffer);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable processor = () -> {
            try {
                Queue<String> processorBuffer = new ConcurrentLinkedQueue<>();
                Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
                processorBuffer = readerExchanger.exchange(processorBuffer);
                while (true) {
                    writerBuffer.add(processorBuffer.poll());
                    if (processorBuffer.isEmpty()) {
                        processorBuffer = readerExchanger.exchange(processorBuffer);
                        writerBuffer = writerExchanger.exchange(writerBuffer);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable writer = () -> {
            try {
                Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
                writerBuffer = writerExchanger.exchange(writerBuffer);
                while (true) {
                    System.out.println("writer线程:"+writerBuffer.poll());
                    if (writerBuffer.isEmpty()) {
                        writerBuffer = writerExchanger.exchange(writerBuffer);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        CompletableFuture.allOf(
                CompletableFuture.runAsync(reader),
                CompletableFuture.runAsync(processor),
                CompletableFuture.runAsync(writer)).join();
    }
}
