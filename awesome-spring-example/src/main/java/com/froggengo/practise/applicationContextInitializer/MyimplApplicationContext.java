package com.froggengo.practise.applicationContextInitializer;

import com.froggengo.practise.beanDefinitionRegistryPostProcessor.MyimplBeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 三种注册方式：
 * 1、resources/META-INF/spring.factories
 * 2、yml中指定:context.initializer.classes
 * 3、调用run()方法之前，先调用springApplication.addInitializers();
 */
public class MyimplApplicationContext implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("我是MyimplApplicationContext，initialize()");
        //这样导致MyimplBeanDefinitionRegistryPostProcessor的级别和 ConfigurationClassPostProcessor
        //执行MyimplBeanDefinitionRegistryPostProcessor时，student还没有注册
        //applicationContext.addBeanFactoryPostProcessor(new MyimplBeanDefinitionRegistryPostProcessor());
    }
}
