package com.froggengo.mapper;

import com.froggengo.MyAnnation.MyMapper;
import com.froggengo.entity.Payment;

import java.util.List;

@MyMapper
public interface PaymentMapper {

    public List<Payment> getList();

    int insert(Payment payment);
}
