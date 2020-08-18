package com.awesomeJdk.practise.bthread;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 1.是否大于核心线程数？否，则创建线程处理。处理结束后，会尝试继续从阻塞队列中获取task，通过判断当前运行的线程数是否大于核心线程数，
 * 如大于则等待keepAliveTime的时间从阻塞队列中获取任务poll(timeout,TimeUnit)，如果到时间了还没有任务进来则销毁（自然结束）。
 * 如果当前运行线程数没有大于核心线程数，则阻塞等待任务take()。
 * 2.所以新进来的任务会首先进入阻塞队列，以便于核心线程可以在处理完前面的任务后，从阻塞队列中获取任务
 * 3.当阻塞队列满了，则开辟新的线程。一起处理阻塞队列的任务。
 */
public class Thread23_ThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,5,5,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(5), Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());
        AtomicInteger a=new AtomicInteger();
        Runnable runnable=()->{
            String name = Thread.currentThread().getName();
            System.out.println("线程："+ name +" 计数："+a.get());
            try {
                TimeUnit.SECONDS.sleep(15);
                System.out.println("线程："+ name +" 结束:"+ LocalDateTime.now());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        IntStream.range(0,20).forEach(n->{
            a.incrementAndGet();//可以看不中间的计数被丢弃了，因为(DiscardOldestPolicy)队列满的时候，第一个元素被取出
            poolExecutor.execute(runnable);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        poolExecutor.execute(runnable);
        /**
         * 期望：
         * 1、2计数直接由核心线程执行
         * 3、4、5、6、7将进入阻塞队列
         * 8、9、10,由新开的线程执行
         * 11进入线程，最老的线程将被丢弃，
         * 1计数的线程处理结束。
         */
    }
    class MyLinkedBlockQueue<Runnable> extends LinkedBlockingQueue<Runnable>{


    }
    @Test
    public void test (){
          final int COUNT_BITS = Integer.SIZE - 3;
          final int CAPACITY   = (1 << COUNT_BITS) - 1;
         final int RUNNING    = -1 << COUNT_BITS;
         final int SHUTDOWN   =  0 << COUNT_BITS;
         final int STOP       =  1 << COUNT_BITS;
         final int TIDYING    =  2 << COUNT_BITS;
         final int TERMINATED =  3 << COUNT_BITS;
        System.out.println("CAPACITY"+CAPACITY);
        System.out.println("RUNNING"+RUNNING);
        System.out.println("SHUTDOWN"+SHUTDOWN);
        System.out.println("STOP"+STOP);
        System.out.println("TIDYING"+TIDYING);
        System.out.println("TERMINATED"+TERMINATED);
        System.out.println(-1 & CAPACITY);
        System.out.println(0 & ~CAPACITY);
        int ctl = RUNNING | 0;
        System.out.println("ctl初始值："+ ctl );
        int workcount = ctl & CAPACITY;
        System.out.println("workcount初始值："+ workcount);
        int runstate = ctl & ~CAPACITY;
        System.out.println("runstate:"+ runstate);

    }
}
