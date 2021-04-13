package com.awesomeJdk.stream;

import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/2/28 18:04.
 */
public class Stream2 {

    public static void main(String[] args) {
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        //stream.spliterator()
        long count = stream.filter(n -> n > 5).filter(n -> n > 6).sorted().count();
        System.out.println(count);
    }

}
