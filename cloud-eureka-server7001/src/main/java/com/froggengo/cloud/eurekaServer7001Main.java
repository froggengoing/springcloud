package com.froggengo.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 作者回答： https://stackoverflow.com/questions/31976236/whats-the-difference-between-enableeurekaclient-and-enablediscoveryclient
 * 博客更新版本分析： https://blog.csdn.net/boling_cavalry/article/details/82668480
 * 上述讨论的是enablediscoveryclient与EnableEurekaClient的区别
 */
@SpringBootApplication
@Slf4j
@EnableEurekaServer
public class eurekaServer7001Main {
    public static void main(String[] args) {
        SpringApplication.run(eurekaServer7001Main.class,args);
    }
}
