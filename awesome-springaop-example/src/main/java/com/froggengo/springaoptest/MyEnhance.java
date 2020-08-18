package com.froggengo.springaoptest;

import org.junit.Test;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.*;
import org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Driver;
import java.util.Arrays;
import java.util.List;
/**
 * System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\class");  --该设置用于输出cglib动态代理产生的类
 * System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");   --该设置用于输出jdk动态代理产生的类
 * System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");--新版jdk
 * */
/**
 * 列1：<a>https://www.baeldung.com/cglib</a>
 * FixedValue 指代理的方法返回固定值，要求方法返回值类型与FixedValue类型值一致
 * 所以掉用service.lengthOfName()报错，因为返回类型不一致
 * cglib共有6中callback类型
 *
 * @see FixedValue
 * @see InvocationHandler
 * @see LazyLoader
 * @see MethodInterceptor
 * @see Dispatcher
 * @see NoOp
 */
public class MyEnhance {

    @Test
    public void test (){
        DriveCar proxy = (DriveCar) Enhancer.create(DriveCar.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("这里是代理");
                return methodProxy.invokeSuper(o, objects);//invoke会报错
            }
        });
        proxy.driver("法拉利");
    }

    /**
     * MethodBeforeAdvice继承advice
     * MethodBeforeAdviceInterceptor继承org.aopalliance.intercept.MethodInterceptor
     * 而 Enhancer 需要的            是 org.springframework.cglib.proxy.MethodInterceptor
     * 所有需要一个中间的类似于适配器的东西就是：org.springframework.aop.framework.CglibAopProxy内部实现类比如 DynamicAdvisedInterceptor
     * CglibAopProxy
     */
    @Test
    public void testSpringAdvice (){
        MethodBeforeAdvice beforeAdvice = new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("MethodBeforeAdvice");
            }
        };
        MethodBeforeAdviceInterceptor interceptor = new MethodBeforeAdviceInterceptor(beforeAdvice);
        //这里无法创建代理对象
        //Enhancer.create(DriveCar.class,interceptor)
    }

    /**
     * DynamicAdvisedInterceptor是CglibAopProxy内部类，
     * 继承org.springframework.cglib.proxy.MethodInterceptor，并封装了切面advisor、切面advice等
     */
    @Test
    public void testReflect () throws Exception {
        //1、准备切面
        AfterReturningAdvice returningAdvice = new AfterReturningAdvice() {
            @Override
            public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
                System.out.println("AfterReturningAdvice");
            }
        };
        MethodBeforeAdvice beforeAdvice = new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("MethodBeforeAdvice");
            }
        };
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.addAdvice(returningAdvice);
        advisedSupport.addAdvice(beforeAdvice);
        advisedSupport.setTarget(new DriveCar());//必须设置bean对象，在spring中由容器在完成初始化后设置
        Object instance = null;
        Class<?> aClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");
        for (Class n: aClass.getDeclaredClasses()){
            if(n.getName().indexOf("DynamicAdvisedInterceptor")!=-1) {
                try {
                    Constructor<?> constructor = n.getDeclaredConstructor(AdvisedSupport.class);
                    constructor.setAccessible(true);//私有类，必须设置访问权限
                    instance = constructor.newInstance(advisedSupport);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        DriveCar driveCar = (DriveCar) Enhancer.create(DriveCar.class, (MethodInterceptor) instance);
        driveCar.driver("宝马");
    }

    /**
     * 怎么判断 那些增强可以使用呢
     * new DefaultAdvisorChainFactory();
     */
    @Test
    public void testAdvisor () throws Exception {
        MethodBeforeAdvice beforeAdvice = new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("MethodBeforeAdvice");
            }
        };
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.addAdvice(beforeAdvice);
        //
        DefaultAdvisorChainFactory factory = new DefaultAdvisorChainFactory();
        Class<DriveCar> driverClass = DriveCar.class;
        Method method = driverClass.getMethod("driver", String.class);
        List<Object> interceptors = factory.getInterceptorsAndDynamicInterceptionAdvice(advisedSupport, method, driverClass);
    }

    /**
     * spring 把上述逻辑封装在ProxyFactory中
     */
    @Test
    public void testProxyFactory (){
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(new DriveCar());
        factory.addAdvice(new AfterReturningAdvice() {
            @Override
            public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
                System.out.println("AfterReturning");
            }
        });
        factory.setProxyTargetClass(true);
        DriveCar proxy = (DriveCar) factory.getProxy();
        proxy.driver("宾利");
    }
}
