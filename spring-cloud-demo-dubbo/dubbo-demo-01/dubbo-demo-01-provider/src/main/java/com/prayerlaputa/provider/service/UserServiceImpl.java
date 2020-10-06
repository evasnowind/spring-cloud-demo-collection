package com.prayerlaputa.provider.service;

import com.prayerlaputa.api.api.UserService;
import com.prayerlaputa.api.dto.UserAddDTO;
import com.prayerlaputa.api.dto.UserDTO;

@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserServiceImpl implements UserService {
    @Override
    public UserDTO get(Integer id) {
        return new UserDTO().setId(id)
                .setName("没有昵称：" + id)
                // 1 - 男；2 - 女
                .setGender(id % 2 + 1);
    }

    @Override
    public Integer add(UserAddDTO addDTO) {
        return (int) (System.currentTimeMillis() / 1000); // 嘿嘿，随便返回一个 id
    }
}
