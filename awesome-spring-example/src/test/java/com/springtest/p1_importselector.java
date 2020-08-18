package com.springtest;


import com.froggengo.practise.Payment.mapper.NotMapperButService;
import com.froggengo.practise.SpringMain;
import com.froggengo.practise.importselector.HelloServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class p1_importselector {//AnnotationAwareAspectJAutoProxyCreator
    @Autowired
    BeanFactory beanFactory;
    @Test
    public void test (){
        HelloServiceImpl bean = beanFactory.getBean(HelloServiceImpl.class);
        bean.setName("frog");
        bean.setWorld("hello");
        bean.say();
    }
    @Test
    public void testDeferredImportSelector (){
        List<String> factories = SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.getClass().getClassLoader());
        factories.stream().forEach(System.out::println);
    }
}

