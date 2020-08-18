package com.springtest;

import com.froggengo.practise.SpringMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P8_InstantiationAwareBeanPostProcessor {
    @Resource
    DefaultListableBeanFactory beanFactory;
    @Test
    public void test (){
        Map<String, InstantiationAwareBeanPostProcessor> map = beanFactory.getBeansOfType(InstantiationAwareBeanPostProcessor.class);
        map.forEach((k,v)->{
            System.out.println(v.getClass( ));
        });
    }
}
