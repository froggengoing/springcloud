package com.froggengo.guava.limit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/guava/ratelimit")
public class QueryController {

    @ServerLimit //限流注解
    @GetMapping("/test")
    public Map res(){
        HashMap<String, String> map = new HashMap<>();
        map.put("hello","world");
        return map;
    }
}
