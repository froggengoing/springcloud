package com.springtest;

import com.froggengo.practise.SpringMain;
import com.froggengo.practise.importBeanDefinitionRegistar.Student;
import com.froggengo.practise.importBeanDefinitionRegistar.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P5_BeanFactoryPostProcess implements BeanFactoryAware {
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testBeanFactoryPostProcess (){
        Map<String, BeanFactoryPostProcessor> map = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        map.forEach((k,v)->{
            System.out.println(v.getClass());
        });
    }

    /**
     * @see PropertySourcesPlaceholderConfigurer
     */
    @Test
    public void testPropertySourcesPlaceholderConfigurer (){
        Student bean = beanFactory.getBean(Student.class);
        System.out.println(bean.getName());
        System.out.println(bean.getClassName());
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
