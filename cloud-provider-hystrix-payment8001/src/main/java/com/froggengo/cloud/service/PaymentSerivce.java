package com.froggengo.cloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.ribbon.hystrix.FallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentSerivce {
    /**
     * 正常访问
     * @param id
     * @return
     */
    public String paymentInfo_ok(int id){
        return "时间"+ LocalDateTime.now() +"线程池"+Thread.currentThread().getName()+"paymentInfo_OK,id"+id+"\t"+"哈哈";
    }

    /**
     * 超时访问
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod="paymentInfo_TimeoutHandler",commandProperties={
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
    })
    public String paymentInfo_Timeout(int id){
       int timeNum=2;
        try {
            TimeUnit.SECONDS.sleep(timeNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //int timeNum =10/0;
        return "时间"+ LocalDateTime.now() +"线程池"+Thread.currentThread().getName()+"paymentInfo_Timeout,id"+id+"\t"+"耗时（s）"+timeNum+"\t"+"哈哈";
    }
    public String paymentInfo_TimeoutHandler(int id){
        return "时间"+ LocalDateTime.now() +"  线程池:"+Thread.currentThread().getName()+"  paymentInfo_TimeoutHandler,id"+id+"\t"+"备胎";
    }

    //################服务熔断

    /**
     * 测试，一直请求id为-1，一定次数后，使用id为11访问，也会跳转至paymentCircuitBreaker_fallback方法
     * 说明多次错误情况下，已经发生熔断
     * @see com.netflix.hystrix.HystrixCommandProperties
     * @param id
     * @return
     */
    //测试过程sleepWindowInMilliseconds太短导致无效果
   @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name="circuitBreaker.enabled",value = "true"),//是否开启断路器
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value = "10"),//请求次数
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value = "5000"),//时间窗口
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value = "70")//失败率
    })
/*    //=====服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
    })*/
    public String paymentcircuitBreaker(@PathVariable("id") int id){
        if(id<0){
            throw new RuntimeException("***********不能为负数");
        }
        return "时间"+ LocalDateTime.now() +"线程池"+Thread.currentThread().getName()+"  paymencircuitBreaker,id"+id+"\t"+"\t"+"哈哈";

    }
    public String paymentCircuitBreaker_fallback(int id){
        return "服务熔断:" +id;
    }

}
