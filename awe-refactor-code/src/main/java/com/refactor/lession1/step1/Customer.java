package com.refactor.lession1.step1;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author froggengo@qq.com
 * @date 2021/1/2 12:28.
 * 重构：
 * 1、statement()，提取计算每个rental的金额到独立的方法accountFor()
 * 2、accountFor()方法其实没有使用Customer的属性，更应该放到Rental类中
 * 3、去掉无用的accountFor()
 */
public class Customer {

    private String _name;
    private Vector<Rental> _rentals = new Vector();

    public Customer(String _name) {
        this._name = _name;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void addRental(Rental rental) {
        _rentals.addElement(rental);
    }

    public String statement() {
        double totalAmount = 0;
        int frequentRenterPoints = 0;
        Enumeration<Rental> elements = _rentals.elements();
        String result = "Rental record for " + get_name() + "\n";
        while (elements.hasMoreElements()) {
            Rental each = elements.nextElement();
            //第三步，不再使用accountFor,而直接调用rental中的方法
            double thisAmount = each.getCharge();
            frequentRenterPoints++;
            if ((each.get_movice().get_priceCode() == Movie.NEW_RELEASE) && each.get_daysRented() > 1) {
                frequentRenterPoints++;
            }
            result += "\t" + each.get_movice().get_title() + "\t" + String.valueOf(thisAmount) + "\n";
            totalAmount += thisAmount;
        }
        result += "Amount owed is " + String.valueOf(totalAmount) + "\n";
        result += "you earned " + String.valueOf(frequentRenterPoints) + "frequent renter pointers";
        return result;
    }

    /**
     * idea 选中需要提取的代码，ctrl+alt+m，将自动提取代码
     * 问题：
     *  这部分代码其实与customer无关，而更应该反在Rental中
     * @see Rental#getCharge()
     */
    private double accountFor(Rental each) {
        //变量名each，应改为更有意义的变量名
        //thisAmount改为result
        double thisAmount = 0;
        switch (each.get_movice().get_priceCode()) {
            case Movie.REGULAR:
                thisAmount += 2;
                if (each.get_daysRented() > 2) {
                    thisAmount += (each.get_daysRented()) * 1.5;
                }
                break;
            case Movie.NEW_RELEASE:
                thisAmount += each.get_daysRented() * 3;
                break;
            case Movie.Children:
                thisAmount += 1.5;
                if (each.get_daysRented() > 3) {
                    thisAmount += (each.get_daysRented() - 3) * 1.5;
                }
                break;
            default:
        }
        return thisAmount;
    }
    private double getThisAmount2(Rental each) {
        return each.getCharge();
    }
}
