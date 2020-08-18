package com.froggengo.pratise.class6_topic.server;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class RbTopicServerMainForSpring {
    public static final String EXCHANGE_NAME ="spring-boot-exchange" ;

    public static void main(String[] args) {
        //连接服务器
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        try(Connection connection=factory.newConnection()){
            Channel channel=connection.createChannel();
            boolean durable=true;
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC,true);
            InputStream in;
            System.out.print("发送消息（格式：活力.颜色.动物.,消息体）：");
            while((in= System.in)!=null){
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String message = read.readLine();
                //输入消息按,分隔。等级,具体消息
                String[] split = message.split(",");
                if (split.length !=2) {
                    System.out.println("请输入正确的消息！格式：等级,具体消息");
                    continue;
                }
                AMQP.BasicProperties pros = MessageProperties.PERSISTENT_TEXT_PLAIN;
                channel.basicPublish(EXCHANGE_NAME, split[0],pros ,split[1].getBytes());
                System.out.println("sent message: "+message);
                System.out.print("发送消息（格式：日志等级,消息体）：");
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
