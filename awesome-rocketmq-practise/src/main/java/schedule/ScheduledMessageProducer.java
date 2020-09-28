package schedule;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class ScheduledMessageProducer {
    
     public static void main(String[] args) throws Exception {
         // Instantiate a producer to send scheduled messages
         DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
         producer.setSendMsgTimeout(3000);
         // Launch producer
         producer.start();
         int totalMessagesToSend = 100;
         for (int i = 0; i < totalMessagesToSend; i++) {
             Message message = new Message("TestTopic", ("Hello scheduled message " + i).getBytes());
             //MessageStoreConfig已经写死了可以选择的延时的时间
             //private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
             // This message will be delivered to consumer 10 seconds later.
             message.setDelayTimeLevel(3);
             // Send the message
             producer.send(message);
         }
    
         // Shutdown producer after use.
         producer.shutdown();
     }
        
 }