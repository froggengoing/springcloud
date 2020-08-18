package com.froggengo.practise.webmvc.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyControllerAdvice  {
    @ExceptionHandler({    Exception.class})
    public Object exception(Exception ex){
        return new ResultObj().setStatus(400).setMessage("失败").setValue(ex.getMessage());
    }
}
