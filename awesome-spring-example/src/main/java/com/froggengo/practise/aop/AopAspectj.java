package com.froggengo.practise.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopAspectj {

/*    //定义切点
    @Pointcut("execution(* com.froggengo.practise.aop.AopBean.*(..))")
    public void pointCut() {}*/

    //前置处理
    @Before("execution(* com.froggengo.practise.aop.*.sale(..))")
    public void auth() {
        System.out.println("模拟权限检查……");
    }
}
