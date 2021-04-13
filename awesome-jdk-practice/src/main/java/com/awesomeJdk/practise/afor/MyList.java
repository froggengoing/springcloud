package com.awesomeJdk.practise.afor;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author froggengo@qq.com
 * @date 2021/2/2 8:54.
 */
public class MyList<T> implements Iterator<T>{
    T[] elementData;
    int size;

    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such


    public MyList(int size) {
        this.elementData = (T[])new Object[size];
    }
    public void add(T object){
        elementData[size++] = object;
    }

    @Override
    public boolean hasNext() {
        return cursor !=size;
    }

    @Override
    public T next() {
        return elementData[cursor++];
    }

    public static void main(String[] args) {
        MyList<IUser> list = new MyList<>(20);
        list.add(new IUser("1",20));
        list.add(new IUser("2",30));
        list.add(new IUser("3",40));
        list.add(new IUser("4",50));
        for (MyList<IUser> it = list; it.hasNext(); ) {
            IUser user = it.next();
            System.out.println(user);
        }
    }

}
