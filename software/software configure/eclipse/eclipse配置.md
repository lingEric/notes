# eclipse配置

## 1.配置编码

General->workspace

Text file encoding ->other:utf-8

## 2.配置模板

Aurora->Editor->模板->new

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    $$Author: lingdonglin22304
    $$Date: ${date} ${time}  
    $$Revision: 1.0  
    $$Purpose: ${cursor}
		
-->
<a:screen xmlns:a="http://www.aurora-framework.org/application" trace="true">
    <a:init-procedure>

    </a:init-procedure>
    <a:view>
        <script><![CDATA[

		]]></script>
        <a:dataSets>

        </a:dataSets>
        <a:screenBody>

        </a:screenBody>
    </a:view>
</a:screen>
```



## 3.配置默认选项页

Aurora->Editor

修改BM和Screen文件默认选项卡为源码