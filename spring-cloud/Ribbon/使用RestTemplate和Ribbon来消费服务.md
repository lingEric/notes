## 背景

项目已经提供两个服务，1.eureka server服务注册中心，2.eureka client服务提供者，提供一个'/hi' API接口。

在实际测试中，可以启动两个eureka client，用于测试负载均衡。

目前：

eureka server:8761

eureka client1:8762

cureka client2:8763

## 创建新module

创建module，取名eureka-ribbon-client,作为服务消费者，通过RestTemplate远程调用eureka-client提供的服务接口。添加核心依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```

application.yml

```yml
spring:
	application:
		name:eureka-ribbon-client
		
server:
	port:8764

eureka:
	client:
		serviceUrl:
			defaultZone:http:localhost:8761/eureka/
	
```



首先，需要将 `RestTemplate` 和 `Ribbon`结合

```java
@configuration
public class RibbonConfig{
    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

写一个RESTful API 接口，在该接口内部调用eureka-client提供的服务，本地controller直接调用该接口，该接口负责实现负载均衡。

```java
@Service
public class RibbonService{
    @Autowired
    RestTemplate restTemplate;
    public String hi(String name){
        return restTemplate.getForObject("http://eurek-client/hi?name="+name,String.class);
    }
}
```

