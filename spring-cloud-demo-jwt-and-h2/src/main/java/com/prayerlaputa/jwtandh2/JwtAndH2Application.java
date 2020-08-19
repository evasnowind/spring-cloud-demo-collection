package com.prayerlaputa.jwtandh2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
/*
在SpringBootApplication上使用@ServletComponentScan注解后，
Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，无需其他代码。
 */
@ServletComponentScan
public class JwtAndH2Application {

    public static void main(String[] args) {
        SpringApplication.run(JwtAndH2Application.class, args);
    }

}
