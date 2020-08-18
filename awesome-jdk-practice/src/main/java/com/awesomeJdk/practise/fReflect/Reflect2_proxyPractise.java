package com.awesomeJdk.practise.fReflect;

import com.awesomeJdk.practise.fReflect.common.Annimal;
import com.awesomeJdk.practise.fReflect.common.MyItem;
import com.awesomeJdk.practise.fReflect.common.parentlog;
import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class Reflect2_proxyPractise {


    @Test
    public void test() throws Exception {
        InvocationHandler invocationHandler = new InvocationHandler() {
            Annimal annimal = new Annimal();//实现InvocationHandler负责注入希望代理的对象

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("测试jdkReflect");
                return method.invoke(annimal, args);
            }
        };
        MyItem proxyInstance = ((MyItem) Proxy.newProxyInstance(Annimal.class.getClassLoader(), Annimal.class.getInterfaces(), invocationHandler));

        int sale = proxyInstance.sale();
        int i = proxyInstance.hashCode();
        proxyInstance.say();
        /**
         * 测试代理类的元信息
         */
        Class<? extends MyItem> aClass = proxyInstance.getClass();
        System.out.println(aClass.getName());
        Method method = aClass.getMethod("sale");
        parentlog annotation = method.getAnnotation(parentlog.class);
        System.out.println("父类注解"+annotation);
        parentlog parentlog = aClass.getAnnotation(parentlog.class);
        System.out.println(parentlog);
        /**
         * 结论无法获取接口上的注解
         */
        System.out.println("--"+Annimal.class.getAnnotation(parentlog.class));
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
    public void testCglib(){
        Annimal enhancer =(Annimal) Enhancer.create(Annimal.class, new MethodInterceptor() {
            Annimal annimal=new Annimal();
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("测试Cglib");
                return  method.invoke(annimal,args);
            }
        });
        enhancer.sale();
        //final方法无法代理
        enhancer.say();

    }
}

