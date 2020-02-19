# 配置mybatis日志级别，打印SQL

## 1.方案一：配置日志文件

```properties
logging.level.org.springboot.demo.mapper=debug
```

其中org.springboot.demo.mapper目录是项目的mybatis接口包

## 2.方案二：修改mybatis配置文件

在mybatis的全局配置文件中添加如下配置

```xml
<setting name="logImpl" value="STDOUT_LOGGING" />
```



另外：idea可以配置mybatis log plugin插件，具体过程不在这里写出，它会自动的拼接参数【前提是要开启mybatis的SQL打印，也就是按照本文内容修改日志级别即可】，在控制台打印完整的SQL语句，对于开发过程，十分方便。