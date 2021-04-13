package com.refactor.lession2.move.move_method.l2;

/**
 * @author froggengo@qq.com
 * @date 2021/1/18 0:57.
 */
public class AccountType {

    public boolean isPremium() {
        return false;
    }

    /**
     * 此方法与AccountType类型关联较强，应使用move method
     * 1. 先encapsulated field 强化变量，改为使用get方法
     * 2. F6，移动方法
     * 但这样会导致Account内丢失overdraftCharge()方法
     * 更好的方案是，复制原方法体到 overdraftCharge_ex()中，而转移overdraftCharge_ex()
     * 这样Account中可以保留原来的overdraftCharge()方法
     * 3. 这时候移动的方法入参时完整的account对象，
     * 使用CTRL+ALT+P提取account.get_daysOverdrawn()为参数入参
     * 此时可以消去入参的Account对象，而只传入_daysOverdrawn
     * @param daysOverdrawn
     */
    double overdraftCharge(int daysOverdrawn) { //译注： 透支金计费， 它和其他class的关系似乎比较密切。
        if (isPremium()) {
            double result = 10;
            if (daysOverdrawn > 7) {
                result += (daysOverdrawn - 7) * 0.85;
            }
            return result;
        } else {
            return daysOverdrawn * 1.75;
        }
    }
}
