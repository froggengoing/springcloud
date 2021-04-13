package com.awesomeJdk.myNetty.exec.v3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

/**
 * @author froggengo@qq.com
 * @date 2021/2/13 0:44.
 */
public class SerialExecutor3Main {
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
    public static void main(String[] args) {
        System.out.println(COUNT_BITS);
        System.out.println(Integer.toBinaryString(COUNT_MASK));
        System.out.println(Integer.toBinaryString(RUNNING));
        System.out.println(Integer.toBinaryString(-536870911));
        System.out.println(Integer.toBinaryString(-536870911&~COUNT_MASK));
        System.out.println(RUNNING);
        System.out.println(Integer.toBinaryString(SHUTDOWN));
        System.out.println(Integer.toBinaryString(STOP));
        System.out.println(Integer.toBinaryString(TIDYING));
        System.out.println(Integer.toBinaryString(TERMINATED));
        System.out.println(Integer.toBinaryString(-1));
        System.out.println(STOP>RUNNING);
        KeySetView<Runnable, Boolean> set = ConcurrentHashMap.newKeySet();
        set.add(()-> System.out.println());

    }
}
