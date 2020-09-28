package com.froggengo.com.second;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;


/**
 * 1、测试同一个quename能不能，实现消费者负载均衡
 * 测试结果，绑定至同一个队列的消费者可以负责均衡
 * 2、测试在同一个队列，声明key不一致
 * 那么这个队里能接受到的消息时，这些不同key的总和，然后在这些消费者中负载均衡
 */
@Component
@RabbitListener(bindings = @QueueBinding(
        exchange = @Exchange(value = "${rabbitMqService2.spring-boot-exchange}",durable = "true",type = ExchangeTypes.TOPIC),
        value = @Queue(value = "spring-boot",durable = "true"),
        key = {"key.#","log.#"}
))
public class RabbitMqService2 {
    @RabbitHandler
    public void processMessage1(String message) {
        System.out.println("十一:"+message);
    }

    @RabbitHandler
    public void processMessage2(byte[] message) {
        System.out.println("十二:"+new String(message));
    }
}
