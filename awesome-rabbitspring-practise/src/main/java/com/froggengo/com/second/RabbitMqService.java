package com.froggengo.com.second;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(bindings = @QueueBinding(
        exchange = @Exchange(value = "spring-boot-exchange",durable = "true",type = "topic"),
        value = @Queue(value = "spring-boot",durable = "true"),
        key = "key.#"
))
public class RabbitMqService {
    @RabbitHandler
    public void processMessage1(String message) {
        System.out.println("一:"+message);
    }

    @RabbitHandler
    public void processMessage2(byte[] message) {
        System.out.println("二:"+new String(message));
    }
}
