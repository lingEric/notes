## 安装mysql 
1. 解压。如：D:\mysql-5.7.22-winx64
2. 在D:\mysql-5.7.22-winx64文件夹中新建my.ini
   内容为：

```ini
[mysqld]
basedir=D:/mysql-5.7.22-winx64    		# 这是你的mysql的路径
datadir=D:/mysql-5.7.22-winx64/data   	# 不要手动创建该目录
port = 3306 		#默认端口，无需更改
max_connections=200
character-set-server=utf8
default-storage-engine=INNODB
# sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
explicit_defaults_for_timestamp=true
# skip-grant-tables
[mysql]
default-character-set=utf8
```

3. 配置环境变量MYSQL_HOME=D:\mysql-5.7.22-winx64
     path增加 ;%MYSQL_HOME%\bin
4. 增加mysqld服务 超级管理员身份运行cmd  命令为：mysqld install
5. cmd命令执行：mysqld  --initialize
6. 启用服务：cmd命令执行：net start mysql
7. 首次设置密码

```
mysql -u root -p
密码为空，直接回车，进入mysql
mysql> use mysql
database changed;
mysql> set password for 'root'@'localhost' = password('root');
mysql>...OK(修改成功)
```



## 重置root密码

在忘记 root 密码的情况下，可以进入 mysql 的安全模式，重置 root 密码。

### 1. 停止 MySQL 服务

打开命令提示符窗口，输入   net stop mysql   关闭 MySQL 服务。

```
C:\Users\Administrator>net stop mysql57
MySQL57 服务正在停止..
MySQL57 服务已成功停止。
```

↑ 服务名称不一定都是 mysql，比如我的就是 mysql57，57代表版本号为5.7

当然你也可以通过计算机管理面板关闭 MySQL 服务。

![img](https://images2015.cnblogs.com/blog/875028/201704/875028-20170417224648821-1264355741.png)

### 2. 切换到 bin 目录

在命令提示符窗口中，通过 cd 命令切换到 mysql 安装目录下的 bin 目录。

```
C:\Users\Administrator>

cd C:\Program Files\MySQL\MySQL Server 5.7\bin

C:\Program Files\MySQL\MySQL Server 5.7\bin>
```

↑ 默认安装目录为 C:\Program Files\MySQL\MySQL Server

### 3. 进入安全模式

在 bin 目录下输入   mysqld --skip-grant-tables   ，跳过权限检查启动 mysql。

**如果你配置了 my.ini 文件，则需要将其引入：**   mysqld --defaults-file="../my.ini" --skip-grant-tables 

```
[mysqld]

basedir = "C:\ProgramData\MySQL\MySQL Server 5.7"
datadir = "C:\ProgramData\MySQL\MySQL Server 5.7\Data"
```

↑ 我在 my.ini 文件中指定了数据的存放路径，如果不引入配置文件，则会提示 No such file or directory 错误。

### 4. 重置账户密码

打开另一个命令提示符窗口（别关闭安全模式窗口），同样切换到 mysql \ bin 目录，输入   mysql   跳过权限验证连接数据库。

```
C:\Program Files\MySQL\MySQL Server 5.7\bin>mysql
Server version: 5.7.16 MySQL Community Server (GPL)
Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
mysql>
```

↑ 也可以指定连接参数 mysql -u <用户名> -p <密码> -h <连接地址> -P <端口号> -D <数据库>

执行   update mysql.user set authentication_string="" where user="root";   重置 root 用户的密码（5.7 之前为 password 字段）。

```
mysql> update mysql.user set authentication_string="" where user="root";
Query OK, 1 row affected (0.00 sec)

mysql> select user,authentication_string from mysql.user;

2 rows in set (0.00 sec)
```

↑ root 用户的 authentication_string 字段已经被清空了

### 5. 刷新权限表

执行   flush privileges;   命令刷新权限表，密码已经重置完成，输入   quit   退出。

```
mysql> flush privileges;
Query OK, 0 rows affected (0.02 sec)

mysql> quit
Bye
```

关闭所有命令提示符窗口，通过任务管理器结束 mysqld.exe 进程。通过服务界面，重启 MySQL 服务，之后就可以直接登录 root 账号了。

 

## 修改 root 密码

出于安全考虑，root 密码不宜为空，所以需要在密码重置之后，再重新设置一个密码。

### 方法一：SET PASSWORD

- SET PASSWORD FOR "username"=PASSWORD("new password");

以 root 身份登录 mysql，再使用 set password 命令修改密码：

```
mysql> set password for root@localhost = password("pswd");
Query OK, 0 rows affected, 1 warning (0.00 sec)
```

### 方法二：mysqladmin

- mysqladmin -u "username" -p password "new password"

执行该命名之后会提示输入原密码，输入正确后即可修改。

```
C:\Program Files\MySQL\MySQL Server 5.7\bin> mysqladmin -u root -p password pswd
Enter password: ****

mysqladmin: [Warning] Using a password on the command line interface can be insecure.
Warning: Since password will be sent to server in plain text, use ssl connection to ensure password safety.
```

### 方法三：UPDATE TABLE

- UPDATE mysql.user SET authentication_string=PASSWORD("new password") WHERE user="username";

在重置 root 密码的同时，也可以设置默认密码。不过密码不能为明文，必须使用 password() 函数加密。

```
mysql> update mysql.user set authentication_string=password("pswd") where user="root";
Query OK, 1 row affected, 1 warning (0.00 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.01 sec)
```

### 方法四： 

```
alter user root@localhost identified by 'tyzZ001!';
```