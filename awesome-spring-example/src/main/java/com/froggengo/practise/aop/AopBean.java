package com.froggengo.practise.aop;

import org.springframework.stereotype.Service;

@Service
public class AopBean {

    String name;
    public void hello(){
        System.out.println("AopBean");
    }

    public int sale(){
        System.out.println("annimal销售");
        return 1000;
    }
}
