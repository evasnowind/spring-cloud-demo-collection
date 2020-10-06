package com.prayerlaputa.consumer.controller;

import com.prayerlaputa.api.api.UserService;
import com.prayerlaputa.api.dto.UserAddDTO;
import com.prayerlaputa.api.dto.UserDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(protocol = "dubbo", version = "1.0.0")
    private UserService userService;

    @GetMapping("/get")
    public UserDTO get(@RequestParam("id") Integer id) {
        return userService.get(id);
    }

    @PostMapping("/add")
    public Integer add(UserAddDTO addDTO) {
        return userService.add(addDTO);
    }

}
