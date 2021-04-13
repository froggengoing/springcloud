package com.refactor.lession2.move.hide_delegate.l2;

public class Person {

    Department _department;


    public Person getManager() {
        return _department.getManager();
    }
    public void setDepartment(Department arg) {
        _department = arg;
    }

}