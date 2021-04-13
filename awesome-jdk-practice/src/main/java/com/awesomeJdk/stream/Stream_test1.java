package com.awesomeJdk.stream;

import com.awesomeJdk.common.Person;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/3/16 8:29.
 */
public class Stream_test1 {

    public static void main(String[] args) {
        ArrayList<Person> list = new ArrayList<>();
        list.add(new Person("1", 20));
        list.add(new Person("2", 22));
        list.add(new Person("3", 24));
        list.add(new Person("4", 26));
        list.add(new Person("5", 28));
        list.add(new Person("6", 20));
        list.add(new Person("7", 18));
        list.add(new Person("8", 16));
        long count = list.stream().filter(n -> n.getAge() > 22).map(n -> new Person(n.getName(), n.getAge() * 2))
            .count();
        System.out.println(count);
    }
}
