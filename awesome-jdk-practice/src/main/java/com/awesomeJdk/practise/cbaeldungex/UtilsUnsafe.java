package com.awesomeJdk.practise.cbaeldungex;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UtilsUnsafe {
    public static Unsafe getUnsafe() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("sun.misc.Unsafe");
        Field theUnsafe = aClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        return unsafe;
    }
}
