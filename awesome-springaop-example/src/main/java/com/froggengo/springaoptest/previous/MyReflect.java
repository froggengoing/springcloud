package com.froggengo.springaoptest.previous;

/*import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;*/
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyReflect {


    @Test
    public void test(){
        InvocationHandler invocationHandler = new InvocationHandler() {
            Annimal annimal = new Annimal();//实现InvocationHandler负责注入希望代理的对象

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("测试jdkReflect");
                return method.invoke(annimal, args);
            }
        };
        Object proxyInstance = Proxy.newProxyInstance(Annimal.class.getClassLoader(), Annimal.class.getInterfaces(),invocationHandler );

        int sale = ((MyItem) proxyInstance).sale();
        int i = ((MyItem) proxyInstance).hashCode();
        ((MyItem) proxyInstance).say();

/*        byte[] cls = ProxyGenerator.generateProxyClass("$Proxy0", proxyInstance.getClass().getInterfaces());
        File file = new File("D:/$Proxy0.class");
        try (FileOutputStream outputStream = new FileOutputStream(file);){
            outputStream.write(cls);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }
    @Test
    public void testJdClass(){
        InvocationHandler invocationHandler = new InvocationHandler() {
            Annimal annimal = new Annimal();//实现InvocationHandler负责注入希望代理的对象

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("测试jdkReflect");
                return method.invoke(annimal, args);
            }
        };
        $Proxy0 $Proxy0 = new $Proxy0(invocationHandler);
        $Proxy0.sale();
    }
    @Test
    public void testCglib(){
/*        Annimal enhancer =(Annimal)Enhancer.create(Annimal.class, new MethodInterceptor() {
           Annimal annimal=new Annimal();
           @Override
           public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
               System.out.println("测试Cglib");
               return  method.invoke(annimal,args);
           }
       });
        enhancer.sale();
        //final方法无法代理
        enhancer.say();*/

    }
}
