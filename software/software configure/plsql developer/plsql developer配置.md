# plsql developer常用配置



## plsql developer配置数据库连接

### 1.配置文件tnsnames.ora

文件完整路径：

D:\software\oracle\instantclient_12_2\tnsnames.ora

![](http://ww1.sinaimg.cn/large/006edVQGgy1g5ouo81ynsj30qt0jftbn.jpg)

### 2.在文件中添加一个数据库连接

```xml
starter=   
	(DESCRIPTION =     
		(ADDRESS_LIST=      
			(LOAD_BALANCE=YES)      
			(FAILOVER=YES)      
			(ADDRESS=(PROTOCOL=tcp)(HOST=192.168.4.48)(PORT=1521))      
			(ADDRESS=(PROTOCOL=tcp)(HOST=192.168.4.49)(PORT=1521))      
			(ADDRESS=(PROTOCOL=tcp)(HOST=192.168.4.50)(PORT=1521))  
		)     
		(CONNECT_DATA =       
			(SERVER = DEDICATED)       
			(SERVICE_NAME = starter)     
		)   
	)
SRM_AURORA = 
	(DESCRIPTION=
		(ADDRESS_LIST=
			(LOAD_BALANCE=YES)
			(FAILOVER=YES)
			(ADDRESS=(PROTOCOL=tcp)(HOST=192.168.4.18)(PORT=1521))
		)
		(CONNECT_DATA=
			(SERVICE_NAME=template_new)
		)
	)   


```

## 配置快捷键自动替换

配置AutoReplace.txt文件路径即可，该文件在同级目录下

## 配置代码美化脚本

配置SRM Cloud.br文件路径即可，该文件在同级目录下



## 配置自动提交事务

plsql developer工具不会自动提交事务，很多时候浪费时间在排查事务上。

![](http://ww1.sinaimg.cn/large/006edVQGly1g5r46m3nf6j30qi0j20w5.jpg)