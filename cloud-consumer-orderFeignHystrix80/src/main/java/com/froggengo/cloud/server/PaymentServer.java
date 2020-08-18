package com.froggengo.cloud.server;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
//fallback表示当服务器出现异常时，将调用的子类实现处理。
//添加了fallback后，居然覆盖了OrderHystrixController里面的超时处理方法
@FeignClient(value="CLOUD-PROVIDER-HYSTRIX-PAYMENT",fallback = PaymentFallBackServer.class)
public interface PaymentServer {


    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentInfo_ok(@PathVariable("id") int id);


    @GetMapping("/payment/hystrix/timeout/{id}")
    public  String payentInfo_Timeout(@PathVariable("id") int id);
}
