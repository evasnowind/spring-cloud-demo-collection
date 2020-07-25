package com.prayerlaputa.resttemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * created on 2020/6/23
 */
@RequestMapping("/rest")
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hi")
    public String getHi() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://service-provider/rest/hi", String.class);
        return "return :" + responseEntity;
    }


    @GetMapping("/getMap")
    public String getMap() {
        String url = "http://service-provider/rest/getMap";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
        return "response=" + responseEntity;
    }


    @GetMapping("/getObj")
    public String getObj() {
        String url = "http://service-provider/rest/getMap";
        ResponseEntity<Person> responseEntity = restTemplate.getForEntity(url, Person.class);
        return "response=" + responseEntity;
    }
}
