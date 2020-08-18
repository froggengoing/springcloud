package com.froggengo.practise.webmvc.arguementResolver;


import com.froggengo.practise.webmvc.ControllerAdvice.ControllerAdviceTest;
import com.froggengo.practise.webmvc.ControllerAdvice.MyMessageControllerAdvice;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MyArguementResolverController {

    @GetMapping("/arg")
    public AnnotationUserEntity getUseer(@AnnotationUser AnnotationUserEntity user){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        return user;
    }
    @GetMapping("/argBody")
    public AnnotationUserEntity getUseerFromBody( AnnotationUserEntity user){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        return user;
    }
    @PostMapping("/requestBody")
    public AnnotationUserEntity getRequestBody(HttpServletRequest request,@RequestBody AnnotationUserEntity user){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        System.out.println(request.getContentType());
        return user;
    }

    /**
     * 本来想测试MyInterceptor#postHandle()
     */
    @PostMapping("/requestModel")
    public AnnotationUserEntity getrequestModel(HttpServletRequest request, @RequestBody AnnotationUserEntity user,ModelAndView model){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        System.out.println(request.getContentType());
        model.addObject("user",user);
        return user;
    }

    /**
     * @see MyMessageControllerAdvice
     * 通过ResponseBodyAdvice完成统一格式转换
     */
    @ControllerAdviceTest
    @GetMapping("/getResult")
    public AnnotationUserEntity getResultJson(HttpServletRequest request, AnnotationUserEntity user){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        System.out.println(request.getContentType());
        return user;
    }

    @GetMapping("/getError")
    public AnnotationUserEntity getError(AnnotationUserEntity user){
        System.out.println(user.getName());
        System.out.println(user.getAddress());
        int i=9/0;
        return user;
    }
}
