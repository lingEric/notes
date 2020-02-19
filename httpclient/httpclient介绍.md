# 简介

HttpClient是Apache Jakarta Common下的子项目，用来提供高效的、最新的、功能丰富的支持HTTP协议的客户端编程工具包，并且它支持HTTP协议最新的版本和建议。
 HttpClient最新版本是HttpClient 4.5.3 (GA)。
 官方下载：[http://hc.apache.org/downloads.cgi](https://link.jianshu.com?t=http://hc.apache.org/downloads.cgi)

# 主要特性

- 基于标准、纯净的Java语言，实现了HTTP1.0和HTTP1.1。
- 以可扩展的面向对象的结构实现了HTTP全部的方法（GET, POST, PUT, DELETE, HEAD, OPTIONS, and TRACE）。
- 支持加密的HTTPS协议（HTTP通过SSL协议）。
- 通过HTTP代理方式建立透明的连接。
- 利用CONNECT方法通过HTTP代理建立隧道的HTTPS连接。
- Basic, Digest, NTLMv1, NTLMv2, NTLM2 Session, SNPNEGO/Kerberos认证方案。
- 插件式的自定义认证方案。
- 可插拔的安全套接字工厂，使得接入第三方解决方案变得更容易
- 连接管理支持使用多线程的的应用。支持设置最大连接数，同时支持设置每个主机的最大连接数，发现并关闭过期的连接。
- 自动化处理Set-Cookie：来自服务器的头，并在适当的时候将它们发送回cookie。
- 可以自定义Cookie策略的插件化机制。
- Request的输出流可以避免流中内容体直接从socket缓冲到服务器。
- Response的输入流可以有效的从socket服务器直接读取相应内容。
- 在HTTP1.0和HTTP1.1中使用用KeepAlive来保持持久连接。
- 可以直接获取服务器发送的响应码和响应头部。
- 具备设置连接超时的能力。
- 支持HTTP/1.1 响应缓存。
- 源代码基于Apache License 可免费获取。

# 一般使用步骤

使用HttpClient发送请求、接收响应，一般需要以下步骤。
 **HttpGet请求响应的一般步骤：**
 1).  创建`HttpClient`对象,可以使用`HttpClients.createDefault()`；
 2).  如果是无参数的GET请求，则直接使用构造方法`HttpGet(String url)`创建`HttpGet`对象即可；
 如果是带参数GET请求，则可以先使用`URIBuilder(String url)`创建对象，再调用`addParameter(String param, String value)`，或`setParameter(String param, String value)`来设置请求参数，并调用build()方法构建一个URI对象。只有构造方法`HttpGet(URI uri)`来创建HttpGet对象。
 3).  创建`HttpResponse`，调用`HttpClient`对象的`execute(HttpUriRequest request)`发送请求，该方法返回一个`HttpResponse`。调用`HttpResponse`的`getAllHeaders()、getHeaders(String name)`等方法可获取服务器的响应头；调用`HttpResponse`的`getEntity()`方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。通过调用`getStatusLine().getStatusCode()`可以获取响应状态码。
 4).  释放连接。

**HttpPost请求响应的一般步骤：**
 1).  创建`HttpClient`对象,可以使用`HttpClients.createDefault()`；
 2).  如果是无参数的GET请求，则直接使用构造方法`HttpPost(String url)`创建`HttpPost`对象即可；
 如果是带参数POST请求，先构建HttpEntity对象并设置请求参数，然后调用setEntity(HttpEntity entity)创建HttpPost对象。
 3).  创建`HttpResponse`，调用`HttpClient`对象的`execute(HttpUriRequest request)`发送请求，该方法返回一个`HttpResponse`。调用`HttpResponse`的`getAllHeaders()、getHeaders(String name)`等方法可获取服务器的响应头；调用`HttpResponse`的`getEntity()`方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。通过调用`getStatusLine().getStatusCode()`可以获取响应状态码。
 4).  释放连接。

# 实例代码实战

构建一个Maven项目，引入如下依赖

```
<dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.3.5</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.7</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-io</artifactId>
        <version>1.3.2</version>
    </dependency>
```

# 实例1：普通的无参数GET请求

打开一个url，抓取响应结果输出成html文件

```
/**
 *普通的GET请求
 */
public class DoGET {
    public static void main(String[] args) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet("http://www.baidu.com");
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                //请求体内容
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //内容写入文件
                FileUtils.writeStringToFile(new File("E:\\devtest\\baidu.html"), content, "UTF-8");
                System.out.println("内容长度："+content.length());
            }
        } finally {
            if (response != null) {
                response.close();
            }
            //相当于关闭浏览器
            httpclient.close();
        }
    }
}
```

# 实例2：执行带参数的GET请求

模拟使用百度搜索关键字"java",并保存搜索结果为html文件

```
import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
/**
 * 带参数的GET请求
 * 两种方式：
 *       1.直接将参数拼接到url后面 如：?wd=java
 *       2.使用URI的方法设置参数 setParameter("wd", "java")
 */
public class DoGETParam {
    public static void main(String[] args) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 定义请求的参数
        URI uri = new URIBuilder("http://www.baidu.com/s").setParameter("wd", "java").build();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(uri);
        //response 对象
        CloseableHttpResponse response = null;
        try {
            // 执行http get请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //内容写入文件
                FileUtils.writeStringToFile(new File("E:\\devtest\\baidu-param.html"), content, "UTF-8");
                System.out.println("内容长度："+content.length());
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
    }
}
```

# 实例3：执行普通的POST请求

无参数的POST请求，并设置Header来伪装浏览器请求

```
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * 常规post请求
 *    可以设置Header来伪装浏览器请求
 */
public class DoPOST {
    public static void main(String[] args) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http POST请求
        HttpPost httpPost = new HttpPost("http://www.oschina.net/");
        //伪装浏览器请求
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //内容写入文件
                FileUtils.writeStringToFile(new File("E:\\devtest\\oschina.html"), content, "UTF-8");
                System.out.println("内容长度："+content.length());
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
    }
}
```

# 实例4：执行带参数的POST请求

模拟开源中国检索java，并伪装浏览器请求，输出响应结果为html文件



![img](https:////upload-images.jianshu.io/upload_images/6597489-dc52f12559ff0614.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

检索java

```
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 带有参数的Post请求
 * NameValuePair
 */
public class DoPOSTParam {
    public static void main(String[] args) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http POST请求
        HttpPost httpPost = new HttpPost("http://www.oschina.net/search");
        // 设置2个post参数，一个是scope、一个是q
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        parameters.add(new BasicNameValuePair("scope", "project"));
        parameters.add(new BasicNameValuePair("q", "java"));
        // 构造一个form表单式的实体
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
        // 将请求实体设置到httpPost对象中
        httpPost.setEntity(formEntity);
        //伪装浏览器
        httpPost.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                //内容写入文件
                FileUtils.writeStringToFile(new File("E:\\devtest\\oschina-param.html"), content, "UTF-8");
                System.out.println("内容长度："+content.length());
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
    }
}
```

# 总结

本文介绍了HttpClient的特性，是按照官方英文文档翻译而来，然后分别介绍了HttpGet和HttpPost的一般使用步骤，最后给出了4个简单的实例的Java代码。

