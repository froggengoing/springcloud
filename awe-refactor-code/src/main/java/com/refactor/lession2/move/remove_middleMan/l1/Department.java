package com.refactor.lession2.move.remove_middleMan.l1;

public class Department {
    private String _chargeCode;
    private Person _manager;
    public Department (Person manager) {
        _manager = manager;
    }
    public Person getManager() {
        return _manager;
    }
}