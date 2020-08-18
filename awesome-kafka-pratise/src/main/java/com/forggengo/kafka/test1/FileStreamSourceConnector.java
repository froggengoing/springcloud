package com.forggengo.kafka.test1;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.*;


public class FileStreamSourceConnector extends SourceConnector {
    private  String TOPIC_CONFIG;
    private String filename;
    private String topic;
    private String FILE_CONFIG;


    @Override
    public void start(Map<String, String> props) {
        // The complete version includes error handling as well.
        filename = props.get(FILE_CONFIG);
        topic = props.get(TOPIC_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return FileStreamSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        // Only one input stream makes sense.
        Map<String, String> config = new HashMap<>();
        if (filename != null)
            config.put(FILE_CONFIG, filename);
        config.put(TOPIC_CONFIG, topic);
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return null;
    }

    @Override
    public String version() {
        return null;
    }
}

