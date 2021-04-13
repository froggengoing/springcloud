package com.refactor.lession2.move.extract_class.l2;

public class TelephoneNumber {

    String _officeAreaCode;
    String _officeNumber;

    public TelephoneNumber() {
    }

    /**
     * 目标将号码相关字段提取到独立的class
     * 1. 执行self encapsulated field，使getTelephoneNumber()转为使用getter和setter
     * 2. ctrl+shift+alt+T，选择delegate，
     */
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