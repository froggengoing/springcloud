package com.froggengo.nacoscolud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    //@Value("${useLocalCache}")
    private boolean useLocalCache;
    @Value("${count}")
    private int count ;
    @Value("${test.cloudvalue}")
    private int localCount;

    /**
     * http://localhost:8080/config/get
     */
    @RequestMapping("/get")
    public boolean get() {
        return useLocalCache;
    }
    @RequestMapping("/count")
    public int getCount() {
        return count;
    }
    @RequestMapping("/localCount")
    public int getLocalCount(){
        return localCount;
    }
}