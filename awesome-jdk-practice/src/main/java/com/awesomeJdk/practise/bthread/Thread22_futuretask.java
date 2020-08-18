package com.awesomeJdk.practise.bthread;

import java.util.concurrent.*;

public class Thread22_futuretask {
    private final ConcurrentMap<Object, Future<String>> taskCache = new ConcurrentHashMap<Object, Future<String>>();
    private int count=0;
    private String executionTask(final String taskName) throws ExecutionException, InterruptedException {
        while (true) {
            System.out.println("第"+(++count)+"次");
            Future<String> future = taskCache.get(taskName); //1.1,2.1
            if (future == null) {
                Callable<String> task = new Callable<String>() {
                    public String call() throws InterruptedException {
                        //......
                        System.out.println("正在执行任务");
                        TimeUnit.SECONDS.sleep(10);
                        return taskName.substring(0,2);
                    }
                };
                //1.2创建任务
                FutureTask<String> futureTask = new FutureTask<String>(task);
                future = taskCache.putIfAbsent(taskName, futureTask); //1.3
                if (future == null) {
                    future = futureTask;
                    futureTask.run(); //1.4执行任务
                }
            }

            System.out.println("因为在同一个线程，阻塞在执行任务run(),等待返回结果");
            try {
                return future.get(); //1.5,2.2线程在此等待任务执行完成
            } catch (CancellationException e) {
                taskCache.remove(taskName, future);
            }
        }
    }

    /**
     * 1、callable任务封装成 FutureTask
     * 2、FutureTask.run()执行任务
     * 3.阻塞等待结果
     * 原理：还是AQS，run()释放锁，get()获取锁。只有当run执行结束才会释放锁资源
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread22_futuretask futuretask = new Thread22_futuretask();
        System.out.println(futuretask.executionTask("假装在执行任务"));
    }
}
