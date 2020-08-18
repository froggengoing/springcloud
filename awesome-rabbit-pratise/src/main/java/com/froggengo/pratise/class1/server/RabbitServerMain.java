package com.froggengo.pratise.class1.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitServerMain {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) {
        //连接服务器
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        try(Connection connection=factory.newConnection()){
            Channel channel=connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            //######################
            channel.confirmSelect();
            // 确认消息监听
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息已经ack，tag: " + deliveryTag);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    // 对于消费者没有ack的消息，可以做一些特殊处理
                    System.out.println("消息被拒签，tag: " + deliveryTag);
                }
            });
            //#######################可靠性投递
            String message="Hello world!";
            channel.basicPublish("", QUEUE_NAME,null,message.getBytes());
            System.out.println("sent message: "+message);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
