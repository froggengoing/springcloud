package com.springtest;

import com.froggengo.practise.SpringMain;
import com.froggengo.practise.importBeanDefinitionRegistar.Student;
import com.froggengo.practise.importBeanDefinitionRegistar.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;

import java.util.Arrays;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P4_BeanDefinitionRegistryPostPocessor {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testBeanDefinitionRegistryPostPocessor (){
        Student bean = beanFactory.getBean(Student.class);
        Teacher teacher = bean.getTeacher();
        System.out.println(teacher.getName());
        System.out.println(teacher.getAge());
        //MyimplBeanDefinitionRegistryPostProcessor中再次修属性值
        System.out.println(bean.getClassName());
    }

}