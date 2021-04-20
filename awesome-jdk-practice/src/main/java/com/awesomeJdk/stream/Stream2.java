package com.awesomeJdk.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/2/28 18:04.
 */
public class Stream2 {

    public static void main(String[] args) {
        Stream<Integer> stream = Stream.of(2,0,12,32,1, 2, 3, 4, 5, 6, 7, 8, 9);
        //stream.spliterator()
        List<Integer> sorted = stream.filter(n -> n > 5).filter(n -> n > 6).sorted().collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

}
