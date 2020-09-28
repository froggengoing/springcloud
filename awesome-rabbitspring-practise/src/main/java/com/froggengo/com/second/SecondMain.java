package com.froggengo.com.second;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecondMain {
    public static void main(String[] args) {
        System.out.println("second");
        SpringApplication.run(SecondMain.class,args);
    }
}
