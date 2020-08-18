package com.froggengo.cloud.server;

import org.springframework.stereotype.Component;

@Component
public class PaymentFallBackServer  implements PaymentServer{
    @Override
    public String paymentInfo_ok(int id) {
        return "通过服务实现类，完成服务超时、异常的处理：paymentInfo_ok";
    }

    @Override
    public String payentInfo_Timeout(int id) {
        return "通过服务实现类，完成服务超时、异常的处理：payentInfo_Timeout";
    }
}
