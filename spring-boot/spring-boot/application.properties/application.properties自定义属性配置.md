# application.properties 常见属性配置



使用application.properties，一般情况下主要用来配置数据库连接、日志相关配置等。除了这些配置内容之外，还有哪些其他特性和使用方法？如下： 
- 一般属性使用 

- 自定义属性使用 

- 属性间的引用（占位符） 
- 随机数的使用 
- 数据类型自动转换 
- 嵌套属性注入

## 一般属性使用 
应用的配置文件可以使用`application.properties`也可以使用`application.yml `
application.properties

```properties
spring.application.name=compute-service
server.port=80
server.tomcat.uri-encoding=GBK
```

application.yml

```yml
spring:
    application:
        name: compute-service
server:
    port: 80
server:
    tomcat:
        uri-encoding: GBK
```



以上属性一般都会被SpringBoot框架直接使用； 
**注意**：使用.yml时，属性名的值和冒号中间必须有空格，如port: 80正确，port:80就是错的。

## 自定义属性使用 
在使用Spring Boot的时候，通常也需要定义一些自己使用的属性，如下方式直接定义：

```properties
com.test.name=Tom
com.test.password=123456
```

然后通过`@Value(“${属性名}”)`注解来加载对应的配置属性，具体如下：

```java
public class MyProperties{
    @Value("${com.test.name}")
    private String name;
    
    @Value("${com.test.password}")
    private String password;
}
```

## 属性间的引用

```properties
app.name=MyApp
app.description=${app.name} is a Spring Boot application
server.port=${port:8080}
```

可以在配置文件中引用前面配置过的属性（需要注意优先级）； 
通过如`${app.name:默认名称}`方法还可以设置默认值，当找不到引用的属性时，会使用默认的属性，如当port属性不存在时会默认使用8080.

## 随机数的使用 
在一些情况下，有些参数我们需要希望它不是一个固定的值，比如密钥、服务端口等。Spring Boot的属性配置文件中可以通过${random}来产生int值、long值或者string字符串，来支持属性的随机值。

随机字符串

```properties
com.test.value=${random.value}
```

随机int

```properties
com.test.number=${random.int}
```

随机long

```properties
com.test.bignumber=${random.long}
```

10以内的随机数

```properties
com.test.test1=${random.int(10)}
```

10-20的随机数

```properties
com.test.test2=${random.int[10,20]}
```

random.int*支持value参数和,max参数，当提供max参数的时候，value就是最小值。



## 数据类型自动转换 

SpringBoot可以方便的将属性注入到一个配置对象中，并实现自动数据类型转换：

```properties
com.test.name=Isea533
com.test.port=8080
com.test.servers[0]=dev.bar.com
com.test.servers[1]=foo.bar.com
```

对应对象为：

```java
@ConfigurationProperties(prefix="com.test")
public class Config {
	private String name;
	private Integer port;
	private List<String> servers = new ArrayList<String>();
    
    public String geName(){
        return this.name;
    }

    public Integer gePort(){
        return this.port;
    }

    public List<String> getServers() {
        return this.servers;
    }
}
```

上面的代码会自动将prefix=”com.test” 前缀为com.test 的属性注入进来，并会自动转换类型。

**注意** 当使用List 的时候需要注意在配置中对List 进行初始化！

## 嵌套属性注入 
SpringBoot还支持嵌套属性注入：

```properties
name=Tom
jdbc.username=root
jdbc.password=root
```

对应的配置类：

```java
@ConfigurationProperties
public class Config {
	private String name;
	private Jdbc jdbc;
	class Jdbc {
		private String username;
		private String password;
		//getter...
    }

	public Integer gePort(){
    	return this.port;
	}

    public Jdbc getJdbc() {
    	return this.jdbc;
	}
}
```

jdbc开头的属性都会注入到Jdbc对象中