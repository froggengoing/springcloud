package com.froggengo.practise.webmvc.servletContextInitializer;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * support type see org.springframework.boot.web.servlet.ServletListenerRegistrationBean
 */
@Component
public class MyListener implements ServletContextListener {
    public  void contextInitialized(ServletContextEvent sce) {
        System.out.println(sce);
        System.out.println("我是ServletContextListener");
    }

    public  void contextDestroyed(ServletContextEvent sce) {
    }
}
