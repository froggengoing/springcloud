package com.awesomeJdk.practise.abaisc;

import org.junit.Test;

public class datetype {

    /**
     * 1、对于short s1=1;s1=s1+1来说，在s1+1运算时会自动提升表达式的类型为int，
     * 那么将int赋予给short类型的变量s1会出现类型转换错误。
     * 2、对于short s1=1;s1+=1来说 +=是java语言规定的运算符，java编译器会对它进行特殊处理，因此可以正确编译。
     */
    @Test
    public void testShort (){

       // short s1 = 1; s1 = s1 + 1;//报错，编译不通过
        short s1 = 1; s1 +=1;
    }

    /**
     * 1、new创建的对象永远不==
     * 2、对象与基本数据类型比较，发生拆箱
     * 3、new创建与非new创建，永远不==
     * 4、两个非new创建，在128内相等，在128以外不==
     *
     */
    @Test
    public void testInteger (){
        Integer i = new Integer(100);
        Integer j = new Integer(100);
        System.out.println(i == j); //1、false
        int m=100;
        System.out.println(i==m);//2、true
        Integer r = new Integer(100);
        Integer s = 100;
        System.out.println(r==s);  //4、false
        Integer xx = 100;//Integer i = Integer.valueOf(100)；
        Integer yy = 100;
        System.out.println(xx == yy); //5、true
        Integer ii = 128;
        Integer jj = 128;
        System.out.println(ii == jj); //6、false

    }

    @Test
    public void testEqual (){

    }

    /**
     * Integer 是引用传值，但每次对Integer的运算都是重新赋值，所以3的位置是新的地址
     */
    @Test
    public void testInteger1 (){
        Integer t=new Integer(200);
        System.out.println(System.identityHashCode(t));//1
        addOne(t);
        System.out.println(t);
    }
    void addOne(Integer t){
        System.out.println(System.identityHashCode(t));//2
        t+=1;
        System.out.println(System.identityHashCode(t));//3
    }

    /**
     * 金额不能使用float和double
     */
    @Test
    public void testFloat (){
        float fff=1.1f;
        float ddd=0.9f;
        float eee=fff-ddd;
        System.out.println(eee);//0.20000005
    }

    /**
     * 即使try的代码块已经返回结果但程序仍然会执行finally里面的代码
     */
    @Test
    public void testTrue (){
        System.out.println(getValue());//3
        System.out.println(getValue2());//8
    }
    int getValue(){
        try{
            return 1;
        }catch (Exception e){
            return 2;
        }finally {
            return 3;
        }
    }
    Integer getValue2(){
        Integer i=5;
        try{
            return i=6;
        }catch (Exception e){
            return i=7;
        }finally {
            return i=8;
        }
    }
}
