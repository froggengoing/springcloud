package com.froggengo.rabbitmqConsumer.springconfig;

import com.froggengo.rabbitmqConsumer.practise.MyRabbitRetryTemplateCustomizer;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

/**问题：
 * RabbitRetryTemplateCustomizer无法获取channel，无法发送basicNack
 */
@Configuration
@EnableConfigurationProperties(RabbitExchangeMetaProperties.class)
public class MyRabbitConfiguration {

    @Bean
    public Queue queue(RabbitExchangeMetaProperties properties) {
/*        System.out.println("DEAD_QUEUE_NAME---"+properties.deadQueueName);
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", properties.deadExchangeName);
        args.put("x-dead-letter-routing-key", properties.deadRoutingKey);*/
        //参数2：多长时间后发送,
        //会导致queue变成TTL队列，消息过了一定时间后丢弃
        //args.put("x-message-ttl",10000);
        return new Queue(properties.deadQueueName, true, false, false);
    }
    @Bean
    public DirectExchange exchange(RabbitExchangeMetaProperties properties){
        return ExchangeBuilder.directExchange(properties.deadExchangeName).durable(true).build();
    }
    //业务交换机和队列绑定
    @Bean
    public Binding deadLetterBinding(RabbitExchangeMetaProperties properties) {
        //return new Binding("cme_UploadHlJFace", Binding.DestinationType.QUEUE, "CME_EXCHANGE", "upload_key", null);
        return BindingBuilder.bind(queue(properties)).to(exchange(properties)).with(properties.deadRoutingKey);
    }

    /**
     * 这里使用匿名的方式：
     * @see MyRabbitRetryTemplateCustomizer
     * RetryListener其实没什么用
     */
    //@Bean
    public RabbitRetryTemplateCustomizer getMyRabbitRetryTemplateCustomizer(){
        /**
         * 这里无法获取到运行的上下文，也就无法获取channel
         * 怎么 执行channel.basicNack？
         * https://docs.spring.io/spring-cloud-stream/docs/Ditmars.SR3/reference/htmlsingle/#_retry_with_the_rabbitmq_binder
         */
        return new RabbitRetryTemplateCustomizer(){
            @Override
            public void customize(Target target, RetryTemplate retryTemplate) {
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
                            RetryOperationsInterceptor interceptor = (RetryOperationsInterceptor) callback;

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
        };
    }
    /**
     * https://my.oschina.net/dengfuwei/blog/1595047 建议
     * 1、处理方法内，手动发送消息至某个队列
     * 2、将消息转为死信消息
     *  消息变成死信有以下几种情况：
     *      消息被拒绝（basic.reject/ basic.nack）并且requeue=false
     *      消息TTL过期（参考：RabbitMQ之TTL（Time-To-Live 过期时间））
     *      队列达到最大长度
     */

/*    @Bean
    public MessageRecoverer getMessageRecoverer(RabbitTemplate rabbitTemplate,RabbitExchangeMetaProperties properties){
        return new RepublishMessageRecoverer(rabbitTemplate, properties.deadExchangeName, properties.deadRoutingKey);
    }*/
}
