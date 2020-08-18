package com.springtest;

import com.froggengo.practise.SpringMain;
import com.froggengo.practise.importBeanDefinitionRegistar.Student;
import com.froggengo.practise.importBeanDefinitionRegistar.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class p2_ImportBeanDefinitionRegistrar {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testProxyCreator (){//AnnotationAwareAspectJAutoProxyCreator
        String[] definitionNames = beanFactory.getBeanDefinitionNames();
        Arrays.stream(definitionNames).forEach(System.out::println);
        Object bean = beanFactory.getBean("org.springframework.aop.config.internalAutoProxyCreator");
        System.out.println("");
    }

    /**
     * AopConfigUtils 工具类直接注册 AnnotationAwareAspectJAutoProxyCreator
     */
    @Test
    public void testAopConfigUtils (){
        //AspectJAutoProxyRegistrar
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(beanFactory);
        Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(System.out::println);
        //注册 AnnotationAwareAspectJAutoProxyCreator
        BeanDefinition bean = (BeanDefinition)beanFactory.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
        System.out.println(bean.hasPropertyValues());
        bean.getPropertyValues().stream().forEach(n-> System.out.println(n.getValue()+"=="+n.getName()));
        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(beanFactory);
        System.out.println("强制使用Cglib");
        bean.getPropertyValues().stream().forEach(n-> System.out.println(n.getValue()+"=="+n.getName()));
    }

    @Test
    public void testSelfImpl (){
        Student bean = beanFactory.getBean(Student.class);
        Teacher teacher = bean.getTeacher();
        System.out.println(teacher.getName());
        System.out.println(teacher.getAge());
    }

}
