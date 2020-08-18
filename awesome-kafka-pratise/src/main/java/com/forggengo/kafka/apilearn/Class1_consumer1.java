package com.forggengo.kafka.apilearn;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Class1_consumer1 {

    /**
     * 消费者接受到消息后立即提交确认，如果提交确认但没有接收的信息，则可能丢失消息
     * 方式：
     * 1、自动提交
     * 2、
     */
    /** 1、生产者先发送1万笔数据，然后在启动消费者，发现无法消费前面的数据。
     * 猜测，只有那种先消费、消费者宕机后重新启动的情况，才会继续消费
     * 解释：当新增group时
     * 每次测试时新增group（可在group后加时间戳区分），初始偏移量offset的配置为kafka.auto.offset.reset=latest，可使每个group获取最新偏移量，只读最新数据。
     * 但不断新增group，topic下的group会不断增加，偏移量_consumer_offsets 多次保存，需要对topic定期清理group_id，较为不友好。
    */
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test1");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //很奇怪的是默认为read_uncommitted，这里即使配置了read_uncommitted，也无法获取事务中未提交的数据
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, " read_uncommitted");
        // ... set additional consumer properties (optional)
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("Class1_producer1_topic"));
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

            TimeUnit.SECONDS.sleep(1);
            records.forEach(n->{
                System.out.println(n.topic()+","+n.headers()+","+n.partition()+","+n.offset()+","+n.key()+"=="+n.value());
            });
        }




    }
}
