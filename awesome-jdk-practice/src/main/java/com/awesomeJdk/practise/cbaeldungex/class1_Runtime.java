package com.awesomeJdk.practise.cbaeldungex;

import org.junit.Test;

import java.util.ArrayList;

/**
 * https://www.baeldung.com/java-heap-memory-api
 */
public class class1_Runtime{
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(" Runtime#freeMemory可用堆空间大小:"+runtime.freeMemory()/1024/1024+"M");
        System.out.println(" Runtime#totalMemory当前占用（为堆保留）空间:"+runtime.totalMemory()/1024/1024+"M");
        //超过将报OutOfMemoryError
        System.out.println(" Runtime#maxMemory虚拟机可用最大空间:"+runtime.maxMemory()/1024/1024+"M");

    }

    /**
     * java -Xms32M -Xmx64M Main
     * -Xmx最大堆空间，
     * -Xms最小堆空间
     */
    @Test
    public void test (){
        ArrayList<Integer> arrayList = new ArrayList<>();
        System.out.println("i \t Free Memory \t Total Memory \t Max Memory");
        for (int i = 0; i < 1000; i++) {
            arrayList.add(i);
            System.out.println(i + " \t " + Runtime.getRuntime().freeMemory()/1024/1024 +
                    " \t \t " + Runtime.getRuntime().totalMemory()/1024/1024 +
                    " \t \t " + Runtime.getRuntime().maxMemory()/1024/1024);
        }
    }
}
