package com.awesomeJdk.practise.abaisc;

import org.junit.Test;

/**
 * 1、加载
 * 2、链接
 * 3、初始化
 */
public class ClassLoaderTest {

    /**
     * Class.forName(className)方法，内部实际调用的方法是  Class.forName(className,true,classloader);
     * 第2个boolean参数表示类是否需要初始化，  Class.forName(className)默认是需要初始化。
     * 一旦初始化，就会触发目标对象的 static块代码执行，static参数也也会被再次初始化。
     * ClassLoader.loadClass(className)方法，内部实际调用的方法是  ClassLoader.loadClass(className,false);
     * 第2个 boolean参数，表示目标对象是否进行链接，false表示不进行链接，由上面介绍可以，
     * 不进行链接意味着不进行包括初始化等一些列步骤，那么静态块和静态对象就不会得到执行
     */
    @Test
    public void testClassLoader () throws ClassNotFoundException {
        //forName0(className, true, ClassLoader.getClassLoader(caller), caller);
        //底层默认进行初始化，这里执行将打印staitc静态 代码块
        //Class<?> aClass = Class.forName(Hellowrold.class.getName());
        //loadClass(name, false);默认不进行初始化
        //这里执行将不执行静态代码块
        Class<?> aClass1 = ClassLoader.getSystemClassLoader().loadClass(Hellowrold.class.getName());
    }
}
