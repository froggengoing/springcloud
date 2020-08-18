package com.froggengo.zookeeper.curator.self;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class myCuratorExample {
    public static void main(String[] args) {
        SpringApplication.run(myCuratorExample.class,args);
    }
}
