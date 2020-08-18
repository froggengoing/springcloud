package com.froggengo.cloud.controller;

import com.froggengo.cloud.server.PaymentFallBackServer;
import com.froggengo.cloud.server.PaymentServer;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OrderHystrixSelfController {

    @Value("${server.port}")
    private String serverPort;
    @Autowired
    PaymentServer paymentServer;

    @GetMapping("/consumer/payment/selfok/{id}")
    public String paymentInfo_ok(@PathVariable("id") int id){
        return paymentServer.paymentInfo_ok(id);
    }

    /**
     * 服务超时，通过本服务中服务接口子类实现完成处理
     * 注意开启
     * @see PaymentFallBackServer#payentInfo_Timeout(int)
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/selftimeout/{id}")
    public  String payentInfo_Timeout(@PathVariable("id") int id){
        return paymentServer.payentInfo_Timeout(id);
    }
}
