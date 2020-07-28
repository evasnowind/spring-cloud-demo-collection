package com.prayerlaputa.zuuldemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenglong.yu
 * created on 2020/7/20
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/hi")
    public String sayHi() {
        return "hi";
    }
}
