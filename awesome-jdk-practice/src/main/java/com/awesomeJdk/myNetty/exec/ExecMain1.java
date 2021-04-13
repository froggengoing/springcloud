package com.awesomeJdk.myNetty.exec;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * @author froggengo@qq.com
 * @date 2021/2/8 0:03.
 */
public class ExecMain1 {

    public static void main(String[] args) {
        new Thread(()->{
            System.out.println("hello world!");
        }).start();
    }

}
