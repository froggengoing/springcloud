package com.froggengo.rabbitmp.test;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class MyProducerSender {
    @Autowired
    RabbitTemplate rabbitmqTemplate;

    @Value("${myRabbitmq.directExchangeName}")
    String exchange;

    public void sendMessage(String routeKey, Object message){
        sendMessage(routeKey,message,null);
    }
    public void sendMessage(String routeKey, Object message, Map<String, Object> properties){
        MessageProperties messageProperties = new MessageProperties();
        if (!CollectionUtils.isEmpty(properties)) {
            Set<Map.Entry<String, Object>> entries = properties.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();
                messageProperties.setHeader(key, value);
            }
        }
        //org.springframework.amqp.core
        //构建消息
        Message msg = MessageBuilder.withBody(message.toString().getBytes()).andProperties(messageProperties).build();
        //id + 时间戳 全局唯一
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //convertAndSend会根据情况，将消息封装为Message
        rabbitmqTemplate.convertAndSend(exchange,routeKey,msg,correlationData);
        //只有确定消费者接收到消息，才会发送下一条信息，每条消息之间会有间隔时间
        //rabbitmqTemplate.sendAndReceive(msg);
    }

}
