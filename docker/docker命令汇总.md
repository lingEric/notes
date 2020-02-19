最近在学习docker技术，将大部分命令（23条）都执行了一遍，做个汇总，做个备忘录。

## Docker优势



[![img](https://testerhome.com/uploads/photo/2017/06b60903-0a74-41fa-8e2a-b51694bb7db7.png!large)](https://testerhome.com/uploads/photo/2017/06b60903-0a74-41fa-8e2a-b51694bb7db7.png!large)

**1、 快速搭建兼容性测试环境**从Docker的镜像与容器技术特点可以预见，当被测应用要求在各类Web服务器、中间件、数据库的组合环境中得到充分验证时，可以快速地利用基础Docker镜像创建各类容器，装载相应的技术组件并快速启动运行，测试人员省去了大量花在测试环境搭建上的时间。

**2、 快速搭建复杂分布式测试环境**Docker的轻量虚拟化特点决定了它可以在一台机器上（甚至是测试人员的一台笔记本电脑上）轻松搭建出成百上千个分布式节点的容器环境，从而模拟以前需要耗费大量时间和机器资源才能搭建出来的分布式复杂测试环境。

**3、 持续集成**Docker可以快速创建和撤销容器，在持续集成的环境中，可以频繁和快速地进行部署和验证工作。

## docker常用命令

**总的来说分为以下几种：**
• 容器生命周期管理 — docker [run|start|stop|restart|kill|rm|pause|unpause]
• 容器操作运维 — docker [ps|inspect|top|attach|events|logs|wait|export|port]
• 容器rootfs命令 — docker [commit|cp|diff]
• 镜像仓库 — docker [login|pull|push|search]
• 本地镜像管理 — docker [images|rmi|tag|build|history|save|import]
• 其他命令 — docker [info|version]

## 1、 列出机器上的镜像（images）

```
docker images [OPTIONS] [REPOSITORY[:TAG]]
```

**OPTIONS说明：**
-a :列出本地所有的镜像（含中间映像层，默认情况下，过滤掉中间映像层）；
--digests :显示镜像的摘要信息；
-f :显示满足条件的镜像；
--format :指定返回值的模板文件；
--no-trunc :显示完整的镜像信息；
-q :只显示镜像ID。

```
列出本地镜像中REPOSITORY为ubuntu的镜像列表
root@runoob:~# docker images  ubuntu
```

其中我们可以根据repository来判断这个镜像是来自哪个服务器，如果没有 / 则表示官方镜像，类似于username/repos_name表示Github的个人公共库，类似于regsistory.example.com:5000/repos_name则表示的是私服。

## 2、 在docker index中搜索image（search）

```
Usage: docker search TERM
# docker search seanlo搜索的范围是官方镜像和所有个人公共镜像。
```

## 3、 从docker registry server 中下拉image或repository（pull）

```
Usage: docker pull [OPTIONS] NAME[:TAG]
# docker pull centos    
#只会下载tag为latest的镜像
# docker pull centos:centos6
当然也可以从某个人的公共仓库（包括自己是私人仓库）拉取，形如docker pull username/repository<:tag_name>
# docker pull seanlook/centos:centos6如果你没有网络，或者从其他私服获取镜像，
形如docker pull registry.domain.com:5000/repos:<tag_name>
# docker pull dl.dockerpool.com:5000/mongo:latest
```

## 4、 推送一个image或repository到registry（push）

与上面的pull对应，可以推送到Docker Hub的Public、Private以及私服，但不能推送到Top Level Repository。

```
# docker push seanlook/mongo
# docker push registry.tp-link.net:5000/mongo:2014-10-27
```

registry.tp-link.NET也可以写成IP，172.29.88.222。
在repository不存在的情况下，命令行下push上去的会为我们创建为私有库，然而通过浏览器创建的默认为公共库。

## 5、 从image启动一个container（run）

Usage: docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
**OPTIONS说明：**
-a stdin: 指定标准输入输出内容类型，可选 STDIN/STDOUT/STDERR 三项；
-d: 后台运行容器，并返回容器ID；
-i: 以交互模式运行容器，通常与 -t 同时使用；
-t: 为容器重新分配一个伪输入终端，通常与 -i 同时使用；
-u:指定容器的用户

-P:指定容器暴露的端口

-v: 给容器挂载存储卷，挂载到容器的某个目录

--name="nginx-lb": 为容器指定一个名称；
--dns 8.8.8.8: 指定容器使用的DNS服务器，默认和宿主一致；
--dns-search example.com: 指定容器DNS搜索域名，默认和宿主一致；
-h "mars": 指定容器的hostname；
-e username="ritchie": 设置环境变量；
--env-file=[]: 从指定文件读入环境变量；
--cpuset="0-2" or --cpuset="0,1,2": 绑定容器到指定CPU运行；
-m :设置容器使用内存最大值；
--net="bridge": 指定容器的网络连接类型，支持 bridge/host/none/Container: 四种类型；
--link=[]: 添加链接到另一个容器；
--expose=[]: 开放一个端口或一组端口；
案例1：使用docker镜像nginx:latest以后台模式启动一个容器,并将容器命名为mynginx。

```
# docker run --name mynginx -d nginx:latest  
```

案例2：使用镜像nginx:latest以后台模式启动一个容器,并将容器的80端口映射到主机随机端口。

```
# docker run -P -d nginx:latest  
```

案例3：使用镜像nginx:latest以交互模式启动一个容器,在容器内执行/bin/bash命令。

```
# runoob@runoob:~$ docker run -it nginx:latest /bin/bash  
```

案例4、运行一个简单的容器，其中需要包含控制台管理，这个容器一执行就会进入到默认的线程”/bin/bash”，直接进入控制台操作。当退出控制后后，容器会被终止。

```
# docker run -i -t centos6.8
```

案例5、我们希望一个容器在它的进程结束后，立马自动删除。

```
# docker run -it --rm  centos6.8  
```

这时候我们进入了容器的控制台，当我们在容器内部exit退出控制台的时候，容器将被终止，同时自动删除。

案例6、运行一个在后台不断执行的容器，同时带有命令，程序被终止后还能重启继续跑，还能用控制台管理.

```
# docker run -d --restart=always centos6.8  ping www.docker.com  
```

这个容器将永久在后台执行，因为ping这个线程不会停止。如果你把ping这个线程终止了，那么容器会重启继续执行ping功能

案例7、我们需要让server-http容器连接server-db容器

```
# docker run -d --name=server-http --link=server-db  centos6.8-httpd /usr/bin/httpd --DFOREGROUND  
```

这时候，我们执行了apache的服务器让它不断的在后台执行，同时，在php里配置mysql的服务器名称为”server-db”，直接用server-db命名就可以了。不需要输入ip地址之类的。我们的server-http指定连接了server-db。server-db在server-http里会被当做一个DNS解析来获取相应的连接ip。

案例8、我们要将宿主机的数据库目录/server/mysql-data挂载到server-db上

```
# docker run -d --name=server-db -p 3306:3306 -v /server/mysql-data:/mysql-data centos6.8-mysql /usr/bin/mysql_safe –d  
```

这时候，你会发现，在server-db根目录下你会发现有一个新的文件夹mysql-data，同时里面的文件内容和宿主机下/server/mysql-data一样。

## 6、 将一个container固化为一个新的image（commit）

```
docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
```

**OPTIONS说明：**
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。

```
将容器a404c6c174a2 保存为新的镜像,并添加提交人信息和说明信息。runoob@runoob:~$ docker commit -a "runoob.com" -m "my apache" a404c6c174a2  mymysql:v1 
```

请注意，当你反复去commit一个容器的时候，每次都会得到一个新的IMAGE ID，假如后面的repository:tag没有变，通过docker images可以看到，之前提交的那份镜像的repository:tag就会变成:，所以尽量避免反复提交。
虽然产生了一个新的image，并且你可以看到大小有100MB，但从commit过程很快就可以知道实际上它并没有独立占用100MB的硬盘空间，而只是在旧镜像的基础上修改，它们共享大部分公共的“片”。

## 7、 开启/停止/重启container（start/stop/restart）

容器可以通过run新建一个来运行，也可以重新start已经停止的container，但start不能够再指定容器启动时运行的指令，因为docker只能有一个前台进程。
容器stop（或Ctrl+D）时，会在保存当前容器的状态之后退出，下次start时保有上次关闭时更改。而且每次进入attach进去的界面是一样的，与第一次run启动或commit提交的时刻相同。

```
docker stop $CONTAINER_IDdocker restart $CONTAINER_ID
```

## 8、 连接到正在运行中的container（attach）

attach是可以带上--sig-proxy=false来确保CTRL-D或CTRL-C不会关闭容器。

```
# docker attach --sig-proxy=false $CONTAINER_ID
```

## 9、 查看image或container的底层信息（inspect）

inspect的对象可以是image、运行中的container和停止的container。
查看容器的内部IP

```
# docker inspect --format='{{.NetworkSettings.IPAddress}}' $CONTAINER_ID172.17.42.35
```

## 10、 删除一个或多个container、image（rm、rmi）

你可能在使用过程中会build或commit许多镜像，无用的镜像需要删除。但删除这些镜像是有一些条件的：
• 同一个IMAGE ID可能会有多个TAG（可能还在不同的仓库），首先你要根据这些 image names 来删除标签，当删除最后一个tag的时候就会自动删除镜像；
• 承上，如果要删除的多个IMAGE NAME在同一个REPOSITORY，可以通过docker rmi 来同时删除剩下的TAG；若在不同Repo则还是需要手动逐个删除TAG；
• 还存在由这个镜像启动的container时（即便已经停止），也无法删除镜像；
删除容器

```
docker rm <container_id/contaner_name>
```

• -f选项用来强制删除
• --no-prune选项会保留被删除镜像中未打标签的父镜像。

```
删除所有停止的容器docker rm $(docker ps -a -q)删除镜像docker rmi <image_id/image_name ...>删除seanlook仓库中的tag     <==# docker rmi seanlook/ubuntu:rm_testUntagged: seanlook/ubuntu:rm_test现在删除镜像，还会由于container的存在不能rmi# docker rmi 195eb90b5349先删除由这个镜像启动的容器    <==# docker rm eef3648a6e77删除镜像                    <==# docker rmi 195eb90b5349删除centos仓库下所有的镜像schen@scvmu01:~$ docker rmi -f $(docker images -q centos | sort -u)Untagged: centos:centos7
```

注意：在删除镜像之前要先用 docker rm 删掉依赖于这个镜像的所有容器（哪怕是已经停止的容器），否则无法删除该镜像。

## 11、 docker build 使用此配置生成新的image

build命令可以从Dockerfile和上下文来创建镜像：

```
docker build [OPTIONS] PATH | URL | -
```

上面的PATH或URL中的文件被称作上下文，build image的过程会先把这些文件传送到docker的服务端来进行的。
如果PATH直接就是一个单独的Dockerfile文件则可以不需要上下文；如果URL是一个Git仓库地址，那么创建image的过程中会自动git clone一份到本机的临时目录，它就成为了本次build的上下文。无论指定的PATH是什么，Dockerfile是至关重要的。

```
使用当前目录的Dockerfile创建镜像。docker build -t runoob/ubuntu:v1 . 使用URL github.com/creack/docker-firefox 的 Dockerfile 创建镜像。docker build github.com/creack/docker-firefox
```

## 12、 给镜像打上标签（tag）

tag的作用主要有两点：一是为镜像起一个容易理解的名字，二是可以通过docker tag来重新指定镜像的仓库，这样在push时自动提交到仓库。

```
# docker tag Registry/Repos:Tag New_Registry/New_Repos:New_Tag
新建一个tag，保留旧的那条记录；将镜像ubuntu:15标记为 runoob/ubuntu:v3镜像。# docker tag ubuntu:15 runoob/ubuntu:v3将同一IMAGE_ID的所有tag，合并为一个新的# docker tag 195eb90b5349 seanlook/ubuntu:rm_test
```

## 13、 查看容器的信息container（ps）

docker ps命令可以查看容器的CONTAINER ID、NAME、IMAGE NAME、端口开启及绑定、容器启动后执行的COMMNAD。经常通过ps来找到CONTAINER_ID。
docker ps 默认显示当前正在运行中的container
docker ps -a 查看包括已经停止的所有容器
docker ps -l 显示最新启动的一个容器（包括已停止的）

## 14、 查看容器中正在运行的进程（top）

容器运行时不一定有/bin/bash终端来交互执行top命令，查看container中正在运行的进程，况且还不一定有top命令，这是docker top 就很有用了。实际上在host上使用ps -ef|grep docker也可以看到一组类似的进程信息，把container里的进程看成是host上启动docker的子进程就对了。

## 15、 登录docker注册服务器（login）

```
root@moon:~# docker loginUsername: usernamePassword: ****Email: user@domain.comLogin Succeeded
```

按步骤输入在 Docker Hub 注册的用户名、密码和邮箱即可完成登录。

## 16、 容器与主机之间的数据拷贝（cp）

```
docker cp [OPTIONS] CONTAINER:SRC_PATH DEST_PATH|-
将主机/www/runoob目录拷贝到容器96f7f14e99ab的/www目录下。# docker cp /www/runoob 96f7f14e99ab:/www/将主机/www/runoob目录拷贝到容器96f7f14e99ab中，目录重命名为www。# docker cp /www/runoob 96f7f14e99ab:/www将容器96f7f14e99ab的/www目录拷贝到主机的/tmp目录中。docker cp  96f7f14e99ab:/www /tmp/
```

## 17、 创建一新的容器但不启动它（create）

```
使用docker镜像nginx:latest创建一个容器,并将容器命名为myrunoobrunoob@runoob:~$ docker create  --name myrunoob  nginx:latest      09b93464c2f75b7b69f83d56a9cfc23ceb50a48a9db7652ee4c27e3e2cb1961f
```

## 18、 在运行的容器中执行命令（exec）

```
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

**OPTIONS说明：**
-d :分离模式: 在后台运行
-i :即使没有附加也保持STDIN 打开
-t :分配一个伪终端

```
在容器mynginx中以交互模式执行容器内/root/runoob.sh脚本runoob@runoob:~$ docker exec -it mynginx /bin/sh /root/runoob.shhttp://www.runoob.com/在容器mynginx中开启一个交互模式的终端runoob@runoob:~$ docker exec -i -t  mynginx /bin/bashroot@b1a0703e41e7:/#
```

## 19、 获取容器日志（logs）

```
docker logs [OPTIONS] CONTAINER
```

**OPTIONS说明：**
-f : 跟踪日志输出
--since :显示某个开始时间的所有日志
-t : 显示时间戳
--tail :仅列出最新N条容器日志

```
查看容器mynginx从2016年7月1日后的最新10条日志。docker logs --since="2016-07-01" --tail=10 mynginx
```

## 20、 获取指定容器的端口映射（port）

```
docker port [OPTIONS] CONTAINER [PRIVATE_PORT[/PROTO]]
查看容器mynginx的端口映射情况。runoob@runoob:~$ docker port mymysql3306/tcp -> 0.0.0.0:3306
```

## 21、 检查容器内文件结构的更改（diff）

```
docker diff [OPTIONS] CONTAINER
```

查看容器mymysql的文件结构更改。

```
runoob@runoob:~$ docker diff mymysql
```

## 22、 将指定镜像保存成归档tar文件（save）

```
docker save [OPTIONS] IMAGE [IMAGE...]
将镜像runoob/ubuntu:v3 生成my_ubuntu_v3.tar文档runoob@runoob:~$ docker save -o my_ubuntu_v3.tar runoob/ubuntu:v3runoob@runoob:~$ ll my_ubuntu_v3.tar
```

## 23、 从归档文件中创建镜像（import）

```
docker import [OPTIONS] file|URL|- [REPOSITORY[:TAG]]
```

**OPTIONS说明：**
-c :应用docker 指令创建镜像；
-m :提交时的说明文字；

```
从镜像归档文件my_ubuntu_v3.tar创建镜像，命名为runoob/ubuntu:v4runoob@runoob:~$ docker import  my_ubuntu_v3.tar runoob/ubuntu:v4  sha256:63ce4a6d6bc3fabb95dbd6c561404a309b7bdfc4e21c1d59fe9fe4299cbfea39
```