package com.froggengo.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SentinelController{
    @Autowired
    SentinelCloudApplication.TestService testService;

    /**
     * 1、sentinel自定义为所有http请求，埋点
     * 也可以使用SentinelResource为所有请求或者方法(比如service层)埋点
     * 在sentinel形式为 /请求地址
     *                      /资源1
     *                      /资源2
     * /getHello
     *    /service
     * 每个资源名，只会出现一次
     * 2、同时也可以使用SentinelResource，指定某些处理方式
     * */
    //@SentinelResource("r1")
    @GetMapping("/getHello")
    public String hello() {
        testService.sayhello();
        testService.sayhello2();
        return "Hello";
    }
    //@SentinelResource("t1")
    @GetMapping("/t1")
    public String t1() {
        testService.sayhello();
        return "t1";
    }
    // blockHandler 是位于当前类下的 exceptionHandler 方法，需符合对应的类型限制.
    //如果blockHandler不在当前类，则指定blockHandlerClass = {ExceptionUtil.class}（方法所在类）
    @SentinelResource(value = "t2", blockHandler = "exceptionHandler")
    @GetMapping("/t2")
    public Map t2(long s,String tt) {
        HashMap hashap = new HashMap();
        hashap.put("msg" ,"操作成功");
        return hashap;
    }

    /**
     * 默认只有BlockException一个参数，如果有多余的参数，这标记了SentinelResource的方法也有同样的参数，否则无效
     * 参数t2(long s)、exceptionHandler(long s, BlockException ex) ，成功
     * t2(long s,String tt)、exceptionHandler(long s, BlockException ex) ，不成功
     * t2(long s,String tt)、exceptionHandler(String tt,long s, BlockException ex) 不成功
     *t2(long s,String tt)、exceptionHandler(long ss,String tt, BlockException ex) 成功
     */
    public Map exceptionHandler(long ss,String tt, BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        HashMap hashap = new HashMap();
        hashap.put("msg" ,"触发限流："+ss);
        System.out.println("自定义异常处理");
        return hashap;
    }
}
