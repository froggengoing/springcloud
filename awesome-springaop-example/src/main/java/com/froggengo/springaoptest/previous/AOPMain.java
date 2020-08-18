package com.froggengo.springaoptest.previous;

import org.aspectj.lang.annotation.*;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class AOPMain {

    @Pointcut("@annotation(com.froggengo.springaoptest.previous.log)")
    public void log(){
    }
    @Around("log()")
    public void aroundAop(){
        System.out.println("Around增强");
    }
    @Before("log()")
    public void beforaop(){
        System.out.println("before前置增强");
    }

    @After("log()")
    public void afteraop(){
        System.out.println("afteraop后置增强");
    }

    @AfterReturning("execution(* com.froggengo.springaoptest.previous.people.*(..))")
    public void aterReturingaop(){
        System.out.println("afterReturn");
    }
}
