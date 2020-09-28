package com.froggengo.practise;

import com.froggengo.practise.beanDefinitionRegistryPostProcessor.MyimplBeanDefinitionRegistryPostProcessor;
import com.froggengo.practise.deferredImportSelector.MyDeferredImportSelector;
import com.froggengo.practise.importBeanDefinitionRegistar.MyImplImportBeanDefinitionRegistar2;
import com.froggengo.practise.importselector.MyHelloServiceImportSelectorImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({MyHelloServiceImportSelectorImpl.class,
        MyImplImportBeanDefinitionRegistar2.class,
        MyimplBeanDefinitionRegistryPostProcessor.class,
        MyDeferredImportSelector.class})
@MapperScan(basePackages = {"com.froggengo.practise.*.mapper"})
public class SpringMain {


    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class,args);

    }
}
