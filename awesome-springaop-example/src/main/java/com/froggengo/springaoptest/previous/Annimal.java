package com.froggengo.springaoptest.previous;

import org.springframework.stereotype.Component;

@Component
public class Annimal implements MyItem {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @log
    @Override
    public int sale(){
        System.out.println("annimal销售");
        return 1000;
    }
    @Override
     public final int say(){
        System.out.println("你好say Hello");
        return 999;
    }

}
