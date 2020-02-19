编辑文件`${tomcat_home}\conf\tomcat-users.xml`文件，添加用户

```
<tomcat-users ...>
	<role rolename="manager-gui"/>
	<user roles="manager-gui" password="admin" username="admin"/>
</tomcat-users>
```

