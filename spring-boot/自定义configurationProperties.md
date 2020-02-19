# 自定义configurationProperties

1. 加入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

2. 创建配置文件

resources/config/cas.properties

```properties
cas.server=a
cas.login=b
cas.logout=c
```

3. 创建配置类

```java
//2.0以上版本需要使用该注解指明配置文件的位置
@PropertySource(value = "classpath:config/cas.properties")
//1.5.x版本使用location指明配置文件的位置
@ConfigurationProperties(prefix = "cas")
@Configuration
public class CasProperties {
    private String server;

    private String login;

    private String logout;
    
    //getter 和 setter
}
```

4. 测试

```java

@SpringBootApplication
@EnableConfigurationProperties(CasProperties.class)
public class OauthApplication implements CommandLineRunner {
    private final
    CasProperties casProperties;

    @Autowired
    public OauthApplication(CasProperties casProperties) {
        this.casProperties = casProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(casProperties.getServer() + casProperties.getLogin() + casProperties.getLogout());
    }
}
```



