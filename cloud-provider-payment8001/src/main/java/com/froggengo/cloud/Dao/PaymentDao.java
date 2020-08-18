package com.froggengo.cloud.Dao;

import com.froggengo.cloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentDao {
    public  int  create(Payment payment);
    public Payment getPaymentById(@Param("id")long id);

    @Select("select id, serial from payment where id =#{id}")
    public Payment getPaymentBy999(@Param("id")long id);
}
