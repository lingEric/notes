## **介绍**

这是 [OAuth 2.0](https://tools.ietf.org/html/draft-ietf-oauth-v2) 的用户指南。OAuth 1.0 与之大不相同，若有需求，请阅读 [1.0 的用户指南](https://projects.spring.io/spring-security-oauth/docs/oauth1.html)。

本用户指南分为两个部分，一部分针对的是 OAuth 2.0 的提供方[译者注：通常指服务提供方]，另一部分则是针对 OAuth 2.0 客户端。对于提供方和客户端双方来说，最好的示例代码是[集成测试](https://github.com/spring-projects/spring-security-oauth/tree/master/tests)和[示例应用](https://github.com/spring-projects/spring-security-oauth/tree/master/samples/oauth2)。

## **OAuth 2.0 提供方**

OAuth 2.0 提供方通过某种机制来提供受 OAuth 2.0 保护的资源。其配置涉到确立 OAuth 2.0 客户端能做什么，是能独立访问受保护的资源，还是保护用户的利益。提供方通过管理和验证 OAuth 2.0 令牌达到目的，令牌就是用来访问受保护资源的。

**在某些情况下**，提供方还必须为用户提供一个接口用于确认授权客户端访问受保护的资源（比如，确认页面）。

## **OAuth 2.0 提供方实现**

OAuth 2.0 的提供方实际涵盖两个角色，即认证服务 (Authorization Service) 和资源服务 (Resource Service)，有时候它们会在同一个应用程序中实现。使用 Spring Security OAuth 的时候你可以选择把把它们分别放在两个应用程序中，也可以选择建立使用同一个认证服务的多个资源服务。对令牌的请求由 Spring MVC 控制器终端进行处理，而标准的 Spring security 请求过滤器会处理对受保护资源的访问。Spring以 Security 过滤器链需要以下各端来实现 OAuth 2.0 认证服务：

- [AuthorizationEndpoint](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/endpoint/AuthorizationEndpoint.html) 服务于认证请求。默认 URL： `/oauth/authorize`。
- [TokenEndpoint](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/endpoint/TokenEndpoint.html) 服务于访问令牌的请求。默认 URL： `/oauth/token`。

下面的过滤器用于实现 OAuth 2.0 资源服务：

- [OAuth2AuthenticationProcessingFilter](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/authentication/OAuth2AuthenticationProcessingFilter.html) 用来对请求给出的身份令牌加载认证。 

在所有 OAuth 2.0 提供方特性中，使用 Spring OAuth @Configuraton 注解来进行配置是最简单的。OAuth 配置也有自己的 XML 命名空间，它的结构描述在 <http://www.springframework.org/schema/security/spring-security-oauth2.xsd>，其命名空间是 `http://www.springframework.org/schema/security/oauth2`



## 授权服务器配置

在配置授权服务器时，您必须考虑客户端用来从最终用户获取访问令牌的授权类型（例如，授权码，用户凭证，刷新令牌）。服务器的配置用于提供客户端详细信息服务和令牌服务的实现，并启用或禁用全局机制的某些方面。但是请注意，每个客户端都可以配置特定的权限，以便能够使用某些授权机制和访问权限。即仅仅因为您的提供程序配置为支持“客户端凭据”授予类型，并不意味着特定的客户端有权使用该授予类型。

`@EnableAuthorizationServer` 注解用于配置 OAuth 2.0 授权服务器机制，以及实现 `AuthorizationServerConfigurer` 的任何 `@Beans` （有一个使用空方法的方便的适配器实现）。以下功能委托给由 Spring 创建并传递给 `AuthorizationServerConfigurer` 的独立配置器：

- `ClientDetailsServiceConfigurer`：定义客户端详细信息服务的配置器。客户详细信息可以初始化，或者可以引用现有的 store
- `AuthorizationServerSecurityConfigurer`：定义令牌端点上的安全约束
- `AuthorizationServerEndpointsConfigurer`：定义授权和令牌端点以及令牌服务

提供程序配置的一个重要方面是将授权代码提供给 OAuth 客户端（在授权代码授权中）的方式。OAuth 客户端通过将最终用户导向授权页面来获得授权码，其中用户可以输入其证书，导致从授权服务器重定向到具有授权码的 OAuth 客户端。OAuth 2 规范详细阐述了这方面的例子。

在 XML 中，有一个 `<authorization-server/>` 元素用于配置 OAuth 2.0 授权服务器。



### 客户端配置详细说明

`ClientDetailsServiceConfigurer` （来自 `AuthorizationServerConfigurer` 的一个回调）可用于定义客户端细节服务中的内存或 JDBC 实现。客户端重要的属性是：

- `clientId`: （必须的）客户端 id
- `secret`: （要求用于受信任的客户端）客户端的机密，如果有的话
- `scope`: 客户范围限制。如果范围未定义或为空（默认），客户端将不受范围限制
- `authorizedGrantTypes`: 授权客户端使用的授予类型。默认值为空
- `authorities`: 授权给客户的认证（常规 Spring Security 认证）

通过直接访问底层存储（例如 `JdbcClientDetailsService` 用例中的数据库表）或者通过 `ClientDetailsManager` 接口（`ClientDetailsService` 也能实现这两种实现），可以在正在运行的应用程序中更新客户端详细信息。

> 注意：JDBC 服务的 schema 未与库打包（因为在实践中可能会有太多的变化），但是你可以从 [test code in github](https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql) 这个例子开始。

### 管理 Token

[`AuthorizationServerTokenServices`](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/token/AuthorizationServerTokenServices.html) 接口定义了管理 OAuth 2.0 token 的必要操作。请注意以下几点：

- 当创建一个可访问 Token 时， 身份认证必须存储起来，以便接受可访问 Token 的资源后面可作为引用。
- 可访问的 Token 用来加载授权创建的身份验证。

当你创建了 `AuthorizationServerTokenServices` 的实现后，你可能会考虑使用 [`DefaultTokenServices`](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/token/DefaultTokenServices.html) ，这个接口中有多种方法，可以用来改变访问 Token 的格式和存储。默认情况下使用随机值创建 token 并处理所有的内容，除了委托给 TokenStore 的 token 持久化操作。默认存储方式是[在内存中实现](https://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/token/store/InMemoryTokenStore.html)，不过也支持另外的实现方式。下面为你介绍其他实现方式。

- 默认的 InMemoryTokenStore 在单个服务器上表现良好（少量传输且失败时不通过热插拔更换到备份服务器）。多数项目都可以从它开始，也可以在开发模式中使用，它可以直接启动，没有其它依赖。
- JdbcTokenStore 是同一个东西，不过是 [JDBC 版本](https://projects.spring.io/spring-security-oauth/docs/JdbcTokenStore)，它把数据保存在关系型数据库中。使用 JDBC 版本可以实现在多个服务器上共享数据库，如果只有一台服务器也可以直接在上面部署服务，或者在有多个组件的情况下使用认证和资源服务器。要使用 JdbcTokenStore，需要在 classpath 中配置 "spring-jdbc"。
- [JSON Web Token (JWT) 版本](https://projects.spring.io/spring-security-oauth/docs/%60JwtTokenStore%60)将所有授权相关的数据通过编码后存储起来（没使用后端存储是其显著优势）。它的缺点是不能轻易地撤消访问令牌，因此它们的有效时间通常都很短，在更新令牌的时候处理撤消。还有一个缺点是，如果令牌存储了大量的用户凭据信息，它就可能变得相当大。不过在 DefaultTokenServices 中，它起着翻译令牌值和认证信息的作用。

> 注意：针对 JDBC 服务的架构所需要的库并未打包在其中（因为在实际使用中有太多不确定因素），不过你可以把 [Github 上的测试代码](https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql)作为示例，从这里开始。一定要使用 @EnableTransactionManagement 来防止在多个客户端创建令牌时产生行数据冲突。还要注意示例架构有明确的主键（PRIMARY KEY）声明——这些在开发环境中也是必需的。

### **JWT 令牌** 

需要在认证服务器中加入 JwtTokenStore 来支持 JWT 令牌。资源服务器要能对令牌进行解码，所以 JwtTokenStore 会依赖 JwtAccessTokenConverter，而且认证服务器和资源服务器要有相同的实现。默认情况下令牌会有签名，而资源服务器也要能验证印鉴，所以它需要与授权服务器相同的对称（签名）密钥（共享密钥或对称密钥），或者，它需要一个与认证服务器所使用的私钥配对的公钥（验证密钥）（公私密钥或非对称密钥）。公钥（如果有的话）会由认证服务器通过 /oauth/token_key 提供，这个地址默认情况下是通过 “deenyAll()” 来保护的。你可以向 AuthorizationServerSecurityConfigurer  注入一个标准的 SpEL 表达式（比如 “permitAll()” 就可以，因为那是公钥）来放开保护限制。

使用 JwtTokenStore 的时候你需要在 classpath 中有 "spring-security-jwt"（你可以在 Spring OAuth 所在的 github 库的另一个发布周期中找到）。

### 授予类型

AuthorizationEndpoint 支持的授权类型可以通过 AuthorizationServerEndpointsConfigurer 进行配置。 默认情况下，除了密码之外，所有的授权类型都是受支持的（请参阅下面的关于如何打开的细节）。 以下属性影响授权类型：

- authenticationManager：通过注入 AuthenticationManager 来开启密码授权。
- userDetailsService：如果你注入一个 UserDetailsService，或者全局地配置了一个UserDetailsService（例如在 GlobalAuthenticationManagerConfigurer中），那么刷新令牌授权将包含对用户详细信息的检查，以确保该帐户仍然是活动的
- authorizationCodeServices：为授权代码授权定义授权代码服务（AuthorizationCodeServices 的实例）。
- implicitGrantService：在 imlpicit 授权期间管理状态。
- tokenGranter：TokenGranter（完全控制授予和忽略上面的其他属性）

在 XML 中，授权类型作为授权服务器的子元素被包含在内。

### **配置端点的 URL**

 `AuthorizationServerEndpointsConfigurer` 有一个 `pathMapping()` 方法。它有两个参数：

- 端点的默认（框架实现）URL 路径
- 必需的自定义路径（以“/”开头）

框架提供的 URL 路径是/oauth/authorize（授权端点），/oauth/token（令牌端点），/oauth/confirm_access（用户在这里发布授权批准），/oauth/error（用于在授权服务器上渲染错误），/oauth/check_token（由资源服务器用来解码访问令牌）和/oauth/token_key（如果使用JWT令牌，公开密钥用于令牌验证）。

注： 授权端点/oauth/authorize（或其映射替代）应该使用Spring Security进行保护，以便只有通过身份验证的用户才能访问。例如使用标准的 Spring Security WebSecurityConfigurer：

```
   @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests().antMatchers("/login").permitAll().and()
        // default protection for all resources (including /oauth/authorize)
            .authorizeRequests()
                .anyRequest().hasRole("USER")
        // ... more configuration, e.g. for form login
    }
```



> 注意：如果您的授权服务器也是一个资源服务器，那么另一个安全筛选器链只具有较低的优先级来控制 API 资源。如果这些请求被访问令牌所保护，那么您需要的路径就不能与主用户面对的过滤器链中的路径相匹配，因此请确保在请求匹配器中仅包含上述 WebSecurityConfigurer 中的非 API 资源。

默认情况下，Spring OAuth 在使用客户端密钥的 HTTP 基本身份验证的 @Configuration 支持中为您保护令牌端点。不过它不适用于使用 XML 的情况（所以它应该被明确定义）。

在 XML 中，<authorization-server/> 元素具有一些可用于以类似方式更改默认端点 URL 的属性。 所以必须显式启用 /check_token 端点（启用 check-token-enabled 属性）。



## 自定义用户界面

大部分的授权服务终端接口是被机器使用的，但是也总有几个资源需要通过界面 UI 的，通过 GET /oauth/confirm_access 获取的页面和 /oauth/error 返回的 html。框架使用白板（whitelabel）来提供界面，所以现实中，大部分的授权服务需要提供自己的界面，这样才能控制界面样式和内容。你所需要做的就是提供一个 springmvc 的 controller，给这个终端接口加上 @RequestMappings，框架默认采用低优先权的分发器(dispatcher)。在 /oauth/confirm_access 终端接口，你期望一个 AuthorizationRequest 绑定到 session，session 携带所有需要用户批准的数据（默认的实现是 WhitelabelApprovalEndpoint，所以以这个为 copy 的起点），你能取得在 request 中的所有数据并且按你喜欢的样子渲染处来。然后用户需要做的就是带着授权或拒绝权限的信息 POST 请求到 /oauth/authorize。请求的额参数通过 AuthorizationEndpoint 被直接传输到 UserApprovalHandler，因此你能够按你需要的解析这些数据。默认的 UserApprovalHandler 依赖于你是否在 AuthorizationServerEndpointsConfigurer 提供 ApprovalStore（提供的情况下是 ApprovalStoreUserApprovalHandler）(没有提供的情况下是 TokenStoreUserApprovalHandler)。

标准的授权 handlers 按如下接受：

TokenStoreUserApprovalHandler：通过判断 user_oauth_approval 等于 true 或 false，简单的返回 yes 或 no。

ApprovalStoreUserApprovalHandler：一组参数的 key 为 scope，.*,这些 key 的“*”等于被请求的域（scope）。参数的值是 true 或者 approved（如果用户授权了权限），否则用户被认为在这个域（scope）被拒绝。如果最终有一个域（scope）被授权，那么权限授权就是成功的。

> 注意：不要忘记加入 CSRF 来保护你渲染给用户的表单页面。Spring Security 默认期望参数中有一个名为“_csrf"的参数（Spring Security 在请求属性中提供该值）。查看 Spring Security 用户手册获取更多相关信息，或者查看 whitelabel 实现作为指导。



### **强制使用 SSL** 

普通的 HTTP 用于测试是足够的，但授权服务器在生产环境中应只使用 SSL。你可以在安全的容器中或在代理之后运行应用程序，如果你正确设置代理和容器（这与 OAuth2 无关），它应该可以正常工作。你可能还想要使用 Spring Security 的 requiresChannel() 约束来保护终端。对 /authorize 端点是由你来做到这一点，作为你的正常应用程序的安全性的一部分。对于 /token 端点，AuthorizationServerEndpointsConfigurer 中有一个可以配置使用 sslOnly() 方法的标志。在这两种情况下，安全通道设置都是可选的，但是如果它在不安全的通道上检测到请求，将会导致 Spring Security 重定向到它认为是安全的通道。



## **自定义错误处理** 

授权服务器中的错误处理使用标准的 Spring MVC 功能，即端点本身的 @ExceptionHandler 方法。用户还可以向端点本身提供 WebResponseExceptionTranslator，这是改变响应内容的最佳方式，而不是被渲染的方式。在授权端点的情况下，异常的渲染可委托给 HttpMesssageConverters（可以添加到 MVC 配置）渲染，并且对于 teh 授权终端，可委托给 OAuth 错误视图（/oauth/error）渲染。 为 HTML 响应提供了白标签错误端点，但用户可能需要提供一个自定义的实现（例如，只需添加一个带有 `@RequestMapping("/oauth/error")` 的 `@Controller`）。



### **将用户角色映射到 Scope 上**

有时限制 Tokens 的 scope 不限于分配给客户端的 Scope ，还参考用户自己的权限是非常有用的。如果你在你的 AuthorizationEndpoint 中使用 DefaultOAuth2RequestFactory ，则可以设置一个 checkUserScopes=true 标签，以将允许的 scope 限制为仅与用户角色相匹配的 scope 。你也可以在 TokenEndpoint 中插入一个 OAuth2RequestFactory ，但这仅在安装了 TokenEndpointAuthenticationFilter 的情况下才能工作（例如，使用密码授权时）——你只需要在 HTTP BasicAuthenticationFilter 之后添加该过滤器即可。当然，你也可以实现自己的规则，将 scope 映射到角色，并安装你自己的 OAuth2RequestFactory 版本。 AuthorizationServerEndpointsConfigurer 允许你注入一个自定义的 OAuth2RequestFactory ，以便你可以在使用 @EnableAuthorizationServer 时用该功能来配置 factory 。



### 资源服务器配置 

资源服务器（可以与授权服务器或单独的应用程序相同）为受 OAuth2 令牌保护的资源提供服务。Spring OAuth 提供了一个实现这种保护的 Spring Security 认证过滤器。您可以在 Configuration 类上使用 @EnableResourceServer 将其打开，并使用 ResourceServerConfigurer 对其进行配置（如有必要）。以下功能可以配置：

- tokenServices：定义令牌服务的 bean（ResourceServerTokenServices 的实例）。
- resourceId：资源的 id（可选，但是推荐，并且将由 auth 服务器验证，如果存在的话）。
- 资源服务器的其他扩展点（例如 tokenExtractor 用于从传入请求中提取令牌）
- 请求受保护资源的匹配器（默认为全部）
- 受保护资源的访问规则（默认为普通的"authenticated"）
- Spring Security 中 HttpSecurity 配置器所允许的受保护资源的其他自定义情况

@EnableResourceServer 批注自动向 Spring Security 筛选器链添加一个 OAuth2AuthenticationProcessingFilter 类型的筛选器。

在 XML 中有一个带有 id 属性的 <resource-server /> 元素 - 这是 servlet Filter 的 bean id，然后可以手动添加到标准 Spring Security 链。

您的 ResourceServerTokenServices 是授权服务器的另一半合同。如果资源服务器和授权服务器在同一个应用程序中，并且您使用了 DefaultTokenServices，那么您不必过多考虑这一点，因为它实现了所有必要的接口，因此它自动保持一致。如果您的资源服务器是一个单独的应用程序，那么您必须确保您匹配授权服务器的功能，并提供知道如何正确解码令牌的 ResourceServerTokenServices。与授权服务器一样，您通常可以使用 DefaultTokenServices，而选择主要通过 TokenStore（后端存储或本地编码）表示。另一种方法是 RemoteTokenServices，它是 Spring OAuth 功能（不是规范的一部分），允许资源服务器通过授权服务器（/ oauth / check_token）上的 HTTP 资源来解码令牌。 如果资源服务器中没有大量流量（每个请求必须使用授权服务器进行验证），或者您可以负担缓存结果，则 RemoteTokenServices 方便。要使用 / oauth / check_token 端点，您需要通过更改 AuthorizationServerSecurityConfigurer 中的访问规则（默认值为“denyAll（）”）来公开它。

```
@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess(
					"hasAuthority('ROLE_TRUSTED_CLIENT')");
		}
```

在这个例子中，我们配置 / oauth / check_token 端点和 / oauth / token_key 端点（所以可信资源可以获得 JWT 验证的公钥）。这两个端点受到使用客户端凭据的 HTTP 基本身份验证的保护。

### **配置 OAuth-Aware 表达式处理程序**

你可能想要利用 Spring Security 的[基于表达式的访问控制](https://docs.spring.io/spring-security/site/docs/3.2.5.RELEASE/reference/htmlsingle/#el-access)。表达式处理程序将默认注册在 `@EnableResourceServer` 设置中。表达式包含 *#oauth2.clientHasRole*, *#oauth2.clientHasAnyRole* 和 *#oath2.denyClient* ，可用于根据 oauth 客户端的角色提供访问控制（请参阅 OAuth2SecurityExpressionMethods 以获取全面的列表）。在 XML 中，你可以使用常规 <http/> 安全配置的表达式处理程序元素注册一个 oauth-aware 表达式处理程序。

### OAuth 2.0 客户端

OAuth 2.0 客户端机制负责访问受 OAuth 2.0 保护的其他服务器的资源。配置涉及建立用户可能访问的相关受保护资源。客户端可能还需要提供用于存储授权代码和用户访问令牌的机制。

### 受保护的资源配置

受保护的资源（或“远程资源”）可以使用 OAuth2ProtectedResourceDetails 类型的 bean 定义来定义。受保护的资源具有以下属性：

- id：资源的 ID。 该 id 只被客户端用来查找资源; 它从未在 OAuth 协议中使用过。 它也被用作 bean 的 id。
- clientId：OAuth 客户端 ID。 这是 OAuth 提供商识别您的客户端的 ID。
- clientSecret：与资源相关的秘密。 默认情况下，没有秘密是空的。
- accessTokenUri：提供访问令牌的提供者 OAuth 端点的 URI。
- scope：逗号分隔的字符串列表，指定对资源的访问范围。 默认情况下，不会指定范围。

clientAuthenticationScheme：客户端用来验证访问令牌端点的方案。 建议值：“http_basic”和“form”。 默认：“http_basic”。 请参阅 OAuth 2 规范的第 2.1 节。



不同的授权类型具有不同的 OAuth2ProtectedResourceDetails 的具体实现（例如“client_credentials”授权类型的 ClientCredentialsResource）。对于需要用户授权的认证类型，还有一个属性：

- userAuthorizationUri：如果用户需要授权访问资源，用户将被重定向到的 URI。请注意，这并不总是必需的，具体取决于支持哪些 OAuth 2 配置文件。

在 XML 中，有一个 <resource/> 元素可用于创建 OAuth2ProtectedResourceDetails 类型的 bean。它具有与上述所有属性匹配的属性。



### 客户端配置

对于 OAuth 2.0 客户端，使用 @EnableOAuth2Client 进行配置是非常简单的。需要做两件事情：

- 创建一个过滤器 bean（ID 为 `oauth2ClientContextFilter`）来存储当前请求和上下文。在请求期间需要进行身份验证的情况下，它将管理 OAuth 身份验证 URI 中的重定向。
- 创建一个 `AccessTokenRequest` 型的 bean 的请求范围。这可以通过授权代码（或隐式）授权客户端使用，以保持与单个用户相关的状态不受冲突。

这个过滤器必须写在应用程序里（例如使用一个 Servlet 初始化器或者 web.xml 用相同的名字配置 DelegatingFilterProxy）。

在 OAuth2RestTemplate 中使用 AccessTokenRequest 如下：

```
@Autowired
private OAuth2ClientContext oauth2Context;

@Bean
public OAuth2RestTemplate sparklrRestTemplate() {
	return new OAuth2RestTemplate(sparklr(), oauth2Context);
}
```

OAuth2ClientContext（会为你）被放置在会话范围内，以保持不同用户的状态分离。没有这个的话，你将不得不在服务器上自己管理与之对等的数据结构，将传入的请求映射到用户，并将每个用户与一个单独的 OAuth2ClientContext 实例相关联。

在 XML 中，有一个带有 id 属性的 <client/> 元素 - 这是 servlet Filter 的 bean id，必须像在 @Configurationcase 中一样映射到 DelegatingFilterProxy（具有相同的名称）。

### **访问受保护的资源**

一旦你提供了资源的所有配置，你现在就可以访问这些资源。建议访问这些资源的方法是使用 Spring 3 中引入的 [RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)。Spring Security 的 OAuth 提供了 RestTemplate [扩展](https://projects.spring.io/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/client/OAuth2RestTemplate.java)，仅需要提供一个 [OAuth2ProtectedResourceDetails](https://projects.spring.io/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/client/resource/OAuth2ProtectedResourceDetails.java) 实例。要将其与用户令牌（授权代码许可）一起使用，你应该考虑使用 @EnableOAuth2Client 配置（或与 XML 相当的 `<oauth:rest-template/>`），它会创建一些请求和会话范围的上下文对象，以便不同用户在运行时不会冲突。

作为一般规则，Web 应用程序不应使用密码授权，因此如果你可以使用 AuthorizationCodeResourceDetails，请避免使用 ResourceOwnerPasswordResourceDetails。如果你确切需要从 Java 客户端获得密码授权，那么请使用相同的机制来配置 OAuth2RestTemplate，并将凭据添加到 AccessTokenRequest（它是一个 Map 并且是临时的），而不是 ResourceOwnerPasswordResourceDetails（它在所有访问令牌之间共享） 。



### **客户端的令牌持久化** 

客户端不需要持久化的令牌，但用户在每次重新启动客户端应用程序时都不需要审批新的令牌授予是很不错的。[ClientTokenServices](https://projects.spring.io/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/client/token/ClientTokenServices.java) 接口定义了为特定用户保存 OAuth 2.0 令牌所必需的操作。这提供了一个 JDBC 实现，但如果你愿意实现自己的服务来将访问令牌和关联的身份认证实例并存储在持久数据库中，则可以这样做。如果要使用此功能，则需要为 OAuth2RestTemplatee.g 提供特殊配置的 TokenProvider。

```
@Bean
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public OAuth2RestOperations restTemplate() {
	OAuth2RestTemplate template = new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext(accessTokenRequest));
	AccessTokenProviderChain provider = new AccessTokenProviderChain(Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
	provider.setClientTokenServices(clientTokenServices());
	return template;
}
```

## **外部 OAuth2 Provider 的客户端定制** 

一些外部的 OAuth2 Provider（例如 [Facebook](https://developers.facebook.com/docs/authentication)）并没有正确地实现规范，或者他们使用的是比 Spring Security OAuth 更老的版本。要在客户端应用程序中使用此类 provider 代码，你可能需要调整客户端基础架构中的各个部分。

以使用 Facebook 为例，在 tonr2 应用程序中有一个 Facebook 的功能（你需要更改配置以添加你自己的、有效的客户端 ID 和密钥 - 它们在 Facebook 网站上很容易生成）。

Facebook 令牌响应还包含一个不符合标准的 JSON 条目（它们使用 expires 而不是 expires_in），所以如果你想在你的应用中使用“到期时间”，你将不得不使用自定义的 OAuth2SerializationService。



