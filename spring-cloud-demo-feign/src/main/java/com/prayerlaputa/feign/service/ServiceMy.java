package com.prayerlaputa.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author chenglong.yu@brgroup.com
 * created on 2020/6/28
 */
@FeignClient(name = "service-provider")
public interface ServiceMy {

    @GetMapping("/rest/say/hi")
    String sayHi(@RequestParam("content") String content);
}
