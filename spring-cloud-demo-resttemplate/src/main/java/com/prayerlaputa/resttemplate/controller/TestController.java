package com.prayerlaputa.resttemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://provider/rest/hi", String.class);
        return "return :" + responseEntity;
    }
}
