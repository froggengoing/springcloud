package com.forggengo.kafka.test1;

import org.apache.kafka.connect.connector.Task;

import java.util.Map;

public class FileStreamSourceTask implements Task {
    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> map) {

    }

    @Override
    public void stop() {

    }
}
