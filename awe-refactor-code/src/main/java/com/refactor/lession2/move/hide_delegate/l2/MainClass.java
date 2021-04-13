package com.refactor.lession2.move.hide_delegate.l2;

/**
 * @author froggengo@qq.com
 * @date 2021/1/24 12:16.
 */
public class MainClass {

    /**
     * 目标隐藏客户端对服务对象之间的关系
     * 1、person类建立委托函数getManager
     * 2、删除person类中getDepartment方法
     * 3、修改引用处为新建立的委托函数
     */
    public static void main(String[] args) {
        Person john = new Person();
        Person manager = john.getManager();
    }
}
