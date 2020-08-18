package com.awesomeJdk.practise.cbaeldungex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存泄露:static变量，但这实例没测出什么意思
 * If collections or large objects are declared as static, then they remain in the memory throughout
 * the lifetime of the application,thus blocking the vital memory that could otherwise be used elsewhere.
 */
public class class4_memoryleak {
    private static Logger log = LoggerFactory.getLogger(class4_memoryleak.class);
    public  List<Double> list = new ArrayList<>();

    public void populateList() {
        for (int i = 0; i < 1000_0000; i++) {
            list.add(Math.random());
        }
        log.info("Debug Point 2");
    }

    public static void main(String[] args) throws InterruptedException {
        log.info("Debug Point 1");
        Thread.sleep(30_000);
        new class4_memoryleak().populateList();
        log.info("Debug Point 3");
    }

}
