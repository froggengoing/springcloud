package com.webservice.practise;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Date;

@WebService
public interface HelloWorld {
    @WebMethod
    String sayHi(String name);
}

