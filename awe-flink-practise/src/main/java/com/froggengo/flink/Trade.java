package com.froggengo.flink;

import java.util.Objects;

public class Trade{
    String id;
    String good;
    int quantity;
    int pay;
    int count;
    double payAvg;
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public Trade(){

    }
    public Trade(String id,String good, int quantity, int pay) {
        this.id=id;
        this.good = good;
        this.quantity = quantity;
        this.pay = pay;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return id.equals(trade.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public double getPayAvg() {
        return payAvg;
    }

    public void setPayAvg(double payAvg) {
        this.payAvg = payAvg;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id='" + id + '\'' +
                ", good='" + good + '\'' +
                ", quantity=" + quantity +
                ", pay=" + pay +
                ", count=" + count +
                ", payAvg=" + payAvg +
                '}';
    }
}
