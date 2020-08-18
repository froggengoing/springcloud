package com.froggengo.practise.importBeanDefinitionRegistar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Teacher {
    @Value("${com.teacher.name:fly}")
    String name;
    @Value("${com.teacher.age:20}")
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
