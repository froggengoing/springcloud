package com.froggengo.practise.aop;

import org.springframework.stereotype.Component;

@Component
public class Annimal implements MyItem {
    @Override
    public int sale(){
        System.out.println("annimal销售");
        return 1000;
    }
    @Override
    public final int hello(){
        System.out.println("你好say Hello");
        return 999;
    }

}
