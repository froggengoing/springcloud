package com.froggengo.autoconfiguration;

import org.springframework.beans.factory.FactoryBean;

public class MyMapperFactoryBean<T> extends MySqlSessionDaoSupport implements FactoryBean<T> {
    private Class<T> mapperInterface;

    private boolean addToConfig = true;
    //否则报错：Bean property 'addToConfig' is not writable or has an invalid setter method.
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    public MyMapperFactoryBean() {
        //intentionally empty
    }


    public MyMapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        return getSqlSession().getMapper(this.mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
