package com.awesomeJdk.stream;

import com.awesomeJdk.common.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/3/14 8:56.
 */
public class Stream_Collect1 {

    public static void main(String[] args) {
        ArrayList<Person> list=new ArrayList<>();
        list.add(new Person("1",20));
        list.add(new Person("2",22));
        list.add(new Person("3",24));
        list.add(new Person("4",26));
        list.add(new Person("5",28));
        list.add(new Person("6",20));
        list.add(new Person("7",22));
        list.add(new Person("8",26));
        Map<Integer, List<Person>> collect = list.stream().collect(Collectors.groupingBy(Person::getAge));
        collect.forEach((k,v)-> System.out.println(v));
        List<Integer> integers = List.of(1,-1);
        boolean b = integers.stream().allMatch(n -> n > 0);
        System.out.println(b);
    }

}
