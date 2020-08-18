package com.froggengo.cloud.controller;

import com.froggengo.cloud.stream.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendController {
    @Autowired
    private SendMsg sendMsg;

    @RequestMapping(value = "/send/{msg}",method = RequestMethod.GET)
    public boolean send(@PathVariable("msg")String msg){
        return sendMsg.sendMsgStr(msg);
    }
}
