package com.froggengo.guava.limit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.aopalliance.intercept.Invocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

@Aspect//生成advisor
@Component //加入容器管理
public class LimitAspect {

    //

    static LoadingCache<String, RateLimiter> cache = CacheBuilder
            .newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) throws Exception {
                    return RateLimiter.create(1);//QPS为5
                }
            });

    @Pointcut("@annotation(com.froggengo.guava.limit.ServerLimit)")
    public void serverlimit() {
    }

    /**
     * @Before,能否执行类似joinPoint.proceed();方法？
     */
    @Around("serverlimit()")
    public Object proceed(ProceedingJoinPoint  joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ServerLimit serverLimit = method.getDeclaredAnnotation(ServerLimit.class);
        System.out.println(System.nanoTime()+" "+serverLimit.type());
        String key = serverLimit.key();
        Object obj;
        try {
            RateLimiter rateLimiter = cache.get(key);
            boolean flag = rateLimiter.tryAcquire();
            if(flag){
                obj = joinPoint.proceed();
            }else {
                System.out.println("无法访问，被限流");
                throw new RuntimeException("无法访问，被限流");
            }
        } catch (Throwable e) { //这里捕获了RuntimeException
            throw new RuntimeException("无法访问，被限流");//再次抛出
        }
        return obj;

    }
}
