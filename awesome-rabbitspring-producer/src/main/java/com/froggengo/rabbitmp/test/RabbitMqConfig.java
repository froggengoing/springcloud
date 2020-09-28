package com.froggengo.rabbitmp.test;


import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
 * 因为需要配置confirm或return，如果自己new RabbitTemplate会导致很多配置没有设置或者需要手动设置
 * 所以还是沿用自动配置的逻辑，使用bean后置处理器
 * @see RabbitmqTemplateBeanPostProcess
 */
//@Configuration
//@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitMqConfig {
    @Value("${myRabbitmq.directExchangeName}")
    String exchange;

    //ConfirmCallback
    private RabbitTemplate.ConfirmCallback confirmCallback=(correlationData,ack,cause)->{
        System.out.println("消息标识"+correlationData);
        System.out.println("消息确认"+ack);
        System.out.println("失败原因"+cause);
        //设置消发送失败处理逻辑
    };
    //ReturnCallback
    private RabbitTemplate.ReturnCallback returnCallback=(Message message, int replyCode, String replyText, String exchange, String routingKey)->{
        System.out.println("消息主体："+new String (message.getBody()));
        System.out.println("相应码："+replyCode);
        System.out.println("响应描述："+replyText);
        System.out.println("交换器："+exchange);
        System.out.println("路由键："+routingKey);
        //这里应设计一个重发逻辑，比如先存储到redis
    };


    @Bean
    public RabbitTemplate rabbitTemplate(RabbitProperties properties,
                                         ObjectProvider<MessageConverter> messageConverter,
                                         ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
                                         ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        messageConverter.ifUnique(template::setMessageConverter);

        RabbitProperties.Template templateProperties = properties.getTemplate();
        if (templateProperties.getRetry().isEnabled()) {
            template.setRetryTemplate(
                    createRetryTemplate(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()),templateProperties.getRetry(),
                                    RabbitRetryTemplateCustomizer.Target.SENDER));
        }
        map.from(templateProperties::getReceiveTimeout).whenNonNull().as(Duration::toMillis)
                .to(template::setReceiveTimeout);
        map.from(templateProperties::getReplyTimeout).whenNonNull().as(Duration::toMillis)
                .to(template::setReplyTimeout);
        map.from(templateProperties::getExchange).to(template::setExchange);
        map.from(templateProperties::getRoutingKey).to(template::setRoutingKey);
        map.from(templateProperties::getDefaultReceiveQueue).whenNonNull().to(template::setDefaultReceiveQueue);

        //#########增加消息确认机制################
        template.setMandatory(determineMandatoryFlag(properties));
        map.from(confirmCallback).to(template::setConfirmCallback);
        map.from(returnCallback).to(template::setReturnCallback);
        System.out.println("------------RabbitMQTemplate初始化完成------------");
        return template;
    }
    private boolean determineMandatoryFlag(RabbitProperties properties) {
        Boolean mandatory = properties.getTemplate().getMandatory();
        return (mandatory != null) ? mandatory : properties.isPublisherReturns();
    }
    RetryTemplate createRetryTemplate(List<RabbitRetryTemplateCustomizer> customizers, RabbitProperties.Retry properties, RabbitRetryTemplateCustomizer.Target target) {
        PropertyMapper map = PropertyMapper.get();
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        map.from(properties::getMaxAttempts).to(policy::setMaxAttempts);
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        map.from(properties::getInitialInterval).whenNonNull().as(Duration::toMillis)
                .to(backOffPolicy::setInitialInterval);
        map.from(properties::getMultiplier).to(backOffPolicy::setMultiplier);
        map.from(properties::getMaxInterval).whenNonNull().as(Duration::toMillis).to(backOffPolicy::setMaxInterval);
        template.setBackOffPolicy(backOffPolicy);
        if (customizers != null) {
            for (RabbitRetryTemplateCustomizer customizer : customizers) {
                customizer.customize(target, template);
            }
        }
        return template;
    }
}
