package com.froggengo.cloud.loadBalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyLoadBalance implements LoadBalancer{

    private AtomicInteger nextServerCyclicCounter=new AtomicInteger();
    private final int getNextServerCount(){

        for(;;){
            int current = nextServerCyclicCounter.get() ;
            int next = current >= Integer.MAX_VALUE ? 0 : current+1;
            System.out.println("访问 计数"+next);
            if (nextServerCyclicCounter.compareAndSet(current,next)) {
                return next;
            }
        }
    }
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstances) {
        int nextServerCount = getNextServerCount();
        int index = nextServerCount % serviceInstances.size();
        System.out.println("服务索引"+index);
        return serviceInstances.get(index);
    }
}
