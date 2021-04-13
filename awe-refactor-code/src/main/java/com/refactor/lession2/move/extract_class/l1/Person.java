package com.refactor.lession2.move.extract_class.l1;

public class Person {

    private String _name;
    /**
     * 目标将号码相关字段提取到独立的class
     * 1. 执行self encapsulated field，使getTelephoneNumber()转为使用getter和setter
     * 2. ctrl+shift+alt+T，选择delegate，选择_officeAreaCode和_officeNumber，以及相应的getter和setter
     * 3. 移除当前类相应的getter和setter
     */
    private String _officeAreaCode;
    private String _officeNumber;

    public String getName() {
        return _name;
    }

    public String getTelephoneNumber() {
        return ("(" + _officeAreaCode + ") " + _officeNumber);
    }

    public String get_officeAreaCode() {
        return _officeAreaCode;
    }

    public void set_officeAreaCode(String _officeAreaCode) {
        this._officeAreaCode = _officeAreaCode;
    }

    public String get_officeNumber() {
        return _officeNumber;
    }

    public void set_officeNumber(String _officeNumber) {
        this._officeNumber = _officeNumber;
    }
}