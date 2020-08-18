package com.froggengo.practise.webmvc.servletContainerInitializer;

import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

@HandlesTypes(MyHandlesTypeImpl.class)
public class MyServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> clz, ServletContext ctx) throws ServletException {
        System.out.println("我是ServletContainerInitializer");
        for (Class<?> cz:clz){
            if(cz.isAssignableFrom(UseForHandleTypeInterface.class)){
                try {
                    UseForHandleTypeInterface ht = (UseForHandleTypeInterface) ReflectionUtils.accessibleConstructor(cz).newInstance();
                    ht.saySomething();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
