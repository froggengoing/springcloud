package com.froggengo.rabbitmqConsumer.practise;

import org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 *
 */
//@Component
public class MyRabbitRetryTemplateCustomizer  implements RabbitRetryTemplateCustomizer {

    /**
     * @see org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer.Target
     * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.RabbitTemplateConfiguration#rabbitTemplate(org.springframework.boot.autoconfigure.amqp.RabbitProperties, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider, org.springframework.amqp.rabbit.connection.ConnectionFactory)
     * @see AbstractRabbitListenerContainerFactoryConfigurer#configure(org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory, org.springframework.amqp.rabbit.connection.ConnectionFactory, org.springframework.boot.autoconfigure.amqp.RabbitProperties.AmqpContainer)
     * 这里会配置两次，一次是
     * */
    @Override
    public void customize(Target target, RetryTemplate retryTemplate) {
        //所以这里应该对target做判断？
        if (target.equals(Target.LISTENER)){
            retryTemplate.registerListener(new RetryListener() {

                @Override
                public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                    System.out.println("第一次异常");
                    //返回true，否则报Retry terminated abnormally by interceptor before first attempt
                    /**
                     * @see RetryTemplate#doExecute(org.springframework.retry.RetryCallback, org.springframework.retry.RecoveryCallback, org.springframework.retry.RetryState)
                     */
                    return true;
                }
                /**
                 *   // 最后一次异常都会调用
                 */
                @Override
                public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                    System.out.println("最后一次异常");
                }

                /**
                 *   // 每次重试失败后都会调用
                 */
                @Override
                public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                    System.out.println("每次异常");
                }
            });
        }


    }
}
