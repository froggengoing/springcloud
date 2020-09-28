package com.froggengo.mybatisplus;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.froggengo.mybatisplus")
public class MybatisPlusMain {
    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusMain.class, args);
    }
}
