package com.refactor.lession2.move.hide_delegate.l1;

/**
 * @author froggengo@qq.com
 * @date 2021/1/24 12:16.
 */
public class MainClass {

    /**
     * 目标隐藏客户端对服务对象之间的关系
     */
    public static void main(String[] args) {
        Person john = new Person();
        Person manager = john.getDepartment().getManager();
    }
}
