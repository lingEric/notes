# docker安装MySQL



-d	后台运行

-e 	设置MySQL参数

-p	端口映射

-v	目录映射【用于docker容器和本机目录共享】



```shell
docker run -d -e MYSQL_ROOT_PASSWORD -p 3306:3306 -v /c/Users/ling/docker/mysql:/etc/mysql/conf.d/ mysql:latest

```



配置文件

C:\Users\ling\docker\mysql

```properties
[mysqld]
basedir = C:/software/mysql-5.7.27-winx64   			
datadir = C:/software/mysql-5.7.27-winx64/data   		
port = 3306 		#默认端口，无需更改
max_connections=200
character-set-server=utf8
default-storage-engine=INNODB
sql_mode=NO_ENGINE_SUBSITUTION,STRICT_TRANS_TABLES
explicit_defaults_for_timestamp=true
# skip-grant-tables
[mysql]
default-character-set=utf8 
```



