package com.froggengo.practise.aop;

import org.junit.Test;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.aspectj.annotation.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 同个aop中的执行顺序
 * @Around→@Before→@After→@Around执行 ProceedingJoinPoint.proceed() 之后的操作→@AfterRunning(如果有异常→@AfterThrowing)
 * 多个Aop的执行顺序：
 * 通过实现order接口或者添加order注解，order值小的最先执行最后结束，参考同心圆
 *
 */
public class TestAop {
    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     * @see AbstractAdvisorAutoProxyCreator#findEligibleAdvisors(java.lang.Class, java.lang.String)
     * @see AbstractAutoProxyCreator#createProxy(java.lang.Class, java.lang.String, java.lang.Object[], org.springframework.aop.TargetSource)
     * @see BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()
     * 1、获取所有切面：
     *      1、获取继承Advisor接口的类
     *      2、获取BeanFactory所有BeanName判断BeanType是否advisorFactory.isAspect(beanType)
     * 3、获取当前类的切面
     *      1、AopUtils.findAdvisorsThatCanApply(advisorList, Annimal.class);判断候选切面中满足要求的切面
     * 4、创建代理
     */
    @Test
    public void test (){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        //1、注册切面类
        AnnotatedBeanDefinitionReader definitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        definitionReader.register(AopAspectj.class);
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, Object.class, true, false);
        if(beanNames !=null && beanNames.length>0){
            Arrays.stream(beanNames).forEach(n-> System.out.println("BeanName:"+n));
        }else System.out.println("空");
        //2、获取所有advisor
        BeanFactoryAspectJAdvisorsBuilder advisorsBuilder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory);
        List<Advisor> advisors = advisorsBuilder.buildAspectJAdvisors();
        advisors.stream().forEach(n-> System.out.println("advisor:"+n));
        //2.1、判断是否@Aspectj
        ReflectiveAspectJAdvisorFactory advisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        System.out.println("是否aspectJ注解："+advisorFactory.isAspect(AopAspectj.class));
        //2.2 手动获取某个Bean的所有 Aspectj 切面
        BeanFactoryAspectInstanceFactory instanceFactory = new BeanFactoryAspectInstanceFactory(beanFactory, "aopAspectj");
        List<Advisor> advisorList = advisorFactory.getAdvisors(instanceFactory);
        advisorList.stream().forEach(n-> System.out.println("手动获取所有 Aspectj 切面："+n));
        //3.获取当前bean的切面
        List<Advisor> advisorsThatCanApply = AopUtils.findAdvisorsThatCanApply(advisorList, Annimal.class);
        advisorsThatCanApply.stream().forEach(n-> System.out.println("Annimal 的切面"+n));
        //4.创建代理
        definitionReader.register(AopBean.class);
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(new AopBean());
        AopBean aopBean = (AopBean) beanWrapper.getWrappedInstance();
        //Object aopBean = autoProxyCreator.postProcessAfterInitialization(AopBean.class, "aopBean");
        AopBean annimalProxy = (AopBean)autoProxyCreator.postProcessAfterInitialization(aopBean, "aopBean");
        annimalProxy.sale();
    }
    @Test
    public void testAop (){

    }

}
