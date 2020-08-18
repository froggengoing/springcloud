package com.froggengo.practise.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    PaymentServer paymentServer;

    @GetMapping("/get/{id}")
    public Payment get(@PathVariable int id){
        return paymentServer.getPayment(new Payment().setId(id));
    }
    @Transactional
    @PostMapping("/insert")
    public int insertPayment(@RequestBody List<Payment> payment){
        int[] count=new int[1];
        payment.stream().forEach(n->{
            int i = paymentServer.insert(n);
            int a=99/0;
            count[0]+=i;
        });
        return count[0];
    }
}


