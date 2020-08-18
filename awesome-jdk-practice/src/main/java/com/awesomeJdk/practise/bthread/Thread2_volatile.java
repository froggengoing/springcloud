package com.awesomeJdk.practise.bthread;

/**
 * 启动参数：-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:+LogCompilation -XX:LogFile=jit.log
 * 查看输出的汇编代码，关注lock指令
 */
public class Thread2_volatile {
    public volatile long sum = 0;

    public int myadd(int a, int b) {
        int temp = a + b;
        sum += temp;
        return temp;
    }

    public static void main(String[] args) {
        Thread2_volatile test = new Thread2_volatile();
        int sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum = test.myadd(sum, 1);
        }

        System.out.println("Sum:" + sum);
        System.out.println("Test.sum:" + test.sum);
    }
}
