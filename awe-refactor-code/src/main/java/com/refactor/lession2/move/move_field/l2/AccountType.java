package com.refactor.lession2.move.move_field.l2;

/**
 * @author froggengo@qq.com
 * @date 2021/1/18 0:57.
 */
public class AccountType {

    public boolean isPremium() {
        return false;
    }
    private double _interestRate;
    public double get_interestRate() {
        return _interestRate;
    }

    public void set_interestRate(double _interestRate) {
        this._interestRate = _interestRate;
    }
}
