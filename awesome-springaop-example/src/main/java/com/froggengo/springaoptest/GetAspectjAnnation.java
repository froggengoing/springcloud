package com.froggengo.springaoptest;

import com.froggengo.springaoptest.previous.AOPMain;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class GetAspectjAnnation {
    /**
     * @see AbstractAspectJAdvisorFactory#findAspectJAnnotationOnMethod(java.lang.reflect.Method)
     * 本示例解析获取，被注释了AspectJ类中，获取method的Before注解属性，
     * 最后创建org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory.AspectJAnnotation 对象
     */
    @Test
    public void test () throws Exception {
        Class<AOPMain> aopMainClass = AOPMain.class;
        Method method = aopMainClass.getMethod("beforaop");
        Before annotation = AnnotationUtils.findAnnotation(method, Before.class);
        Class aClass = loadClass("org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory");
        Class<Annotation> innerClass = loadInnerClass("AspectJAnnotation", aClass);
        Constructor<Annotation> constructor=getGenericContructor(innerClass,Annotation.class);
        //此时创建的是AspectJAnnotation对象
        Object o = constructor.newInstance(annotation);
        //annotation=@org.aspectj.lang.annotation.Before(argNames=, value=log()),
        // annotationType=AtBefore,pointcutExpression=log(),argumentNames=
        System.out.println(ReflectionToStringBuilder.toString(o));
    }

    private <T> Constructor<T> getGenericContructor(Class<T> innerClass, Class<T> annotationClass) throws NoSuchMethodException {
       // Constructor<T> declaredConstructor = innerClass.getDeclaredConstructor();
        Constructor<T> constructor = innerClass.getDeclaredConstructor(annotationClass);
        constructor.setAccessible(true);
        return constructor;
    }

    public Class loadClass(String className) throws Exception {
        Class<?> aClass = Class.forName(className);
        return aClass;
    }
    public Class loadInnerClass(String className,Class parentClass) throws Exception {
        for (Class subClass : parentClass.getDeclaredClasses()) {
            if (subClass.getName().endsWith(className)) {
                return subClass;
            }
        }
        return null;
    }
    public Constructor getConstructor(Class aclass,Class<?>... args) throws NoSuchMethodException {
        Constructor constructor = aclass.getDeclaredConstructor(args);
        constructor.setAccessible(true);
        return constructor;
    }
    public Method getMethod(Class aclass,String name,Class<?>... args) throws NoSuchMethodException {
        Method method = aclass.getMethod(name,args);
        method.setAccessible(true);
        return method;
    }
}
