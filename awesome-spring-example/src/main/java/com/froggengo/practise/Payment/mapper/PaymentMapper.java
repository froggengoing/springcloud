package com.froggengo.practise.Payment.mapper;

import com.froggengo.practise.Payment.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


public  interface PaymentMapper {

    @Insert("insert into payment (id,serial)values (#{id},#{serial})")
    public int  insert (Payment payment);

    @Select("select id , serial from payment where id=#{id}")
    public Payment getPayment(Payment payment);
}
