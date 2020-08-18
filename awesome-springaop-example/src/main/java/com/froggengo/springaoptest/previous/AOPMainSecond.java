package com.froggengo.springaoptest.previous;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AOPMainSecond {

/*    @Pointcut("@annotation(com.froggengo.springaoptest.previous.log)")
    public void log(){
    }
    @Before("log()")
    public void beforaop(){
        System.out.println("before前置增强");
    }

    @After("log()")
    public void afteraop(){
        System.out.println("afteraop后置增强");
    }
*/

    @AfterReturning("execution(* com.froggengo.springaoptest.DriveCar.*(..))")
    public void aterReturingaop(){
        System.out.println("afterReturn");
    }
}
