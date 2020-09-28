package batch;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * the message is illegal, maybe msg body or properties length not matched. msg body length limit 128k, msg properties length limit 32k.
 */
public class BatchProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("batch_test");
        producer.setSendMsgTimeout(3000);
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        ArrayList<Message> messages = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            messages.add(new Message("TopicTest", "TagA", ("Hello world-" + i).getBytes()));
        }
        //批量发送的消息大小不能超过1M，如果超过，则需要将消息分隔为小于1M的子列表
        ListSplitter splitter = new ListSplitter(messages);
        producer.setMaxMessageSize(1024*1024);
        while (splitter.hasNext()) {
           try {
               List<Message> listItem = splitter.next();
               System.out.println(listItem.size());
               producer.send(listItem);
           } catch (Exception e) {
               e.printStackTrace();
               //handle the error
           }
        }
        producer.shutdown();
    }
}
