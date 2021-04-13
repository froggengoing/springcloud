package com.refactor.lession2.move.inline_class.l2;

/**
 * 目标：TelephoneNumber相关字段内联到person类中
 * 没有好的办法只能手动将相应指定移动过来
 */
public class Person {

    private final TelephoneNumber telephoneNumber = new TelephoneNumber();
    private String _name;

    public String getName() {
        return _name;
    }

    public String getTelephoneNumber() {
        return ("(" + telephoneNumber.get_officeAreaCode() + ") " + telephoneNumber.get_officeNumber());
    }


    public String get_officeAreaCode() {
        return telephoneNumber.get_officeAreaCode();
    }

    public void set_officeAreaCode(String _officeAreaCode) {
        telephoneNumber.set_officeAreaCode(_officeAreaCode);
    }

    public String get_officeNumber() {
        return telephoneNumber.get_officeNumber();
    }

    public void set_officeNumber(String _officeNumber) {
        telephoneNumber.set_officeNumber(_officeNumber);
    }
}