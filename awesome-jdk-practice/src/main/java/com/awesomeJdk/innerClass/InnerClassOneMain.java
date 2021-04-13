package com.awesomeJdk.innerClass;

import com.awesomeJdk.innerClass.OuterClassOne.InnerClass;

/**
 * @author froggengo@qq.com
 * @date 2021/2/8 17:51.
 */
public class InnerClassOneMain {

    public static void main(String[] args) {
        OuterClassOne innerClassOne = new OuterClassOne();
        innerClassOne.method1();
        InnerClass innerClass2 = innerClassOne.new InnerClass();
        innerClass2.innerMehtod1();
    }

}
