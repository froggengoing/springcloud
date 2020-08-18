package com.forggengo.kafka.test1;



public class kafkaMain {
    public static void main(String[] args) {
        int i = Math.abs("test".hashCode()) % 50;
        System.out.println(i);

    }
}
