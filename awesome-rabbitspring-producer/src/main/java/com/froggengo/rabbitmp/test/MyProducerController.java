package com.froggengo.rabbitmp.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class MyProducerController {
    @Autowired
    MyProducerSender sender;

    @GetMapping("/send/{key}/{msg}")
    public String  sendMessage(@PathVariable String key, @PathVariable Object msg){
        sender.sendMessage(key,msg);
        return (String)msg;
    }

}
