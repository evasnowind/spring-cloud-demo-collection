package com.prayerlaputa.feign.controller;

import com.prayerlaputa.feign.service.ServiceMy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenglong.yu
 * created on 2020/6/28
 */
@RestController
@RequestMapping("/rest/my")
public class MyController {

    @Autowired
    private ServiceMy serviceMy;

    @GetMapping("/greeting")
    public String sayHiByFeign(String content) {
        String res = serviceMy.sayHi(content);
        return res;
    }
}
