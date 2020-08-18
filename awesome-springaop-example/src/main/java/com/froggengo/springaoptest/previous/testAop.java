package com.froggengo.springaoptest.previous;


import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.*;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.aspectj.annotation.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;

import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 同个aop中的执行顺序
 * @Around→@Before→@Around→@After执行 ProceedingJoinPoint.proceed() 之后的操作→@AfterRunning(如果有异常→@AfterThrowing)
 * 多个Aop的执行顺序：
 * 通过实现order接口或者添加order注解，order值小的最先执行最后结束，参考同心圆
 *
 */
public class testAop {

    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)
     * @see AbstractAdvisorAutoProxyCreator#findEligibleAdvisors(Class, String)
     * @see AbstractAutoProxyCreator#createProxy(Class, String, Object[], org.springframework.aop.TargetSource)
     * @see BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()
     * 1、获取所有切面：
     *      1、获取继承Advisor接口的类
     *      2、获取BeanFactory所有BeanName判断BeanType是否advisorFactory.isAspect(beanType)
     * 2、获取当前类的切面
     *      1、AopUtils.findAdvisorsThatCanApply(advisorList, Annimal.class);判断候选切面中满足要求的切面
     * 3、创建代理
     */
    @Test
    public void testAopJ(){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader definitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        definitionReader.register(AOPMain.class);
        definitionReader.register(AOPMainIntefacce.class);
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, Object.class, true, false);
        if(beanNames !=null && beanNames.length>0){
            Arrays.stream(beanNames).forEach(n-> System.out.println("BeanName:"+n));
        }else System.out.println("空");

        BeanFactoryAspectJAdvisorsBuilder advisorsBuilder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory);
        List<Advisor> advisors = advisorsBuilder.buildAspectJAdvisors();
        advisors.stream().forEach(n-> System.out.println("advisor:"+n));
        //判断class是否aspectj
        ReflectiveAspectJAdvisorFactory advisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        System.out.println("是否aspectJ注解："+advisorFactory.isAspect(AOPMain.class));
        //
        BeanFactoryAspectInstanceFactory instanceFactory = new BeanFactoryAspectInstanceFactory(beanFactory, "AOPMain");
        List<Advisor> advisorList = advisorFactory.getAdvisors(instanceFactory);
        advisorList.stream().forEach(n-> System.out.println("手动获取所有 Aspectj 切面："+n));
        //
        List<Advisor> advisorsThatCanApply = AopUtils.findAdvisorsThatCanApply(advisorList, Annimal.class);
        advisorsThatCanApply.stream().forEach(n-> System.out.println("Annimal 的切面"+n));
        //创建代理
        Annimal annimal = new Annimal();
        Annimal annimalProxy = (Annimal)autoProxyCreator.postProcessAfterInitialization(annimal, "Annimal");
        annimalProxy.sale();
/*        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setProxyTargetClass(true);
        //proxyFactory.copyFrom();
        proxyFactory.addAdvisors(advisorList);
        proxyFactory.setTargetSource(new SingletonTargetSource(annimal));
        Annimal proxyAnnimal = (Annimal) proxyFactory.getProxy(ClassUtils.getDefaultClassLoader());
        proxyAnnimal.sale();*/

    }

    /**
     *
     * @see AbstractAutoProxyCreator#postProcessAfterInitialization(Object, String)
     * 例一：Spring框架使用 AnnotationAwareAspectJAutoProxyCreator 为Bean创建代理
     * 知识点补充：
     * 1、启动类添加注解@EnableAspectJAutoProxy ,即通过@Import(AspectJAutoProxyRegistrar.class)
     * 2、AspectJAutoProxyRegistrar作为ImportBeanDefinitionRegistrar，为Beanfactory注解 AnnotationAwareAspectJAutoProxyCreator.class
     * 3、AnnotationAwareAspectJAutoProxyCreator可以BeanFactory中获取实现Advisor接口的类，以及带有注解@Aspect的类
     * 4、后接下文postProcessAfterInitialization()创建代理类
     */
    @Test
    public void aopFirst(){
        //1、将@Aspect注解的类添加到BeanFacroty
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader definitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        definitionReader.register(AOPMain.class);
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, Object.class, true, false);
        assert Arrays.asList(beanNames).contains("AOPMain");
        //2、创建切面
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        autoProxyCreator.setProxyTargetClass(true);//必须设置，否则走jdk，这里就报错
        Annimal annimal = new Annimal();
        //如果没有设置true，这里使用接口也可以
        Annimal annimalProxy = (Annimal)autoProxyCreator.postProcessAfterInitialization(annimal, "annimal");
        annimalProxy.sale();
    }
    /**AnnotationAwareAspectJAutoProxyCreator工作原理
     *
     */
    @Test
    public void testAutoProxyCreator(){
        //1、将@Aspect注解的类添加到BeanFacroty
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader definitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        definitionReader.register(AOPMain.class);
        definitionReader.register(AOPMainIntefacce.class);
        String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, Object.class, true, false);
        assert Arrays.asList(beanNames).contains("AOPMain");
        //assert Arrays.asList(beanNames).contains("AOPMainIntefacce");
        //2、获取实现advisor接口的BeanName
        List<Advisor> advisorList=new ArrayList<>();
        String[] advisorNames  = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, Advisor.class, true, false);
        if (advisorNames != null) {
            Arrays.stream(advisorNames).forEach(n->{
                System.out.println("获取实现advisor接口："+n);
                advisorList.add((Advisor) beanFactory.getBean(n));
            } );
        }
        //3、获取@AspectJ 接口的BeanName及切面
        ReflectiveAspectJAdvisorFactory advisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        if (beanNames != null) {
            Arrays.stream(beanNames).filter(n->advisorFactory.isAspect(beanFactory.getType(n))).forEach(n->
            { System.out.println("获取aspectj的beanName:"+n);
            });
        }
        BeanFactoryAspectJAdvisorsBuilder advisorsBuilder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory);
        List<Advisor> advisors = advisorsBuilder.buildAspectJAdvisors();
        advisorList.addAll(advisors);
        //4、满足当前切面的advisor
        advisorList.stream().forEach(n-> System.out.println("所有advisor："+n));
        List<Advisor> advisorsThatCanApply = AopUtils.findAdvisorsThatCanApply(advisorList, Annimal.class);
        advisorsThatCanApply.stream().forEach(n-> System.out.println("满足条件的advisor："+n));
        // 获取增强advice

/*        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        Annimal annimal = new Annimal();
        Annimal annimalProxy = (Annimal)autoProxyCreator.postProcessAfterInitialization(annimal, "annimal");
        annimalProxy.sale();*/

        //手动创建代理
        /*        Annimal annimal = new Annimal();
        ProxyFactory proxyFactoryBean=new ProxyFactory();
        proxyFactoryBean.setTarget(annimal);//设置目标对象
        proxyFactoryBean.addAdvice(new AOPMainIntefacce().getAdvice());//为目标对象织入增强
        Annimal annimalProxy=(Annimal)proxyFactoryBean.getProxy();
        annimalProxy.sale();*/
    }

    /**
     * AspectJ生成Advisor
     *
     */
    @Test
    public void testAdvisorCreate(){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader beanDefinitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
        beanDefinitionReader.register(AOPMain.class);
        //生产AOPmain相应的Advisors
        ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        MetadataAwareAspectInstanceFactory aspectInstanceFactory =new BeanFactoryAspectInstanceFactory(beanFactory, "AOPMain");
        List<Advisor> advisors = aspectJAdvisorFactory.getAdvisors(aspectInstanceFactory);
        advisors.stream().forEach(n-> System.out.println("解析@Aspect获得增强advice"+n));
        /**
         * 分步骤解析，还原内部实现，模拟创建advice
         * 1、找出aspectj注解的方法
         * 2、获取pointCut
         * @see ReflectiveAspectJAdvisorFactory#getAdvisors(org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory)
         */
        Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
        MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
                new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);
        //1、找出非pointcut注解的方法
        final List<Method> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(AOPMain.class, method -> {
            // Exclude pointcuts
            if (AnnotationUtils.getAnnotation(method, Pointcut.class) == null) {
                methods.add(method);
                //System.out.println(method);
            }
        });
        //获取方法对应的pointcut
        //AspectJExpressionPointcut ;
        final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = new Class<?>[] {
                Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};
        List<Annotation> annotationList=new ArrayList<>();
        List<Advice> adviceList=new ArrayList<>();
        methods.stream().forEach(method-> {
            System.out.println("方法名："+method);
            Arrays.stream(ASPECTJ_ANNOTATION_CLASSES).forEach(
                    toLookFor->{
                        Annotation result = AnnotationUtils.findAnnotation(method, (Class<Annotation>) toLookFor);
                        if(result !=null){
                            System.out.println("  AOP注解名："+result+",注解类型："+result.annotationType());
                            annotationList.add(result);
                             AspectJExpressionPointcut ajexp =new AspectJExpressionPointcut(aspectClass, new String[0], new Class<?>[0]);
                            String ex = resolveExpression(result);
                            System.out.println("  切点："+ex);
                            ajexp.setExpression(ex);
                            AbstractAspectJAdvice springAdvice;
                            if(result.annotationType().equals(After.class)){
                                springAdvice = new AspectJAfterAdvice( method, ajexp, aspectInstanceFactory);
                                adviceList.add(springAdvice);
                            }else if(result.annotationType().equals(AfterReturning.class)){
                                springAdvice = new AspectJAfterReturningAdvice(
                                        method, ajexp, aspectInstanceFactory);
                                adviceList.add(springAdvice);
                            }else if(result.annotationType().equals(Before.class)){
                                springAdvice = new AspectJMethodBeforeAdvice(
                                        method, ajexp, aspectInstanceFactory);
                                adviceList.add(springAdvice);
                            }else{
                                System.out.println("不属于AOP注解");
                            }
                        }
                    }
            );

           }
        );
        List<Advisor> advisorList=new ArrayList<>();
        adviceList.forEach(advice-> {
            System.out.println("手动编写增强"+advice);
            DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(advice);
            advisorList.add(defaultPointcutAdvisor);
        });
        Annimal annimal = new Annimal();
        ProxyFactory proxyFactoryBean=new ProxyFactory();
        proxyFactoryBean.setTarget(annimal);//设置目标对象
        //proxyFactoryBean.addAdvice(new AOPMainIntefacce().getAdvice());//为目标对象织入增强
        proxyFactoryBean.addAdvisors(advisorList);
        Annimal annimalProxy=(Annimal)proxyFactoryBean.getProxy();
        annimalProxy.sale();
    }

    private String resolveExpression(Annotation annotation) {
        String[] EXPRESSION_ATTRIBUTES = new String[] {"pointcut", "value"};
        for (String attributeName : EXPRESSION_ATTRIBUTES) {
            Object val = AnnotationUtils.getValue(annotation, attributeName);
            if (val instanceof String) {
                String str = (String) val;
                if (!str.isEmpty()) {
                    return str;
                }
            }
        }
        throw new IllegalStateException("Failed to resolve expression: " + annotation);
    }
}
