package com.webservice.practise;

import javax.xml.ws.Endpoint;
import java.rmi.RemoteException;

/**
 * 浏览器调用：http://127.0.0.1:8888/webservice/ws?wsdl
 */
public class TestService {
    public static void main(String args[]){
        HellowWorlds h = new HellowWorlds();
        String s = h.sayHi("yangwenxue");

        Endpoint.publish("http://127.0.0.1:8888/webservice/ws",new HellowWorlds());
        System.out.println("调webservice:"+s);
    }
}
