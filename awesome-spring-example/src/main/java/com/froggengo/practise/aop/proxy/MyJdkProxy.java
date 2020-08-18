package com.froggengo.practise.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyJdkProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
