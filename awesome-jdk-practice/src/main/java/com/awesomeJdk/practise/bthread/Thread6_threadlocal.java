package com.awesomeJdk.practise.bthread;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

public class Thread6_threadlocal {


    private static ThreadLocal<LocalDateTime> THREADLOCAL_TIME =new ThreadLocal();

    void begin(){
        THREADLOCAL_TIME.set(LocalDateTime.now());
    }
    void end(){
        System.out.println(Duration.between(THREADLOCAL_TIME.get(),LocalDateTime.now()));
    }

    public static void main(String[] args) throws InterruptedException {
        Thread6_threadlocal threadlocal = new Thread6_threadlocal();
        threadlocal.begin();
        Thread.sleep(10_000);
        threadlocal.end();
    }
}
