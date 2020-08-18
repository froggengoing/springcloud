package com.springtest;

import com.froggengo.practise.SpringMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/*@SpringBootTest(classes = SpringMain.class)
@RunWith(SpringRunner.class)*/
public class P9_RestTemplateTest {
    @Test
    public void test (){
        String url= "https://www.163.com/";
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getForObject(url,)
    }
}
