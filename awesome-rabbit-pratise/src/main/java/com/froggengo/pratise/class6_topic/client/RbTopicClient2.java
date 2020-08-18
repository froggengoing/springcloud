package com.froggengo.pratise.class6_topic.client;




import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.froggengo.pratise.class6_topic.server.RbTopicServerMain.EXCHANGE_NAME;


public class RbTopicClient2 {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queue = channel.queueDeclare().getQueue();

        //输入感兴趣的日志等级，按逗号,分隔
        System.out.print("输入感兴趣的日志等级，按逗号,分隔");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String message = reader.readLine();
        final String[] split = message.split(",");
        if(split.length<1){
            throw new Exception("输入感兴趣的topic" +"，按逗号,分隔");
        }
        for (int i = 0; i < split.length; i++) {
            //绑定多个事件
            System.out.println("toppic:"+split[i]);
            channel.queueBind(queue, EXCHANGE_NAME,split[i]);
        }

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

        boolean autoAck = false;
        channel.basicConsume(queue,autoAck,deliverCallback,n->{});
    }
    private static void doWork(String task) throws InterruptedException {
        for (char ch: task.toCharArray()) {
            //System.out.println("正在处理"+ch);
            if (ch == '.') Thread.sleep(1000);
        }
    }
}
