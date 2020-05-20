package com.prayerlaputa.eureka_provider.controller;

import com.prayerlaputa.eureka_provider.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/rest")
@RestController
public class DemoController {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Resource
    private HealthStatusService healthStatusService;

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private Integer serverPort;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }


    @GetMapping("/hi")
    public String getHi() {
        return applicationName + " " + serverPort + " say: hi";
    }

    @GetMapping("/health")
    public String health(@RequestParam("status") Boolean status) {
        healthStatusService.setStatus(status);
        return healthStatusService.getStatus();
    }

    @PostMapping("/post/hi")
    public String getPostHi() {
        return applicationName + " " + serverPort + " say:hi post request!";
    }

    @GetMapping("/getMap")
    public Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "prayer");
        return map;
    }

    @GetMapping("/getObj")
    public Person getObj() {
        Person person = new Person();
        person.setId(100);
        person.setName("xiaoming");
        return person;
    }


}
