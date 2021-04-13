package com.froggengo.mapper;

import com.froggengo.entity.Payment;

import java.util.Collections;
import java.util.List;

public class PaymentMapperAopTestImpl implements PaymentMapper {

    @Override
    public List<Payment> getList(Payment payment) {
        System.out.println("getList");
        return Collections.emptyList();
    }
    @Override
    public int insert(Payment payment) {
        System.out.println("insert");
        return 0;
    }
}
