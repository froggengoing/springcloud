package com.froggengo.cloud.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SendOutputChannel {
    // 这里可以定义不同的通道
    String MSG_SENDER  = "msgSender"; // 通道名
    String MSG_SENDER2 = "msgSender2"; // 通道名

    @Output(SendOutputChannel.MSG_SENDER)
    MessageChannel msgSender();

    @Output(SendOutputChannel.MSG_SENDER2)
    MessageChannel msgSender2();

}