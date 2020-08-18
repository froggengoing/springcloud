package com.froggengo.nacosboot.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConfigController {
/**
 curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=example&group=DEFAULT_GROUP&content=useLocalCache=true"
 curl -X PUT 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=example&ip=127.0.0.1&port=8080'
 */
    //配置中心
    @NacosValue(value="${useLocalCache:false}",autoRefreshed = true)
    private boolean usrLocalCache;
    //服务发现
    @NacosInjected
    private NamingService namingService;

    @GetMapping("/config/get")
    public boolean get(){
        return usrLocalCache;
    }

    @GetMapping("/discovery/getService")
    public List<ServiceInfo> getService() throws NacosException {
        List<ServiceInfo> subscribeServices = namingService.getSubscribeServices();
        return subscribeServices;
    }

    @GetMapping("/discovery/get")
    public  List<Instance> getInstance(@RequestParam("serviceName") String name) throws NacosException {
        System.out.println(name);
        List<Instance> allInstances = namingService.getAllInstances(name);
        return allInstances;
    }
}
