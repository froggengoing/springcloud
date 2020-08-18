package com.froggengo.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@Slf4j
@EnableEurekaServer
public class eurekaServer7001Main {
    public static void main(String[] args) {
        SpringApplication.run(eurekaServer7001Main.class,args);
    }
}
