package com.awesomeJdk.practise.bthread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Thread16_forkjoin  extends RecursiveTask<Integer> {

    private  int max;
    private  int min;
    private  int count;

    Thread16_forkjoin(int min, int max,int count){
        this.min=min;
        this.max=max;
        this.count = count;
    }
    @Override
    protected Integer compute() {
        for (int i = 0; i < count; i++) {
            System.out.print("  ");
        }
        count++;
        System.out.println("线程:"+Thread.currentThread().getName()+", 区间："+max+"-"+min);
        int sum=0;
        if(max - min <= 2){
            for (int i = min; i <= max; i++) {
                sum += i;
            }
        }else{
            int mid = (max + min )/ 2;
            Thread16_forkjoin startTask = new Thread16_forkjoin(min, mid,count);
            Thread16_forkjoin endTask = new Thread16_forkjoin(mid+1, max,count);
            startTask.fork();
            endTask.fork();
            Integer left = startTask.join();
            Integer right = endTask.join();
            sum = left + right;
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread16_forkjoin forkjoin = new Thread16_forkjoin(1, 10,0);
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Integer> submit = pool.submit(forkjoin);
        Integer res = submit.get();
        System.out.println(res);
    }
}
