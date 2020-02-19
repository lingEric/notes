# Spring boot集成Rabbit MQ使用初体验

![undefined](http://ww1.sinaimg.cn/large/006edVQGgy1g7f0o36vxzj30m8032q37.jpg)

## 1.rabbit mq基本特性

首先介绍一下rabbitMQ的几个特性



> **Asynchronous Messaging**
> Supports [multiple messaging protocols](https://www.rabbitmq.com/protocols.html), [message queuing](https://www.rabbitmq.com/tutorials/tutorial-two-python.html), [delivery acknowledgement](https://www.rabbitmq.com/reliability.html), [flexible routing to queues](https://www.rabbitmq.com/tutorials/tutorial-four-python.html), [multiple exchange type](https://www.rabbitmq.com/tutorials/amqp-concepts.html).

**异步消息** 

支持多种消息传递协议，消息排队，传递确认，灵活路由规则，多种交换类型。这些应该是rabbitmq最核心的特性了。

> **Developer Experience**
>
> Deploy with [BOSH, Chef, Docker and Puppet](https://www.rabbitmq.com/download.html). Develop cross-language messaging with favorite programming languages such as: Java, .NET, PHP, Python, JavaScript, Ruby, Go, [and many others](https://www.rabbitmq.com/devtools.html).

**部署体验？**

与BOSH，Chef，Docker和Puppet一起部署。使用喜欢的编程语言来开发跨语言消息传递，例如Java，.NET，PHP，Python，JavaScript，Ruby，Go等。

> **Distributed Deployment**
>
> Deploy as [clusters](https://www.rabbitmq.com/clustering.html) for high availability and throughput; [federate](https://www.rabbitmq.com/federation.html) across multiple availability zones and regions.

**分布式部署**

部署为集群以实现高可用性和吞吐量；跨多个可用区域和区域联合。

> **Enterprise & Cloud Ready**
>
> Pluggable [authentication](https://www.rabbitmq.com/authentication.html), [authorisation](https://www.rabbitmq.com/access-control.html), supports [TLS](https://www.rabbitmq.com/ssl.html) and [LDAP](https://www.rabbitmq.com/ldap.html). Lightweight and easy to deploy in public and private clouds.

**企业和云就绪**

可插拔身份验证，授权，支持TLS和LDAP。轻巧且易于在公共和私有云中进行部署。

> **Tools & Plugins**
>
> Diverse array of [tools and plugins](https://www.rabbitmq.com/devtools.html) supporting continuous integration, operational metrics, and integration to other enterprise systems. Flexible [plug-in approach](https://www.rabbitmq.com/plugins.html) for extending RabbitMQ functionality.

**工具&插件**

工具和插件的种类繁多，支持持续集成，运营指标以及与其他企业系统的集成。灵活的插件方法，用于扩展RabbitMQ功能。

> **Management & Monitoring**
>
> HTTP-API, command line tool, and UI for [managing and monitoring](https://www.rabbitmq.com/management.html) RabbitMQ.

管理和监控

HTTP-API支持，命令行工具，管理和监控界面。

![undefined](http://ww1.sinaimg.cn/large/006edVQGgy1g7f0lhtlo7j30eg0fu409.jpg)



## 2.rabbit mq核心概念

①Message

消息，消息就是数据的载体，由消息头和消息体组成。消息体是不透明的，而消息头由一系列的可选属性组成，这些属性包括routing-key(路由键，也就是消息是如何分发给队列的)，priority（相对于其它消息的优先权），delivery-mode（指定是否需要持久化存储）

②Publisher

消息的生产者，向交换机发布消息的客户端应用程序。

③Exchange

交换机用来接收生产者发送的消息并将这些消息按照路由规则或者交换机类型路由到指定的队列。交换机有4种类型：direct（默认），fanout，topic，以及headers，这四种类型支持不同的路由策略。

![undefined](http://ww1.sinaimg.cn/large/006edVQGgy1g7f0fg1fqgj30xi0kgjtl.jpg)

④Queue

消息队列，用于保存消息直到发送给消费者，是消息的容器。一个消息可以存入一个或多个队列，一直到消费者消费这个消息，才会从队列中删除。

⑤Binding

绑定，指定交换机和队列的绑定规则，可以理解为一个过滤器，当路由键符合这个绑定规则时，就会将消息发送给队列。交换机和队列之间的绑定可以是多对多的关系

⑥Connection

一个TCP连接

⑦Channel

信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。

⑧Consumer

消息的消费者，表示一个从消息队列中取得消息的客户端应用程序。

⑨Virtual Host

虚拟主机，表示一批交换机、消息队列和相关对象。虚拟主机是共享相同的身份认证和加密环境的独立服务器域。每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥有自己的队列、交换器、绑定和权限机制。vhost 是 AMQP 概念的基础，必须在连接时指定，RabbitMQ 默认的 vhost 是 / 。

⑩Broker

表示消息队列服务器实体。

更详细的说明请参考官方文档：https://www.rabbitmq.com/tutorials/amqp-concepts.html

## 3.交换机类型和消息路由

- Direct Exchange

![rabbitmq-direct.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7f10kpmtvj30cl07i3yq.jpg)

消息中的路由键（routing key）如果和 Binding中的 binding key 一致，交换器就将消息发到对应的队列中。路由键与队列名完全匹配，如果一个队列绑定到交换机要求路由键为“dog”，则只转发 routing key 标记为“dog”的消息，不会转发“dog.puppy”，也不会转发“dog.guard”等等。它是完全匹配、单播的模式。

- Fanout Exchange

![rabbitmq-fanout.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7f12m9tc8j30cl08m74m.jpg)

每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。fanout 交换器不处理路由键【路由键被忽略】，只是简单的将队列绑定到交换器上，每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消息。fanout 类型转发消息是最快的。

- Topic Exchange

![rabbitmq-topic.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7f1576ddej30f90870tz.jpg)

topic 交换器通过模式匹配分配消息的路由键属性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上。它将路由键和绑定键的字符串切分成单词，这些**单词之间用点隔开**。它同样也会识别两个通配符：符号“#”和符号“**”**。

> 注意#匹配0个或者多个单词，*匹配一个单词



## 4.开始使用

我们先看spring boot的[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-amqp)是怎么说的吧。

首先，添加这些配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /test
```

rabbitmq默认用户名密码为guest:guest

先上配置类，RabbitTemplate使用自动配置好的，自动注入进来就可以了，我们还需要配置一个RabbitAdmin对象，RabbitAdmin有两个构造方法

```java
/**
* Construct an instance using the provided {@link ConnectionFactory}.
* @param connectionFactory the connection factory - must not be null.
*/
public RabbitAdmin(ConnectionFactory connectionFactory) {
    Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
    this.connectionFactory = connectionFactory;
    this.rabbitTemplate = new RabbitTemplate(connectionFactory);
}

/**
* Construct an instance using the provided {@link RabbitTemplate}. Use this
* constructor when, for example, you want the admin operations to be performed within
* the scope of the provided template's {@code invoke()} method.
* @param rabbitTemplate the template - must not be null and must have a connection
* factory.
* @since 2.0
*/
public RabbitAdmin(RabbitTemplate rabbitTemplate) {
    Assert.notNull(rabbitTemplate, "RabbitTemplate must not be null");
    Assert.notNull(rabbitTemplate.getConnectionFactory(), "RabbitTemplate's ConnectionFactory must not be null");
    this.connectionFactory = rabbitTemplate.getConnectionFactory();
    this.rabbitTemplate = rabbitTemplate;
}
```

但实际看他们的构造函数，发现如果我们不需要自己定制RabbitTemplate，直接使用第一个构造方法即可。

```java
@Configuration
public class RabbitMqConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(rabbitTemplate);
    }
}
```

类似这样，就配置好了。

接下来写一个测试类，测试声明交换机，队列，以及发送消息和接收消息等操作。

首先是声明交换机类型，四种交换机对应的构造方法如下

```java
//参数列表分别是：1.交换器名称,2.是否持久化,3.是否自动删除【指的是当最后一个与它绑定的队列删除时，是否自动删除该交换机】
TopicExchange topicExchange = new TopicExchange("default.topic", true, false);
DirectExchange directExchange = new DirectExchange("default.direct", true, false);
FanoutExchange fanoutExchange = new FanoutExchange("default.fanout", true, false);
HeadersExchange headersExchange = new HeadersExchange("default.headers", true, false);
rabbitAdmin.declareExchange(topicExchange);
rabbitAdmin.declareExchange(directExchange);
rabbitAdmin.declareExchange(fanoutExchange);
rabbitAdmin.declareExchange(headersExchange);
```

然后是声明队列

```java
//1.队列名称,2.声明一个持久队列,3.声明一个独立队列,4.是否自动删除队列
Queue queue1 = new Queue("queue1", true, false, false);
Queue queue2 = new Queue("queue2", true, false, false);
Queue queue3 = new Queue("queue3", true, false, false);
Queue queue4 = new Queue("queue4", true, false, false);
rabbitAdmin.declareQueue(queue1);
rabbitAdmin.declareQueue(queue2);
rabbitAdmin.declareQueue(queue3);
rabbitAdmin.declareQueue(queue4);
```

然后把队列和交换机相互绑定

```java
//1.queue:绑定的队列,2.topicExchange:绑定到那个交换器,3.test.send.topic:绑定的路由名称[routing key]
rabbitAdmin.declareBinding(BindingBuilder.bind(queue1).to(fanoutExchange));
rabbitAdmin.declareBinding(BindingBuilder.bind(queue2).to(fanoutExchange));
rabbitAdmin.declareBinding(BindingBuilder.bind(queue3).to(topicExchange).with("mq.*"));
rabbitAdmin.declareBinding(BindingBuilder.bind(queue4).to(directExchange).with("mq.direct"));
```

因为fanout类型的交换机忽略routing key属性，所以不需要设置。

完整测试代码如下

```java
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RabbitMqTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testDeclare() {
        //参数列表分别是：1.交换器名称,2.是否持久化,3.是否自动删除【指的是当最后一个与它绑定的队列删除时，是否自动删除该交换机】
        TopicExchange topicExchange = new TopicExchange("default.topic", true, false);
        DirectExchange directExchange = new DirectExchange("default.direct", true, false);
        FanoutExchange fanoutExchange = new FanoutExchange("default.fanout", true, false);
        HeadersExchange headersExchange = new HeadersExchange("default.headers", true, false);
        rabbitAdmin.declareExchange(topicExchange);
        rabbitAdmin.declareExchange(directExchange);
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitAdmin.declareExchange(headersExchange);

        //1.队列名称,2.声明一个持久队列,3.声明一个独立队列,4.是否自动删除队列
        Queue queue1 = new Queue("queue1", true, false, false);
        Queue queue2 = new Queue("queue2", true, false, false);
        Queue queue3 = new Queue("queue3", true, false, false);
        Queue queue4 = new Queue("queue4", true, false, false);
        rabbitAdmin.declareQueue(queue1);
        rabbitAdmin.declareQueue(queue2);
        rabbitAdmin.declareQueue(queue3);
        rabbitAdmin.declareQueue(queue4);

        //1.queue:绑定的队列,2.topicExchange:绑定到那个交换器,3.test.send.topic:绑定的路由名称[routing key]
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue1).to(fanoutExchange));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue2).to(fanoutExchange));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue3).to(topicExchange).with("mq.*"));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue4).to(directExchange).with("mq.direct"));
    }
}
```

运行结果如下：

![Snipaste_2019-09-28_17-33-54.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fd73jl7ej30l7053aa9.jpg)

![Snipaste_2019-09-28_17-35-35.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fd7rhhiij30qa05lq37.jpg)

再看一下绑定情况：

Direct交换机

![Snipaste_2019-09-28_17-36-26.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fd8n7yn7j30hh0dwdg9.jpg)

Fanout交换机

![Snipaste_2019-09-28_17-37-06.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fd9xmtdnj30me0f4jrv.jpg)

Topic交换机

![Snipaste_2019-09-28_17-38-59.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fdbaar55j30if0ddgm0.jpg)

全都测试成功，接下来就可以开始发送消息了。

发送消息有多个API可用，这里选择高亮的那个API，实际还有send方法可用，不过需要自己来构建消息

![Snipaste_2019-09-28_17-45-28.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fdi8k9x2j317g0d0q4t.jpg)

![Snipaste_2019-09-28_17-54-51.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fdrt4lvoj30wv04yjru.jpg)

```java
@Test
public void testSendMessage() {
    //1.交换机，2.路由键，3.发送的消息体【这里的消息体会自动转换为消息，也可以自己构建消息对象】
    rabbitTemplate.convertAndSend("default.topic","mq.whatever.this.is",new Student(1,"mmp","male",234));
}
```

测试结果如下：

![Snipaste_2019-09-28_18-01-01.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fdy8an04j30so08gwet.jpg)

一定要注意topic类型的交换机的路由键的匹配规则，#匹配0个或者多个单词，*匹配一个单词

那如果不想每次都是在测试类里面创建交换机和队列，可以怎么做呢？可以在程序入口类里面实现CommandLineRunner接口，代码如下，这样的话，每次启动都会声明一次，当然重复声明不会报错，但会覆盖之前的声明，比如说之前声明的时候定义的routing key可能就会被覆盖。

```java
@SpringBootApplication
@EnableRabbit
public class AmqpApplication implements CommandLineRunner {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    public static void main(String[] args) {
        SpringApplication.run(AmqpApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        //参数列表分别是：1.交换器名称,2.是否持久化,3.是否自动删除【指的是当最后一个与它绑定的队列删除时，是否自动删除该交换机】
        TopicExchange topicExchange = new TopicExchange("default.topic", true, false);
        DirectExchange directExchange = new DirectExchange("default.direct", true, false);
        FanoutExchange fanoutExchange = new FanoutExchange("default.fanout", true, false);
        HeadersExchange headersExchange = new HeadersExchange("default.headers", true, false);
        rabbitAdmin.declareExchange(topicExchange);
        rabbitAdmin.declareExchange(directExchange);
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitAdmin.declareExchange(headersExchange);

        //1.队列名称,2.声明一个持久队列,3.声明一个独立队列,4.是否自动删除队列
        Queue queue1 = new Queue("queue1", true, false, false);
        Queue queue2 = new Queue("queue2", true, false, false);
        Queue queue3 = new Queue("queue3", true, false, false);
        Queue queue4 = new Queue("queue4", true, false, false);
        rabbitAdmin.declareQueue(queue1);
        rabbitAdmin.declareQueue(queue2);
        rabbitAdmin.declareQueue(queue3);
        rabbitAdmin.declareQueue(queue4);

        //1.queue:绑定的队列,2.topicExchange:绑定到那个交换器,3.test.send.topic:绑定的路由名称[routing key]
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue1).to(fanoutExchange));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue2).to(fanoutExchange));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue3).to(topicExchange).with("mq.*"));
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue4).to(directExchange).with("mq.direct"));
    }
}
```

但其实这样做还是比较复杂的，而且完全没有必要，更加简单方便的做法是，把那些配置声明的对象直接添加到IOC容器中，让spring自动的去调用相应的声明方法，真是纵享丝滑呀，类似下面这样子：

```java
@Bean
public Queue Queue() {
    return new Queue("hello");
}
```



继续测试接收消息，有一个注解很方便。

![Snipaste_2019-09-28_18-10-09.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fe7q6vcaj30k20e3jsk.jpg)

```java
@Service
public class ReceiverService {
    @RabbitListener(queues = {"queue3"})
    public void receive(Student student) {
        System.out.println("接收到消息并打印："+student);
    }
}
```

测试结果如下：

```
接收到消息并打印：student{id=1, name='mmp', gender='male', age=234}
```



也可以直接使用方法接收消息

```java
@Test
public void testReceive() {
    Student student = (Student) rabbitTemplate.receiveAndConvert("queue3");
    System.out.println(student);
}
```

测试结果如下：

```json
student{id=1, name='mmp', gender='male', age=234}
```



如果想让发送的学生对象使用JSON格式怎么办呢？

需要定制一下：

```java
@Configuration
public class RabbitMqConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        return new RabbitAdmin(rabbitTemplate);
    }
}
```

测试一下：

![Snipaste_2019-09-28_18-21-46.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7fejtlm25j30gk08naab.jpg)

源码地址：https://github.com/lingEric/springboot-integration-hello



更多官方tutorials请移步https://github.com/rabbitmq/rabbitmq-tutorials

