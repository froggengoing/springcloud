package com.froggengo.practise.A_ServletLearn;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import java.io.IOException;

public class MyFilter implements Filter {
    private static Log log= LogFactory.getLog(MyFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("MyFilter#init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("MyFilter#doFilter() before");
        chain.doFilter(request, response);
        log.info("MyFilter#doFilter() after");
    }

    @Override
    public void destroy() {
        log.info("MyFilter#destroy()");
    }
}