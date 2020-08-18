package com.froggengo.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerMain3344 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerMain3344.class,args);
        //http://127.0.0.1:3344/config/test/master
        //http://127.0.0.1:3344/master/config-prod.yml
        //http://127.0.0.1:3344/dev/config-dev.yml
    }
}
