package com.froggengo.practise.importBeanDefinitionRegistar;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImplImportBeanDefinitionRegistar2 implements ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        System.out.println("我是 ImportBeanDefinitionRegistrar，registerBeanDefinitions()");
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Student.class);
        beanDefinition.getPropertyValues().addPropertyValue("teacher",new RuntimeBeanReference(Teacher.class));
        registry.registerBeanDefinition("student",beanDefinition);
    }
}
