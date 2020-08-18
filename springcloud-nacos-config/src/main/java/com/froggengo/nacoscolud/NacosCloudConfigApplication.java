package com.froggengo.nacoscolud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NacosCloudConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosCloudConfigApplication.class, args);
    }
}
