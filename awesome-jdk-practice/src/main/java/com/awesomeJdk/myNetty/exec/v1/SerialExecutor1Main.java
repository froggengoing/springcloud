package com.awesomeJdk.myNetty.exec.v1;

import com.awesomeJdk.myNetty.exec.v1.SerialExecutor1;

public class SerialExecutor1Main {

    public static void main(String[] args) {
        SerialExecutor1 serialExecutor = new SerialExecutor1();
        serialExecutor.execute(()->{
            System.out.println(1);
        });
        serialExecutor.execute(()->{
            System.out.println(2);
        });
        serialExecutor.execute(()->{
            System.out.println(3);
        });
    }
}
