package com.froggengo.pratise.class1.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setPassword("admin");
        factory.setUsername("admin");
        //不能使用try-with-resources
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        System.out.println("Waiting for message: ");
        DeliverCallback deliverCallback=(c,d)->{
            final String message = new String(d.getBody(), "UTF-8");
            System.out.println("reveived message: "+ message +" ");
        };
        //开启自动确认
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag->{});
    }
}
