package com.froggengo.rabbitmqConsumer;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(bindings = {@QueueBinding(
        value = @Queue(value = "${myRabbitmq.testDirectQueue}",
                durable = "${myRabbitmq.testDirectQueue.durable}",
                arguments = {
                @Argument(name="x-dead-letter-exchange",value = "${myrabbitmq.deadExchangeName}"),
                @Argument(name="x-dead-letter-routing-key",value = "${myrabbitmq.deadRoutingKey}"),
                }),
        exchange=@Exchange(value = "${myRabbitmq.directExchangeName}",durable = "${myRabbitmq.directExchangeName.durable}"),
        key = {"log","test"}
        )})
@Service
public class MyConsumerRecv {
    private static Logger log = LoggerFactory.getLogger(MyConsumerRecv.class);
    //org.springframework.amqp.core.Message
    //这个无法接收到信息，使用Message也不行
    @RabbitHandler
    public void getMessage(String msg){
        System.out.println("-------------接收到消息--------------");
        //System.out.println(new String(msg.getBody()));
        System.out.println(msg);
    }
    //没有这个方法，会一致报错，Listener method 'no match'
    @RabbitHandler
    public void getMsg(/*@Payload */ byte[] msg, Channel channel,Message message) throws IOException {
        //try{
            System.out.println("-------------接收到消息--------------");
            //System.out.println(new String(msg.getBody()));
//        System.out.println(new String(msg));
            log.info(new String(msg));
            //simple.retry.max-attempts:异常会连续收到3次消息
            //simple.retry.enabled:true
            int i=1/0;
            //配置acknowledge-mode=auto，spring自动根据是否抛出异常，选择发送nack还是ack
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
       // }
        /*catch (Exception e){
            //如果在方法内部捕获可异常，会到值配置文件的重试机制失效
            //所以这里交给
            log.error(e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,false);
        }*/

    }

}
