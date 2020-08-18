package com.froggengo.pratise.class2_ack.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个producer、两个消费者。消息roundbin模式分发给消费者
 * 消费者使用ack机制，处理过程一台消费者宕机可以将消费传给下一个消费者处理。
 */
public class RecvTaskQue2 {
    private final static String QUEUE_NAME = "task_queue";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //队列持久化，宕机不消失
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            byte[] body = delivery.getBody();
            String msg = new String(body,"Utf-8");
            try {
                doWork(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("处理结束 "+msg);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        };
        //关闭自动确认autoAck=false
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,n->{});
    }
    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            System.out.println("正在处理"+ch);
            if (ch == '.') Thread.sleep(1000);
        }
    }
}
