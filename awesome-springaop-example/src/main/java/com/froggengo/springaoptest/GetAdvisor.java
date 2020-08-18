package com.froggengo.springaoptest;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

/**
 * @see org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors(org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory)
 * 通过GetAspectjAnnation获取的AspectJAnnotation，进一步封装成Advisor
 * @see org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl;
 */
public class GetAdvisor{
    @Test
    public void test (){
       //1、 AspectJExpressionPointcut
        //2、new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
        //				this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }
}
