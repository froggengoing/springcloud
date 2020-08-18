package com.froggengo.cloud.Controller;

import com.froggengo.cloud.entities.CommonResult;
import com.froggengo.cloud.entities.Payment;
import com.froggengo.cloud.loadBalance.LoadBalancer;
import com.froggengo.cloud.loadBalance.MyLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
public class OrderController {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    LoadBalancer myLoadBalance;

   // private static final String PAYMANT_URL="http://localhost:8001";
   private static final String PAYMANT_URL="http://CLOUD-PAYMENT-SERVICE";
    @PostMapping("/consumer/payment/create")
    public CommonResult<Payment> creat(@RequestBody  Payment payment){
        System.out.println(payment.getSerial() +"  ");
        return restTemplate.postForObject(PAYMANT_URL+"/payment/create",payment,CommonResult.class);
    }
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") long id){
        return restTemplate.getForObject(PAYMANT_URL+"/payment/get/"+id,CommonResult.class);
    }

    @GetMapping("/consumer/payment/lbget/{id}")
    public CommonResult<Payment> getPaymentLB(@PathVariable("id") long id){
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        if(instances == null || instances.size() <= 0)
        {
            return null;
        }

        ServiceInstance instances1 = myLoadBalance.instances(instances);
        URI uri = instances1.getUri();
        System.out.println(instances1.getHost());
        System.out.println(instances1.getPort());
        System.out.println(instances1.getInstanceId());
        System.out.println(instances1.getServiceId());
        System.out.println(instances1.getUri());
        return restTemplate.getForObject(uri.toString()+"/payment/get/"+id,CommonResult.class);
    }
}
