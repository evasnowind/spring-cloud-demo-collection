package com.prayerlaputa.resttemplate.intercept;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author chenglong.yu@brgroup.com
 * created on 2020/6/24
 */
public class MyRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        System.out.println("拦截啦！！！");
        System.out.println(request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        System.out.println(response.getHeaders());
        return response;
    }
}
