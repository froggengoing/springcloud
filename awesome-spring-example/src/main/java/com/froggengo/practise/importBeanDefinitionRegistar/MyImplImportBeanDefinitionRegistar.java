package com.froggengo.practise.importBeanDefinitionRegistar;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.Arrays;
import java.util.Set;

public class MyImplImportBeanDefinitionRegistar implements ImportBeanDefinitionRegistrar {//SimpleMetadataReaderFactory

    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new MyClassPathScanner(registry);
        scanner.addIncludeFilter(( metadataReader, metadataReaderFactory)-> {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            Resource resource = metadataReader.getResource();
            return classMetadata.getClassName().toLowerCase().contains("student");
        });
        int scan = scanner.scan("com.froggengo.practise.importBeanDefinitionRegistar");
    }

    class MyClassPathScanner extends ClassPathBeanDefinitionScanner{

        public MyClassPathScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }
        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

            if (beanDefinitions.isEmpty()) {
                logger.warn("No MyBatis mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
            } else {
                processBeanDefinitions(beanDefinitions);
            }

            return beanDefinitions;
        }

        private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
            GenericBeanDefinition definition;
            for(BeanDefinitionHolder n:beanDefinitions){
                if( n.getBeanName().toLowerCase().contains("student")){
                    definition = (GenericBeanDefinition) n.getBeanDefinition();
                    //根据teacher，调用setTeacher
                    definition.getPropertyValues().addPropertyValue("teacher",new RuntimeBeanReference(Teacher.class));
                }
            };
        }
    }
}
