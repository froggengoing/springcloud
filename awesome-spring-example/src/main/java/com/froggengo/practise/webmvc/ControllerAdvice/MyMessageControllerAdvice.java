package com.froggengo.practise.webmvc.ControllerAdvice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;

/**
 * 注意这里泛型为Object，如果指定为ResultObj，会报ClassCastException
 */
@RestControllerAdvice
//@Component
public class MyMessageControllerAdvice  implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        for (Annotation annotation : returnType.getMethod().getDeclaredAnnotations()) {
            System.out.println("annation:"+annotation.getClass());
        }
        return returnType.getMethod().getName().indexOf("getResultJson")!=-1;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return new ResultObj().setStatus(200).setMessage("操作成功").setValue(body);
    }
}
