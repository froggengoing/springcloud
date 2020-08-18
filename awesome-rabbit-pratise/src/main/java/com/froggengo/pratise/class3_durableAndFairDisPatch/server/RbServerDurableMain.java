package com.froggengo.pratise.class3_durableAndFairDisPatch.server;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class RbServerDurableMain {
    public static final String QUEUE_NAME="Durable_Task";

    public static void main(String[] args) {
        //连接服务器
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        try(Connection connection=factory.newConnection()){
            Channel channel=connection.createChannel();
            //消息持久化,1、queue要求设置durable为true，2.发布消息时设置MessageProperties.PERSISTENT_TEXT_PLAIN
            //注意：这并不能完全确保消息一定能持久化，因为接受消息------与存储值硬盘之间存在短暂的时间差
            //如果需要强持久化，可以使用publisher confirms
            boolean durable=true;
            //这里直接指定了queue的名称
            channel.queueDeclare(QUEUE_NAME,durable,false,false,null);
            InputStream in;
            while((in= System.in)!=null){
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String message = read.readLine();
                AMQP.BasicProperties pros = MessageProperties.PERSISTENT_TEXT_PLAIN;
                channel.basicPublish("", QUEUE_NAME,pros ,message.getBytes());
                System.out.println("sent message: "+message);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
