# oracle 12c创建Local User

Oracle 12c有Common user【需用c##做为前缀】和local user的概念，分别对应cdb和pdb。

这里记录如何创建local user并授权以及成功登录。

## 1.创建用户并授权

```sql
SQL> select con_id,dbid,NAME,OPEN_MODE from v$pdbs;

SQL> alter session set container = pdborcl; --pdborcl是创建的可插拔数据库，服务名通过上一条指令查看

Session altered

SQL> create user srm identified by srm default tablespace users;

User created

SQL> grant connect,resource to srm;

Grant succeeded

```

## 2.修改文件tnsnames.ora

文件路径

D:\oracle\product\12.2.0\dbhome_1\network\admin\tnsnames.ora

添加以下内容：

```
//注意这里的服务名称
PDBORCL =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = pdborcl)
    )
  ) 
```

