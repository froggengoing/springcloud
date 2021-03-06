package com.refactor.lession2.move.extract_class.l2;

public class Person {

    private final TelephoneNumber telephoneNumber = new TelephoneNumber();
    private String _name;

    public String getName() {
        return _name;
    }
    /**
     * 目标将号码相关字段提取到独立的class
     * 1. 执行self encapsulated field，使getTelephoneNumber()转为使用getter和setter
     * 2. ctrl+shift+alt+T，选择delegate，选择_officeAreaCode和_officeNumber，以及相应的getter和setter
     * 3. 移除当前类相应的getter和setter
     */
    public String getTelephoneNumber() {
        return ("(" + telephoneNumber.get_officeAreaCode() + ") " + telephoneNumber.get_officeNumber());
    }
}