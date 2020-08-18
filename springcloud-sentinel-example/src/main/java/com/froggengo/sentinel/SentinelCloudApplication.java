package com.froggengo.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SentinelCloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(SentinelCloudApplication.class,args);
    }

    @Service
    public class TestService{
        @SentinelResource("service")
        public void sayhello(){
            System.out.println("hello");
        }
        @SentinelResource("service2")
        public void sayhello2(){
            System.out.println("hello");
        }
    }
}
