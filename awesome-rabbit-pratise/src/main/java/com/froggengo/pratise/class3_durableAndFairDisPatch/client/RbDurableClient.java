package com.froggengo.pratise.class3_durableAndFairDisPatch.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RbDurableClient {
    private final static String QUEUE_NAME = "Durable_Task";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //服务器默认roun-ribbon的方式，不断的给每个consumer发送消息，即使consumer没有完成上一个消息的处理
        //这可能导致消息在consumer中不断堆积
        //这里设置为1，是告诉服务器，每次只给我发送一个消息，且必须等我确认后，在给我发下一条
        channel.basicQos(1);
        DeliverCallback deliverCallback=(consumerTag, delivery)->{
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
        //如果在处理过程中，程序死掉了，自动会将消息传个下一个consumer处理
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,n->{});
    }
    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            System.out.println("正在处理");
            if (ch == '.') Thread.sleep(1000);
        }
    }
}
