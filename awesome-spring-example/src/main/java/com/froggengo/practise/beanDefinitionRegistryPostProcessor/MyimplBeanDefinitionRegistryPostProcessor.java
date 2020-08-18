package com.froggengo.practise.beanDefinitionRegistryPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Arrays;

public class MyimplBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("我是BeanDefinitionRegistryPostProcessor，postProcessBeanDefinitionRegistry()");
        //Arrays.stream(registry.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("#################### 开始修改  ####################");
        BeanDefinition definition = registry.getBeanDefinition("student");
        definition.getPropertyValues().addPropertyValue("className","初三一班");
        //使用占位符测试 PropertySourcesPlaceholderConfigurer
        definition.getPropertyValues().addPropertyValue("name","${com.froggengo.test}");
        System.out.println("#################### 结束修改  ####################");
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("我是BeanFactoryPostProcessor");
    }
}
