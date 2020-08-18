package com.froggengo.practise.aop.proxy;





import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyCglibProxy implements MethodInterceptor {

    public static MyCglibProxy proxy=new MyCglibProxy();

    @SuppressWarnings("unchecked")
    public <T>T getMyProxy(Class<T> clz){
        return  (T) Enhancer.create(clz, this);

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {



        return null;
    }
}
