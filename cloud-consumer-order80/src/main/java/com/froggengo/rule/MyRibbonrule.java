package com.froggengo.rule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRibbonrule {

    @Bean
    public IRule getMyRule(){
        return new RandomRule();
    }

}
