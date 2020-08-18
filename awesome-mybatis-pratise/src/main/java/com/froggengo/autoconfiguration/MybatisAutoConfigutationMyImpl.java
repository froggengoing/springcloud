package com.froggengo.autoconfiguration;

import com.froggengo.MyAnnation.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
//必须使用@Import(),为什么？
public class MybatisAutoConfigutationMyImpl implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    private static Logger log = LoggerFactory.getLogger(MybatisAutoConfigutationMyImpl.class);
    BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("通过BeanFactoryAware自动注入beanFactory");
        this.beanFactory=beanFactory;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        System.out.println("registerBeanDefinitions");
        log.info("Searching for mappers annotated with @Mapper");
        ClassPathMapperScannerMyImpl scanner = new ClassPathMapperScannerMyImpl(registry);
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        scanner.setAnnotationClass(MyMapper.class);
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(packages));
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeanDefinitions(importingClassMetadata,registry,null);

    }
}
