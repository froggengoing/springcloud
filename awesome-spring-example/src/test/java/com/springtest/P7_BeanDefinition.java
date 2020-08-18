package com.springtest;

import com.froggengo.practise.SpringMain;
import com.froggengo.practise.importBeanDefinitionRegistar.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P7_BeanDefinition {
    @Resource
    DefaultListableBeanFactory beanFactory;
    @Test
    public void test (){
        BeanDefinition definition = beanFactory.getBeanDefinition("teacher");
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        Arrays.stream(propertyValues.getPropertyValues()).forEach(System.out::println);
    }
}
