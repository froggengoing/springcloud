package com.froggengo.pratise.class4_publisAndSubscribe.client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 与前面的额例子不同是的，没有指定QUEUE的名称，
 * 而是由rabbitmq服务器创建，并获取。
 * 所以每个consumer都有单独的queue，进而获取所有消息
 */
public class RbPubSubClient2 {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //1.声明类型
        channel.exchangeDeclare("logs", BuiltinExchangeType.FANOUT,true);
        //2.从服务器获取，创建的临时queue
        String queue = channel.queueDeclare().getQueue();
        //3.bindings
        channel.queueBind(queue,"logs","");
        //以下为第一次使用报错
        //channel.exchangeBind(queue,"logs","");
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
        channel.basicConsume(queue,autoAck,deliverCallback,n->{});
    }
    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            System.out.println("正在处理");
            if (ch == '.') Thread.sleep(1000);
        }
    }
}
