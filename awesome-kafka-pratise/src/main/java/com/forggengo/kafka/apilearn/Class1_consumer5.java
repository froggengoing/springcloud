package com.forggengo.kafka.apilearn;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 手动设置offset，并消费数据
 */
public class Class1_consumer5 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test1");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer=null;
        try{
            consumer = new KafkaConsumer<>(props);
            Set<TopicPartition> assignment=null;
            //同时subscript和assign会导致 Subscription to topics, partitions and pattern are mutually exclusive
            //consumer.subscribe(Arrays.asList("Class1_producer1_topic"));
            List<PartitionInfo> partitionInfos = consumer.partitionsFor("Class1_producer1_topic");
            List<TopicPartition> topicPartitions = partitionInfos.stream()
                    .map(n -> new TopicPartition(n.topic(), n.partition()))
                    .collect(Collectors.toList());
            TopicPartition topicPartition2 = topicPartitions.stream().filter(n->n.partition()==2).findFirst().get();
            consumer.assign(topicPartitions);
            consumer.seek(topicPartition2,33300);
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                records.forEach(n->{
                    System.out.println(n.topic()+","+n.headers()+","+n.partition()+","+n.offset()+","+n.key()+"=="+n.value());
                });
            }
        }finally {
            if(consumer!=null){
                consumer.close();
            }
        }
    }

}
