package com.froggengo.com.fourth;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 *
 */
public class RabbitMqConfig4 {

    public ConnectionFactory cachingConnectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
/*        factory.setAddresses();
        factory.setVirtualHost();
        factory.setUsername();
        factory.setPassword();
        factory.setPort();
        factory.setHost();
        factory.setConnectionTimeout();
        factory.setPublisherReturns();
        factory.setPublisherConfirmType();
        factory.setRequestedHeartBeat();

        factory.setCacheMode();
        factory.setChannelCacheSize();
        factory.setChannelCheckoutTimeout();
        factory.setCloseExceptionLogger();
        factory.setConnectionListeners();*/


        return factory;
    }
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate();
/**
        template.setConnectionFactory();
        template.setMandatory();
        template.setMandatoryExpression();
        template.setMandatoryExpressionString();

        template.setEncoding();
        template.setExchange();
        template.setDefaultReceiveQueue();
        template.setRoutingKey();

        template.setConfirmCallback();
        template.setReturnCallback();

        template.setAfterReceivePostProcessors();
        template.setBeforePublishPostProcessors();
        template.setChannelTransacted();

        template.setCorrelationDataPostProcessor();
        template.setCorrelationKey();

        template.setMessageConverter();
        template.setMessagePropertiesConverter();
        template.setNoLocalReplyConsumer();
        template.setReceiveConnectionFactorySelectorExpression();
        template.setReceiveTimeout();
        template.setRecoveryCallback();
        template.setReplyAddress();
        template.setReplyErrorHandler();
        template.setReplyTimeout();
        template.setRetryTemplate();

        template.setSendConnectionFactorySelectorExpression();
        template.setTaskExecutor();
        template.setUseDirectReplyToContainer();
        template.setUsePublisherConnection();
        template.setUserCorrelationId();
        template.setUserIdExpression();
        template.setUserIdExpressionString();
        template.setUseTemporaryReplyQueues();
 **/
    return template;
    }
}
