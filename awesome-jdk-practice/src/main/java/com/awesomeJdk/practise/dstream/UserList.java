package com.awesomeJdk.practise.dstream;

import com.awesomeJdk.practise.dstream.myinterface.ForEach;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author froggengo@qq.com
 * @date 2021/2/2 8:27.
 * ForEach实际内部调用了for (int i = 0; i < size; i++)
 */
public class UserList<T> implements ForEach<T> {

    Object[] elementData;
    int size;

    public UserList(int size) {
        this.elementData = new Object[size];
    }

    public void add(T object) {
        elementData[size++] = object;
    }

    /**
     * 迭代器遍历
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        for (int i = 0; i < size; i++) {
            action.accept((T) elementData[i]);
        }
    }
    public static void main(String[] args) {
        UserList<ReduceUser> list = new UserList<>(20);
        list.add(new ReduceUser("1", 29));
        list.add(new ReduceUser("2", 30));
        list.add(new ReduceUser("3", 40));
        list.add(new ReduceUser("4", 50));
        System.out.println(list.elementData.length);
        list.forEach(n -> System.out.println(n));
        ArrayList<Integer> intList = new ArrayList<>();
        Stream<Integer> stream = intList.stream();
    }
}
