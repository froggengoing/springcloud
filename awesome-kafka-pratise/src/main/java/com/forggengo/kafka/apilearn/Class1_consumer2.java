package com.forggengo.kafka.apilearn;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * #############enable.auto.commit=false##############
 *
 * ############isolation.level=read_committed##################
 *
 *
 *############max.poll.interval.ms#######
 * 增大时间，给consumer更多的处理时间
 * 相对的，增大了 rebalance 的时间，因为rebalance发生在poll的调用中
 * ##########max.poll.records##########
 * 限制在单个poll调用中返回的记录数
 * ###########ession.timeout.ms####
 *
 */
public class Class1_consumer2 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test1");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // ... set additional consumer properties (optional)
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("foo", "bar"));
        final int minBatchSize = 200;
        List<ConsumerRecord<String, String>> buffer = new ArrayList <>();
        while (true) {
             ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
             for (ConsumerRecord<String, String> record : records) {
                 buffer.add(record);
             }
             if (buffer.size() >= minBatchSize) {
                 //insertIntoDb(buffer);
                 buffer.forEach(n->{
                     System.out.println(n.topic()+","+n.headers()+","+n.partition()+","+n.offset()+","+n.key()+"=="+n.value());
                 });
                 consumer.commitSync();
                 buffer.clear();
             }
        }
    }
}
