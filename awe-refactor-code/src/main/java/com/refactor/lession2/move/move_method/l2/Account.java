package com.refactor.lession2.move.move_method.l2;

/**
 * @author froggengo@qq.com
 * @date 2021/1/18 0:55.
 */
public class Account {

    private AccountType _type;
    private int _daysOverdrawn;

    double bankCharge() {
        double result = 4.5;
        if (get_daysOverdrawn() > 0) {
            result += _type.overdraftCharge(this.get_daysOverdrawn());
        }
        return result;
    }


    public int get_daysOverdrawn() {
        return _daysOverdrawn;
    }

}
