# spring boot集成Elastic search初体验

## 1.简单介绍elastic search

官方文档地址：https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html

Elasticsearch是一个分布式的文档存储引擎，属于NoSQL Repository。它将数据存储已序列化为JSON文档的复杂数据结构。当集群中有多个Elasticsearch节点时，存储的文档会分布在整个集群中，并且可以从任何节点立即访问。 存储文档时，将在1秒内几乎实时地对其进行索引和完全搜索。 Elasticsearch使用称为倒排索引的数据结构，该结构支持非常快速的全文本搜索。反向索引列出了出现在任何文档中的每个唯一单词，并标识了每个单词出现的所有文档。 

索引可以认为是文档的优化集合，每个文档都是字段的集合，这些字段是包含数据的键值对。默认情况下，Elasticsearch对每个字段中的所有数据建立索引，并且每个索引字段都具有专用的优化数据结构。例如，文本字段存储在倒排索引中，数字字段和地理字段存储在BKD树中。使用按字段数据结构组合并返回搜索结果的能力使Elasticsearch如此之快。 Elasticsearch还具有无模式的能力，这意味着可以为文档建立索引，而无需明确指定如何处理文档中可能出现的每个不同字段。启用动态映射后，Elasticsearch自动检测并将新字段添加到索引。此默认行为使索引和浏览数据变得容易-只需开始建立索引文档，Elasticsearch就会检测布尔值，浮点数和整数值，日期和字符串并将其映射到适当的Elasticsearch数据类型。 

但是，最终，您比Elasticsearch更了解您的数据以及如何使用它们。您可以定义规则以控制动态映射，并显式定义映射以完全控制字段的存储和索引方式。 定义自己的映射使您能够： 区分全文字符串字段和精确值字符串字段 执行特定于语言的文本分析 优化字段以进行部分匹配 使用自定义日期格式 使用无法自动检测到的数据类型，例如geo_point和geo_shape 为不同的目的以不同的方式对同一字段建立索引通常很有用。例如，您可能希望将一个字符串字段索引为全文搜索的文本字段，以及作为排序或汇总数据的关键字字段。或者，您可能选择使用多个语言分析器来处理包含用户输入的字符串字段的内容。 在搜索时也会使用在索引期间应用于全文字段的分析链。当您查询全文字段时，对查询文本进行相同的分析，然后再在索引中查找术语。

虽然您可以将Elasticsearch用作文档存储并检索文档及其元数据，但真正的强大之处在于能够轻松访问基于Apache Lucene搜索引擎库构建的全套搜索功能。 Elasticsearch提供了一个简单，一致的REST API，用于管理您的集群以及索引和搜索数据。为了进行测试，您可以直接从命令行或通过Kibana中的开发者控制台轻松提交请求。在您的应用程序中，您可以将Elasticsearch客户端用于您选择的语言：Java，JavaScript，Go，.NET，PHP，Perl，Python或Ruby。

Elasticsearch旨在始终可用，并可以根据您的需求进行扩展。它是通过自然分布来实现的。您可以将服务器（节点）添加到集群以增加容量，Elasticsearch会自动在所有可用节点之间分配数据和查询负载。无需大修您的应用程序，Elasticsearch知道如何平衡多节点集群以提供扩展性和高可用性。节点越多越好。 这是如何运作的？在幕后，Elasticsearch索引实际上只是一个或多个物理碎片的逻辑分组，其中每个碎片实际上是一个独立的索引。通过在多个分片之间的索引中分配文档，并在多个节点之间分配这些分片，Elasticsearch可以确保冗余，这既可以防止硬件故障，又可以在将节点添加到集群中时提高查询能力。随着集群的增长（或收缩），Elasticsearch会自动迁移碎片以重新平衡集群。 分片有两种类型：主数据库和副本数据库。索引中的每个文档都属于一个主分片。副本分片是主分片的副本。副本可提供数据的冗余副本，以防止硬件故障并增加处理读取请求（如搜索或检索文档）的能力。 创建索引时，索引中主碎片的数量是固定的，但是副本碎片的数量可以随时更改，而不会中断索引或查询操作。

更多详细介绍请查看[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)

**注意**为了描述方便，后文elasticsearch将简称es

### 1.0基本概念

索引index：

一个 ElasticSearch 集群可以包含多个索引，类比MySQL的话，就相当于一个数据库

类型type：

一个索引下面可以有多个类型，类比MySQL的话，就相当于一张数据表

文档document：

不同的类型存储着多个文档，也就是一条一条的记录，一个文档就是一条记录。

属性property：

一个文档有多个属性，类似于MySQL中的字段，也就是一条记录的数据列column。

### 1.1elastic search接口API

elastic search的接口API可以参考[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html) ，或者使用API工具查看，具体请参考[这篇文档](https://www.cnblogs.com/ericling/p/11616571.html)，Zeal和Velocity都是支持Elastic search的。

另外，我在我的项目源码里面也整理了postman的调用接口文档（其实是从GitHub上面找来的），你可以直接导入到你的postman里面就可以了，具体请看[链接](https://github.com/lingEric/springboot-integration-hello)

![Snipaste_2019-10-02_00-37-01.png](http://ww1.sinaimg.cn/large/006edVQGgy1g7j69ai38jj31hc0rtdl6.jpg)

这里面整理了有索引，类型，文档，索引元数据，监控信息，分析，搜索等常用API以及对应的测试用例，如果不会用postman的话，请一定要花时间学会使用它，因为这个工具在实际开发中是使用非常广泛的。

## 2.docker部署elastic search

接下来开始在本地虚拟机docker环境下部署elastic search.

更多详细部署说明，请参考[官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/6.3/docker.html)

```
 //拉取镜像
 docker pull elasticsearch:6.7.0
 //查看镜像
 docker images
 //运行镜像
 docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node"  docker.io/elasticsearch:6.7.0
 //查看容器状态 
 docker ps

```

访问elastic search页面，http://localhost:9200/，部署成功的话，你应该可以看到这个结果

```json
{
    "name": "_YclAxJ",
    "cluster_name": "docker-cluster",
    "cluster_uuid": "V_ywsgpfS0uOkf9bPaH8Kg",
    "version": {
        "number": "6.7.0",
        "build_flavor": "default",
        "build_type": "docker",
        "build_hash": "8453f77",
        "build_date": "2019-03-21T15:32:29.844721Z",
        "build_snapshot": false,
        "lucene_version": "7.7.0",
        "minimum_wire_compatibility_version": "5.6.0",
        "minimum_index_compatibility_version": "5.0.0"
    },
    "tagline": "You Know, for Search"
}
```

从他的返回信息可以看出，es的版本号为6.7.0

## 3.项目搭建

项目依旧使用spring boot的2.1.8.RELEASE版本进行搭建，我们先看一下[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-elasticsearch)是怎么描述的。

Spring Boot supports several HTTP clients:

- The official Java "Low Level" and "High Level" REST clients
- [Jest](https://github.com/searchbox-io/Jest)

The transport client is still being used by [Spring Data Elasticsearch](https://github.com/spring-projects/spring-data-elasticsearch), which you can start using with the `spring-boot-starter-data-elasticsearch` “Starter”.

也就是说spring boot其实是支持两种方式和elasticsearch进行交互的。一个是spring boot官方集成的rest客户端，还有一个是第三方常用客户端Jest

下面两种方式分别讲解。

### 3.1Jest使用

按照惯例，先导入依赖，我们可以参考一下[官方文档](https://github.com/searchbox-io/Jest/tree/master/jest)

```xml
<!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
<dependency>
    <groupId>io.searchbox</groupId>
    <artifactId>jest</artifactId>
    <version>6.3.1</version>
</dependency>
```

导入上述依赖之后，JestAutoConfiguration便会自动给我们配置JestClient，我们需要做的便是指定es的相关连接参数即可

```yaml
spring:
  elasticsearch:
    jest:
      uris: http://192.168.33.128:9200
```

接下来我们就可以开始测试es的接口了。

```java
@Test
public void testJestClient() {
    Student student = new Student(1, "mmp", "male", 12);
    Index index = new Index.Builder(student).index("hello").type("student").build();
    try {
        jestClient.execute(index);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

测试结果如下：

```json
{
    "took": 171,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 1.0,
        "hits": [
            {
                "_index": "hello",
                "_type": "student",
                "_id": "dGGbim0BB4xWr2BC_E13",
                "_score": 1.0,
                "_source": {
                    "id": 1,
                    "name": "mmp",
                    "gender": "male",
                    "age": 12
                }
            }
        ]
    }
}
```

可以看到，es中自动的添加了hello索引，和student类别，并插入了该文档。

接下来测试一下搜索，我们可以先用postman模拟一下搜索的内容，

![postman模拟搜索学生记录](http://ww1.sinaimg.cn/large/006edVQGgy1g7jq2fj5jmj31hc0rtwhr.jpg)

上述搜索请求，是发送的POST请求，对hello索引下的student类型进行搜索，同时搜索的字段是name，该字段需要满足关键字mmp，同时返回的结果需要进行高亮。

我们看一下该请求的响应结果是什么。

```json
{
    "took": 161,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 0.2876821,
        "hits": [
            {
                "_index": "hello",
                "_type": "student",
                "_id": "dGGbim0BB4xWr2BC_E13",
                "_score": 0.2876821,
                "_source": {
                    "id": 1,
                    "name": "mmp",
                    "gender": "male",
                    "age": 12
                },
                "highlight": {
                    "name": [
                        "<em>mmp</em>"
                    ]
                }
            }
        ]
    }
}
```

可以看到，es成功地把之前测试的数据查询出来了，并且也对响应的name字段的相应数据进行了高亮处理。接下来我们需要使用代码来完成这件事。

```java
@Test
public void testJestClient2() {
    String json = "{  \n" +
        "   \"query\":{  \n" +
        "      \"multi_match\":{  \n" +
        "         \"query\":\"mmp\",\n" +
        "         \"fields\":[  \n" +
        "            \"name\"\n" +
        "         ]\n" +
        "      }\n" +
        "   },\n" +
        "   \"highlight\":{  \n" +
        "      \"fields\":{  \n" +
        "         \"name\":{  \n" +
        "            \"type\":\"plain\"\n" +
        "         }\n" +
        "      }\n" +
        "   }\n" +
        "}";
    Search search = new Search.Builder(json).addIndex("hello").addType("student").build();
    try {
        SearchResult result = jestClient.execute(search);
        System.out.println(result);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

测试结果如下：

```json
Result: {"took":7,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":1,"relation":"eq"},"max_score":0.2876821,"hits":[{"_index":"hello","_type":"student","_id":"dGGbim0BB4xWr2BC_E13","_score":0.2876821,"_source":{"id":1,"name":"mmp","gender":"male","age":12},"highlight":{"name":["<em>mmp</em>"]}}]}}, isSucceeded: true, response code: 200, error message: null
```

和前面的postman测试结果一致

### 3.2spring boot starter data elasticsearch使用

首先导入该starter的相关依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

然后配置es的连接信息

```yaml
spring:
  data:
    elasticsearch:
      cluster-nodes: 192.168.33.128:9300
```

到这里继续看[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-connecting-to-elasticsearch-spring-data)，发现spring data支持两种方式来操作es，一种是使用repository的方式，另外一种是使用ElasticsearchTemplate 。

先测试一下repository的方式，新建一个对应的repository，在这之前先加一个pojo类，因为student字段过于简单不方便测试。

```java
@Document(indexName = "hello", type = "blog")
public class Blog implements Serializable {
    private Integer id;
    private String title;
    private String author;
    private String content;
    private String digest;
    private LocalDateTime localDateTime;
    //省略构造器，getter，setter，toString
}
```

对应的repository





