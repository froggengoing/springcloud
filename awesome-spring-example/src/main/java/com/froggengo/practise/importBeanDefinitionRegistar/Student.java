package com.froggengo.practise.importBeanDefinitionRegistar;

public class Student {
    String name;
    String className;

    Teacher t1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getTeacher() {
        return t1;
    }

    public void setTeacher(Teacher teacher) {
        this.t1 = teacher;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
