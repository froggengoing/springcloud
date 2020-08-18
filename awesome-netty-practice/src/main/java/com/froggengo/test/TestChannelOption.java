package com.froggengo.test;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestChannelOption<T> {
    /**
     * <T>表示返回泛型，只起到占位符的作用
     * @param name
     * @param <T>
     * @return
     */
    public static <T> TestChannelOption<T> newInstance(String name){
        return null;
    }


    @Test
    public void test(){
        Thread thread = Thread.currentThread();
        ThreadGroup threadGroup = thread.getThreadGroup();
        String name1 = thread.getName();
        System.out.println(name1); //main
        ThreadGroup parent = threadGroup.getParent();
        String name = parent.getName();
        System.out.println(name); //system
        System.out.println(threadGroup.activeCount());
        System.out.println(threadGroup);
    }

    /**
     * A=8,B=9
     * 按位与&,A&B=8，即1000
     * 按位或|,A|B=9，即1001
     * 按位异或^,A^B=1，即0001,如果相对应位值相同，则结果为0，否则为1
     * 按位取反~,~A=7，即0111
     * 左移 <<,A << 2 = 32，即1000 00
     * 右移 >>,A >> 2 = 2，即0010
     */
    @Test
    public void  testTime(){
        long toNanos = TimeUnit.SECONDS.toNanos(1);
        System.out.println(toNanos); //1000 000 000
        int r=1;
        int w=4;
        int c=8;
        int a=16;
        System.out.println(r & ~c);
        System.out.println(w & ~c);
        System.out.println(c & ~c);
        System.out.println(a & ~c);
        System.out.println(r & c);
    }
}
