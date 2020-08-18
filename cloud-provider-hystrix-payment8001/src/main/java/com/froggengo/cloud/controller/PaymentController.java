package com.froggengo.cloud.controller;

import com.froggengo.cloud.service.PaymentSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    PaymentSerivce paymentSerivce;
    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentinfo_OK(@PathVariable("id") int id){
        String result=paymentSerivce.paymentInfo_ok(id);
        log.info("端口"+serverPort+","+"结果："+result);
        return result;
    }
    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentinfo_Timeout(@PathVariable("id") int id){
        String result=paymentSerivce.paymentInfo_Timeout(id);
        log.info("端口"+serverPort+","+"结果："+result);
        return result;
    }

    //服务熔断
    @GetMapping("/payment/circuit/{id}")
    public String paymentcircuitBreaker(@PathVariable("id")int id){
        String res = paymentSerivce.paymentcircuitBreaker(id);
        log.info("****结果"+res);
        return res;
    }
}
