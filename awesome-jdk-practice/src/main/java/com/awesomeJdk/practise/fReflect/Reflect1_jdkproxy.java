package com.awesomeJdk.practise.fReflect;


import com.awesomeJdk.practise.fReflect.common.Hello;
import com.awesomeJdk.practise.fReflect.common.HelloImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Reflect1_jdkproxy {
    public static void main(String[] args) {
        HelloImpl hello = new HelloImpl();
        Hello hello1 = (Hello) Proxy.newProxyInstance(Reflect1_jdkproxy.class.getClassLoader(), new Class[]{Hello.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代理前");
                Proxy proxy1 = (Proxy) proxy;
                //System.out.println(proxy1.toString());
                //method.invoke(proxy);如果这里使用proxy会造成嵌套调用死循环
                method.invoke(hello);
                System.out.println("代理后");
                return null;
            }
        });
        hello1.helloWorld();

    }
}
