package com.awesomeJdk.anonymous;

import com.awesomeJdk.common.Person;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import org.junit.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/4/15 11:23.
 */
public class AnonymousLearn {

    @Test
    public void testBiConsumer (){
        BiConsumer<Person, String> biConsumerRef = Person::setName;
        BiConsumer<Person, String> biConsumer = (Person person, String name) -> person.setName(name);
        test(Person::setAge);
        test((Person a ,Integer b)->a.setAge(b));
    }
    public static  void  test(BiConsumer<Person,Integer> consumer){
        Person person = new Person("1", 28);
        consumer.accept(person,2);
        System.out.println(person);
    }
    @Test
    public void testObjectMethod() {
        List<String> list = Arrays.asList("aaaa", "bbbb", "cccc");
        //对象实例语法	instanceRef::methodName
        list.forEach(this::print);
        list.forEach(n-> System.out.println(n));
    }

    public void print(String content){
        System.out.println(content);
    }
    @Test
    public void testArray (){
        IntFunction<int[]> arrayMaker = int[]::new;
        // creates an int[10]
        int[] array = arrayMaker.apply(10);
        array[9]=10;
        System.out.println(array[9]);
    }
}
