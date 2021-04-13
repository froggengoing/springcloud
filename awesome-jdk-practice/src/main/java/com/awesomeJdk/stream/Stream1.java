package com.awesomeJdk.stream;

import com.awesomeJdk.common.Person;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author froggengo@qq.com
 * @date 2021/2/27 18:48.
 */
public class Stream1 {

    public static void main(String[] args) {
        Collector<Person, ?, Double> averagingInt = Collectors.averagingInt(Person::getAge);
        ArrayList<Person> list=new ArrayList<>();
        list.add(new Person("1",20));
        list.add(new Person("2",22));
        list.add(new Person("3",24));
        list.add(new Person("4",26));
        list.add(new Person("5",28));
        list.add(new Person("6",20));
        list.add(new Person("7",18));
        list.add(new Person("8",16));
        Stream<Person> sorted = list.stream()/*.sorted()*/;
        Double collect = sorted.peek(System.out::println).collect(averagingInt);
        System.out.println("--");
        Optional<Person> first = list.stream().peek(System.out::println).findFirst();
        System.out.println(first.get());
        System.out.println(collect);
        System.out.println("##");
        System.out.println(list.stream().takeWhile(n -> n.getAge() < 24).peek(System.out::println).count());
        int reduce = list.stream().mapToInt(n -> n.getAge()).reduce(0, (a, b) -> a + b);
        System.out.println(reduce);
        System.out.println("spliterator");
        Spliterator<Person> spliterator = list.stream().spliterator();
        System.out.println(spliterator.characteristics());

    }
}
