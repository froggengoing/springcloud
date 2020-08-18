package com.awesomeJdk.practise.cbaeldungex;


import sun.misc.Unsafe;

/**
 * https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html
 */
public class class5_unsafe {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Unsafe unsafe = UtilsUnsafe.getUnsafe();
        // 数组第一个元素的偏移地址,即数组头占用的字节数
        int[] intarr = new int[0];
        System.out.println(unsafe.arrayBaseOffset(intarr.getClass()));
        // 数组中每个元素占用的大小
        System.out.println(unsafe.arrayIndexScale(intarr.getClass()));

        // 获取实例字段的偏移地址,偏移最小的那个字段(仅挨着头部)就是对象头的大小
        System.out.println(unsafe.objectFieldOffset(VO.class.getDeclaredField("a")));
        System.out.println(unsafe.objectFieldOffset(VO.class.getDeclaredField("b")));

        // fieldOffset与objectFieldOffset功能一样,fieldOffset是过时方法,最好不要再使用
        System.out.println(unsafe.fieldOffset(VO.class.getDeclaredField("b")));
        // 获取类的静态字段偏地址
        System.out.println(unsafe.staticFieldOffset(VO.class.getDeclaredField("c")));
        System.out.println(unsafe.staticFieldOffset(VO.class.getDeclaredField("d")));
        System.out.println(unsafe.staticFieldOffset(VO.class.getDeclaredField("e")));

    }
}
