package com.prayerlaputa.gatewaywithjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
/*
Servlet、Filter、Listener 可以直接通过 @WebServlet、@WebFilter、@WebListener 注解自动注册，无需其他代码。
加上这个注解才WebFilter才会生效
 */
@ServletComponentScan
public class GatewayWithJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWithJwtApplication.class, args);
    }

}
