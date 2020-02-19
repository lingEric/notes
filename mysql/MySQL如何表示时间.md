MySQL中有关TIMESTAMP和DATETIME的总结

# **一、MySQL中如何表示当前时间？**

其实，表达方式还是蛮多的，汇总如下：

```sql
CURRENT_TIMESTAMP

CURRENT_TIMESTAMP()

NOW()

LOCALTIME

LOCALTIME()

LOCALTIMESTAMP

LOCALTIMESTAMP()
```



 

# **二、关于TIMESTAMP和DATETIME的比较**

一个完整的日期格式如下：YYYY-MM-DD HH:MM:SS[.fraction]，它可分为两部分：date部分和time部分，其中，date部分对应格式中的“YYYY-MM-DD”，time部分对应格式中的“HH:MM:SS[.fraction]”。对于date字段来说，它只支持date部分，如果插入了time部分的内容，它会丢弃掉该部分的内容，并提示一个warning。

如下所示：



```
mysql> create table test(id int,hiredate date);
Query OK, 0 rows affected (0.01 sec)

mysql> insert into test values(1,'20151208000000');
Query OK, 1 row affected (0.00 sec)

mysql> insert into test values(1,'20151208104400');
Query OK, 1 row affected, 1 warning (0.01 sec)
mysql> select * from test;
+------+------------+
| id   | hiredate   |
+------+------------+
|    1 | 2015-12-08 |
|    1 | 2015-12-08 |
+------+------------+
2 rows in set (0.00 sec)
```



注：第一个没提示warning的原因在于它的time部分都是0

 

**TIMESTAMP和DATETIME的相同点：**

1> 两者都可用来表示YYYY-MM-DD HH:MM:SS[.fraction]类型的日期。

 

**TIMESTAMP和DATETIME的不同点：**

1> 两者的存储方式不一样

对于TIMESTAMP，它把客户端插入的时间从当前时区转化为UTC（世界标准时间）进行存储。查询时，将其又转化为客户端当前时区进行返回。

而对于DATETIME，不做任何改变，基本上是原样输入和输出。

 

下面，我们来验证一下

首先创建两种测试表，一个使用timestamp格式，一个使用datetime格式。



```
mysql> create table test(id int,hiredate timestamp);
Query OK, 0 rows affected (0.01 sec)

mysql> insert into test values(1,'20151208000000');
Query OK, 1 row affected (0.00 sec)

mysql> create table test1(id int,hiredate datetime);
Query OK, 0 rows affected (0.01 sec)

mysql> insert into test1 values(1,'20151208000000');
Query OK, 1 row affected (0.00 sec)

mysql> select * from test;
+------+---------------------+
| id   | hiredate            |
+------+---------------------+
|    1 | 2015-12-08 00:00:00 |
+------+---------------------+
1 row in set (0.01 sec)

mysql> select * from test1;
+------+---------------------+
| id   | hiredate            |
+------+---------------------+
|    1 | 2015-12-08 00:00:00 |
+------+---------------------+
1 row in set (0.00 sec)
```



两者输出是一样的。

 

其次修改当前会话的时区



```
mysql> show variables like '%time_zone%'; 
+------------------+--------+
| Variable_name    | Value  |
+------------------+--------+
| system_time_zone | CST    |
| time_zone        | SYSTEM |
+------------------+--------+
2 rows in set (0.00 sec)

mysql> set time_zone='+0:00';
Query OK, 0 rows affected (0.00 sec)

mysql> select * from test;
+------+---------------------+
| id   | hiredate            |
+------+---------------------+
|    1 | 2015-12-07 16:00:00 |
+------+---------------------+
1 row in set (0.00 sec)

mysql> select * from test1;
+------+---------------------+
| id   | hiredate            |
+------+---------------------+
|    1 | 2015-12-08 00:00:00 |
+------+---------------------+
1 row in set (0.01 sec)
```



上述“CST”指的是MySQL所在主机的系统时间，是中国标准时间的缩写，China Standard Time UT+8:00

通过结果可以看出，test中返回的时间提前了8个小时，而test1中时间则不变。这充分验证了两者的区别。

 

2> 两者所能存储的时间范围不一样

timestamp所能存储的时间范围为：'1970-01-01 00:00:01.000000' 到 '2038-01-19 03:14:07.999999'。

datetime所能存储的时间范围为：'1000-01-01 00:00:00.000000' 到 '9999-12-31 23:59:59.999999'。

 

总结：TIMESTAMP和DATETIME除了存储范围和存储方式不一样，没有太大区别。当然，对于跨时区的业务，TIMESTAMP更为合适。

 

**三、关于TIMESTAMP和DATETIME的自动初始化和更新**

首先，我们先看一下下面的操作



```
mysql> create table test(id int,hiredate timestamp);
Query OK, 0 rows affected (0.01 sec)

mysql> insert into test(id) values(1);
Query OK, 1 row affected (0.00 sec)

mysql> select * from test;
+------+---------------------+
| id   | hiredate            |
+------+---------------------+
|    1 | 2015-12-08 14:34:46 |
+------+---------------------+
1 row in set (0.00 sec)

mysql> show create table test\G
*************************** 1. row ***************************
       Table: test
Create Table: CREATE TABLE `test` (
  `id` int(11) DEFAULT NULL,
  `hiredate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1
1 row in set (0.00 sec)
```



看起来是不是有点奇怪，我并没有对hiredate字段进行插入操作，它的值自动修改为当前值，而且在创建表的时候，我也并没有定义“show create table test\G”结果中显示的“ DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP”。

其实，这个特性是自动初始化和自动更新（Automatic Initialization and Updating）。

自动初始化指的是如果对该字段（譬如上例中的hiredate字段）没有显性赋值，则自动设置为当前系统时间。

自动更新指的是如果修改了其它字段，则该字段的值将自动更新为当前系统时间。

它与“explicit_defaults_for_timestamp”参数有关。

默认情况下，该参数的值为OFF，如下所示：



```
mysql> show variables like '%explicit_defaults_for_timestamp%';
+---------------------------------+-------+
| Variable_name                   | Value |
+---------------------------------+-------+
| explicit_defaults_for_timestamp | OFF   |
+---------------------------------+-------+
1 row in set (0.00 sec)
```



下面我们看看官档的说明：

By default, the first TIMESTAMP column has both DEFAULT CURRENT_TIMESTAMP and ON UPDATE CURRENT_TIMESTAMP if neither is specified explicitly。

很多时候，这并不是我们想要的，如何禁用呢？

\1. 将“explicit_defaults_for_timestamp”的值设置为ON。

\2. “explicit_defaults_for_timestamp”的值依旧是OFF，也有两种方法可以禁用

​     1> 用DEFAULT子句该该列指定一个默认值

​     2> 为该列指定NULL属性。

如下所示：



```
mysql> create table test1(id int,hiredate timestamp null);
Query OK, 0 rows affected (0.01 sec)

mysql> show create table test1\G
*************************** 1. row ***************************
       Table: test1
Create Table: CREATE TABLE `test1` (
  `id` int(11) DEFAULT NULL,
  `hiredate` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1
1 row in set (0.00 sec)

mysql> create table test2(id int,hiredate timestamp default 0);
Query OK, 0 rows affected (0.01 sec)

mysql> show create table test2\G
*************************** 1. row ***************************
       Table: test2
Create Table: CREATE TABLE `test2` (
  `id` int(11) DEFAULT NULL,
  `hiredate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=latin1
1 row in set (0.00 sec)
```



 

在MySQL 5.6.5版本之前，Automatic Initialization and Updating只适用于TIMESTAMP，而且一张表中，最多允许一个TIMESTAMP字段采用该特性。从MySQL 5.6.5开始，Automatic Initialization and Updating同时适用于TIMESTAMP和DATETIME，且不限制数量。