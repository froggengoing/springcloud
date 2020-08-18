package com.froggengo.autoconfiguration;

import com.froggengo.MyAnnation.MyMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.Set;

public class ClassPathMapperScannerMyImpl extends ClassPathBeanDefinitionScanner {

    private Class<MyMapper> annotationClass;
    //mapper的真实对象,自定义实现
    private MyMapperFactoryBean<?> mapperFactoryBean = new MyMapperFactoryBean<Object>();

    private boolean addToConfig = true;
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }


    private String sqlSessionFactoryBeanName;
    private SqlSessionFactory sqlSessionFactory;

    public ClassPathMapperScannerMyImpl(BeanDefinitionRegistry registry) {
        super(registry);
    }


    public void setAnnotationClass(Class<MyMapper> annotationClass) {
        this.annotationClass=annotationClass;
    }

    /**
     * ClassPathBeanDefinitionScanner默认只注册component和 ManagedBean
     * 如需注册其他注解或接口，需要使用addIncludeFilter()添加，
     * 还必须重写isCandidateComponent()
     */
    public void registerFilters() {
        //这里只添加@MyMapper类
        addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }



    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        processBeanDefinitions(beanDefinitions);
        return beanDefinitions;
    }

    /**
     *  问题：
     *      1. GenericBeanDefinition和BeanDefinitionHolder区别
     * @param beanDefinitions
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = ((GenericBeanDefinition) holder.getBeanDefinition());
            //修改beandefintion的beanclass为mapperFactoryBean
            ConstructorArgumentValues constructorArgumentValues = definition.getConstructorArgumentValues();
            constructorArgumentValues.addGenericArgumentValue(definition.getBeanClassName());
            definition.setBeanClass(mapperFactoryBean.getClass());

            definition.getPropertyValues().addPropertyValue("addToConfig",addToConfig);

            //调试发现，这里均为null
            if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
                definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
            } else if (this.sqlSessionFactory != null) {
                definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
            }

            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            definition.setLazyInit(false);
        }

    }
}
