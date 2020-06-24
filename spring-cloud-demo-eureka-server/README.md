# 服务的注册与发现



## 1、Eureka 单节点搭建

1. pom文件

   添加依赖

   ```xml
   <dependency>
   	<groupId>org.springframework.cloud</groupId>
   	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
   </dependency>
   ```

   完整版如下（已添加了actuator等依赖）

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.6.RELEASE</version>
   		<relativePath/> <!-- lookup parent from repository -->
   	</parent>
   	<groupId>com.prayerlaputa</groupId>
   	<artifactId>eureka-server</artifactId>
   	<version>0.0.1-SNAPSHOT</version>
   	<name>eureka-server</name>
   	<description>Demo project for eureka</description>
   
   	<properties>
   		<java.version>1.8</java.version>
   		<spring-cloud.version>Hoxton.SR3</spring-cloud.version>
   	</properties>
   
   	<dependencies>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-test</artifactId>
   			<scope>test</scope>
   			<exclusions>
   				<exclusion>
   					<groupId>org.junit.vintage</groupId>
   					<artifactId>junit-vintage-engine</artifactId>
   				</exclusion>
   			</exclusions>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-actuator</artifactId>
   		</dependency>
   
   		<!-- 添加注册中心权限依赖  -->
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-security</artifactId>
   		</dependency>
   	</dependencies>
   
   	<!-- start.spring.io自动生成的pom文件中，缺少下面这个配置，将导致部分包找不到，坑啊。。。idea的spring initializr创建spring cloud项目则会补全下面这个部分 -->
   	<dependencyManagement>
   		<dependencies>
   			<dependency>
   				<groupId>org.springframework.cloud</groupId>
   				<artifactId>spring-cloud-dependencies</artifactId>
   				<version>${spring-cloud.version}</version>
   				<type>pom</type>
   				<scope>import</scope>
   			</dependency>
   		</dependencies>
   	</dependencyManagement>
   
   	<build>
   		<plugins>
   			<plugin>
   				<groupId>org.springframework.boot</groupId>
   				<artifactId>spring-boot-maven-plugin</artifactId>
   			</plugin>
   		</plugins>
   	</build>
   
   </project>
   
   ```

   

2. application.yml

   ```yaml
   spring:
     application:
       name: eureka-server
   
   server:
     port: 8761
   
   eureka:
     instance:
       appname: aha-eureka
       hostname: prayer1.com
     client:
       #是否将自己注册到Eureka Server,默认为true，由于当前就是server，故而设置成false，表明该服务不会向eureka注册自己的信息
       register-with-eureka: false
       #是否从eureka server获取注册信息，由于单节点，不需要同步其他节点数据，用false
       fetch-registry: false
       service-url:
         defaultZone: http://${spring.security.user.name}:${spring.security.user.password}@${eureka.instance.hostname}:${server.port}/eureka/
         #defaultZone: http://prayer1.com:8761/eureka/
   ```

   注意，建议先自己手工配置一个host，用于配置defaultZone。记得好像是在集群部署时如果直接用localhost会有问题，详情读者可以自己查一下。

3. Application类

   ```
   启动类上添加此注解标识该服务为配置中心
   @EnableEurekaServer
   ```

   

4. 



## 2、eureka client端配置

1. pom添加依赖

   ```xml
   	<dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
   	</dependency>
   ```

   完整版如下

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.6.RELEASE</version>
   		<relativePath/> <!-- lookup parent from repository -->
   	</parent>
   	<groupId>com.prayerlaputa</groupId>
   	<artifactId>eureka-consumer</artifactId>
   	<version>0.0.1-SNAPSHOT</version>
   	<name>eureka-consumer</name>
   	<description>Demo project for eureka</description>
   
   	<properties>
   		<java.version>1.8</java.version>
   		<spring-cloud.version>Hoxton.SR3</spring-cloud.version>
   	</properties>
   
   	<dependencies>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter</artifactId>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-test</artifactId>
   			<scope>test</scope>
   			<exclusions>
   				<exclusion>
   					<groupId>org.junit.vintage</groupId>
   					<artifactId>junit-vintage-engine</artifactId>
   				</exclusion>
   			</exclusions>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
   		</dependency>
   
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-devtools</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
   
   	</dependencies>
   
   	<!-- start.spring.io自动生成的pom文件中，缺少下面这个配置，将导致部分包找不到，坑啊。。。 -->
   	<dependencyManagement>
   		<dependencies>
   			<dependency>
   				<groupId>org.springframework.cloud</groupId>
   				<artifactId>spring-cloud-dependencies</artifactId>
   				<version>${spring-cloud.version}</version>
   				<type>pom</type>
   				<scope>import</scope>
   			</dependency>
   		</dependencies>
   	</dependencyManagement>
   
   	<build>
   		<plugins>
   			<plugin>
   				<groupId>org.springframework.boot</groupId>
   				<artifactId>spring-boot-maven-plugin</artifactId>
   			</plugin>
   		</plugins>
   	</build>
   
   </project>
   
   ```

   加入`spring-boot-starter-web`主要是为了程序能一直运行，维持在线状态，否则注册一下就退出了。

2. application.yml

   ```yaml
   eureka:
     client:
       service-url:
         defaultZone: http://prayer1.com:8761/eureka/
   server:
     port: 8762
   spring:
     application:
       name: consumer
   ```

   

3. Application类

   添加注解

   ```java
   @EnableEurekaClient
   ```

   



## 3、添加security安全访问控制

该部分主要参考https://blog.csdn.net/fox_bert/article/details/100854794

1. eureka注册中心添加依赖

   ```xml
    <!-- 添加权限依赖  -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-security</artifactId>
           </dependency>
   ```

   

2. eureka注册中心添加配置信息

   2.1 添加`spring.security.security.user.name`  `spring.security.security.user.password`

   2.2 `eureka.client.service-url.defaultZone`添加security用户名和密码

   ```
   spring:
     application:
       #这个spring应用的名字(之后调用会用到)
       name: homepage-eureka
     #1、添加安全访问配置，设置访问用户名和密码
     security:
       user:
         name: admin
         password: admin
    
   server:
     #服务注册中心端口号
     port: 8000
    
   eureka:
     instance:
       #服务注册中心实例的主机名
       hostname: localhost
     client:
       # 表示是否从 eureka server 中获取注册信息（检索服务），默认是true
       fetch-registry: false
       # 表示是否将自己注册到 eureka server（向服务注册中心注册自己）,默认是true
       register-with-eureka: false
       service-url:
         #服务注册中心的配置内容，指定服务注册中心的位置，eureka 服务器的地址（注意：地址最后面的 /eureka/ 这个是固定值）
         #defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
         #2、在原先的基础上添加security用户名和密码（例如：http://username:password@localhost:8000/eureka/）
         defaultZone: http://${spring.security.user.name}:${spring.security.user.password}@${eureka.instance.hostname}:${server.port}/eureka/
    
   ```

3. eureka client 向注册中心注册时需修改配置

   `eureka.client.service-url.defaultZone`添加security用户名和密码，，格式为 **username : password@**

   修改前：**http://localhost:8000/eureka/**

   修改后：**http://admin:admin@localhost:8000/eureka/**

   ```
   spring:
     application:
       name: homepage-eureka-client
    
   server:
     port: 8101
    
   eureka:
     client:
       service-url:
         #将自己注册进下面这个地址的服务注册中心
         defaultZone: http://admin:admin@localhost:8000/eureka/
   ```

4. ### 遇到的问题：eureka开启验证后服务无法连接注册中心  

   运行客户端时，无法注册到eureka，提示

   ```
   com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
   ```

   **解决步骤：**

   **（1）客户端向服务注册中心注册时是否有添加账号密码**

   参见`3、eureka client向注册中心注册时需修改配置`

   **（2）是否有关闭security的csrf**

   Spring Cloud 2.0 以上的security默认启用了csrf检验，要在eurekaServer端配置security的csrf检验为false

   因为如果不将csrf检验关闭，会出现其他服务无法注册进 eureka注册中心的情况（我就遇到了这个问题）

   步骤：

   1. 添加一个继承 WebSecurityConfigurerAdapter 的类
   2. 在类上添加 @EnableWebSecurity 注解；
   3. 覆盖父类的 configure(HttpSecurity http) 方法，关闭掉 csrf

   示例如下：

```

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 
/**
 * eureka开启验证后无法连接注册中心?
 * spring Cloud 2.0 以上）的security默认启用了csrf检验，要在eurekaServer端配置security的csrf检验为false
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        super.configure(http);
    }
}
```