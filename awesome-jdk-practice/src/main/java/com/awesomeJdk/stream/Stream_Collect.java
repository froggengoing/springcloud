package com.awesomeJdk.stream;

import com.awesomeJdk.common.Person;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author froggengo@qq.com
 * @date 2021/3/14 8:56.
 */
public class Stream_Collect {

    public static void main(String[] args) {
        ArrayList<Person> list=new ArrayList<>();
        list.add(new Person("1",20));
        list.add(new Person("2",22));
        list.add(new Person("3",24));
        list.add(new Person("4",26));
        list.add(new Person("5",28));
        list.add(new Person("6",20));
        list.add(new Person("7",18));
        list.add(new Person("8",16));
        ArrayList<Person> newList = list.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println("--newList--");
        newList.stream().forEach(System.out::println);
    }
}
