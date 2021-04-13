package com.awesomeJdk.practise.gcollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author froggengo@qq.com
 * @date 2021/1/31 10:36.
 */
public class CollectionNull {

    public static void main(String[] args) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(null);
        list.add(null);
        list.remove("");
        list.contains("");
        System.out.println(list);
        LinkedHashSet<Object> set = new LinkedHashSet<>();
        set.add(null);
        System.out.println(set.add(null));
        System.out.println(set);
        HashMap<Object, String> map = new HashMap<>();
        map.put(null, "我的key是null");
        map.put(1, null);
        System.out.println(map);
        HashSet<Object> objects = new HashSet<>(list);
        TreeSet<Object> treeSet = new TreeSet<>();
        treeSet.addAll(list);

    }

}
