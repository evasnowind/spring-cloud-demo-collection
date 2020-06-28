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



## 3、其他



## 参考资料

- 