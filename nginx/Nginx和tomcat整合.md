> Nginx是一款轻量级的Web服务器/反向代理服务器及电子邮件（IMAP/POP3）代理服务器。在Java的Web架构中，通常使用Tomcat和Nginx进行配合，Nginx作为反向代理服务器，可以对后台的Tomcat服务器负载均衡，也可以让Nginx处理静态页面的请求、Tomcat处理JSP页面请求达到动静分离的目的。

# Nginx简介

Nginx ("engine x") 是一个高性能的HTTP和反向代理服务器，也是一个IMAP/POP3/SMTP服务器，是由Igor Sysoev为俄罗斯访问量第二的Rambler.ru站点开发的。其特点是占有内存少，并发能力强，事实上nginx的并发能力确实在同类型的网页服务器中表现较好，中国大陆使用nginx网站用户有：百度、京东、新浪、网易、腾讯、淘宝等。

截止到2014年12月31日,Nginx仅次于apache成为第二大web服务器软件,而在全球最忙碌top10000网站中使用比例更是高达42.7%。其发展速度和流行程度已经远远超过其它同类软件,成为大型网站和高并发网站的首选。

Nginx由内核和一系列模块组成，内核提供web服务的基本功能,如启用网络协议,创建运行环境,接收和分配客户端请求,处理模块之间的交互。Nginx的各种功能和操作都由模块来实现。Nginx的模块从结构上分为核心模块、基础模块和第三方模块。

核心模块： HTTP模块、EVENT模块和MAIL模块
基础模块： HTTP Access模块、HTTP FastCGI模块、HTTP Proxy模块和HTTP Rewrite模块
第三方模块： HTTP Upstream Request Hash模块、Notice模块和HTTP Access Key模块及用户自己开发的模块

这样的设计使Nginx方便开发和扩展，也正因此才使得Nginx功能如此强大。Nginx的模块默认编译进nginx中，如果需要增加或删除模块，需要重新编译Nginx,这一点不如Apache的动态加载模块方便。如果有需要动态加载模块，可以使用由淘宝网发起的web服务器Tengine，在nginx的基础上增加了很多高级特性，完全兼容Nginx，已被国内很多网站采用。

# 开发环境搭建

下面进入正题，对Nginx和Tomcat进行整合使用。首选是开发环境的搭建，包括JDK、Tomcat和Nginx。本文的实验环境是Ubuntu。

## JDK安装

下载JDK，并解压到指定目录中。

```
lap@lap-KVM:~$ wget http://120.52.72.24/download.oracle.com/c3pr90ntc0td/otn-pub/java/jdk/7u79-b15/jdk-7u79-linux-x64.tar.gz
lap@lap-KVM:~$ tar zxvf jdk-7u79-linux-x64.tar.gz 
lap@lap-KVM:~$ sudo mv jdk1.7.0_79/ /usr/local/jdk
```

配置环境变量，在/etc/profile中添加JAVA_HOME等路径。

```
export JAVA_HOME=/usr/local/jdk
export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/jre/lib
```

加载环境变量。

```
lap@lap-KVM:~$ source /etc/profile
```

查看是否配置成功。

```
java -version
```

显示以下结果则配置成功：

![clipboard.png](https://segmentfault.com/img/bVGSFx?w=445&h=66)

## Tomcat安装

第二步是Tomcat的安装。

下载Tomcat，并解压到指定目录中。

```
lap@lap-KVM:~$ wget http://apache.fayea.com/tomcat/tomcat-8/v8.5.9/bin/apache-tomcat-8.5.9.tar.gz
lap@lap-KVM:~$ tar zxvf apache-tomcat-8.5.9.tar.gz 
lap@lap-KVM:~$ sudo mv apache-tomcat-8.5.9/ /usr/local/tomcat
```

关于Tomcat的配置以及设置普通用户等在这里就不提了。直接启动Tomcat。

```
lap@lap-KVM:~$ sudo /usr/local/tomcat/bin/startup.sh
```

若出现以下提示，表示找不到JAVA_HOME的路径

![clipboard.png](https://segmentfault.com/img/bVGS2z?w=510&h=32)

此时需要在Tomcat的bin目录下的catalina.sh中加入以下信息：

![clipboard.png](https://segmentfault.com/img/bVGS27?w=577&h=142)

再次启动Tomcat，成功

![clipboard.png](https://segmentfault.com/img/bVGS3J?w=681&h=113)

此时就可以打开[http://localhost:8080](http://localhost:8080/)，看到Tomcat的主页。

![clipboard.png](https://segmentfault.com/img/bVGT9l?w=1217&h=463)

## Nginx安装

接下来就是主角Nginx。

下载Nginx，解压，安装到指定目录。

```
lap@lap-KVM:~$ wget http://labfile.oss.aliyuncs.com/nginx-1.7.9.tar.gz
lap@lap-KVM:~$ tar zxvf nginx-1.7.9.tar.gz
lap@lap-KVM:~$ cd nginx-1.7.9
lap@lap-KVM:~/nginx-1.7.9$ ./configure --prefix=/usr/local/nginx --with-http_ssl_module --with-http_gzip_static_module --with-http_stub_status_module
lap@lap-KVM:~/nginx-1.7.9$ # make && make install
```

切换到Nginx的安装目录下，启动Nginx。

```
lap@lap-KVM:~$ cd /usr/local/nginx/
lap@lap-KVM:/usr/local/nginx$ sudo ./nginx-1.7.9/objs/nginx
```

此时访问[http://localhost](http://localhost/)，将可以看到Nginx的欢迎界面。(因为我不是直接在Nginx本机上访问的，所以是用的Nginx服务器的IP:192.168.6.86来访问的，本文接下去的部分都是这样。)

![clipboard.png](https://segmentfault.com/img/bVGT9g?w=939&h=300)

# Nginx配置

Nginx可以通过调整配置文件的参数，对性能进行优化。这个配置文件就是nginx.conf。

## nginx.vim

题外话，由于使用Nginx经常需要编辑nginx.conf，我们可以借助nginx.vim来使nginx.conf语法高亮，看起来更清晰明了。

- 到[http://www.vim.org/scripts/sc...](http://www.vim.org/scripts/script.php?script_id=1886)下载nginx.vim。

- 移动到/usr/share/vim/vim74/syntax/目录

- 在/usr/share/vim/vim74/filetypr.vim中添加：

  ```
  au BufRead,BufNewFile /usr/local/nginx/conf/* set ft=nginx
  ```

再打开nginx.conf，已经有语法高亮了：

![clipboard.png](https://segmentfault.com/img/bVGTeI?w=572&h=628)

## nginx.conf

nginx.conf里面提供了很多可以调节优化的地方，这里简要了解http模块的几个常用参数。这一部分内容参考[实验楼的教程](https://www.shiyanlou.com/courses/95/labs/432/document)。

如图，是我的nginx.conf的配置

![clipboard.png](https://segmentfault.com/img/bVGTLS?w=873&h=549)

简单介绍一下红色方框内的一些配置参数。

第一个方框内的是关于日志的设置：

- log_format 定义日志格式
- access_log 设置是否保存访问日志，设置为off可以降低磁盘IO而提升速度。

第二个方框内的是一些基本设置：

- sendfile 指向sendfile()函数。sendfile()在磁盘和TCP端口（或者任意两个文件描述符）之间复制数据。sendfile()直接从磁盘上读取数据到操作系统缓冲，因此会更有效率。
- tcp_nopush 配置nginx在一个包中发送全部的头文件，而不是一个一个发送。
- tcp_nodelay 配置nginx不要缓存数据，快速发送小数据。
- keepalive_timeout 指定了与客户端的keep-alive链接的超时时间。服务器会在这个时间后关闭链接。

第三个方框内的是关于压缩功能的设置：

- gzip 打开压缩功能可以减少需要发送的数据的数量。
- gzip_disable 为指定的客户端禁用 gzip 功能。
- gzip_proxied 允许或禁止基于请求、响应的压缩。设置为any，就可以gzip所有的请求。
- gzip_comp_level 设置了数据压缩的等级。等级可以是 1-9 的任意一个值，9 表示最慢但是最高比例的压缩。
- gzip_types 设置进行 gzip 的类型。

接下来看一下http模块中的子模块server，以及server中的子模块location的配置：

![clipboard.png](https://segmentfault.com/img/bVGTQx?w=331&h=286)

其中：

- listen 表示当前的代理服务器监听的端口，默认的是监听80端口。
- server_name 表示监听到之后需要转到哪里去，localhost表示转到本地，也就是直接到nginx文件夹内。
- location 表示匹配的路径。
- root 表示到指定文件路径寻找文件，可用于静态文件。
- index 表示默认主页，可以指定多个，按顺序查找。
- deny和allow 是访问控制设置，禁止或允许某个IP或者某个IP段访问。也可以指定unix，允许socket的访问。
- limit_rate_after 设置不限速传输的响应大小。当传输量大于此值时，超出部分将限速传送。
- limit_rate 限制向客户端传送响应的速率限制。参数的单位是字节/秒，设置为0将关闭限速。比如图片中表示不限速部分为3m，超过了3m后限速为20k/s。

关于Nginx的更多配置解释，可以参考[这篇博文](https://lufficc.com/blog/configure-nginx-as-a-web-server)，讲的非常详细。

注意，修改完配置文件后需要重启nginx生效。当然，如果你的Nginx是用apt-get安装的，可以用nginx -s reload命令重新加载配置文件即可。

# Nginx+Tomcat

接下来就进入本文的主题，将Nginx和Tomcat整合在一起使用。这部分工作其实也就是修改nginx.conf里的配置。

## JSP页面请求交给Tomcat处理

具体的，在nginx.conf中新建一个location，用正则表达式将所有JSP的请求匹配到该location中：

![clipboard.png](https://segmentfault.com/img/bVGT5Y?w=473&h=261)

其中最核心的就是"proxy_pass [http://localhost:8080](http://localhost:8080/);"这条配置，它将匹配到的请求都转发给Tomcat去处理。

其他的配置：

- proxy_set_header Host $host; 后端的Web服务器可以通过X-Forwarded-For获取用户真实IP。
- client_max_body_size 10m; 允许客户端请求的最大单文件字节数。
- client_body_buffer_size 128k; 缓冲区代理缓冲用户端请求的最大字节数。
- proxy_connect_timeout 90; Nginx跟后端服务器连接超时时间。
- proxy_read_timeout 90; 连接成功后，后端服务器响应时间。
- proxy_buffer_size 4k; 设置代理服务器保存用户头信息的缓冲区大小。
- proxy_buffers 6 32k; proxy_buffers缓冲区。
- proxy_busy_buffers_size 64k; 高负荷下缓冲大小。
- proxy_temp_file_write_size 64k; 设定缓存文件夹大小。

此时访问<http://localhost/index.jsp>，会发现跳转到了Tomcat的页面。但是你会发现，此时的页面是这样的：

![clipboard.png](https://segmentfault.com/img/bVGT9x?w=1031&h=410)

这是因为虽然JSP的请求转发给Tomcat的了，但是图片、css等静态文件却找不到。所以接下来我们要配置静态文件的路径，完成动静分离。

## 动静分离

对于静态文件的请求，我们也新建一个location，将常见图片、css、js等请求匹配到该location中

![clipboard.png](https://segmentfault.com/img/bVGUbs?w=354&h=71)

如图所示，配置非常简单，通过root关键字，将匹配到的请求都到tomcat/webapps/ROOT目录下直接查找。而expires 30d则表示使用expires缓存模块，缓存到客户端30天。

配置完后重启Nginx。再输入<http://localhost/index.jsp>，会发现此时的Tomcat页面已经正常显示了。我们已经完成了JSP请求与静态文件请求的动静分离。但是实际上也只是显示这个页面而已，当你点击页面上的其他链接时，会显示404，这是显然的，因为我们只配置了ROOT目录。

![clipboard.png](https://segmentfault.com/img/bVGUe4?w=1320&h=458)

如果配置完仍然发现无法读取静态文件，看看访问<http://localhost/tomcat.png>时是否显示403 forbidden。如果是的话就是因为权限问题导致的，这里简单的解决办法是把nginx.conf首行的user设为root:

![clipboard.png](https://segmentfault.com/img/bVGUgh?w=352&h=191)

当然，如果不想使用root用户运行，可以通过修改目录访问权限解决403问题，但不能把目录放在root用户宿主目录下，放在任意一个位置并给它755，或者通过chown改变它的拥有者与Nginx运行身份一致也可以解决权限问题。

## 负载均衡

接下来我们来实验一下负载均衡。

在nginx.conf中，通过配置upstream，可以很轻松配置后台服务器的负载均衡：

![clipboard.png](https://segmentfault.com/img/bVG5dY?w=305&h=208)

具体的，可以在upstream中配置后台Tomcat服务器的地址，这里我是配置了两台服务器，分别是本机也就是192.168.6.86和192.168.6.32，其中本机安装的是Tomcat8，另一台安装的是Tomcat7，以示区别。然后在location中，将请求转发给配置好的upstream处理。这里为了不跟前面的配置产生冲突，我新配置了一个server，监听8888端口。

然后，访问[http://localhost:8888](http://localhost:8888/)。刷新几次，会发现请求转发到了不同的服务器上：

![clipboard.png](https://segmentfault.com/img/bVG5fg?w=825&h=455)

![clipboard.png](https://segmentfault.com/img/bVG5fi?w=834&h=454)

可以看到，显示的页面分别是Tomcat7和Tomcat8的主页，证明请求转发成功。

另外，你也可以在server后面加上一个权重weight，权重越大表示访问到的机会越大，默认为1。

![clipboard.png](https://segmentfault.com/img/bVG5fT?w=312&h=206)