package com.springtest;

import com.froggengo.practise.SpringMain;
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
public class p3_deferredImportSelector {
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Test
    public void testAutoConfigurationImportSelector (){
        String[] definitionNames = beanFactory.getBeanDefinitionNames();
        Arrays.stream(definitionNames).forEach(System.out::println);
        ProxyTransactionManagementConfiguration bean = beanFactory.getBean(ProxyTransactionManagementConfiguration.class);
        System.out.println(bean);
    }
}
