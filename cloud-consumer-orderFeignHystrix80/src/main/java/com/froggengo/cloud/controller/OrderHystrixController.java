package com.froggengo.cloud.controller;

import com.froggengo.cloud.server.PaymentServer;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
//指定全局服务降级
@DefaultProperties(defaultFallback = "paymentInfo_global_TimeoutHandler",commandProperties={
        @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
})//这里很奇怪如果没有设置超时时间限制为3000，payentInfo_global_Timeout也会跳转至异常。
//但是openfeign的超时异常已经设置为5秒了。
public class OrderHystrixController {

    @Value("${server.port}")
    private String serverPort;
    @Autowired
    PaymentServer paymentServer;

    @GetMapping("/consumer/payment/ok/{id}")
    public String paymentInfo_ok(@PathVariable("id") int id){
        return paymentServer.paymentInfo_ok(id);
    }


    @GetMapping("/consumer/payment/timeout/{id}")
    @HystrixCommand(fallbackMethod="paymentInfo_TimeoutHandler",commandProperties={
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })//这里配置的时间，与openfeign的超时设置重叠，应该是时间短的为准。
    /**
     * 参考ribbon.ReadTimeout
     */
    public  String payentInfo_Timeout(@PathVariable("id") int id){
        return paymentServer.payentInfo_Timeout(id);
    }

    public String paymentInfo_TimeoutHandler(@PathVariable("id") int id){
        return "服务器繁忙，清稍后再试！";
    }
    @GetMapping("/consumer/payment/globaltimeout/{id}")
    @HystrixCommand
    public  String payentInfo_global_Timeout(@PathVariable("id") int id){
        return paymentServer.payentInfo_Timeout(id);
    }

    /**
     * 全局异常不能带参数，否则报错。
     * 估计是全局异常，对所有的方法生效，所以无法考虑入参
     * @return
     */
    public String paymentInfo_global_TimeoutHandler(){
        return "服务器繁忙，清稍后再试！全局异常提示！！";
    }
}
