package com.webservice.practise;

import javax.jws.WebService;
import java.rmi.RemoteException;
import java.util.Date;

@WebService//(endpointInterface="com.webservice.practise.HelloWorld",serviceName="HelloWorldWs")//指定webservice所实现的接口以及服务名称
public class HellowWorlds implements HelloWorld {

    @Override
    public String sayHi(String name) {
        return name + "您好！现在时间是：" + new Date();
    }
}
