package com.froggengo.cloud;

import cn.hutool.db.sql.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderCS80Main {
    public static void main(String[] args) {
        SpringApplication.run(OrderCS80Main.class,args);
    }
}
