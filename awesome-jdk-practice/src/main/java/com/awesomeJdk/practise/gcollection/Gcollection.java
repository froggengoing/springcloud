package com.awesomeJdk.practise.gcollection;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Gcollection {
    /**
     * https://www.baeldung.com/java-fail-safe-vs-fail-fast-iterator#:~:text=Fail-Fast%20systems%20abort%20operation%20as-fast-as-possible%20exposing%20failures%20immediately,to%20avoid%20raising%20failures%20as%20much%20as%20possible.
     * fail-fast：这类collection类，不允许在iterator过程中修改集合元素
     */
    @Test
    public void testArrayList (){
        //1、非并发包在iterator()过程中，修改集合比如添加和删除，都是导致fail-fast
        //以下将报java.util.ConcurrentModificationException
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(100);
        numbers.add(99);
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            Integer number = iterator.next();
            numbers.add(50);
        }

    }

    /**
     * fail-safe
     * java.util.concurrent下的集合类操作时 ，迭代器是对原集合的拷贝，对原数据的修改迭代器并不感知
     */

    @Test
    public void testCopyOnWriteArrayList (){
        //2、并发包允许在iterator过程中，修改集合元素
        CopyOnWriteArrayList<Integer> list=new CopyOnWriteArrayList<>();
        list.add(100);
        list.add(99);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer number = iterator.next();
            list.add(50);
        }
        System.out.println(list.size());
        list.forEach(n-> System.out.println(n));
    }

    /**
     *    Enumeration                     Iterator
     *   ----------------                ----------------
     *   hasMoreElement()                hasNext()
     *   nextElement()                   next()
     *   N/A                             remove()
     *   Enumeration:The functionality of this interface is duplicated by the Iterator interface. In addition,
     *   Iterator adds an optional remove operation, and has shorter method names. New implementations
     *   should consider using Iterator in preference to Enumeration.
     */
    @Test
    public void testVector (){
        Vector<Integer> vector = new Vector<>();
        vector.add(99);
        vector.add(100);
        vector.add(1);
        Enumeration<Integer> elements = vector.elements();
        while (elements.hasMoreElements()){
            System.out.println(elements.nextElement());
        }
        Iterator<Integer> iterator = vector.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

    /**
     * Arrays.asList(s) ：Returns a fixed-size list backed by the specified array
     * 返回Arrays自己内部实现的ArrayList，并不是我们通常使用的List，并没有实现add()方法
     * java.lang.UnsupportedOperationException
     */
    @Test
    public void asList (){
        String[] s={"你","还","在","吗"};
        List<String> list = Arrays.asList(s);
        list.add("我在啊");
        System.out.println(list.size());
    }

    /**
     * java.util.ConcurrentModificationException
     */
    @Test
    public void testRemove (){
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        for(String n:list){
            if("c".equals(n))list.remove("c");
        }
        list.forEach(System.out::println);
    }

    /**
     * 继承iterator，增加反向遍历 ，修改set，add
     */
    @Test
    public void testListInterator (){
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        ListIterator<String> iterator = list.listIterator();
        //这里 必须先 正向遍历？不能设置intext吗？
        while(iterator.hasNext()){
            iterator.next();
        }
        while (iterator.hasPrevious()){
            String previous = iterator.previous();
            if(previous.equals("a")) iterator.set("g");
            System.out.println(previous);
            if(previous.equals("b")) iterator.add("f");
        }
        iterator.forEachRemaining(System.out::print);
    }

    /**
     * 排序
     */
    @Test
    public void testListSort(){
        ArrayList<String> list = new ArrayList<>();
        list.add("11");
        list.add("12");
        list.add("99");
        list.add("1");
        list.add("2");
        //底层是TimSort，结合了归并排序和插入 排序
        Collections.sort(list,(a,b)->{
            return Integer.valueOf(b)-Integer.valueOf(a);
        });
        list.forEach(System.out::println);
        list.sort((a,b)->{
            return Integer.valueOf(b)-Integer.valueOf(a);

        });
        //无法使用add方法
        Collection<String> clist = Collections. unmodifiableCollection(list);
    }

    /**
     * 1、第一次putval，resize进行初始化：初始容量为16，负载因子为0.75，阈值为12
     * 2、putval后modcount达到阈值，则扩容，需要重新计算所有节点的位置，当前容量左移1位即*2，阈值也是
     * 3、当链表，达到8个是转为红黑树
     * 4、新加入的值put在链表末尾
     * 5、HashMap 中，null 可以作为键
     */
    @Test
    public void testMap (){
        HashMap<String, String> map = new HashMap<>();
        map.put("A","A");
        map.put("B","B");
        map.put("C","C");
        map.put("D","D");
        map.put("E","E");
        map.put("F","F");
        map.put("G","G");
        map.put("H","H");
        map.put("I","I");
        map.put("J","J");
        map.put("K","K");
        map.put("L","L");
        map.put("M","M");
        map.put("N","N");
        map.put("O","O");
        map.put("P","P");
        map.put("Q","Q");
        map.put("R","R");
        map.put("S","T");


    }
}
