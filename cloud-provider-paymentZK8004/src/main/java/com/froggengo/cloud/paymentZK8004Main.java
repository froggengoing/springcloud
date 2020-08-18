package com.froggengo.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class paymentZK8004Main {
    public static void main(String[] args) {
        SpringApplication.run(paymentZK8004Main.class,args);
    }
}
