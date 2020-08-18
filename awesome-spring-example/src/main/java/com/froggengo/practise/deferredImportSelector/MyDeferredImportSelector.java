package com.froggengo.practise.deferredImportSelector;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public  class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports (AnnotationMetadata importingClassMetadata) {
        return new String []{BeanForDeferred.class.getName()};
    }
}
