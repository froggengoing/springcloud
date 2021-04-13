package com.refactor.lession2.move.remove_middleMan.l1;

/**
 * @author froggengo@qq.com
 * @date 2021/1/24 12:16.
 */
public class MainClass {

    /**
     * 目标：删除客户端对服务对象之间的委托关系
     * 1、再person类中getManager方法上，ctrl+alt+shift+t，选择remove middleMan
     */
    public static void main(String[] args) {
        Person john = new Person();
        Person manager = john.getManager();
    }
}
