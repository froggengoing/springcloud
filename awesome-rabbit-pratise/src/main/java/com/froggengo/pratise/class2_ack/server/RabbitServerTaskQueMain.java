package com.froggengo.pratise.class2_ack.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * publisher不断发消息，多个worker情况下，通过round-bibbon方式处理接收到的信息。。
 */
public class RabbitServerTaskQueMain {
    private final static String QUEUE_NAME = "task_queue";
    public static void main(String[] args) {
        //连接服务器
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        try(Connection connection=factory.newConnection()){
            Channel channel=connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            InputStream in;
            while((in= System.in)!=null){
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String message = read.readLine();
                channel.basicPublish("", QUEUE_NAME,null,message.getBytes());
                System.out.println("sent message: "+message);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
