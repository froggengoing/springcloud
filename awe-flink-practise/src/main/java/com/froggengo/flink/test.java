package com.froggengo.flink;

import org.apache.flink.api.java.functions.KeySelector;

public class test {
    public static void main(String[] args) {
        KeySelector<String, String> selector = new KeySelector<String, String>() {

            @Override
            public String getKey(String value) throws Exception {
                return null;
            }
        };
    }
}
