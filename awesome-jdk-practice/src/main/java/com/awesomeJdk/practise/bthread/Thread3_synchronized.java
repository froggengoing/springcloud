package com.awesomeJdk.practise.bthread;

import org.junit.Test;

/**
 * 查看java反编译文件
 * "C:\Program Files\Java\jdk1.8.0_45\bin\javap.exe" -c -l -s -p
 * D:\1PracticeProject\cloud2020\awesome-jdk-practice\target\classes\com\awesomeJdk\practise\bthread\Thread3_synchronized.class
 */
public class Thread3_synchronized {
   public static void main(String[] args) {
        synchronized (Thread3_synchronized.class){
        }
        method();
        method2();
    }
    public static synchronized void  method(){
    }
    public static  void  method2(){
    }

}
