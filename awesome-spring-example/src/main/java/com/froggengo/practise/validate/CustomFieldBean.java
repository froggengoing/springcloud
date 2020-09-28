package com.froggengo.practise.validate;

public class CustomFieldBean {
    @IsMobile
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}