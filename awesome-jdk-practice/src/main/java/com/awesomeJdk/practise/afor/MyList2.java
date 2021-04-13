package com.awesomeJdk.practise.afor;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author froggengo@qq.com
 * @date 2021/2/2 8:54.
 * 实现了Iterable<T>接口的类,可以使用
 * for(T t : Ts<T> ts ){
 *
 * }
 * 但for(T t : Ts )编译后,查看class会修改为
 * while(it.hasNext()) {
 *      IUser user = (IUser)it.next();
 *      System.out.println(user);
 * }
 * 也可以反编译查看 反编译:javap -verbose classAbsoluteName
 *  71: invokevirtual #18                 // Method iterator:()Ljava/util/Iterator;
 *  74: astore_2
 *  75: aload_2
 *  76: invokeinterface #19,  1           // InterfaceMethod java/util/Iterator.hasNext:()Z
 *  81: ifeq          104
 *  84: aload_2
 *  85: invokeinterface #20,  1           // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
 */
public class MyList2<T> implements Iterable<T> {

    T[] elementData;
    int size;

    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such


    public MyList2(int size) {
        this.elementData = (T[]) new Object[size];
    }

    public static void main(String[] args) {
        MyList2<IUser> list = new MyList2<>(20);
        list.add(new IUser("1", 20));
        list.add(new IUser("2", 30));
        list.add(new IUser("3", 40));
        list.add(new IUser("4", 50));
        for (IUser iUser : list) {
            System.out.println(iUser);
        }
    }

    public void add(T object) {
        elementData[size++] = object;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (int i = 0; i < size; i++) {
            action.accept((T) elementData[i]);
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    class Itr implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public T next() {
            return elementData[cursor++];
        }
    }
}
