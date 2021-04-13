package com.awesomeJdk.Optional;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 11:15.
 */
public class OptionalPractise {

    public static void main(String[] args) {

        Optional<String> value = Optional.ofNullable("").map(n -> "我不是null");
        Optional<String> value1 = Optional.ofNullable("11");
        value.ifPresentOrElse(n -> {
            System.out.println("非空则执行," + n);
        }, () -> {
            System.out.println("空则执行");
        });
        value1.ifPresentOrElse(n -> {
            System.out.println("非空则执行," + n);
        }, () -> {
            System.out.println("空则执行");
        });
        //or 和 orElse
        String testStr = null;
        String value2 = Optional.ofNullable(testStr).orElse("默认值");
        System.out.println(value2);
        Optional<String> value3 = Optional.ofNullable(testStr).or(() -> Optional.of("默认值2"));
        System.out.println(value3.get());
    }
}
