# Aurora项目开发备份

## 1.使用git把项目克隆到本地

File=》Import=》Git=》Projects from git

**分支切换**：根据release分支切换到任务分支

## 2.配置tomcat

修改文件conf/server.xml，添加节点标签`<Context>`，每次添加新标签时，把原有标签注释掉，不删除

```xml
<!--注意位置-->
<Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
	<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
	   prefix="localhost_access_log" suffix=".txt"
	   pattern="%h %l %u %t &quot;%r&quot; %s %b" />

	<Context path="/" reloadable="false" docBase="D:\eclipseworkspacea\SRM-Starter\webRoot" /> 

</Host>
```

上述配置是为了物理映射网站的本地路径。

注意：如果出现端口冲突，可以自定义端口号

设置tomcat最大缓存大小【9.0版本需要修改】,修改文件conf\context.xml

```xml
<Context>	
	<Resources cachingAllowed="true" cacheMaxSize="100000" />
	...
</Context>
```

修改控制台输出编码问题【7.0版本】

到tomcat/conf/目录下 

修改logging.properties 找到 java.util.logging.ConsoleHandler.encoding = utf-8这行 更改为 java.util.logging.ConsoleHandler.encoding = GBK

## 3.配置日志

修改文件`{project_basepath}/webRoot/WEB-INF/uncertain.local.xml`

修改日志文件的位置

修改uiPackageBasePath属性

```xml
<!-- 刚clone时的配置 -->
<?xml version="1.0" encoding="UTF-8"?>
<uncertain-engine defaultLogLevel="INFO">
        <path-config logPath="D:\WORKSPACE\logs" uiPackageBasePath="D:\WORKSPACE\SRM-Starter\webRoot\WEB-INF\aurora.ui" />
</uncertain-engine>

<!-- 修改之后的配置 -->
<path-config logPath="D:\logs\aurora" uiPackageBasePath="D:\eclipseworkspacea\SRM-Starter\webRoot\WEB-INF\aurora.ui" />
```



## 4.配置数据库连接

修改文件`{project_basepath}/webRoot/WEB-INF/aurora.database/datasource.config`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<dc:data-source-config xmlns:dc="aurora.datasource" useTransactionManager="false">
    <dc:database-connections>           
        <dc:database-connection driverClass="oracle.jdbc.driver.OracleDriver" url="jdbc:oracle:thin:@(DESCRIPTION=
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
				)" userName="srm" password="srm" pool="true" initSql="BEGIN sys_nls_language_pkg.set_nls_language(p_nls_language => ${/session/@lang}); END;">        
			<dc:properties>
				minPoolSize=3
				maxPoolSize=50
				testConnectionOnCheckin=true
				checkoutTimeout=30000
				idleConnectionTestPeriod=60
				maxIdleTime=120
				preferredTestQuery=select 1 from dual
				debugUnreturnedConnectionStackTraces=true
                		unreturnedConnectionTimeout=12000
                		acquireIncrement=5
			</dc:properties>        
        </dc:database-connection>

    </dc:database-connections>  
</dc:data-source-config>
```



## 5.新功能的开发

### 1.开发步骤

#### 1.建表

注意规范命名，注意建索引，注意添加字段备注

```plsql
WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR EXIT FAILURE ROLLBACK;
-- log名和文件名一致
spool .log

prompt
prompt Creating table table name
prompt ====================================
prompt
whenever sqlerror continue
whenever sqlerror exit failure rollback

-- table script code here...

spool off
exit
```

参考建表脚本

```sql

-- table script code here ...
create table HIM_ADDRESS_HEADERS_22304
(
  him_address_id          NUMBER not null,
  him_address_code        VARCHAR2(500) not null,
  him_address_desc        VARCHAR2(500),
  him_address_province    NUMBER not null,
  him_address_city        NUMBER not null,
  him_address_county      NUMBER not null,
  him_address_detail      VARCHAR2(1000),
  him_address_contact     VARCHAR2(500) not null,
  him_address_createdate  DATE default sysdate not null,
  him_address_remark      VARCHAR2(500),
  creation_date           DATE default sysdate not null,
  created_by              NUMBER,
  last_update_date        DATE default sysdate not null,
  last_updated_by         NUMBER
);

-- Add comments to the table 
comment on table HIM_ADDRESS_HEADERS_22304
  is 'HIM_ADDRESS_HEADERS_22304';
  
-- Add comments to the columns 
comment on column HIM_ADDRESS_HEADERS_22304.him_address_id
  is '主键,序列HIM_ADDRESS_HEADERS_22304_s';

-- Create/Recreate primary, unique and foreign key constraints 
alter table HIM_ADDRESS_HEADERS_22304
  add constraint HIM_ADDRESS_HEADERS_22304_PK primary key (HIM_ADDRESS_ID);
```



#### 2.建序列

序列名:	表名_S

```plsql
WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR EXIT FAILURE ROLLBACK;

-- log名和文件名一致
spool .log

prompt
prompt Creating sequence sequence name
prompt ====================================
prompt
whenever sqlerror continue
whenever sqlerror exit failure rollback

--sequence script code here...

spool off
exit
```

参考建序列脚本

```plsql
create sequence HIM_ADDRESS_HEADERS_22304_S
minvalue 1
maxvalue 9999999999999999999999999999
start with 1281
increment by 1
cache 20;
```

#### 3.创建基本存储过程

包名：	表名_PKG

文件名后缀：pck

该存储过程可以通过工具包快速生成，然后修改细节部分

```plsql
-- 该工具包提供一些常用的存储过程，可以方便的使用
BEGIN 
  js_tools_pkg.print_table_dml('table_name');
END;
-- 对于insert类型的存储过程，统一定义主键形参，方便之后返回主键，即使实际开发中不会用到，为了防止需求变更，还是建议定义主键形参【IN OUT模式】
-- 执行完功能脚本或系统值集脚本之后，记得提交事务
-- 检查who字段
v_address_record.creation_date          := SYSDATE;
v_address_record.created_by             := p_user_id;
v_address_record.last_update_date       := SYSDATE;
v_address_record.last_updated_by        := p_user_id;
```

#### 4.写init包下的脚本

一般有三个文件，执行完脚本，记得提交事务！！！

> 1. INSTALL_HIM22304.sql【页面注册、功能注册、页面分配、BM分配】
> 2. INITIALIZE_SEEDDATA_SYS_CODE.sql【值集配置】
> 3. INITIALIZE_HIM22304_SYS_PROMPT.sql【页面国际化配置】

##### 1.功能定义

INSTALL_HIM22304.sql【每次开发新功能，此步骤必不可少】

- 页面注册

```sql
-- 模糊查询页面
SELECT * FROM sys_service WHERE service_name LIKE '%bid_scoring_elements.screen%';
```



- 功能定义

```sql
-- 查询指定功能编码
SELECT * FROM sys_function WHERE function_code = 'AP7120';
```



- 分配页面



- 分配bm

```sql
-- 模糊查询bm 
SELECT * FROM sys_function_bm_access sfba WHERE sfba.bm_name LIKE '%bid%';
```





```plsql
WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR EXIT FAILURE ROLLBACK;
-- log名和文件名一致
spool INSTALL_.log

set feedback off
set define off
begin
-- function script code here

end;
/

commit;
set feedback on
set define on

spool off

exit
```

参考代码如下

```plsql
begin

-- 注册页面
sys_data_load_pkg.load_sys_service('modules/him/HIM22304/him_address_headers_22304.screen','HIM_ADDRESS_HEADERS_22304',1,1,0);
sys_data_load_pkg.load_sys_service('modules/him/HIM22304/him_address_headers_22304.svc','HIM_ADDRESS_HEADERS_22304_crud',1,1,0);

-- 功能注册
sys_data_load_pkg.load_sys_function('HIM_ADDRESS_HEADERS_22304','收货地址维护22304','收货地址维护22304','HIM_ADDRESS21742','F','REQUIRE','STANDARD','modules/him/HIM22304/him_address_headers_22304.screen','5010','','');


--分配页面
sys_data_load_pkg.load_function_service('modules/him/HIM22304/him_address_headers_22304.screen','modules/him/HIM22304/him_address_headers_22304.screen');
sys_data_load_pkg.load_function_service('modules/him/HIM22304/him_address_headers_22304.screen','modules/him/HIM22304/him_address_headers_22304.svc');

--分配BM
sys_data_load_pkg.load_function_bm('modules/him/HIM22304/him_address_headers_22304.screen','him.HIM22304.him_address_city_22304');

end;

```

##### 2.页面国际化

INITIALIZE_HIM22304_SYS_PROMPT.sql【用于页面国际化，所有页面中使用到的label或者文本都通过编码来查询具体的值】

```plsql
WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR EXIT FAILURE ROLLBACK;
-- log名和文件名一致
spool INITIALIZE_HIM22304_SYS_PROMPT.log

set feedback off
set define off
begin

end;
/

commit;
set feedback on
set define on

spool off

exit
```

参考脚本

```plsql
begin
sys_prompt_pkg.sys_prompts_load('HIM_ADDRESS_HEADERS_22304.HIM_ADDRESS_CODE','ZHS','收货地址编码');
sys_prompt_pkg.sys_prompts_load('HIM_ADDRESS_HEADERS_22304.HIM_ADDRESS_CODE','US','Code Of Receiving Address');
end;
```

##### 3.值集定义

INITIALIZE_SEEDDATA_SYS_CODE.sql【值集定义】

```plsql
WHENEVER SQLERROR EXIT FAILURE ROLLBACK;
WHENEVER OSERROR EXIT FAILURE ROLLBACK;

-- log名和文件名一致
spool INSTALL_SEEDDATA_SYS_CODE.log

set feedback off
set define off
begin

 sys_code_pkg.insert_sys_code('SEX', 'SEX', 'SEX', 'SEX', 'US');
 sys_code_pkg.update_sys_code('SEX', '性别', '性别', '性别', 'ZHS');

 sys_code_pkg.insert_sys_code_value('SEX', 'FEMALE', 'Female', 'US');
 sys_code_pkg.update_sys_code_value('SEX', 'FEMALE', '女', 'ZHS');

 sys_code_pkg.insert_sys_code_value('SEX', 'MALE', 'Male', 'US');
 sys_code_pkg.update_sys_code_value('SEX', 'MALE', '男', 'ZHS');

 sys_code_pkg.insert_sys_code_value('SEX', 'INDETERMINATE', 'Indeterminate', 'US');
 sys_code_pkg.update_sys_code_value('SEX', 'INDETERMINATE', '不确定', 'ZHS');

end;
/

commit;
set feedback on
set define on

spool off

exit
```

#### 5.用户角色分配

给相应的用户角色分配刚才开发的功能

云平台管理》系统管理-云级》系统角色管理

#### 6.更新缓存

云平台管理》开发维护》缓存数据重载



### 2.通用功能

#### 1.多语言

1. prompt多语言

```plsql
-- 组件国际化
select * from sys_prompts WHERE PROMPT_CODE LIKE 'DEPT%';

BEGIN
 sys_prompt_pkg.sys_prompts_load('CUX_EMPLOYEE.EMPLOYEE_NAME','ZHS' ,'员工姓名');
 sys_prompt_pkg.sys_prompts_load('CUX_EMPLOYEE.EMPLOYEE_NAME','US' ,'Employee Name');
END;
```

2. 消息多语言

```plsql
-- 错误消息多语言
select * from sys_messages where message_name like '%用户不能为空%';

begin
	 sys_message_pkg.insert_message('TEST0829_EMPLOYEE_NAME_IS_NULL_ERROR','Error','Employee Name Is Null.','US');
sys_message_pkg.insert_message('TEST0829_EMPLOYEE_NAME_IS_NULL_ERROR','错误','员工姓名不能为空！','ZHS');
end;
```

#### 2.异常处理

```plsql
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    sys_raise_app_error_pkg.raise_user_define_error(p_message_code            => 'HIM22304_NO_DATA_FOUND',
                                                    p_created_by              => p_user_id,
                                                    p_package_name            => c_pkg_name,
                                                    p_procedure_function_name => 'update_him_address_headers');
    raise_application_error(sys_raise_app_error_pkg.c_error_number,
                            sys_raise_app_error_pkg.g_err_line_id);
  WHEN OTHERS THEN
    sys_raise_app_error_pkg.raise_sys_others_error(p_message                 => dbms_utility.format_error_backtrace || ' ' ||SQLERRM,
                                                   p_created_by              => p_user_id,
                                                   p_package_name            => c_pkg_name,
                                                   p_procedure_function_name => 'update_him_address_headers');
  
    raise_application_error(sys_raise_app_error_pkg.c_error_number,
                            sys_raise_app_error_pkg.g_err_line_id);
  
END;

```

#### 3.常用表和视图、包

```plsql
-- 常用的几张表
-- 功能定义表
SELECT * FROM sys_function;

-- 操作消息提示
SELECT * FROM Sys_Messages;

-- 值集定义表
SELECT * FROM Sys_Codes;

-- 值集value表
SELECT * FROM sys_code_values;

-- 组件多语言表
SELECT * FROM sys_prompts;

-- 页面定义表（包括screen和svc文件）
SELECT * FROM Sys_Service;

-- 分配bm表
select * from sys_function_bm_access t where t.bm_name like '%22304%'



-- 常用视图
-- 方便查询值集操作
sys_code_values_v



```

## 6.常用备份

### 1.省市区三级联动查询数据

```plsql
--省
select fd.description_text,
       fe.parent_id,
       fe.region_id,
       fe.level_num,
       fe.country_code,
       fe.region_code
  from fnd_region_code fe, fnd_descriptions fd
 where fd.description_id = fe.description_id
   and fd.language = 'ZHS'
   and fe.level_num = 0

--江西省的市
select fd.description_text,
       fe.parent_id,
       fe.region_id,
       fe.level_num,
       fe.country_code,
       fe.region_code
  from fnd_region_code fe, fnd_descriptions fd
 where fd.description_id = fe.description_id
   and fd.language = 'ZHS'
   and fe.level_num = 1
   and fe.parent_id = 36 
  
--南昌市的区 
select fd.description_text,
       fe.parent_id,
       fe.region_id,
       fe.level_num,
       fe.country_code,
       fe.region_code
  from fnd_region_code fe, fnd_descriptions fd
 where fd.description_id = fe.description_id
   and fd.language = 'ZHS'
   and fe.level_num = 2
   and fe.parent_id = 3601
```

注意三级联动的两个细节：

1. 隔行修改：在修改其它行的一二级时，dataset中的下一级数据会重新查询，此时再编辑另外的行，可以选择不合法数据
2. 级联清空：在修改一级【二级】内容时，需要把下面的二三级都相应清空



### 2.值集查询

当涉及到lov或者comboBox时，一般为了满足多语言的需求，业务表中不会存放实际的描述值，而是存放code_value。数据库不应该存储code_value_id,因为如果数据库迁移了，那么这个id是不确定的，而应该存储code_value。另外一个原因是，如果存储code_value_id,在某些情况下，可能需要手动的设置字段具体的值，比如修改发票状态时，导致业务逻辑与id耦合，不宜维护。

保险起见：为了防止需求更改，凡是涉及到多语言的字段，统一设计为varchar2类型！！！

**值集涉及到的三张表**

![](http://ww1.sinaimg.cn/large/006edVQGly1g5c2llr1mwj31bv0cctd1.jpg)注意：表fnd_descriptions的主键不唯一，每新增一条code_value,fnd_descriptions就会新增1*n条记录【其中n指language的个数】，通常情况下为2【即中文'ZHS',英文'US'】

#### 1.作为选项时

在这种情况下，需要查询值集描述信息和对应的code_value_id,code_value，有两种实现方式

```plsql
-- 1.通过lookupCode
-- 即dataSet中配置lookupCode属性
-- 2.自定义sql查询

-- 如果是lov，则必须定义lovService，同时配置lov查询以及自动完成
```

#### 2.显示描述时

在这种情况下，业务表的sql查询，需要关联多语言表，还需要做字段映射【lov】或者配置属性【comboBox】

```plsql
-- 示例，业务表【aim】中实际存放code_value
SELECT fd.description_text
  FROM sys_code_values  scv,
       fnd_descriptions fd
 WHERE aim.buyer_bank = scv.code_value
       AND scv.code_value_name_id = fd.description_id
       AND fd.language = 'ZHS'

-- 注意切换多语言为${/session/@lang}
```

### 3.bm常用标签或方法

```plsql
-- 1.where子句
-- 自动拼接where条件，与标签<bm:query-fields>搭配使用
#WHERE_CLAUSE# 
-- 例如
<bm:query-fields>
	<bm:query-field name="him_address_code" queryExpression="him_address_code like &apos;%&apos; || ${@him_address_code} || &apos;%&apos; "/>
</bm:query-fields>

-- 2.时间转换
p_him_address_createdate   => to_date(${@him_address_createdate},'yyyy-MM-dd hh24:mi:ss')

-- 3.常用session域变量
p_user_id   => ${/session/@user_id});
-- 'ZHS'或'US'
fd.language = ${/session/@lang}

-- 4.传参方式
    -- 1.通过session域传参，参考上面
    -- 2.通过queryExpression，参考上面
    -- 3.通过${@parameter_name}，调用存储过程时，传参写法
	-- 4.fe.parent_id = ${/parameter/@parent_id} ，查询时传参写法




-- 5.权限控制
#AUTHORIY_FROM#
#WHERE_CLAUSE#
#AUTHORIY_WHERE#

#ORDERY_BY_CLAUSE#
```

### 4.dataset操作

#### 1.get

![](http://ww1.sinaimg.cn/large/006edVQGgy1g5lbc8yw0lj30c20hiwgr.jpg)

#### 2.set

![](http://ww1.sinaimg.cn/large/006edVQGgy1g5lbc8ycsxj30h90lzn07.jpg)

### 5.渲染函数的使用

```plsql
--发票状态渲染函数
function acp_management22304_invoice_status_renderer(text, record, node){
	if(text=='已开票'){
	    return '<font color="green">'+text+'</font>';
	}else{
	    return '<font color="red">'+text+'</font>';
	}
}          
      
--在组件中使用renderer属性调用              
renderer="acp_management22304_invoice_status_renderer"
--已定义的常用渲染函数
Aurora.formatDateTime
Aurora.formatDate

-- 利用渲染函数实现点击文本，调用其它函数
```

