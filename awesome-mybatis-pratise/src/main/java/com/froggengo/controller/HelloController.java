package com.froggengo.controller;

import com.froggengo.entity.Payment;
import com.froggengo.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
public class HelloController {

    @Autowired
    PaymentMapper paymentMapper;

    @GetMapping("/hello")
    public List<Payment> get(){
        List<Payment> list = paymentMapper.getList(null);
        return list;
    }

    @Transactional
    @GetMapping("/insert")
    public List<Payment> insert(){
        Payment payment = new Payment();
        Payment payment1 = new Payment();
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        payment.setId(uuid.toString().substring(0,15));
        payment.setSerial(uuid.toString().substring(16,30));
        payment1.setId(uuid.toString().substring(16,30));
        payment1.setSerial(uuid.toString().substring(0,15));

        int count = paymentMapper.insert(payment);
        //int i1 = 99/ 0;
        int count2 = paymentMapper.insert(payment1);
        return Arrays.asList(payment);
    }

}
