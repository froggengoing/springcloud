package com.froggengo.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class orderZK80Controller {

    private static final String PAYMENT_URL = "http://cloud-provider-paymment";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/zk")
    public String getZKInfo(){
        String forObject = restTemplate.getForObject(PAYMENT_URL + "/payment/zk", String.class);
        return forObject;
    }

}
