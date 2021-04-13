package com.awesomeJdk.myNetty.exec;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author froggengo@qq.com
 * @date 2021/2/8 0:03.
 */
public class ExecMain {

    public static void main(String[] args) throws InterruptedException {
//        Executors.newFixedThreadPool(1).execute(()-> System.out.println("Hello world!"));
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS,
                                                                       new LinkedBlockingDeque<>());
        Callable<Integer> runnable1=()->{
            return 1;
        };
        Callable<Integer> runnable2=()->{
            throw new RuntimeException("111");
        };
        Callable<Integer> runnable3=()->{
            return 3;
        };
        List<Callable<Integer>> runnableList = List.of(runnable1, runnable2,runnable3);
        List<Future<Integer>> futures = executor.invokeAll(runnableList);
        futures.forEach(n->{
            Integer integer = null;
            try {
                integer = n.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(integer);
        });
    }

}
