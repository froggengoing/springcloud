package com.froggengo.mapper;

import com.froggengo.MyAnnation.MyMapper;
import com.froggengo.entity.Payment;

import java.util.List;

@MyMapper
public interface PaymentMapper {

    List<Payment> getList(Payment payment);

    int insert(Payment payment);
}
