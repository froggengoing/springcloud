package com.froggengo.practise.Payment;

import com.froggengo.practise.Payment.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServerImpl implements PaymentServer {
    @Autowired
    PaymentMapper paymentMapper;

    @Override
    public int insert(Payment payment) {
        return paymentMapper.insert(payment);
    }

    @Override
    public Payment getPayment(Payment payment) {
        return paymentMapper.getPayment(payment);
    }




}
