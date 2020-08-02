package com.prayerlaputa.gatewaywithjwt.controller;


import com.prayerlaputa.gatewaywithjwt.constant.JwtConst;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenglong.yu
 * created on 2020/8/1
 */
@RestController
public class JwtController {


    @GetMapping("/api/test")
    public String testApi(HttpServletRequest request, HttpServletResponse response) {
        String oldJwt = request.getHeader(JwtConst.JWT_HEADER_NAME);
        String newJwt = response.getHeader(JwtConst.JWT_HEADER_NAME);

        return String.format("Your old JWT is:\n%s \nYour new JWT is:\n%s\n", oldJwt, newJwt);
    }
}
