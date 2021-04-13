package com.awesomeJdk.innerClass;

import com.awesomeJdk.common.Person;

/**
 * @author froggengo@qq.com
 * @date 2021/2/8 17:37.
 */
public class OuterClassOne {
    private int wi=2;
    Person wPerson = new Person("fly");
    public void method1(){
        int a=0;
        Person person = new Person("fly");
        class MethodInnerClass1 {
            public void innerMehtod1(){
                //内部类可以读取外部非final变量但不能修改
                //  a=8;
                person.setName("wlu");
                wPerson = person;
                System.out.println(a);
                System.out.println(wi);
                System.out.println(person);
                System.out.println(wPerson);
            }
        }
        MethodInnerClass1 innerClass1 = new MethodInnerClass1();
        innerClass1.innerMehtod1();
        //不能在修改从内部类引用的本地变量必须是最终变量或实际上的最终变量
        //a=6;

    }
    class InnerClass {
        //inner class can not have static declaration
        //static int ab=5;
        public void innerMehtod1(){
            //内部类可以读取外部非final变量但不能修改
            //  a=8;
            OuterClassOne.this.wi=5;
            wi=7;
            wPerson.setName("wlu");
            System.out.println(wi);
            System.out.println(wPerson);
        }
    }
}
