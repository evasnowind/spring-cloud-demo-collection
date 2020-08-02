package com.prayerlaputa.gatewaywithjwt.filter;


import com.prayerlaputa.gatewaywithjwt.constant.JwtConst;
import com.prayerlaputa.gatewaywithjwt.service.JwtService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 *
 * 无需添加@Component注解，在启动类添加@ServletComponentScan注解
 * 后，会自动将带有@WebFilter的注解进行注入！
 *
 * @Order 指定过滤器的执行顺序,值越大越靠后执行
 *
 * @author chenglong.yu
 * created on 2020/8/1
 */
@WebFilter(urlPatterns = "/*", filterName = "jwtFilter")
@Order(1)
public class JwtFilter implements Filter {

    private static final List<String> WHITE_LIST = Collections.singletonList("/api/register");

    @Resource
    private JwtService jwtService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("JwtFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("JwtFilter");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String jwt = httpServletRequest.getHeader(JwtConst.JWT_HEADER_NAME);
        if (WHITE_LIST.contains(httpServletRequest.getRequestURI())) {
            //首次登陆，走正常逻辑即可
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (isValidToken(jwt)) {
            //已授权访问，需要更新token
            updateToken(httpServletResponse, jwt);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            //拦截未授权访问
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void updateToken(HttpServletResponse httpServletResponse, String jwt) {
        String payload = jwtService.parseToken(jwt);
        String newToken = jwtService.generateToken(payload);
        httpServletResponse.setHeader(JwtConst.JWT_HEADER_NAME, newToken);
    }

    private boolean isValidToken(String token) {
        return jwtService.isValidToken(token);
    }


    @Override
    public void destroy() {
        System.out.println("JwtFilter destroy");
    }
}
