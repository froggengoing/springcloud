package com.froggengo;


import com.froggengo.autoconfiguration.MybatisAutoConfigutationMyImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Import({MybatisAutoConfigutationMyImpl.class})
@EnableTransactionManagement
public class MybaitspractiseMain {

    public static void main(String[] args) {
        SpringApplication.run(MybaitspractiseMain.class, args);
    }
}
/**
 * @see org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Pointcut, java.lang.Class, boolean)
 * @see org.springframework.transaction.interceptor.TransactionAttributeSourcePointcut#matches(java.lang.reflect.Method, java.lang.Class)
 */