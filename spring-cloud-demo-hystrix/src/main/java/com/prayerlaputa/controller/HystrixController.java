package com.prayerlaputa.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenglong.yu@brgroup.com
 * created on 2020/7/13
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class HystrixController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/say/hi")
    @HystrixCommand(fallbackMethod = "sayHiFallback")
    public String sayHi(String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);

        String url ="http://service-provider/rest/say/hi?content={content}";

        Map<String, String> map = Collections.singletonMap("content", content);
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class,map);
        log.info("sayHi return entity={}.", entity);
        return entity.toString();
    }

    public String sayHiFallback(String content, Throwable throwable) {
        log.info("sayHiFallback content={}, throw={}.", content, throwable);
        return "fallback:" + content;
    }
}
