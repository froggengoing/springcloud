package com.froggengo.practise.eventListener;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MyEventListener {
    //ContextStartedEvent无效
    //ContextRefreshedEvent有效
    //为什么？
    @EventListener(classes = {ContextRefreshedEvent.class})
    public void EventCall(ContextRefreshedEvent event){
        System.out.println("=============ContextStartedEvent==============Event:"+event.getApplicationContext().getApplicationName());
    }
}
