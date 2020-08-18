package com.froggengo.mapper;

import com.froggengo.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapperOrigin {
    public List<Payment> getList();
}
