package com.froggengo.rabbitmqConsumer.springconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "myrabbitmq")
public class RabbitExchangeMetaProperties {

    public  String deadExchangeName;
    public  String deadQueueName;
    public  String deadRoutingKey;

    public  String testDirectQueue;

    public String getDeadExchangeName() {
        return deadExchangeName;
    }

    public void setDeadExchangeName(String deadExchangeName) {
        this.deadExchangeName = deadExchangeName;
    }

    public String getDeadQueueName() {
        return deadQueueName;
    }

    public void setDeadQueueName(String deadQueueName) {
        this.deadQueueName = deadQueueName;
    }

    public String getDeadRoutingKey() {
        return deadRoutingKey;
    }

    public void setDeadRoutingKey(String deadRoutingKey) {
        this.deadRoutingKey = deadRoutingKey;
    }

    public String getTestDirectQueue() {
        return testDirectQueue;
    }

    public void setTestDirectQueue(String testDirectQueue) {
        this.testDirectQueue = testDirectQueue;
    }
}
