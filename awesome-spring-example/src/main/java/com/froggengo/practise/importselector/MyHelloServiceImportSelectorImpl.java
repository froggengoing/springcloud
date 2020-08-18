package com.froggengo.practise.importselector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class MyHelloServiceImportSelectorImpl implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        System.out.println("我是 ImportSelector，selectImports()");
        return new String[]{ HelloServiceImpl.class.getName()};
    }
}
