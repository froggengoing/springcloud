package com.froggengo.practise.webmvc.servletContextInitializer;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("MyFilter init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("MyFilter doFilter() before");
        chain.doFilter(request, response);
        System.out.println("MyFilter doFilter() after");
    }

    @Override
    public void destroy() {
        System.out.println("MyFilter doFilter() after");
    }
}
