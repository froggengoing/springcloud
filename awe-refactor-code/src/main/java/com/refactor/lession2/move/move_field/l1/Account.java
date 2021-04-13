package com.refactor.lession2.move.move_field.l1;

public class Account {

    private AccountType _type;
    /**
     * 目标：_interestRate移动至AccountType中
     * 1. self encapsulate field
     * 2. 直接复制到目标类
     * 3. 手动修改源类中的self encapsulate方法，指向AccountType中复制的方法
     * 4. 对源类使用inline method，将方法内联，并移除getter方法
     * 5. 对_interestRate属性使用safe delete
     */
    private double _interestRate;

    double interestForAmount_days(double amount, int days) {
        return _interestRate * amount * days / 365;
    }
}