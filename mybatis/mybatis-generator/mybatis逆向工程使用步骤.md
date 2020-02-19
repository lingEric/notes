## 1.在maven中引入相应jar包
```xml
<!-- MBG -->
<!-- https://mvnrepository.com/artifact/org.mybatis.generator/mybatis-generator-core -->
<dependency>
	<groupId>org.mybatis.generator</groupId>
	<artifactId>mybatis-generator-core</artifactId>
	<version>1.3.5</version>
</dependency>
```

## 2.创建相应配置文件
配置mbg.xml文件，文件内容包括以下几个部分
1)  数据库连接信息
2）指定JavaBean生成的位置
3）指定sql映射文件生成的位置
4）指定dao接口生成的位置
5）tabel标签指定每个表的生成策略
6）commentGenerator去掉注释配置

这是我的配置示例。

```xml

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>

	<context id="DB2Tables" targetRuntime="MyBatis3">
		<!--  设置不生成英文注释-->
		<commentGenerator>
			<property name="suppressAllComments" value="true" />
		</commentGenerator>
		
		<!-- 配置数据库连接 -->
		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://localhost:3306/ssm_crud" userId="root"
			password="123456">
		</jdbcConnection>
	
		<javaTypeResolver>
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>
	
		<!-- 指定javaBean生成的位置 -->
		<javaModelGenerator targetPackage="com.ling.bean"
			targetProject=".\src\main\java">
			<property name="enableSubPackages" value="true" />
			<property name="trimStrings" value="true" />
		</javaModelGenerator>
	
		<!--指定sql映射文件生成的位置 -->
		<sqlMapGenerator targetPackage="mapper" targetProject=".\src\main\resources">
			<property name="enableSubPackages" value="true" />
		</sqlMapGenerator>
	
		<!-- 指定dao接口生成的位置，mapper接口 -->
		<javaClientGenerator type="XMLMAPPER"
			targetPackage="com.ling.dao" targetProject=".\src\main\java">
			<property name="enableSubPackages" value="true" />
		</javaClientGenerator>
	
		<!-- table指定每个表的生成策略 -->
		<table tableName="tbl_emp" domainObjectName="Employee"></table>
	</context>
</generatorConfiguration>
```



## 3.开始生成
1）创建java文件
2）指定mbg文件位置

```java

package com.ling.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MBGTest {

	public static void main(String[] args) throws Exception {
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		File configFile = new File("mbg.xml");
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
				callback, warnings);
		myBatisGenerator.generate(null);
	}
}

```

运行上面的代码就可以自动生成JavaBean,mapper接口和SQL映射文件了。