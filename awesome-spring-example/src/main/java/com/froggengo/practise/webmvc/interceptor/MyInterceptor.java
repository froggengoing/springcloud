package com.froggengo.practise.webmvc.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyInterceptor implements HandlerInterceptor {
    public  boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("MyInterceptor==="+request.getRequestURI());
        return true;
    }
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) throws Exception {
    //如果要修改modelandview，必须在controller返回 ModelAndView对象
        //这样会在@ResponseBody下，导致circuit循环请求"/requestModel"
    /*        modelAndView.addObject("test","测试HandlerInterceptor");
        System.out.println(modelAndView.getModel().get("test").toString());
        System.out.println(modelAndView.getModel().get("user").toString());*/
        System.out.println("com.froggengo.practise.webmvc.interceptor.MyInterceptor.postHandle");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
        System.out.println("HandlerInterceptor  :afterCompletion");
    }
}
