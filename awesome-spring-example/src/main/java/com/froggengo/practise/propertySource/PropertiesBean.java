package com.froggengo.practise.propertySource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PropertiesBean {
    @Autowired
    MyCustomMapProperties myCustomProperties;
    @PostConstruct
    public void test(){
        myCustomProperties.getMap().forEach((k,v)->{
            System.out.println(k+"="+v);
        });
    }
}
