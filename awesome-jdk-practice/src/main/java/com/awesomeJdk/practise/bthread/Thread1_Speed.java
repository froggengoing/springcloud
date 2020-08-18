package com.awesomeJdk.practise.bthread;



public class Thread1_Speed {
    public static final int COUNT=10000_0000;
    public static void main(String[] args) throws InterruptedException {
        concurrency();
        serial();
    }

    /**
     * 测试发现，for循环必须使用long类型才有区别
     * 如果使用int时间与count基本无关
     */
    private static void serial() {
        long time = System.currentTimeMillis();
        int a = 0;
        //下面这里使用long和int的时间差距很大，差不多100倍
        for (long i = 0; i < COUNT; i++) {
            a+=5;
        }
        int b=0;
        for (long i = 0; i < COUNT; i++) {
            b--;
        }
        long end = System.currentTimeMillis() - time;
        System.out.println("串行："+end +" 。b = "+b);
    }
    private static void concurrency() throws InterruptedException {
        long time = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            int a = 0;
            for (long i = 0; i < COUNT; i++) {
                a++;
            }
        });
        thread.start();
        int b=0;
        for (long i = 0; i < COUNT; i++) {
            b--;
        }
        thread.join();
        System.out.println("并发："+String.valueOf(System.currentTimeMillis()-time)+" 。b = "+b);

    }

}
