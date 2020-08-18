package com.froggengo.practise.webmvc.servletContextInitializer;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

/**
 * 1、@WebServlet => 代替 servlet 配置，@WebFilter => 代替 filter 配置，@WebListener => 代替 listener 配置
 * 2、ServletContextInitializer
 * 3、filter可以直接加@Component等注解，注入beanfactory即可,
 * 4、servlet使用ServletRegistrationBean
 */
@Component
public class MyServletRegistration  {
    @Bean
    public ServletRegistrationBean  myServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new MyServlet(), "/myServlet");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
