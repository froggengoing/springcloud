package com.froggengo.pratise.class7_deadLetter.server;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.HashMap;
import java.util.Map;

public class DeadLetterMain {

    public static void main(String[] args) {
/*        Map<String, Object> argsMap = new HashMap<>();
        // 死信交换器/死信队列
        argsMap.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        // 设置队列过期时间（第一次设置一个值后，以后不能设置一个更大的值）
        argsMap.put("x-message-ttl", 60000);

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
        // 死信的关系一定要在queue申明时指定，而不能在exchange申明时指定
        channel.queueDeclare(QUEUE_NAME, true, false, false, argsMap);*/
    }

}
