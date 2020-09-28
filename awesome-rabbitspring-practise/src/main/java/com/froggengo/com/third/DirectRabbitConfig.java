package com.froggengo.com.third;


import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * https://blog.csdn.net/qq_35387940/article/details/100514134
 */
@Configuration
public class DirectRabbitConfig {

    @Value("${myRabbitmq.directExchangeName}")
    public static  String directExchangeName;
    @Value("${myRabbitmq.directExchangeName.durable}")
    public static  boolean directExchangeDurable;
    @Value("${myRabbitmq.directExchangeName.autoDelete}")
    public static  boolean directExchangeAutoDelete;

    @Value("${myRabbitmq.testDirectQueue}")
    public static  String queueName;
    @Value("${myRabbitmq.testDirectQueue.durable}")
    public static  boolean queueDurable;
    //队列 起名：TestDirectQueue
    @Bean
    public Queue TestDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);
        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(queueName,true);
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange TestDirectExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(directExchangeName,directExchangeDurable,directExchangeAutoDelete);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting

    /**
     * 如果exchange需要声明多个队列以及routingkey，则声明多个bindings
     * @return
     */
    @Bean
    Binding bindingDirect() {

        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
    }



    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }



}
