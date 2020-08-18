package com.froggengo.pattern.single;

import org.junit.Test;

public class Class1_singleObject {
    @Test
    public void test (){
        Class1_nothing nothing = Class1_object6.object.newInstance();
    }

}

/**
 * 1、懒汉式
 */
class Class1_object1{
    private static  Class1_object1 object1;
    private Class1_object1(){
    }
    public static Class1_object1 newInstance(){
        if(object1==null){
            object1=new Class1_object1();
        }
        return object1;
    }
}

/**
 * 2、饿汉式
 */
class Class1_object2{
    private static  Class1_object2 object1=new Class1_object2();
    private Class1_object2(){
    }
    public static Class1_object2 newInstance(){
        return object1;
    }
}

/**
 * 3、第一种懒汉式，可能导致多线程判断为空，进而同时new创建对象
 * 添加synchronized避免这个问题，
 * 新问题，每次获取对象需要等待另一个线程。
 */
class Class1_object3{
    private static  Class1_object3 object1;
    private Class1_object3(){
    }
    public synchronized  static Class1_object3 newInstance(){
        if(object1==null){
            object1=new Class1_object3();
        }
        return object1;
    }
}

/**
 * 4、双重判断+synchronized，避免地第3中效率低下的问题
 * 为啥双重判断？假设A和B同时判断为null,A线程成功进入同步块，完成对象创建
 * 此时B也会获得锁，如果不再次判断是否为null，那么B线程会再次创建对象
 */
class Class1_object4{
    private static  Class1_object4 object1;
    private Class1_object4(){
    }
    public static  Class1_object4 newInstance(){
        if(object1==null){
            synchronized(Class1_object4.class){
                if(object1==null){
                    object1=new Class1_object4();
                }
            }
        }
        return object1;
    }
}

/**
 * 6、volatile 防止指令重排
 * 创建对象三部曲：
 *  1、分配空间
 *  2、初始化
 *  3、变量指向对象地址
 *  第2和3可能出现指令重排，顺序变为 1-3-2
 *  这样会导致B拿到的是没有初始化的对象，那么在执行的时候，就可能报NullPointException
 */
class Class1_object5{
    private static  Class1_object5 object1;
    private Class1_object5(){
    }
    public static  Class1_object5 newInstance(){
        if(object1==null){
            synchronized(Class1_object5.class){
                if(object1==null){
                    object1=new Class1_object5();
                }
            }
        }
        return object1;
    }


}
/**
 * 7、反射和序列化会破坏单例模式,
 **/

/**
 * 8、Effective Java书中给出的，使用枚举实现单例模式
 */
enum Class1_object6{
    object;
    private Class1_nothing o=null;
    private Class1_object6(){
        o=new Class1_nothing();
    }
    public Class1_nothing newInstance(){
        return o;
    }
}
class Class1_nothing {
}