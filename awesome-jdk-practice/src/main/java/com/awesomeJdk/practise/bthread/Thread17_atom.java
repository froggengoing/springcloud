package com.awesomeJdk.practise.bthread;

import java.util.Objects;
import java.util.concurrent.atomic.*;

public class Thread17_atom {
    public static void main(String[] args) {
        //boolean为volatile
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(false);
        //incrementAndGet只关心加1操作，不关心当前值，所以不断尝试在当前值上加1进行cas
        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.incrementAndGet();
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        atomicIntegerArray.getAndSet(0,3);
        //同样不关心当前值，只关心delta值有没有加入到数组中
        atomicIntegerArray.getAndAdd(0,5);

        //
        AtomicReference<User> atomicReference = new AtomicReference<>();
        User user = new User("fly", 18);
        System.out.println(user);
        atomicReference.set(user);
        System.out.println(atomicReference.compareAndSet(user, new User("fly2", 28)));
        System.out.println("reference:"+atomicReference.get());
        System.out.println("user:"+user);
        AtomicIntegerFieldUpdater<User> updater = AtomicIntegerFieldUpdater.newUpdater(User.class,"age");
        updater.compareAndSet(user,18,60);
        System.out.println(user);
    }
    static class User {
        String name;
        //Exception in thread "main" java.lang.IllegalArgumentException: Must be volatile type
        volatile int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return age == user.age &&
                    name.equals(user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }
}
