# OpenFeign 的使用

## 1、基本概念

#### Feign  

Feign是Spring Cloud组件中的一个轻量级RESTful的HTTP服务客户端

Feign内置了Ribbon，用来做客户端负载均衡，去调用服务注册中心的服务。

Feign的使用方式是：使用Feign的注解定义接口，调用这个接口，就可以调用服务注册中心的服务

Feign支持的注解和用法请参考官方文档：https://github.com/OpenFeign/feign

Feign本身不支持Spring MVC的注解，它有一套自己的注解

#### OpenFeign  

OpenFeign是Spring Cloud 在Feign的基础上支持了Spring MVC的注解，如@RequesMapping等等。
OpenFeign的@FeignClient可以解析SpringMVC的@RequestMapping注解下的接口，
并通过动态代理的方式产生实现类，实现类中做负载均衡并调用其他服务。

上述引自：[Ribbon、Feign和OpenFeign的区别](https://blog.csdn.net/zimou5581/article/details/89949852)

## 2、使用  

### 2.1 pom引入依赖  

```yaml
<!-- 引入feign依赖 ，用来实现接口伪装 -->
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```



### 2.2 打开注解  

在Application启动类上加`@EnableFeignClients`注解。

```
@EnableFeignClients
@SpringBootApplication
public class FeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }

}

```



### 2.3 添加接口/注解

此处仅给出最简单的示例。

一般一个服务提供者，写一个interface。

发现服务提供者的方式：  

-  借助`eureka`：name是 虚拟主机名，默认服务名，请求时 会将它解析成注册表中的服务。
- 不借助`eureka`：自定义一个client名字。就用url属性指定 服务器列表。url=“http://ip:port/”，此时的name作用就是创建负载均衡器。

```java
@FeignClient(name = "service-provider")
public interface ServiceMy {

    @GetMapping("/say/hi")
    String sayHi(@RequestParam("content") String content);
}
//sayHi方法将向service-provider服务的 /say/hi 接口发送请求。
```

### 2.4 调用服务  

与平时我们写的服务一样，调用即可（当然，前提时所调用的服务已经实现，光自己配好、别人没实现那就白搭了~.~）

```
@RestController
@RequestMapping("/rest/my")
public class MyController {

    @Autowired
    private ServiceMy serviceMy;

    @GetMapping("/greeting")
    public String sayHiByFeign(String content) {
        return serviceMy.sayHi(content);
    }
}
```

浏览器中即可测试。



### 2.5 修改负载均衡参数

Feign默认已集成了Ribbon做客户端的负载均衡，默认的策略是随机策略，想要更改策略的话，在配置文件中天几家如下配置即可

```yaml
service-provider:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
    # NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule #配置规则 随机
    # NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RoundRobinRule #配置规则 轮询
    # NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RetryRule #配置规则 重试
    # NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule #配置规则 响应时间权重
    # NFLoadBalancerRuleClassName: com.netflix.loadbalancer.BestAvailableRule #配置规则 最空闲连接策略
    # 其他可配置的参数：
    ConnectTimeout: 500 #请求连接超时时间
    ReadTimeout: 1000 #请求处理的超时时间
    OkToRetryOnAllOperations: true #对所有请求都进行重试
    MaxAutoRetriesNextServer: 2 #切换实例的重试次数
    MaxAutoRetries: 1 #对当前实例的重试次数
```

建议去官网看究竟有哪些可配置参数，比较懒的可以参考下这篇文章：[Spring Cloud Feign 负载均衡策略配置](https://blog.csdn.net/guoqiusheng/article/details/88898426)



## 3、自定义配置

#### 3.1 security权限控制示例

首先试试加上安全控制，步骤如下：

修改**服务提供者**的配置：

修改1：引入security包

```xml
<!-- 安全认证 -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

修改2：添加security配置

添加如下代码

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 关闭csrf
		http.csrf().disable();
		// 表示所有的访问都必须认证，认证处理后才可以正常进行
		http.httpBasic().and().authorizeRequests().anyRequest().fullyAuthenticated();
		// 所有的rest服务一定要设置为无状态，以提升操作效率和性能
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}
```

修改3：配置文件中添加security配置

```yaml
spring: 
  security: 
    user: 
      name: provider-auth
      password: 12345
```



此时，再调用feign实现的接口，产生如下信息：

```
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Mon Jun 29 15:37:30 CST 2020
There was an unexpected error (type=Internal Server Error, status=500).
[401] during [GET] to [http://service-provider/rest/say/hi?content=world] [ServiceMy#sayHi(String)]: [{"timestamp":"2020-06-29T07:37:30.851+0000","status":401,"error":"Unauthorized","message":"Unauthorized","path":"/rest/say/hi"}]
```

##### 给Feign请求添加权限配置

有两种方式：

- 方法1：添加拦截器

- 方法2：自定义配置类

下面演示的是自定义配置类添加配置类的方式。

自定义配置类：

```java
//@Configuration
public class FeignAuthConfig {

    @Bean
    public BasicAuthenticationInterceptor basicAuthenticationInterceptor() {
        return new BasicAuthenticationInterceptor("provider-auth", "12345");
    }
}
```

给Feign接口上添加配置：

```java
@FeignClient(name = "service-provider", configuration = FeignAuthConfig.class)
```

注意：如果在配置类上添加了@Configuration注解，并且该类在@ComponentScan所扫描的包中，那么该类中的配置信息就会被所有的@FeignClient共享。最佳实践是：不指定@Configuration注解（或者指定configuration，用注解忽略），而是手动：

```
@FeignClient(name = "service-provider", configuration = FeignAuthConfig.class)
```



方法1：添加拦截器需要做如下修改：

以下代码没亲自试验，从别的帖子抄的，大家请自行尝试。

```
//java代码添加拦截器
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class MyBasicAuthRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		// TODO Auto-generated method stub
		template.header("Authorization", "Basic cm9vdDpyb290");
	}
}

//spring配置文件中添加配置：
feign:
  client: 
    config:  
      service-provider: 
        
        request-interceptors:
        - com.prayerlaputa.xxx.MyBasicAuthRequestInterceptor
```



#### 3.2 属性配置  

##### 3.2.1 指定服务名称配置  

```yam
   feign:
     client: 
       config:  
         service-provider: 
           connect-timeout: 5000
           read-timeout: 5000
           logger-level: full
           
```

##### 3.2.2 通用配置

```yam
   feign:
     client: 
       config:  
         default: 
           connect-timeout: 5000
           read-timeout: 5000
           logger-level: full
```

 属性配置比Java代码优先级高。也可通过配置设置java代码优先级高。

```yaml
feign:
	client: 
		default-to-properties: false
```



feign在方法上可以设置：@RequestMapping,@ResponseBody。

方法中的参数可以设置：@RequestBody等等，Spring MVC中的注解。

推荐使用yml配置方式，在yml中按 代码提示键，可以看到所有配置。



## 4、Feign继承

1. 编写通用服务接口A，接口方法上写@RequestMapping()，此接口用于 feign。
2. 服务提供者 实现上面接口A。
3. 服务消费者的feign client接口 继承A。



```
common组件：
package com.online.taxi.common.interactor;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.online.taxi.common.dto.ResponseResult;
import com.online.taxi.common.dto.order.ForecastRequest;
import com.online.taxi.common.dto.order.ForecastResponse;

public interface CommonServiceForecast {
	
	@RequestMapping(value = "/forecast/single",method = RequestMethod.POST)
	public ResponseResult<ForecastResponse> forecast(@RequestBody ForecastRequest 				forecastRequest);
	
}

提供者：
@RestController
public class ServiceForecastController implements CommonServiceForecast {

	@Override
	@PostMapping("/forecast")
	public ResponseResult<ForecastResponse> forecast(@RequestBody ForecastRequest forecastRequest) {
		// 业务逻辑
		return null;
	}

}

消费者
@FeignClient(name = "service-valuation")
public interface ServiceForecast extends CommonServiceForecast {

}
```

这样服务端和客户端就耦合了，这么用，会方便编码。自己权衡取舍。没有对错。



## 5、Feign压缩  

开启压缩可以有效节约网络资源，但是会增加CPU压力，建议把最小压缩的文档大小适度调大一点，进行gzip压缩。

```sh
feign:
  compression:
    request:
      enabled: true
    response: #设置返回值后，接受参数要改一下。
      enabled: true  

点注解进去，看看默认值
org.springframework.cloud.openfeign.encoding
/**
	 * The list of supported mime types.
	 */
	private String[] mimeTypes = new String[] { "text/xml", "application/xml",
			"application/json" };

	/**
	 * The minimum threshold content size.
	 */
	private int minRequestSize = 2048; 单位是B。
```

源码

```
org.springframework.cloud.openfeign.encoding.FeignContentGzipEncodingInterceptor

方法 判断内容是否超过配置的大小
private boolean contentLengthExceedThreshold(Collection<String> contentLength) {

		try {
			if (contentLength == null || contentLength.size() != 1) {
				return false;
			}

			final String strLen = contentLength.iterator().next();
			final long length = Long.parseLong(strLen);
			return length > getProperties().getMinRequestSize();
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
在HTTP协议中，有Content-Length的详细解读。Content-Length用于描述HTTP消息实体的传输长度the transfer-length of the message-body。在HTTP协议中，消息实体长度和消息实体的传输长度是有区别，比如说gzip压缩下，消息实体长度是压缩前的长度，消息实体的传输长度是gzip压缩后的长度。

```



## 6、日志配置  

实现步骤：

1. 在application.yml配置文件中开启日志级别配置
2. 编写配置类，定义日志级别bean。
3. 在接口的@FeignClient中指定配置类
4. 重启项目，测试访问

```yaml
#开启debug模式
debug: true
logging:
  level:
    com.prayerlaputa.feign.ServiceMy: debug

```

```java
public class FeignAuthConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("provider-auth", "12345");
    }

    //添加日志级别配置
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
      /**
     * FULL Log the headers, body, and metadata for both requests and responses.
     * 全部：请求和响应的头、体、元数据
     */

    /**
     * Bean 把当前对象注入到spring容器中
     * <bean id= class=></>
     * @return
     */
    /**
     *  NONE No logging.不做日志
     */
    /**
     * BASIC Log only the request method and URL and the response status code and execution time.
     * 仅仅是只记录请求方法，URL地址，响应状态码和执行时间
     */
    /**
     * HEADERS Log the basic information along with request and response headers.
     * headers：记录基本信息，请求和响应的头
     */
}
```

在接口的@FeignClient中指定配置类，这步在上面已完成。重启、测试即可。输出类似这样：

```
2020-06-29 16:42:11.063 DEBUG 9828 --- [nio-8765-exec-4] o.s.w.c.HttpMessageConverterExtractor    : Reading to [java.lang.String] as "text/plain;charset=UTF-8"
2020-06-29 16:42:11.071 DEBUG 9828 --- [nio-8765-exec-4] m.m.a.RequestResponseBodyMethodProcessor : Using 'text/html', given [text/html, application/xhtml+xml, image/webp, image/apng, application/xml;q=0.9, application/signed-exchange;v=b3;q=0.9, */*;q=0.8] and supported [text/plain, */*, text/plain, */*, application/json, application/*+json, application/json, application/*+json]
2020-06-29 16:42:11.071 DEBUG 9828 --- [nio-8765-exec-4] m.m.a.RequestResponseBodyMethodProcessor : Writing ["service-provider(port=18763) echo:hi to response 'hellomotto2'"]
2020-06-29 16:42:11.078 DEBUG 9828 --- [nio-8765-exec-4] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
2020-06-29 16:42:13.532 DEBUG 9828 --- [nio-8765-exec-3] o.s.web.servlet.DispatcherServlet        : GET "/rest/my/greeting?content=hellomotto2", parameters={masked}
2020-06-29 16:42:13.533 DEBUG 9828 --- [nio-8765-exec-3] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.prayerlaputa.feign.controller.MyController#sayHiByFeign(String)
2020-06-29 16:42:13.621 DEBUG 9828 --- [nio-8765-exec-3] o.s.w.c.HttpMessageConverterExtractor    : Reading to [java.lang.String] as "text/plain;charset=UTF-8"
2020-06-29 16:42:13.622 DEBUG 9828 --- [nio-8765-exec-3] m.m.a.RequestResponseBodyMethodProcessor : Using 'text/html', given [text/html, application/xhtml+xml, image/webp, image/apng, application/xml;q=0.9, application/signed-exchange;v=b3;q=0.9, */*;q=0.8] and supported [text/plain, */*, text/plain, */*, application/json, application/*+json, application/json, application/*+json]
2020-06-29 16:42:13.622 DEBUG 9828 --- [nio-8765-exec-3] m.m.a.RequestResponseBodyMethodProcessor : Writing ["service-provider(port=18764) echo:hi to response 'hellomotto2'"]
2020-06-29 16:42:13.623 DEBUG 9828 --- [nio-8765-exec-3] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
2020-06-29 16:45:01.131  INFO 9828 --- [trap-executor-0] c.n.d.s.r.aws.ConfigClusterResolver      : Resolving eureka endpoints via configuration
```





## 参考资料

- [Spring Cloud Feign 请求压缩 、Feign的日志级别配置](https://blog.csdn.net/sinat_39179993/article/details/94356971)
- 