package com.froggengo.rabbitmqConsumer.practise;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;

/**
 * 参考：SimpleRabbitListenerContainerFactoryConfigurer
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration#simpleRabbitListenerContainerFactory(org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer, org.springframework.amqp.rabbit.connection.ConnectionFactory)
 * ConsumerConfig中的配置方法会导致yml配置文件无效，
 * 这样其实是去了自动配置的意义
 * 这里 SimpleRabbitListenerContainerFactoryConfigurer 会配置一次SimpleRabbitListenerContainerFactory
 * 所以这里再定义一次没有意义
 * 使用RabbitRetryTemplateCustomizer的实现类
 * @see org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer#retryTemplateCustomizers
 */
public class RabbitTemplateConfigure extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
    @Override
    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {

    }
}
