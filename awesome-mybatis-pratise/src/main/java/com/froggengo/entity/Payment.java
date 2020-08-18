package com.froggengo.entity;

public class Payment {

    String id;
    String serial;

    public Payment getDetail() {
        return detail;
    }

    public void setDetail(Payment detail) {
        this.detail = detail;
    }

    Payment detail;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
