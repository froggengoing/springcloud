package com.froggengo.cloud.stream;

import com.froggengo.cloud.channel.SendOutputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;

@EnableBinding(value={SendOutputChannel.class})
public class SendMsgImpl implements  SendMsg {
    private static Logger log = LoggerFactory.getLogger(SendMsgImpl.class);

    @Autowired
    private SendOutputChannel sendOutputChannel;

    @Override
    public boolean timerMessageSource() {
        Message<String> build = MessageBuilder.withPayload("From timerMessageSource"+"  时间："+ LocalDateTime.now().getNano()).setHeader("partitionKey", "1").build();
        boolean send = sendOutputChannel.msgSender().send(build);
        log.info("timerMessageSource 发送消息： "+build.toString()+"  时间："+ LocalDateTime.now().getNano());
        return send;
    }

    @Override
    public boolean sendMsgStr(String str) {
        Message<String> build = MessageBuilder.withPayload("From sendMsgStr："+str+"  时间："+ LocalDateTime.now().getNano()).setHeader("partitionKey", "0").build();
        boolean send = sendOutputChannel.msgSender().send(build);
        log.info("sendMsgStr 发送消息： "+build.toString() +"  时间："+ LocalDateTime.now().getNano());
        return send;
    }
}
