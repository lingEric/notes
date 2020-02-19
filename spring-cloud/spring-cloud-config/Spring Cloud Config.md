# Spring Cloud Config

在我们了解spring cloud config之前，我可以想想一个配置中心提供的核心功能应该有什么

- 提供服务端和客户端支持
- 集中管理各环境的配置文件
- 配置文件修改之后，可以快速的生效
- 可以进行版本管理
- 支持大的并发查询
- 支持各种语言

Spring Cloud Config可以完美的支持以上所有的需求。

Spring Cloud Config项目是一个解决分布式系统的配置管理方案。它包含了Client和Server两个部分，server提供配置文件的存储、以接口的形式将配置文件的内容提供出去，client通过接口获取数据、并依据此数据初始化自己的应用。Spring cloud使用git或svn存放配置文件，默认情况下使用git，我们先以git为例做一套示例。



首先在github上面创建了一个文件夹config-repo用来存放配置文件，为了模拟生产环境，我们创建以下三个配置文件：

```
// 开发环境
neo-config-dev.properties
// 测试环境
neo-config-test.properties
// 生产环境
neo-config-pro.properties
```

每个配置文件中都写一个属性neo.hello,属性值分别是 hello im dev/test/pro 。下面我们开始配置server端





## server端

### 1、添加依赖

```
<dependencies>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
	</dependency>
</dependencies>
```

需要多引入`spring-cloud-starter-eureka`包，来添加对eureka的支持。

### 2、配置文件

```

server:
  port: 8001
spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/ityouknow/spring-cloud-starter/     # 配置git仓库的地址
          search-paths: config-repo                             # git仓库地址下的相对地址，可以配置多个，用,分割。
          username: username                                        # git仓库的账号
          password: password                                    # git仓库的密码
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8000/eureka/   ## 注册中心eurka地址
```

增加了eureka注册中心的配置

### 3、启动类

启动类添加`@EnableDiscoveryClient`激活对配置中心的支持

```
@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```

这样server端的改造就完成了。先启动eureka注册中心，在启动server端，在浏览器中访问：`http://localhost:8000/`就会看到server端已经注册了到注册中心了。

![img](http://www.itmind.net/assets/images/2017/springcloud/eureka-config01.jpg)

按照上篇的测试步骤对server端进行测试服务正常。

### 4、测试

首先我们先要测试server端是否可以读取到github上面的配置信息，直接访问：`http://localhost:8001/neo-config/dev`

返回信息如下：

```
{
    "name": "neo-config", 
    "profiles": [
        "dev"
    ], 
    "label": null, 
    "version": null, 
    "state": null, 
    "propertySources": [
        {
            "name": "https://github.com/ityouknow/spring-cloud-starter/config-repo/neo-config-dev.properties", 
            "source": {
                "neo.hello": "hello im dev"
            }
        }
    ]
}
```

上述的返回的信息包含了配置文件的位置、版本、配置文件的名称以及配置文件中的具体内容，说明server端已经成功获取了git仓库的配置信息。

如果直接查看配置文件中的配置信息可访问：`http://localhost:8001/neo-config-dev.properties`，返回：`neo.hello: hello im dev`

修改配置文件`neo-config-dev.properties`中配置信息为：`neo.hello=hello im dev update`,再次在浏览器访问`http://localhost:8001/neo-config-dev.properties`，返回：`neo.hello: hello im dev update`。说明**server端会自动读取最新提交的内容**

仓库中的配置文件会被转换成web接口，访问可以参照以下的规则：

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

以neo-config-dev.properties为例子，它的application是neo-config，profile是dev。client会根据填写的参数来选择读取对应的配置。

## 客户端

### 1、添加依赖

```
<dependencies>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
```

需要多引入`spring-cloud-starter-eureka`包，来添加对eureka的支持。

### 2、配置文件

```
spring.application.name=spring-cloud-config-client
server.port=8002

spring.cloud.config.name=neo-config
spring.cloud.config.profile=dev
spring.cloud.config.label=master
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.serviceId=spring-cloud-config-server

eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

主要是去掉了`spring.cloud.config.uri`直接指向server端地址的配置，增加了最后的三个配置：

- `spring.cloud.config.discovery.enabled` ：开启Config服务发现支持
- `spring.cloud.config.discovery.serviceId` ：指定server端的name,也就是server端`spring.application.name`的值
- `eureka.client.serviceUrl.defaultZone` ：指向配置中心的地址

这三个配置文件都需要放到`bootstrap.properties`的配置中

### 3、启动类

启动类添加`@EnableDiscoveryClient`激活对配置中心的支持

```
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigClientApplication.class, args);
	}
}
```

启动client端，在浏览器中访问：`http://localhost:8000/` 就会看到server端和client端都已经注册了到注册中心了。

![img](http://www.itmind.net/assets/images/2017/springcloud/eureka-config02.jpg)

### 4、web测试

使用`@Value`注解来获取server端参数的值

```
@RestController
class HelloController {
    @Value("${neo.hello}")
    private String hello;

    @RequestMapping("/hello")
    public String from() {
        return this.hello;
    }
}
```

启动项目后访问：`http://localhost:8002/hello`，返回：`hello im dev update`说明已经正确的从server端获取到了参数。到此一个完整的服务端提供配置服务，客户端获取配置参数的例子就完成了。

我们在进行一些小实验，手动修改`neo-config-dev.properties`中配置信息为：`neo.hello=hello im dev update1`提交到github,再次在浏览器访问`http://localhost:8002/hello`，返回：`neo.hello: hello im dev update`，说明**获取的信息还是旧的参数**，这是为什么呢？因为springboot项目只有在启动的时候才会获取配置文件的值，修改github信息后，client端并没有再次去获取，所以导致这个问题。



总结：

server端每次都会获取到**最新的**配置文件

client端并**不会主动**去获取新的配置文件，只在项目启动时，获取一次。

## 高可用

为了模拟生产集群环境，我们改动server端的端口为8003，再启动一个server端来做服务的负载，提供高可用的server端支持。

![img](http://www.itmind.net/assets/images/2017/springcloud/eureka-config03.jpg)

如上图就可发现会有两个server端同时提供配置中心的服务，防止某一台down掉之后影响整个系统的使用。

我们先单独测试服务端，分别访问：`http://localhost:8001/neo-config/dev`、`http://localhost:8003/neo-config/dev`返回信息：

```
{
    "name": "neo-config", 
    "profiles": [
        "dev"
    ], 
    "label": null, 
    "version": null, 
    "state": null, 
    "propertySources": [
        {
            "name": "https://github.com/ityouknow/spring-cloud-starter/config-repo/neo-config-dev.properties", 
            "source": {
                "neo.hello": "hello im dev"
            }
        }
    ]
}
```

说明两个server端都正常读取到了配置信息。

再次访问：`http://localhost:8002/hello`，返回：`hello im dev update`。说明客户端已经读取到了server端的内容，我们随机停掉一台server端的服务，再次访问`http://localhost:8002/hello`，返回：`hello im dev update`，说明达到了高可用的目的。



## refresh

Spring Cloud Config分服务端和客户端，服务端负责将git（svn）中存储的配置文件发布成REST接口，客户端可以从服务端REST接口获取配置。但客户端并不能主动感知到配置的变化，从而主动去获取新的配置。客户端如何去主动获取新的配置信息呢，springcloud已经给我们提供了解决方案，**每个**客户端通过POST方法触发各自的`/refresh`。【不推荐，因为这种方式，在每次新增一个客户端时，都要多refresh一次或者多添加一个webhook，并不优雅，解决方案：使用消息总线，即spring-cloud-bus】

### 1、添加依赖

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

增加了`spring-boot-starter-actuator`包，`spring-boot-starter-actuator`是一套监控的功能，可以监控程序在运行时状态，其中就包括`/refresh`的功能。

### 2、 开启更新机制

需要给加载变量的类上面加载`@RefreshScope`，在客户端执行`/refresh`的时候就会更新此类下面的变量值。

```
@RestController
@RefreshScope // 使用该注解的类，会在接到SpringCloud配置中心配置刷新的时候，自动将新的配置更新到该类对应的字段中。
class HelloController {

    @Value("${neo.hello}")
    private String hello;

    @RequestMapping("/hello")
    public String from() {
        return this.hello;
    }
}
```

### 3、测试

*springboot 1.5.X 以上默认开通了安全认证，所以需要在配置文件application.properties添加以下配置*

```
management.security.enabled=false
```

OK 这样就改造完了，以post请求的方式来访问`http://localhost:8002/refresh`【客户端】 就会更新修改后的配置文件。

我们再次来测试，首先访问`http://localhost:8002/hello`，返回：`hello im dev`，我将库中的值修改为`hello im dev update`。在win上面打开cmd执行`curl -X POST http://localhost:8002/refresh`，返回`["neo.hello"]`说明已经更新了`neo.hello`的值。我们再次访问`http://localhost:8002/hello`，返回：`hello im dev update`,客户端已经得到了最新的值。

每次**手动刷新客户端**也很麻烦，有没有什么办法只要提交代码就自动调用客户端来更新呢，github的webhook是一个好的办法。

### 4、webhook

作用：每次手动修改了git仓库中的配置信息时，仓库自动发送post请求到指定的url去触发refresh操作，免去每次手动refresh。

WebHook是当某个事件发生时，通过发送http post请求的方式来通知信息接收方。Webhook来监测你在Github.com上的各种事件，最常见的莫过于push事件。如果你设置了一个监测push事件的Webhook，那么每当你的这个项目有了任何提交，这个Webhook都会被触发，这时Github就会发送一个HTTP POST请求到你配置好的地址。

如此一来，你就可以通过这种方式去自动完成一些重复性工作，比如，你可以用Webhook来自动触发一些持续集成（CI）工具的运作，比如Travis CI；又或者是通过 Webhook 去部署你的线上服务器。下图就是github上面的webhook配置。

![img](http://www.itmind.net/assets/images/2017/springcloud/webhook.jpg)

- `Payload URL` ：触发后回调的URL
- `Content type` ：数据格式，两种一般使用json
- `Secret` ：用作给POST的body加密的字符串。采用HMAC算法
- `events` ：触发的事件列表。

| events事件类型 | 描述                       |
| -------------- | -------------------------- |
| push           | 仓库有push时触发。默认事件 |
| create         | 当有分支或标签被创建时触发 |
| delete         | 当有分支或标签被删除时触发 |

> svn也有类似的hook机制，每次提交后会触发post-commit脚本，我们可以在这里写一些post请求

**弊端**

这样我们就可以利用hook的机制去触发客户端的更新，但是当客户端越来越多的时候hook支持的已经不够优雅，另外每次增加客户端都需要改动hook也是不现实的。

**解决方案：**

可以使用spring-cloud-bus 实现，具体参看以下内容



## Spring Cloud Bus

Spring cloud bus通过轻量消息代理连接各个分布的节点。这会用在广播状态的变化（例如配置变化）或者其他的消息指令。Spring bus的一个核心思想是通过分布式的启动器对spring boot应用进行扩展，也可以用来建立一个多个应用之间的通信频道。目前唯一实现的方式是用AMQP消息代理作为通道，同样特性的设置（有些取决于通道的设置）在更多通道的文档中。

Spring cloud bus被国内很多都翻译为消息总线，也挺形象的。大家可以将它理解为管理和传播所有分布式项目中的消息既可，其实本质是利用了MQ的广播机制在分布式的系统中传播消息，目前常用的有Kafka和RabbitMQ。利用bus的机制可以做很多的事情，其中配置中心客户端刷新就是典型的应用场景之一，我们用一张图来描述bus在配置中心使用的机制。

![img](http://www.itmind.net/assets/images/2017/springcloud/configbus1.jpg)

根据此图我们可以看出利用Spring Cloud Bus做配置更新的步骤:

- 1、提交代码触发post给客户端A发送bus/refresh
- 2、客户端A接收到请求从Server端更新配置并且发送给Spring Cloud Bus
- 3、Spring Cloud bus接到消息并通知给其它客户端
- 4、其它客户端接收到通知，请求Server端获取最新配置
- 5、全部客户端均获取到最新的配置

## 项目示例

根据以上代码进行改造，使用RabbitMQ来做示例。

**客户端spring-cloud-config-client改造**

### 1、添加依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

需要多引入`spring-cloud-starter-bus-amqp`包，增加对消息总线的支持

### 2、配置文件

```
## 刷新时，关闭安全验证
management.security.enabled=false
## 开启消息跟踪
spring.cloud.bus.trace.enabled=true

spring.rabbitmq.host=192.168.9.89
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=123456
```

配置文件需要增加RebbitMq的相关配置，这样客户端代码就改造完成了。

### 3、测试

依次启动spring-cloud-eureka、spring-cloud-config-server、spring-cloud-config-client项目，在启动spring-cloud-config-client项目的时候我们会发现启动日志会输出这样的一条记录。

```
2017-05-26 17:05:38.568  INFO 21924 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/bus/refresh],methods=[POST]}" onto public void org.springframework.cloud.bus.endpoint.RefreshBusEndpoint.refresh(java.lang.String)
```

说明客户端已经具备了消息总线通知的能力了，为了更好的模拟消息总线的效果，我们更改客户端spring-cloud-config-client项目的端口为8003、8004依次启动，这样测试环境就准备好了。启动后eureka后台效果图如下：

![img](http://www.itmind.net/assets/images/2017/springcloud/configbus3.jpg)

我们先分别测试一下服务端和客户端是否正确运行，访问：`http://localhost:8001/neo-config/dev`，返回信息：

```
{
    "name": "neo-config", 
    "profiles": [
        "dev"
    ], 
    "label": null, 
    "version": null, 
    "state": null, 
    "propertySources": [
        {
            "name": "https://github.com/ityouknow/spring-cloud-starter/config-repo/neo-config-dev.properties", 
            "source": {
                "neo.hello": "hello im dev"
            }
        }
    ]
}
```

说明server端都正常读取到了配置信息。

依次访问：`http://localhost:8002/hello`、`http://localhost:8003/hello`、`http://localhost:8004/hello`，返回：`hello im dev`。说明客户端都已经读取到了server端的内容。

现在我们更新`neo-config-dev.properties` 中`neo.hello`的值为`hello im dev update`并提交到代码库中，访问：`http://localhost:8002/hello` 依然返回`hello im dev`。我们对端口为8002的客户端发送一个`/bus/refresh`的post请求。在win下使用下面命令来模拟webhook.

```
curl -X POST http://localhost:8002/bus/refresh
```

执行完成后，依次访问：`http://localhost:8002/hello`、`http://localhost:8003/hello`、`http://localhost:8004/hello`，返回：`hello im dev update`。说明三个客户端均已经拿到了最新配置文件的信息，这样我们就实现了图一中的示例。

## 改进版本

在上面的流程中，我们已经达到了利用消息总线触发一个客户端`bus/refresh`,而刷新所有客户端的配置的目的。但这种方式并不优雅。原因如下：

- 打破了微服务的职责单一性。微服务本身是业务模块，它本不应该承担配置刷新的职责。
- 破坏了微服务各节点的对等性。
- 有一定的局限性。例如，微服务在迁移时，它的网络地址常常会发生变化，此时如果想要做到自动刷新，那就不得不修改WebHook的配置。

因此我们将上面的架构模式稍微改变一下

![img](http://www.itmind.net/assets/images/2017/springcloud/configbus2.jpg)

这时Spring Cloud Bus做配置更新步骤如下:

- 1、提交代码触发post请求给bus/refresh
- 2、server端接收到请求并发送给Spring Cloud Bus
- 3、Spring Cloud bus接到消息并通知给其它客户端
- 4、其它客户端接收到通知，请求Server端获取最新配置
- 5、全部客户端均获取到最新的配置

这样的话我们在server端的代码做一些改动，来支持`bus/refresh`

### 1、添加依赖

```
<dependencies>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-bus-amqp</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
	</dependency>
</dependencies>
```

需要多引入`spring-cloud-starter-bus-amqp`包，增加对消息总线的支持

### 2、配置文件

```
server:
  port: 8001
spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/ityouknow/spring-cloud-starter/     # 配置git仓库的地址
          search-paths: config-repo                             # git仓库地址下的相对地址，可以配置多个，用,分割。
          username: username                                        # git仓库的账号
          password: password                                    # git仓库的密码
  rabbitmq:
    host: 192.168.0.6
    port: 5672
    username: admin
    password: 123456

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8000/eureka/   ## 注册中心eurka地址


management:
  security:
     enabled: false
```

配置文件增加RebbitMq的相关配置，关闭安全验证。这样server端代码就改造完成了。

### 3、测试

依次启动spring-cloud-eureka、spring-cloud-config-server、spring-cloud-config-client项目，改动spring-cloud-config-client项目端口为8003、8004依次启动。测试环境准备完成。

按照上面的测试方式，访问server端和三个客户端测试均可以正确返回信息。同样修改`neo-config-dev.properties`中`neo.hello`的值为`hello im dev update`并提交到代码库中。在win下使用下面命令来模拟webhook触发server端`bus/refresh`.

```
curl -X POST http://localhost:8001/bus/refresh
```

执行完成后，依次访问：`http://localhost:8002/hello`、`http://localhost:8003/hello`、`http://localhost:8004/hello`，返回：`hello im dev update`。说明三个客户端均已经拿到了最新配置文件的信息，这样我们就实现了上图中的示例。

## 其它

### 局部刷新

某些场景下（例如灰度发布），我们可能只想刷新部分微服务的配置，此时可通过`/bus/refresh`端点的destination参数来定位要刷新的应用程序。

例如：`/bus/refresh?destination=customers:8000`，这样消息总线上的微服务实例就会根据destination参数的值来判断是否需要刷新。其中，`customers:8000`指的是各个微服务的ApplicationContext ID。

destination参数也可以用来定位特定的微服务。例如：`/bus/refresh?destination=customers:**`，这样就可以触发customers微服务所有实例的配置刷新。

### 跟踪总线事件

一些场景下，我们可能希望知道Spring Cloud Bus事件传播的细节。此时，我们可以跟踪总线事件（RemoteApplicationEvent的子类都是总线事件）。

跟踪总线事件非常简单，只需设置`spring.cloud.bus.trace.enabled=true`，这样在`/bus/refresh`端点被请求后，访问`/trace`端点就可获得类似如下的结果：

```
{
  "timestamp": 1495851419032,
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "stores:8002",
    "destination": "*:**"
  }
  },
  {
  "timestamp": 1495851419033,
  "info": {
    "signal": "spring.cloud.bus.sent",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "spring-cloud-config-client:8001",
    "destination": "*:**"
  }
  },
  {
  "timestamp": 1495851422175,
  "info": {
    "signal": "spring.cloud.bus.ack",
    "type": "RefreshRemoteApplicationEvent",
    "id": "c4d374b7-58ea-4928-a312-31984def293b",
    "origin": "customers:8001",
    "destination": "*:**"
  }
}
```

这个日志显示了`customers:8001`发出了RefreshRemoteApplicationEvent事件，广播给所有的服务，被`customers:9000`和`stores:8081`接受到了。想要对接受到的消息自定义自己的处理方式的话，可以添加`@EventListener`注解的AckRemoteApplicationEvent和SentApplicationEvent类型到你自己的应用中。或者到TraceRepository类中，直接处理数据。

这样，我们就可清晰地知道事件的传播细节。

## `/bus/refresh` BUG

`/bus/refresh` 有一个很严重的BUG，一直没有解决：对客户端执行`/bus/refresh`之后，挂到总线的上的客户端都会从Eureka注册中心撤销登记；如果对server端执行`/bus/refresh`,server端也会从Eureka注册中心撤销登记。再用白话解释一下，就是本来人家在Eureka注册中心注册的好好的，只要你对着它执行一次`/bus/refresh`，立刻就会从Euraka中挂掉。

其实这个问题挺严重的，本来你利用`/bus/refresh`给所有的节点来更新配置信息呢，结果把服务从Euraka中给搞掉了，那么如果别人需要调用客户端的服务的时候就直接歇菜了。不知道国内有童鞋公司在生产中用到这个功能没有，用了不就很惨烈。在网上搜索了一下，国内网友和国外网友都遇到过很多次，但是一直没有解决，很幸运就是我在写这篇文章的**前三天**，Netflix修复了这个问题，使用Spring Cloud最新版本的包就可以解决这个问题。由此也可以发现Spring Cloud还在快速的发展中，最新的版本可能也会有一些不稳定性，可见路漫漫而修远兮。

在pom中使用Spring Cloud的版本，解决这个bug.

```
<properties>
	<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
	<java.version>1.8</java.version>
	<spring-cloud.version>Dalston.SR1</spring-cloud.version>
</properties>
```

主要是这句：`<spring-cloud.version>Dalston.SR1</spring-cloud.version>`