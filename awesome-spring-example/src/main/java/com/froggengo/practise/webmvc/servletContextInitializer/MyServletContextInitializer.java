package com.froggengo.practise.webmvc.servletContextInitializer;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class MyServletContextInitializer implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("MyServletContextInitializer");
        System.out.println(servletContext.getMajorVersion());
        //servletContext.addFilter();
        //servletContext.addListener();
        //servletContext.addServlet();
        System.out.println("MyServletContextInitializer 结束");
    }
}
