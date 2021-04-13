package com.refactor.lession1.step1;

/**
 * @author froggengo@qq.com
 * @date 2021/1/2 12:28.
 */
public class Rental {

    private Movie _movice;
    private int _daysRented;

    public Rental(Movie _movice, int _daysRented) {
        this._movice     = _movice;
        this._daysRented = _daysRented;
    }

    public Movie get_movice() {
        return _movice;
    }

    public void set_movice(Movie _movice) {
        this._movice = _movice;
    }

    public int get_daysRented() {
        return _daysRented;
    }

    public void set_daysRented(int _daysRented) {
        this._daysRented = _daysRented;
    }

    /**
     * idea 选中需要提取的代码，ctrl+alt+m，将自动提取代码
     */
    public double getCharge() {
        //变量名each，应改为更有意义的变量名
        //thisAmount改为result
        double thisAmount = 0;
        switch (get_movice().get_priceCode()) {
            case Movie.REGULAR:
                thisAmount += 2;
                if (get_daysRented() > 2) {
                    thisAmount += (get_daysRented()) * 1.5;
                }
                break;
            case Movie.NEW_RELEASE:
                thisAmount += get_daysRented() * 3;
                break;
            case Movie.Children:
                thisAmount += 1.5;
                if (get_daysRented() > 3) {
                    thisAmount += (get_daysRented() - 3) * 1.5;
                }
                break;
            default:
        }
        return thisAmount;
    }
}
