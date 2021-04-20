package com.awesomeJdk.anonymous;

public class Example {

    private String name;

    Example(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        InterfaceExample com = Example::new;
        Example bean = com.create("hello world");
        System.out.println(bean.name);
    }

    interface InterfaceExample {

        Example create(String name);
    }
}
