package com.froggengo.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class OrderController {

    private static final String PAYMENT_URL = "http://consul-provider-payment";
    @Autowired
    private  RestTemplate restTemplate;

    @GetMapping("/consumer/payment/consul")
    public String getZKInfo(){
        String forObject = restTemplate.getForObject(PAYMENT_URL + "/payment/consul", String.class);
        return forObject;
    }


}


