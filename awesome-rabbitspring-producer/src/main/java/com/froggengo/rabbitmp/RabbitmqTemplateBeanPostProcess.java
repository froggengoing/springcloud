package com.froggengo.rabbitmp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitmqTemplateBeanPostProcess implements BeanPostProcessor {

    //ConfirmCallback
    private RabbitTemplate.ConfirmCallback confirmCallback=(correlationData,ack,cause)->{
        System.out.println("消息标识"+correlationData);
        Message message = correlationData.getReturnedMessage();
        System.out.println("消息主体"+new String(message.getBody()));
        System.out.println("是否失败"+ack);
        System.out.println("失败原因"+cause);
        //设置消发送失败处理逻辑
    };
    //先返回ReturnCallback再返回confirmcallback
    //ReturnCallback
    private RabbitTemplate.ReturnCallback returnCallback=(Message message, int replyCode, String replyText, String exchange, String routingKey)->{
        System.out.println("消息主体："+new String (message.getBody()));
        System.out.println("相应码："+replyCode);
        System.out.println("响应描述："+replyText);
        System.out.println("交换器："+exchange);
        System.out.println("路由键："+routingKey);
        //这里应设计一个重发逻辑，比如先存储到redis
    };
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof RabbitTemplate){
            System.out.println("##########增加消息确认机制########");
            RabbitTemplate beanRabbit = (RabbitTemplate) bean;
            beanRabbit.setConfirmCallback(confirmCallback);
            beanRabbit.setReturnCallback(returnCallback);
        }
        return bean;
    }
}
