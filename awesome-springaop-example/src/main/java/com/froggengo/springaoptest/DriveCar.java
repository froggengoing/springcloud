package com.froggengo.springaoptest;

import org.springframework.stereotype.Service;

@Service
public class DriveCar implements Drive {
    @Override
    public void driver(String name) {
        System.out.println("I am  driving a car ,"+ name);
    }
}
