import com.froggengo.controller.HelloController;
import com.froggengo.mapper.PaymentMapper;
import com.froggengo.mapper.PaymentMapperAopTestImpl;
import org.aopalliance.aop.Advice;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.BeforeAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Method;
import java.net.Proxy;

public class AopTransaction {
    @Test
    public void test (){
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        boolean isAop = AopUtils.canApply(advisor, HelloController.class);
        /**
         * @see AbstractAutoProxyCreator#postProcessAfterInitialization(java.lang.Object, java.lang.String)
         * @see AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
         *
         * @see org.springframework.transaction.interceptor.TransactionAttributeSourcePointcut#matches(java.lang.reflect.Method, java.lang.Class)
         * @see AbstractFallbackTransactionAttributeSource#getTransactionAttribute(java.lang.reflect.Method, java.lang.Class)
         * @see AnnotationTransactionAttributeSource#findTransactionAttribute(java.lang.reflect.Method)
         * @see org.springframework.transaction.annotation.SpringTransactionAnnotationParser.parseTransactionAnnotation(java.lang.reflect.AnnotatedElement)
         * @see BeanFactoryTransactionAttributeSourceAdvisor
         * @see AbstractAutoProxyCreator#createProxy(java.lang.Class, java.lang.String, java.lang.Object[], org.springframework.aop.TargetSource)
    */

    }
    public void beforeMethod(){
        System.out.println("im before");
    }

    @Test
    public void testAdivsorSupprot (){
        ProxyFactory proxyFactory = new ProxyFactory(new PaymentMapperAopTestImpl());
        proxyFactory.addInterface(PaymentMapper.class);
        proxyFactory.addAdvice(new MethodBeforeAdvice(){

            @Override
            public void before(Method method, Object[] args, Object target) throws Throwable {
                System.out.println("im before");
            }
        });
        proxyFactory.addAdvice(new AfterReturningAdvice(){
            @Override
            public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
                System.out.println("after");
            }
        });
        //proxyFactory.addAdvisors(advisedSupport.getAdvisors());
        PaymentMapper proxy = (PaymentMapper)proxyFactory.getProxy();
        proxy.getList(null);

        TransactionAspectSupport t;
    }
    @Test
    public void testProxyFactory (){
        ProxyFactory factory = new ProxyFactory();

    }

}
