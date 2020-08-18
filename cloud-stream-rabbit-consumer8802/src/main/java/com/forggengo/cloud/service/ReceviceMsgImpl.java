package com.forggengo.cloud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(value = {Sink.class})
public class ReceviceMsgImpl implements ReceviceMsg {

    private static Logger logger = LoggerFactory.getLogger(ReceviceMsgImpl.class);

    @StreamListener(Sink.INPUT)
    @Override
    public void receive(String payload) {
        logger.info("接收消息："+payload);
    }
}