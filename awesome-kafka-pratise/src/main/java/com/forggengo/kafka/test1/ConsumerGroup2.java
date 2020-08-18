package com.forggengo.kafka.test1;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

/**
 * 结束到数据时，先保存唉本地。
 * 数量达到5时，打印，并发送确认的恢复
 * 如果接收到3个数据时候，宕机了。重启后，依然会重新接收为被确认的数据。
 * 注意，重复消费为题。
 * 在dosomething()与commitsysn()之间宕机，可能会导致重复消费。
 */
public class ConsumerGroup2{
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test1", "test2"));
        final int minBatchSize = 5;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
            }
            if (buffer.size() >= minBatchSize) {
                dosomething(buffer);
                consumer.commitSync();//手动执行 提交
                buffer.clear();
            }
        }

    }

    private static void dosomething(List<ConsumerRecord<String, String>> buffer) {
        buffer.forEach(record->{
            System.out.printf("offset = %d, key = %s, value = %s\n",
                    record.offset(), record.key(), record.value());
        });

    }
}
