package com.forggengo.kafka.apilearn;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Class1_producer1 {
    /**config:
     * ##########acks#######################
     * acks:0,立即发送至socket buffer，不等待确认。retries配置项失效。
     * acks:1,等待leader写入成功，不确保其他follower同步成功
     * acks:all/-1,等待所有同步副本写入成功,只要还有一个同步副本在，数据就不会丢
     * ##########bootstrap.servers##########
     * 格式（full set of servers）：host1:port1,host2:port2
     * #########batch.size、linger.ms#######
     * batch.size //批量发送给服务器消息的数量
     * linger.ms=5 //发送的数据先存放在buffer中，5ms后再批量发送
     * #########max.block.ms
     * 控制send()和partitionsFor()最大阻塞时间
     * #########buffer.memory
     * 消息待发送缓冲区的总内存大小，缓存区满了，将导致send阻塞，超过max.block.ms将TimeoutException
     * #########enable.idempotencem幂等性
     * 如果设置了幂等性，retries默认为Integer.MAX_VALUE，acks默认为all
     * 单会话幂等性
     * #########transactional.id#######
     * 该模式下enable.idempotencem默认为true
     * 如配置了事务，则replication.factor至少为3，min.insync.replicas为2，消费者必须配置为读已提交
     * transactional.id可以使同一个producer建立多会话的事务
     * #############################
     */
    /**
     * 1、1万笔数据：第一次要创建topic所以时间很长
     * acks=all发送需要14776、1190、1162、1184
     * acks=1,998、891
     * acks=0,760
     * acks=1，linger.ms=100，降至864、861
     * acks=all，linger.ms=100，将至749、861
     * 2、topic不存在时，默认创建一个partition的topic，这里应该是使用broker的默认配置
     * offset
     * partition
     */
    @Test
    public void test() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //批量发送
        props.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        //自定义partition
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"");
        //启用幂等性
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        Producer<String, String> producer = new KafkaProducer<>(props);
        long begin = System.currentTimeMillis();
        //for (int i = 0; i < 10000; i++)
        for (int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("Class1_producer1_topic", Integer.toString(i) + "十", Integer.toString(i)), (k, v) -> {
                System.out.println("offset:" + k.offset() + ", partition:" + k.partition() + ", topic:" + k.topic());
            });
        System.out.println(System.currentTimeMillis() - begin);
        producer.close();//关闭连接也耗费了
        System.out.println(System.currentTimeMillis() - begin);

    }

    @Test
    public void testTransaction() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        //props.put(ProducerConfig.BATCH_SIZE_CONFIG, "2");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id");
        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

        producer.initTransactions();

        try {
            producer.beginTransaction();
            for (int i = 0; i < 100; i++) {
                producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i)));
                //打开以下注释后，这10条数据都不会提交,消费者也无法获取
/*                if(i>=70) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int i1 = 99 / 0;
                }*/
            }
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            producer.abortTransaction();
        }
        producer.close();

    }

    /**
     * 异步获取服务器返回结果
     */
    @Test
    public void testSimple() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "fly");
        //props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id");
/*        Producer<byte[], byte[]> producer = new KafkaProducer<>(props, new ByteArraySerializer(), new ByteArraySerializer());
        byte[] key = "key".getBytes();
        byte[] value = "value".getBytes();*/
        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("Class1_producer1_topic", "key111", "value111");
        //立即获取future结果
        producer.send(record).get();
        //回调方式获取future结果

        List<ProducerRecord<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            //方式一：先把消息放入list，再发送，这种方式导致负载均衡失效
            //list.add( new ProducerRecord<String,String>("Class1_producer1_topic", ">>"+i+"-key111"+i, ">>"+i+"value111"));
            //方式二：直接发送
            producer.send(new ProducerRecord<String, String>("Class1_producer1_topic", ">>" + i + "-key111" + i, ">>" + i + "value111"),
                    new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            } else {
                                System.out.println("offset:" + metadata.offset() + ", partition:" + metadata.partition() + ", topic:" + metadata.topic());
                            }
                        }
                    });
        }
/*        list.forEach(n->{
            producer.send(record,
                    new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if(e != null) {
                                e.printStackTrace();
                            } else {
                                System.out.println("offset:"+metadata.offset()+", partition:"+metadata.partition()+", topic:"+metadata.topic());
                            }
                        }
                    });
        });*/


        TimeUnit.SECONDS.sleep(5);
    }
}
