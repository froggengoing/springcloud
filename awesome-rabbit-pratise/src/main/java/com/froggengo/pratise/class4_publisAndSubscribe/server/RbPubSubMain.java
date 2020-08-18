package com.froggengo.pratise.class4_publisAndSubscribe.server;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

/**
 * 1. 生产者将消息发送至指定的echange
 * 2. 多个消费者各种创建不同的队列，并从echange接收到相同的消息
 * 3。测试消费中中途宕机，重启开启，并不会接收到宕机期间丢弃的消息
 */
public class RbPubSubMain {
    public static final String QUEUE_NAME="PS_Task";
    //exchange 类型：direct, topic, headers 和 fanout
    //fanout将所有接收到的消息发送给所有知道的consumer
    //命令查看rabbitmqctl list_exchanges
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
            //1.声明exchange类型，
            channel.exchangeDeclare("logs",BuiltinExchangeType.FANOUT,true);
            //channel.queueDeclare(QUEUE_NAME,durable,false,false,null);
            //2.获取mq服务器生成的临时队列
            //String queue = channel.queueDeclare().getQueue();
            //3.bindings
            //channel.exchangeBind("logs",queue,"");
            InputStream in;
            while((in= System.in)!=null){
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String message = read.readLine();
                AMQP.BasicProperties pros = MessageProperties.PERSISTENT_TEXT_PLAIN;
                //3.指定exchange,同时routing key为空字符串
                channel.basicPublish("logs", "",pros ,message.getBytes());
                System.out.println("sent message: "+message);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
