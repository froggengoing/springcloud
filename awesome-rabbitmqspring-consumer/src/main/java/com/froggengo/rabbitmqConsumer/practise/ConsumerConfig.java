package com.froggengo.rabbitmqConsumer.practise;



import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@Configuration
public class ConsumerConfig {

    @Autowired
    Environment environment;

    private  String DEAD_EXCHANGE_NAME;
    //@Value("${myrabbitmq.deadQueueName}")
    //@Bean先于@Value执行，导致@Bean无法获取配置文件的值
    private  String DEAD_QUEUE_NAME;
    //@Value("${myrabbitmq.deadRoutingKey}")
    private  String DEAD_ROUTING_KEY;

    @PostConstruct
    public void setValue(){
/*        this.DEAD_EXCHANGE_NAME=environment.getProperty("myrabbitmq.deadExchangeName");
        this.DEAD_QUEUE_NAME=environment.getProperty("myrabbitmq.deadQueueName");
        this.DEAD_ROUTING_KEY=environment.getProperty("myrabbitmq.deadRoutingKey");*/
    }

    /**
     * 第二个参数：queue的持久化是通过durable=true来实现的。
     * 第三个参数：exclusive：排他队列，如果一个队列被声明为排他队列，该队列仅对首次申明它的连接可见，并在连接断开时自动删除。这里需要注意三点：
     　　  1. 排他队列是基于连接可见的，同一连接的不同信道是可以同时访问同一连接创建的排他队列；
     　　  2.“首次”，如果一个连接已经声明了一个排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同；
     　　  3.即使该队列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除的，这种队列适用于一个客户端发送读取消息的应用场景。
     * 第四个参数：自动删除，如果该队列没有任何订阅的消费者的话，该队列会被自动删除。这种队列适用于临时队列。
     */

//声明队列，并给队列增加x-dead-letter-exchange和x-dead-letter-routing-key参数，用于指定死信队列的路由和routingKey
    @Bean
    public Queue queue() {
        System.out.println("DEAD_QUEUE_NAME---"+DEAD_QUEUE_NAME);
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);
        //参数2：多长时间后发送,
        //会导致queue变成TTL队列，消息过了一定时间后丢弃
       //args.put("x-message-ttl",10000);
        return new Queue(DEAD_QUEUE_NAME, true, false, false, args);
    }
    @Bean
    public DirectExchange  exchange(){
        return ExchangeBuilder.directExchange(DEAD_EXCHANGE_NAME).durable(true).build();
    }
    //业务交换机和队列绑定
    @Bean
    public Binding deadLetterBinding() {
        //return new Binding("cme_UploadHlJFace", Binding.DestinationType.QUEUE, "CME_EXCHANGE", "upload_key", null);
        return BindingBuilder.bind(queue()).to(exchange()).with(DEAD_ROUTING_KEY);
    }
    /**
     * https://developer.aliyun.com/article/316814
     * spring.rabbitmq.listener.retry配置的重发是在消费端应用内处理的，不是rabbitqq重发
     * 可以配置MessageRecoverer对异常消息进行处理，此处理会在listener.retry次数尝试完并还是抛出异常的情况下才会调用，默认有两个实现：
     * RepublishMessageRecoverer：将消息重新发送到指定队列，需手动配置:
     * RejectAndDontRequeueRecoverer：如果不手动配置MessageRecoverer，会默认使用这个，实现仅仅是将异常打印抛出
     *
     * 缺点：无法进行 channel.basicNack
     */
/*    @Bean
    public MessageRecoverer getMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, DEAD_EXCHANGE_NAME, DEAD_ROUTING_KEY);
    }*/

    /**
     * https://www.jianshu.com/p/4904c609632f
     * 1、使用simpleRabbitListenerContainerFactory创建SimpleMessageListenerContainer
     * 有个好处就是在application.properties中对SimpleMessageListenerContainer的配置可以生效。
     * @param simpleRabbitListenerContainerFactory
     * @return
     */
    //@Bean
    public SimpleMessageListenerContainer config(SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory,RabbitTemplate rabbitTemplate) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setMessageListener(new MessageListenerAdapter(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                try {
                    // 处理业务逻辑
                }catch (Throwable r) {
                    // 失败抛出异常重试
                    throw r;
                }

            }
        }));
        endpoint.setId(String.valueOf(UUID.randomUUID()));
        SimpleMessageListenerContainer container = simpleRabbitListenerContainerFactory.createListenerContainer(endpoint);

        // 配置队列信息
        container.setQueueNames("队列1","队列2");
        // 配置重试
        container.setAdviceChain(createRetry(rabbitTemplate));
        return container;
    }
    private RetryOperationsInterceptor createRetry( RabbitTemplate rabbitTemplate) {

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                // 第一次重试调用
                return false;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                // 最后一次重试会调用

            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                // 每次重试失败后都会调用
            }
        });
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(5));
        retryTemplate.setBackOffPolicy(new NoBackOffPolicy());
        //org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
        return RetryInterceptorBuilder
                .stateless()
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, DEAD_EXCHANGE_NAME, DEAD_ROUTING_KEY))
                .retryOperations(retryTemplate)
                .build();
/*        return RetryInterceptorBuilder.stateless()
                .retryOperations(retryTemplate).recoverer(new RejectAndDontRequeueRecoverer()).build();*/
    }
}
