package com.springtest;

import com.froggengo.practise.SpringMain;
import com.froggengo.practise.aop.Annimal;
import com.froggengo.practise.aop.AopBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)
public class P6_AOP {
    @Resource
    DefaultListableBeanFactory beanFactory;
    @Test
    public void test (){
        AopBean bean = beanFactory.getBean(AopBean.class);
        bean.sale();
        Annimal bean1 = beanFactory.getBean(Annimal.class);
        bean1.sale();
    }
}
