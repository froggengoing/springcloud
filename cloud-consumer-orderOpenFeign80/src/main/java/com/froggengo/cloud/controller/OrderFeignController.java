package com.froggengo.cloud.controller;

import com.froggengo.cloud.entities.CommonResult;
import com.froggengo.cloud.entities.Payment;
import com.froggengo.cloud.service.PaymentFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderFeignController {
    @Autowired
    PaymentFeignService paymentFeignService;

    @GetMapping("/consumerfeign/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") long id){
        return paymentFeignService.getPaymentById(id);
    }

    /**
     * 默认openFeign等待远程结果返回时间为1秒，修改配置
     * ribbon:
     *   ReadTimeout: 5000
     *   ConnectTimeout: 5000
     * @return
     */
    @GetMapping(value = "/consumer/payment/feign/timeout")
    public String paymentFeignTimeout(){
        return paymentFeignService.paymentFeignTimeout();
    }

}
