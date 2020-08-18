package com.froggengo.practise.aop;

import com.froggengo.practise.aop.proxy.PersonService;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.*;
import org.junit.Test;
//import proxy.proxy.PersonService$$EnhancerByCGLIB$$e2f295b;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class testProxy {
/**
 * System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\class");  --该设置用于输出cglib动态代理产生的类
 * System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");   --该设置用于输出jdk动态代理产生的类
 * System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");--新版jdk
 * */
    /**
     * 列1：<a>https://www.baeldung.com/cglib</a>
     * FixedValue 指代理的方法返回固定值，要求方法返回值类型与FixedValue类型值一致
     * 所以掉用service.lengthOfName()报错，因为返回类型不一致
     * MyJdkProxy /PersonService
     * cglib共有6中callback类型MyCglibProxy
     * FixedValue
     * InvocationHandler
     * LazyLoader
     * MethodInterceptor
     * Dispatcher
     * NoOp
     */
    @Test
    public void testProxy(){
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "F:\\dev\\SpringLearn\\src\\test\\java\\proxy");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((FixedValue)()-> "hello froggengo");
        PersonService service = (PersonService) enhancer.create();
        String s = service.sayHello(" gg");
        //Integer gg = service.lengthOfName("gg");
        //assertEquals("hello froggengo1", s);//断言、不等时候回报错
        System.out.println(s);
    }

    /**
     * 列2：callback中编写希望代理的方法
     */
    @Test
    public void testProxy2(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((MethodInterceptor)(Object obj, Method method, Object[] arg, MethodProxy proxy)->{
            if (method.getDeclaringClass() !=Object.class && method.getReturnType() == String.class) {
                //do everything you want;
                return "result from proxy";
            }else {
                return proxy.invokeSuper(obj,arg);//proxy.invoke()会报错
            }
        });
        PersonService personService = (PersonService) enhancer.create();
        System.out.println(personService.sayHello(null));
        System.out.println(personService.lengthOfName("1234"));
    }

    /**
     *getDeclaredMethod：获取当前类的所有声明的方法，包括public、protected和private修饰的方法,不包括父类
     * getMethod：获取当前类和父类的所有public的方法
     */
    @Test
    public void testproxy3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("auth",String.class);
        beanGenerator.addProperty("bookname",String.class);
        Object mybean = beanGenerator.create();
        Method setAuth=mybean.getClass().getMethod("setAuth",String.class);
        setAuth.invoke(mybean,"miss wang");
        Method getAth=mybean.getClass().getMethod("getAuth");
        System.out.println(getAth.invoke(mybean));
    }

    @Test
    public void testProxy3(){
        Mixin mixin = Mixin.create(new Class[]{Interface1.class, Interface2.class, MixinInterface.class},
                new Object[]{new Class1(), new Class2()});
        MixinInterface mixinDelegate = (MixinInterface) mixin;

        System.out.println(mixinDelegate.first());
        System.out.println(mixinDelegate.second());
    }
/*    @Test
    public void testPersonService (){
        PersonService$$EnhancerByCGLIB$$e2f295b cglib= new PersonService$$EnhancerByCGLIB$$e2f295b();
        cglib.setCallback(0, new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "HELLO";
            }
        });
*//*        cglib.setCallback(0, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println(method.invoke(obj, args));
                return "frog";
            }
        });*//*
        System.out.println(cglib.sayHello("nihao"));
    }*/

}

interface Interface1 {
    String first();
}

interface Interface2 {
    String second();
}

class Class1 implements Interface1 {
    @Override
    public String first() {
        return "first behaviour";
    }
}

class Class2 implements Interface2 {
    @Override
    public String second() {
        return "second behaviour";
    }
}

interface MixinInterface extends Interface1, Interface2 { }