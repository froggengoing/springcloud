package com.awesomeJdk.stream;

import com.awesomeJdk.common.Person;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/3/14 8:56.
 */
public class Stream_Collect {

    @Test
    public void testCollect() {
        ArrayList<Person> list = getData();
        ArrayList<Person> newList = list.stream().collect(ArrayList<Person>::new, ArrayList::add, ArrayList::addAll);
        System.out.println("--newList--");
        newList.stream().forEach(System.out::println);
        Collector<Person, ?, List<Person>> personListCollector = Collectors.toList();
        List<Person> collect = list.stream().collect(personListCollector);
        List<Person> collect2 = list.stream().<List<Person>>collect(() -> new ArrayList<>(), (t, v) -> t.add(v),
                                                                    (r, l) -> r.addAll(l));
    }

    @Test
    public void testCollectMap() {
        ArrayList<Person> list = getData();
        Map<String, Integer> mapCollect = list.stream()
            .collect(Collectors.toMap(n -> n.getName() + "-test", n -> n.getAge() * 2));
        mapCollect.forEach((k, v) -> System.out.println(k + "==" + v));
    }

    private ArrayList<Person> getData() {
        ArrayList<Person> list = new ArrayList<>();
        list.add(new Person("1", 20));
        list.add(new Person("2", 22));
        list.add(new Person("3", 24));
        list.add(new Person("4", 26));
        list.add(new Person("5", 28));
        list.add(new Person("6", 20));
        list.add(new Person("7", 28));
        list.add(new Person("8", 26));
        return list;
    }

    @Test
    public void testGroupBy() {
        Collector<Person, ?, Map<Integer, List<Person>>> collector = Collectors.groupingBy(Person::getAge);
        Map<Integer, List<Person>> collect = getData().stream().collect(collector);
        collect.forEach((k, v) -> System.out.println(k + "==" + v));
/*        Collectors.joining();
        Collectors.maxBy();
        Collectors.averagingInt();
        Collectors.partitioningBy();
        Collectors.mapping();
        Collectors.reducing();*/
    }

    @Test
    public void testJoiner() {
        List<String> list = Arrays.asList("1", "2", "3");
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        StringJoiner joiner2 = new StringJoiner("-", "(", ")");
        list.forEach(joiner::add);
        System.out.println(joiner.toString());
        list.forEach(joiner2::add);
        System.out.println(joiner2.toString());
        StringJoiner merge = joiner.merge(joiner2);
        System.out.println(merge.toString());
    }

    @Test
    public void testJoining() {
        Collector<CharSequence, ?, String> joining = Collectors.joining(",", "[", "]");
        String collect = getData().stream().map(Person::getName).collect(joining);
        System.out.println(collect);
    }

    @Test
    public void testMaxBy() {
        Collector<Person, ?, Optional<Person>> collector = Collectors
            .maxBy(Comparator.comparing(Person::getAge));
        Optional<Person> collect = getData().stream().collect(collector);
        collect.ifPresent(System.out::println);
    }

    @Test
    public void testReduce() {
        Collector<Person, ?, Optional<Person>> reducing = Collectors
            .reducing((Person a, Person b) -> new Person("-1", a.getAge() + b.getAge()));
        Optional<Person> sumAge = getData().stream().collect(reducing);
        sumAge.ifPresent(System.out::println);
    }
    @Test
    public void testAveragingInt (){
        Collector<Integer, ?, Double> integerDoubleCollector = Collectors.averagingInt((Integer n) -> n * 2);
    }
    @Test
    public void testPartition (){
        Collector<Person, ?, Map<Boolean, List<Person>>> collector = Collectors
            .partitioningBy((Person n) -> n.getAge() < 25);
        Map<Boolean, List<Person>> collect = getData().stream().collect(collector);
        collect.forEach((k,v)-> System.out.println(k+"="+v));
    }
    @Test
    public void test (){
        Collector<Person, ?, List<Integer>> mapping = Collectors.mapping((Person a) -> a.getAge(), Collectors.toList());
        List<Integer> collect = getData().stream().collect(mapping);
        collect.forEach(System.out::println);
    }
}
