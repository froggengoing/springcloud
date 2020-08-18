package com.froggengo.practise.webmvc.arguementResolver;

public class AnnotationUserEntity {
    String name;
    String address;
    public AnnotationUserEntity(){

    }
    public AnnotationUserEntity(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
