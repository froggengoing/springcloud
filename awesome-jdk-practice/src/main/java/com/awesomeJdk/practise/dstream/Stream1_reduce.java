package com.awesomeJdk.practise.dstream;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * https://www.baeldung.com/java-stream-reduce
 */
public class Stream1_reduce {
    //@Test
    public static void main (String[] arg){
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        //identity作为类型标识，以及初始值
        System.out.println(numbers.stream().reduce(5,(a,b)->a+b));
        System.out.println(numbers.parallelStream().reduce(0,Integer::sum));
        System.out.println(numbers.parallelStream().reduce(5,Integer::sum));
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5", "6");
        //identity作为类型标识，以及初始值
        System.out.println(strings.stream().reduce("初始值：",(a,b)->a+b));
        System.out.println(strings.stream().reduce("初始值：",String::concat));
        System.out.println(strings.parallelStream().reduce("初始值：",String::concat));
        //复杂对象的reduce，需要使用一个combiner
        List<ReduceUser> users = Arrays.asList(new ReduceUser("John", 30), new ReduceUser("Julie", 35));
        Integer res = users.stream().reduce(0, (particalResult, user) -> particalResult + user.getMoney(), Integer::sum);
        Integer reduce = users.stream().map(ReduceUser::getMoney).reduce(0, Integer::sum);
        System.out.println(reduce);
        System.out.println(res);
    }
}
