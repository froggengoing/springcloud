package com.awesomeJdk.practise.a1Compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.util.comparator.Comparators;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 14:01.
 */
public class a1Compare {

    public static void main(String[] args) {
        //
        Comparator<Player> comparing = Comparator
            .comparing(Player::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder()));

        //如果数据有空，则必须添加nullLast或nullFirst判断，否则包NPE
        ArrayList<Player> rawList = new ArrayList<>();
        rawList.add(new Player("a1", 1));
        rawList.add(new Player("a2", 10));
        rawList.add(new Player("a4", null));
        rawList.add(new Player("a3", 5));
        rawList.add(new Player("a4", 4));
        rawList.add(new Player("a5", null));
        ArrayList<Player> list1 = new ArrayList<>();
        list1.addAll(rawList);

        Collections.sort(list1, comparing);
        list1.forEach(System.out::println);

        ArrayList<Player> list2 = new ArrayList<>();
        list2.addAll(rawList);
        Comparator<Player> comparing2 = Comparator.comparingInt(Player::getOrderNum);
        //NPE异常
        //list2.sort(comparing2);
        //list2.forEach(System.out::println);
        List<String> a = List.of("a", "b", "c");
        a.forEach(System.out::println);

    }

    static class Player {

        String name;
        Integer orderNum;

        public Player(String name, Integer orderNum) {
            this.name     = name;
            this.orderNum = orderNum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(Integer orderNum) {
            this.orderNum = orderNum;
        }

        @Override
        public String toString() {
            return "Player{" +
                "name='" + name + '\'' +
                ", orderNum=" + orderNum +
                '}';
        }
    }
}
