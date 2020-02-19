1. 为 Web 服务器创建密钥库。

   使用 Java™keytool命令来创建密钥库。

   ```
   keytool -genkey -alias tomcat -keyalg RSA -keystore D:\keystore\keystore.jks
   ```

2. 根据认证中心提供的指示信息，将 SSL 证书和对应的链证书导入到密钥库中。

3. 编辑 conf/server.xml 文件来定义连接符以使用 SSL。

   此连接器必须指向您的密钥库。

   ```xml
   <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
              maxThreads="150" scheme="https" secure="true"
              clientAuth="false" sslProtocol="TLS"
              keystoreFile="/path/to/keystore.jks"
              keystorePass="mypassword" />
   ```

   ```xml
   <!-- 配置记录 -->
   <Connector port="8443" protocol="HTTP/1.1" connectionTimeout="20000" SSLEnabled="true" maxThreads="150" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="D:\\keystore\\keystore.jks" keystorePass="wodeshijie" />
   ```

   

4. 重新启动 Web 服务器。 您现在可以通过 https://myserver:8443/... 访问 Web 服务器