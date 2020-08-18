package com.froggengo.practise.webmvc.ControllerAdvice;


public class ResultObj {
    int status;
    String message;
    Object value;

    public int getStatus() {
        return status;
    }

    public ResultObj setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultObj setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public ResultObj setValue(Object value) {
        this.value = value;
        return this;
    }
}
