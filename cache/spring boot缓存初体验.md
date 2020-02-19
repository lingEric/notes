# spring boot缓存初体验

## 1.项目搭建

使用MySQL作为数据库，spring boot集成mybatis来操作数据库，所以在使用springboot的cache组件时，需要先搭建一个简单的ssm环境。

首先是项目依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.3.2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.48</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

```

数据库测试用的数据

```sql
CREATE TABLE `student`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, 'eric', 'male', 22);
INSERT INTO `student` VALUES (2, 'alice', 'female', 23);
INSERT INTO `student` VALUES (3, 'bob', 'male', 21);
```

对应的实体类代码如下：

```java
public class Student {
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
    //省略构造函数，getter，setter，toString
}
```

对应的mapper：

```java
public interface StudentMapper {
    @Select("select * from student where id = #{id}")
    Student getStudentById(Integer id);
}
```

对应的service:

```java
@Service
public class StudentService {
    @Autowired
    private StudentMapper studentMapper;

    public Student getStudentById(Integer id) {
        return studentMapper.getStudentById(id);
    }
}
```

对应的测试类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoCacheApplicationTests {
    @Autowired
    private StudentService studentService;

    /**
     * 测试mybatis是否正确配置
     */
    @Test
    public void contextLoads() {
        System.out.println(studentService.getStudentById(1));
    }

}
```

运行上面的测试方法，成功打印

```json
student{id=1, name='eric', gender='male', age=22}
```

项目的架子基本搭建成功了，接下来就是使用springboot提供的缓存注解来测试一下。

在这之前，先了解一些背景知识。

首先是[JSR107](https://github.com/jsr107/jsr107spec)缓存规范，Java Caching定义了5个核心接口，分别是CachingProvider, CacheManager, Cache, Entry
和 Expiry。

- CachingProvider
定义了创建、配置、获取、管理和控制多个CacheManager。一个应用可以在运行期访问多个CachingProvider。
- CacheManager
定义了创建、配置、获取、管理和控制多个唯一命名的Cache，这些Cache存在于CacheManager的上下文中。一个CacheManager仅被一个CachingProvider所拥有。
- Cache
是一个类似Map的数据结构并临时存储以Key为索引的值。一个Cache仅被一个CacheManager所拥有。
- Entry
是一个存储在Cache中的key-value对。
- Expiry 
每一个存储在Cache中的条目有一个定义的有效期。一旦超过这个时间，条目为过期的状态。一旦过期，条目将不可访问、更新和删除。缓存有效期可以通过ExpiryPolicy设置。

![Snipaste_2019-09-23_21-58-01.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7c6bg2lemj30py0erwfm.jpg)

Spring从3.1开始定义org.springframework.cache.Cache和org.springframework.cache.CacheManager接口来统一不同的缓存技术，并支持使用JCache 【JSR-107】注解简化我们开发。

我们先看一下Cache接口的基本结构：

<img src="http://ww1.sinaimg.cn/large/006edVQGgy1g7c6p5wapxj30ml0lx3ze.jpg" alt="Snipaste_2019-09-25_23-33-25.png" style="zoom:80%;" />

Cache接口为缓存的组件规范各种缓存的基本操作，spring提供了各种常用的xxxCache实现，比如：RedisCache,EhCacheCache,ConcurrentMapCache等等。

在当前添加的依赖下，可以找到这些Cache实现

![Snipaste_2019-09-25_23-32-20.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7c6o4n138j31hc0b4jte.jpg)

每次调用需要缓存功能的方法时，Spring就会去检查指定参数的目标方法是否已经被调用过，如果有就直接从缓存中获取，没有则调用方法并将结果缓存之后再返回给用户，之后的数据都是直接从缓存中获取。

所以使用缓存需要考虑以下几个方面：

- 确定方法是否需要缓存
- 确定方法缓存的策略（比如key的设定，缓存的数据是使用json格式还是Java序列化）
- 缓存和数据库数据一致性如何保证
- 每次从缓存中读取之前缓存过的数据

首先并不是所有方法都需要缓存，一般来讲都是频繁访问并且不经常修改的数据才需要缓存。

key的生成策略可以直接使用key属性来指定，也可以指定keyGenerator

缓存的数据默认情况下都是使用Java序列号的方式，我们可以将它存储为json格式，看项目需要。

缓存的一致性，这个比较复杂，本文不涉及到高并发情况下缓存和数据库一致的讨论，只是保证在数据修改或删除时，及时地更新缓存中的数据。换句话说，就是数据在缓存之后，如果之后调用了修改的方法，把数据修改了，需要CachePut注解及时地把缓存里的数据也一并修改，或者，调用了删除的方法，需要使用CacheEvict注解来删除相应缓存的数据。

至于每次都从缓存中读取已经缓存过的数据，这个事情就交给Spring来自动处理吧。

| **Cache**          | 缓存接口，封装缓存的基本操作                                 |
| ------------------ | ------------------------------------------------------------ |
| **CacheManager**   | 缓存管理器，管理各种缓存组件，一个应用程序可以有多个缓存管理器 |
| **@Cacheable**     | 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存     |
| **@CacheEvict**    | 清空缓存                                                     |
| **@CachePut**      | 保证方法被调用，又希望结果被缓存，一般用于修改数据。         |
| **@EnableCaching** | 开启基于注解的缓存                                           |
| **keyGenerator**   | 缓存数据时key生成策略                                        |
| **serialize**      | 缓存数据时value序列化策略                                    |

`@CachePut`和`@Cacheable`两个注解的区别是什么呢？

@CachePut：这个注释可以确保方法被执行，同时方法的返回值也被记录到缓存中。

@Cacheable：当重复使用相同参数调用方法的时候，方法本身不会被调用执行，即方法本身被略过了，取而代之的是方法的结果直接从缓存中找到并返回了。

> ​	对于@CachePut这个注解，它的作用是什么呢，每次方法都执行，那么缓存的意义是什么呢？答案很简单，同一个缓存实例的相同的key的缓存的数据，可以用@CachePut更新，而@Cacheable在取值的时候，是@CachePut更新后的值。但同时也要**注意**确保是同一个缓存实例对象，并且key要保证一致！！！



@Cacheable,@CachePut,@CacheEvict注解的常用属性如下：

| 属性                                        | 作用                                                         | 示例                                                         |
| ------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| value                                       | 缓存的名称，在   spring 配置文件中定义，必须指定至少一个     | 例如：      @Cacheable(value=”mycache”) 或者       @Cacheable(value={”cache1”,”cache2”} |
| key                                         | 缓存的   key，可以为空，如果指定要按照   SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合 | 例如：      @Cacheable(value=”testcache”,key=”#userName”)    |
| condition                                   | 缓存的条件，可以为空，使用   SpEL 编写，返回   true 或者 false，只有为   true 才进行缓存/清除缓存，在调用方法之前之后都能判断 | 例如：      @Cacheable(value=”testcache”,condition=”#userName.length()>2”) |
| allEntries   (**@CacheEvict**   )           | 是否清空所有缓存内容，缺省为   false，如果指定为 true，则方法调用后将立即清空所有缓存 | 例如：      @CachEvict(value=”testcache”,allEntries=true)    |
| beforeInvocation   **(@CacheEvict)**        | 是否在方法执行前就清空，缺省为   false，如果指定为 true，则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法执行抛出异常，则不会清空缓存 | 例如：   @CachEvict(value=”testcache”，beforeInvocation=true) |
| unless   **(@CachePut)**   **(@Cacheable)** | 用于否决缓存的，不像condition，该表达式只在方法执行之后判断，此时可以拿到返回值result进行判断。条件为true不会缓存，fasle才缓存 | 例如：      @Cacheable(value=”testcache”,unless=”#result   == null”) |

Cache SpEL available metadata

| **名字**        | **位置**           | **描述**                                                     | **示例**             |
| --------------- | ------------------ | ------------------------------------------------------------ | -------------------- |
| methodName      | root object        | 当前被调用的方法名                                           | #root.methodName     |
| method          | root object        | 当前被调用的方法                                             | #root.method.name    |
| target          | root object        | 当前被调用的目标对象                                         | #root.target         |
| targetClass     | root object        | 当前被调用的目标对象类                                       | #root.targetClass    |
| args            | root object        | 当前被调用的方法的参数列表                                   | #root.args[0]        |
| caches          | root object        | 当前方法调用使用的缓存列表（如@Cacheable(value={"cache1",   "cache2"})），则有两个cache | #root.caches[0].name |
| *argument name* | evaluation context | 方法参数的名字. 可以直接 #参数名 ，也可以使用 #p0或#a0 的形式，0代表参数的索引； | #iban 、 #a0 、  #p0 |
| result          | evaluation context | 方法执行后的返回值（仅当方法执行之后的判断有效，如‘unless’，’cache put’的表达式 ’cache evict’的表达式beforeInvocation=false） | #result              |

这个掌握就好，没有必要去死记硬背，默认情况下的配置都是够用的。

## 2.缓存使用过程解析

首先需要引入spring-boot-starter-cache依赖

然后使用@EnableCaching开启缓存功能

然后就可以使用缓存注解来支持了。

先看一下官方API里面是怎么说的吧：

```java
@Target(value=TYPE)
@Retention(value=RUNTIME)
@Documented
@Import(value=CachingConfigurationSelector.class)
public @interface EnableCaching
```

Enables Spring's annotation-driven cache management capability，To be used together with @[`Configuration`](../../../../org/springframework/context/annotation/Configuration.html) classes as follows:

```java
@Configuration
@EnableCaching
public class AppConfig {

    @Bean
    public MyService myService() {
        // configure and return a class having @Cacheable methods
        return new MyService();
    }
    @Bean
    public CacheManager cacheManager() {
        // configure and return an implementation of Spring's CacheManager SPI
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
        return cacheManager;
    }
}
```

 @EnableCaching is responsible for registering the necessary Spring components that power annotation-driven cache management, such as the CacheInterceptor and the proxy- or AspectJ-based advice that weaves the interceptor into the call stack when @Cacheable methods are invoked.

官方文档的描述简洁明了，我们只需要开启缓存，然后定制CacheManager即可。

If the JSR-107 API and Spring's JCache implementation are present, the necessary components to manage standard cache annotations are **also** registered. This creates the proxy- or AspectJ-based advice that weaves the interceptor into the call stack when methods annotated with `CacheResult`, `CachePut`, `CacheRemove` or `CacheRemoveAll` are invoked.

强大的spring同样支持了JSR107缓存注解！！！当然，本文还是主要以讲解spring的缓存注解为主。

For those that wish to establish a **more direct** relationship between `@EnableCaching` and the **exact** cache manager bean to be used, the [`CachingConfigurer`](../../../../org/springframework/cache/annotation/CachingConfigurer.html) callback interface may be implemented. Notice the `@Override`-annotated methods below:

如果想要明确地定制你的CacheManager，可以像下面这样使用

```java
 @Configuration
 @EnableCaching
 public class AppConfig extends CachingConfigurerSupport {

     @Bean
     public MyService myService() {
         // configure and return a class having @Cacheable methods
         return new MyService();
     }

     @Bean
     @Override
     public CacheManager cacheManager() {
         // configure and return an implementation of Spring's CacheManager SPI
         SimpleCacheManager cacheManager = new SimpleCacheManager();
         cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
         return cacheManager;
     }

     @Bean
     @Override
     public KeyGenerator keyGenerator() {
         // configure and return an implementation of Spring's KeyGenerator SPI
         return new MyKeyGenerator();
     }
 }
```

This approach may be desirable simply because it is **more explicit**, or it may be **necessary** in order to distinguish between two `CacheManager`

因为一个应用环境下可以有多个CacheManager，这样声明CacheManager可以更加直观。

Notice also the `keyGenerator` method in the example above. This allows for customizing the strategy for cache key generation, per Spring's [`KeyGenerator`](../../../../org/springframework/cache/interceptor/KeyGenerator.html) SPI. Normally, `@EnableCaching` will configure Spring's [`SimpleKeyGenerator`](../../../../org/springframework/cache/interceptor/SimpleKeyGenerator.html) for this purpose, but **when implementing** `CachingConfigurer`, a key generator **must be provided explicitly**. Return `null` or `new SimpleKeyGenerator()` from this method **if no customization is necessary**.

如果实现了`CachingConfigurer`接口，就需要明确定义keyGenerator

[`CachingConfigurer`](../../../../org/springframework/cache/annotation/CachingConfigurer.html) offers additional customization options: it is recommended to extend from [`CachingConfigurerSupport`](../../../../org/springframework/cache/annotation/CachingConfigurerSupport.html) that provides a default implementation for all methods which can be useful if you do not need to customize everything. See [`CachingConfigurer`](../../../../org/springframework/cache/annotation/CachingConfigurer.html) Javadoc for further details.

可以通过继承CachingConfigurerSupport来实现其它的定制功能。CachingConfigurerSupport类的结构如下，可以只对你需要定制的功能进行重写，其它的一律默认返回null即可，如果返回null，那么spring boot 的自动配置就会生效。

```java

/**
 * An implementation of {@link CachingConfigurer} with empty methods allowing
 * sub-classes to override only the methods they're interested in.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see CachingConfigurer
 */
public class CachingConfigurerSupport implements CachingConfigurer {

	@Override
	public CacheManager cacheManager() {
		return null;
	}

	@Override
	public KeyGenerator keyGenerator() {
		return null;
	}

	@Override
	public CacheResolver cacheResolver() {
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return null;
	}

}

```

The [`mode()`](../../../../org/springframework/cache/annotation/EnableCaching.html#mode--) attribute controls how advice is applied: If the mode is [`AdviceMode.PROXY`](../../../../org/springframework/context/annotation/AdviceMode.html#PROXY) (the default), then the other attributes control the behavior of the proxying. Please note that proxy mode allows for interception of calls through the **proxy only**; local calls within the same class **cannot** get intercepted that way.

Note that if the [mode()](../../../../org/springframework/cache/annotation/EnableCaching.html#mode--) is set to [`AdviceMode.ASPECTJ`](../../../../org/springframework/context/annotation/AdviceMode.html#ASPECTJ), then the value of the [`proxyTargetClass()`](../../../../org/springframework/cache/annotation/EnableCaching.html#proxyTargetClass--) attribute will be ignored. Note also that in this case the `spring-aspects` module JAR must be present on the classpath, with compile-time weaving or load-time weaving applying the aspect to the affected classes. There is no proxy involved in such a scenario; local calls will be intercepted as well.

真是纵享丝滑。

## 3.实际上手

`@CacheConfig`注解可以定义当前类的所有使用到缓存注解（`@Cacheable`,`@CachePut`,`@CacheEvict`）的通用配置，下面的示例代码实际只配置了当前类的缓存名称

```java
@Service
@CacheConfig(cacheNames = "student")
public class StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Cacheable
    public Student getStudentById(Integer id) {
        System.out.println("从数据库中查询学生：" + id);
        return studentMapper.getStudentById(id);
    }

    @CachePut
    public Student updateStudent(Student student) {
        System.out.println("更新数据库中的学生数据：" + student);
        studentMapper.updateStudent(student);
        return student;
    }

    @CacheEvict
    public void deleteStudent(Integer id) {
        System.out.println("删除数据库中的学生："+id);
        studentMapper.delStudent(id);
    }
}

```

上面只是简单的使用这三个注解，更加详细的属性使用，请看后面的内容。我们先测试一下缓存的使用效果。

测试类的代码如下：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoCacheApplicationTests {
    @Autowired
    private StudentService studentService;

    @Test
    public void contextLoads() {
        System.out.println(studentService.getStudentById(1));
    }

    @Test
    public void testUpdate() {
        studentService.updateStudent(new Student(1,"gotohell","female",23));
    }

    @Test
    public void testDelete() {
        studentService.deleteStudent(1);
    }
}

```

首先测试@Cacheable注解，第一次调用该方法，打印的日志如下：

```shell
从数据库中查询学生：1
student{id=1, name='mmm', gender='male', age=21}
```

第二次调用该方法，打印的日志如下：

```java
student{id=1, name='mmm', gender='male', age=21}
```

说明缓存已经生效了，没有从数据库中获取学生数据。我们看一下缓存里面的内容，

![Snipaste_2019-09-26_22-52-46.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7db5bcun6j30yg08mta2.jpg)

这是默认使用jdk序列化存储的结果，我们可以选择采用json格式存储数据。另外，key的生成策略，默认是cache名称前缀加上方法参数，我觉得这个默认情况下就已经够用了，不需要再进行额外的定制。

再来测试一下修改，

打印日志如下：

```shell
更新数据库中的学生数据：student{id=1, name='gotohell', gender='female', age=23}
```

查看数据库中的数据，已经修改成功，redis的数据由于是序列化的，这里就不截图了，我们直接再调用一次查询看它有没有更新即可。

打印结果如下：

```shell
student{id=1, name='mmm', gender='male', age=21}
```

说明没有更新缓存中的数据，难道是@CachePut注解不起作用吗？

查看一下redis

![Snipaste_2019-09-26_23-01-43.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7dbeidqlhj30i70463yg.jpg)



这才发现，原来第二次修改的数据，默认使用的缓存key是对象，为什么呢，因为默认情况下，key的生成策略就是缓存名称student+方法的参数，而更新方法的参数就是学生对象，所以测试拿不到更新之后的数据，因为两个key不一致。

那么只要把更新方法的key指定为1不就可以了吗

```java
@CachePut(key = "#result.id")
public Student updateStudent(Student student) {
    System.out.println("更新数据库中的学生数据：" + student);
    studentMapper.updateStudent(student);
    return student;
}
```

重新指定的key就是这样子，它支持spring的表达式，具体的使用规则，前面已经列出表格了。重新测试之后，打印日志如下：

```shell
student{id=1, name='gotohell', gender='female', age=23}
```

获取到了更新之后的数据，说明key起作用了。

再来测试一下删除，打印日志如下：

```shell
删除数据库中的学生：1
```

数据库中的数据已经成功删除了，缓存中的数据也已经清空了。

![Snipaste_2019-09-26_23-11-59.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7dbp5x0vbj30yw08odfz.jpg)

这个时候再去调用查询，打印的日志如下：

```shell
从数据库中查询学生：1
null
```

从打印的日志来看，是查询了数据库的，因为缓存里面已经没有了，但是数据库中的数据也是删除了的，所以返回了null

## 4.使用JSON来序列化对象

这个就需要我们来定制CacheManager了，加入一个新的配置类

```java
@Configuration
public class MyRedisConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置CacheManager的值序列化方式为json序列化
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer());
        RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair).entryTtl(Duration.ofHours(1));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }
}

```

重新测试查询方法，发现缓存的值采用了JSON格式序列化方式。

![Snipaste_2019-09-26_23-46-09.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7dcoyi6bwj30ym08ndgf.jpg)



源码地址：https://github.com/lingEric/springboot-integration-hello



