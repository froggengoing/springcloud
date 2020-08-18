package com.awesomeJdk.practise.bthread;

import java.util.concurrent.locks.LockSupport;

public class Thread12_ConditionTest{
    public static void main(String[] args) throws InterruptedException {
        Thread12_Condition condition = new Thread12_Condition();
        Thread thread1 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            System.out.println("进入线程：" + thread.getName());
            System.out.println("    执行conditionAwait：");
            condition.conditionWait();
            System.out.println("    其他线程执行 conditionNotify()后");
        });
        thread1.start();
        Thread.sleep(2_000);
        System.out.println("    线程"+thread1.getName()+", 线程状态"+thread1.getState());
        Thread.sleep(10_000);
        condition.conditionNotify();
    }


}
