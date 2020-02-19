配置多个`WebSecurityConfigurerAdapter`

# 1.使用`casAuthenticationProvider`

```java
@EnableWebSecurity
@Configuration
@Order(1)
public class CasSecurityConfig extends WebSecurityConfigurerAdapter {

    //注入casAuthenticationProvider
    private AuthenticationProvider authenticationProvider;
    //注入casAuthenticationEntryPoint
    private AuthenticationEntryPoint authenticationEntryPoint;
    private SingleSignOutFilter singleSignOutFilter;
    private LogoutFilter logoutFilter;

    @Autowired
    public CasSecurityConfig(CasAuthenticationProvider casAuthenticationProvider, AuthenticationEntryPoint authenticationEntryPoint, LogoutFilter logoutFilter, SingleSignOutFilter singleSignOutFilter) {
        this.authenticationProvider = casAuthenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;

        this.logoutFilter = logoutFilter;
        this.singleSignOutFilter = singleSignOutFilter;

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //hello 受限资源
                .authorizeRequests()
                .antMatchers("/hello")
                .authenticated()
                .and()

                //cas authentication
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)

                //登出地址
                .and()
                .logout().logoutUrl("/logout")

                //单点登出配置
                .and()
                .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
                .addFilterBefore(logoutFilter, LogoutFilter.class);



    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(ServiceProperties serviceProperties) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(serviceProperties);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

}
```

区别`authenticationManager`方法和`authenticationManagerBean`

* `authenticationManager`方法只用于当前类的配置
* `authenticationManagerBean`会作为一个bean，注入到 oauth的authorizationServer配置中

# 2.使用`DaoAuthenticationProvider`

```java
@EnableWebSecurity
@Configuration
@Order(2)
public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login");

    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}

```

# 3.启动日志

```
Mapping filters: filterRegistrationBean urls=[/*], springSecurityFilterChain urls=[/*], characterEncodingFilter urls=[/*], hiddenHttpMethodFilter urls=[/*], formContentFilter urls=[/*], requestContextFilter urls=[/*], httpTraceFilter urls=[/*], logoutFilter urls=[/*], singleSignOutFilter urls=[/*], casAuthenticationFilter urls=[/*]

Eagerly initializing {casSecurityConfig=com.hand.oauth.config.CasSecurityConfig$$EnhancerBySpringCGLIB$$c6296006@67709005, formLoginSecurityConfig=com.hand.oauth.config.FormLoginSecurityConfig$$EnhancerBySpringCGLIB$$f0b40d76@59826895}

===
授权服务器的配置
===
Creating filter chain: OrRequestMatcher [requestMatchers=[Ant [pattern='/oauth/token'], 
Ant [pattern='/oauth/token_key'], 
Ant [pattern='/oauth/check_token']]], 
[
org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@18d2546a, 
org.springframework.security.web.context.SecurityContextPersistenceFilter@6caf0493, 
org.springframework.security.web.header.HeaderWriterFilter@72f6a611, 
org.springframework.security.web.authentication.logout.LogoutFilter@62940940, 
org.springframework.security.web.authentication.www.BasicAuthenticationFilter@199997cc, 
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@16cec650, 
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@130941d1, 
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@338df15f, 
org.springframework.security.web.session.SessionManagementFilter@53860534, 
org.springframework.security.web.access.ExceptionTranslationFilter@1103d855, 
org.springframework.security.web.access.intercept.FilterSecurityInterceptor@2352d6ed]

===
cas与spring security整合的配置
===
Creating filter chain: any request, [
org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@4b6b6175, 
org.springframework.security.web.context.SecurityContextPersistenceFilter@5ab17cab, 
org.springframework.security.web.header.HeaderWriterFilter@1d8ca1d6, 
org.springframework.security.web.csrf.CsrfFilter@28e00d64, 
org.springframework.security.web.authentication.logout.LogoutFilter@60e6f0ca, 
org.springframework.security.web.authentication.logout.LogoutFilter@436c019e, 
org.jasig.cas.client.session.SingleSignOutFilter@64638660, 
org.springframework.security.web.authentication.www.BasicAuthenticationFilter@44c61259, 
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@5f45ee22, 
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@273f660d, 
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@bb17c7, 
org.springframework.security.web.session.SessionManagementFilter@7212d4a0, 
org.springframework.security.web.access.ExceptionTranslationFilter@7f05037, 
org.springframework.security.web.access.intercept.FilterSecurityInterceptor@1d3d2a0b]

===
表单登录配置
===
Creating filter chain: any request, [
org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@2599b597, 
org.springframework.security.web.context.SecurityContextPersistenceFilter@4d8a5d0, 
org.springframework.security.web.header.HeaderWriterFilter@9540f8a, 
org.springframework.security.web.csrf.CsrfFilter@38ff1d4b, 
org.springframework.security.web.authentication.logout.LogoutFilter@1d01d36a, 
org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter@5cb6133f, 
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@1fccc355, 
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@364da779, 
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@7eb4a832, 
org.springframework.security.web.session.SessionManagementFilter@448ded4f, 
org.springframework.security.web.access.ExceptionTranslationFilter@39044167]

===
资源服务器配置
===
Creating filter chain: OrRequestMatcher [
requestMatchers=[Ant [pattern='/qq/**']]], 
[org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@1a0f1c46, 
org.springframework.security.web.context.SecurityContextPersistenceFilter@5f94f39e, 
org.springframework.security.web.header.HeaderWriterFilter@33d9948c, 
org.springframework.security.web.authentication.logout.LogoutFilter@19222a46, 
org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter@1ed69137, 
org.springframework.security.web.savedrequest.RequestCacheAwareFilter@212f9b94, 
org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@5e2d06c9, 
org.springframework.security.web.authentication.AnonymousAuthenticationFilter@7e5db702, 
org.springframework.security.web.session.SessionManagementFilter@6d7557b5, 
org.springframework.security.web.access.ExceptionTranslationFilter@40f7c64e, 
org.springframework.security.web.access.intercept.FilterSecurityInterceptor@1771e7f9]

===
访问端点
/
===

2019-04-12 11:40:53.406 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token']
2019-04-12 11:40:53.406 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/oauth/token'
2019-04-12 11:40:53.407 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token_key']
2019-04-12 11:40:53.407 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/oauth/token_key'
2019-04-12 11:40:53.407 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/check_token']
2019-04-12 11:40:53.407 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/oauth/check_token'
2019-04-12 11:40:53.407 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.web.util.matcher.OrRequestMatcher  : No matches found
2019-04-12 11:40:53.408 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-04-12 11:40:53.409 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-04-12 11:40:53.410 DEBUG 13484 --- [nio-8083-exec-1] w.c.HttpSessionSecurityContextRepository : No HttpSession currently exists
2019-04-12 11:40:53.410 DEBUG 13484 --- [nio-8083-exec-1] w.c.HttpSessionSecurityContextRepository : No SecurityContext was available from the HttpSession: null. A new one will be created.
2019-04-12 11:40:53.412 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-04-12 11:40:53.413 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 4 of 14 in additional filter chain; firing Filter: 'CsrfFilter'
2019-04-12 11:40:53.415 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 5 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:40:53.415 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/logout'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 6 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /' doesn't match 'POST /logout'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 7 of 14 in additional filter chain; firing Filter: 'SingleSignOutFilter'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 8 of 14 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.s.HttpSessionRequestCache        : saved request doesn't match
2019-04-12 11:40:53.416 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-04-12 11:40:53.418 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
//由于端点`/`不需要认证，所以创建了匿名访问身份
2019-04-12 11:40:53.420 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.a.AnonymousAuthenticationFilter  : Populated SecurityContextHolder with anonymous token: 'org.springframework.security.authentication.AnonymousAuthenticationToken@814986b7: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: null; Granted Authorities: ROLE_ANONYMOUS'

2019-04-12 11:40:53.420 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-04-12 11:40:53.420 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.session.SessionManagementFilter  : Requested session ID 2AFEE1F051B19D2C6E898A784BFF91B3 is invalid.
2019-04-12 11:40:53.420 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-04-12 11:40:53.420 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
2019-04-12 11:40:53.421 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/hello'
2019-04-12 11:40:53.421 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.a.i.FilterSecurityInterceptor    : Public object - authentication not attempted
2019-04-12 11:40:53.423 DEBUG 13484 --- [nio-8083-exec-1] o.s.security.web.FilterChainProxy        : / reached end of additional filter chain; proceeding with original chain
2019-04-12 11:40:53.430 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/logout'
2019-04-12 11:40:53.430 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/'; against '/login/cas'
2019-04-12 11:40:53.430 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = false
2019-04-12 11:40:53.430 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorConfigured = false
2019-04-12 11:40:53.431 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorRequest = false
2019-04-12 11:40:53.431 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.cas.web.CasAuthenticationFilter    : proxyTicketRequest = false
2019-04-12 11:40:53.431 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.cas.web.CasAuthenticationFilter    : requiresAuthentication = false
2019-04-12 11:40:53.433 DEBUG 13484 --- [nio-8083-exec-1] o.s.web.servlet.DispatcherServlet        : GET "/", parameters={}
2019-04-12 11:40:53.442 DEBUG 13484 --- [nio-8083-exec-1] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String com.hand.oauth.controller.HelloController.index()
2019-04-12 11:40:53.463 DEBUG 13484 --- [nio-8083-exec-1] o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
2019-04-12 11:40:53.781 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@56fff598
2019-04-12 11:40:53.781 DEBUG 13484 --- [nio-8083-exec-1] w.c.HttpSessionSecurityContextRepository : SecurityContext is empty or contents are anonymous - context will not be stored in HttpSession.
2019-04-12 11:40:53.784 DEBUG 13484 --- [nio-8083-exec-1] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
2019-04-12 11:40:53.789 DEBUG 13484 --- [nio-8083-exec-1] o.s.s.w.a.ExceptionTranslationFilter     : Chain processed normally
2019-04-12 11:40:53.790 DEBUG 13484 --- [nio-8083-exec-1] s.s.w.c.SecurityContextPersistenceFilter : SecurityContextHolder now cleared, as request processing completed

===
访问端点
/hello
被保护的端点，需要登录认证
===


2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token']
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/token'
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token_key']
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/token_key'
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/check_token']
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/check_token'
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.web.util.matcher.OrRequestMatcher  : No matches found
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-04-12 11:41:11.020 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] w.c.HttpSessionSecurityContextRepository : No HttpSession currently exists
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] w.c.HttpSessionSecurityContextRepository : No SecurityContext was available from the HttpSession: null. A new one will be created.
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 4 of 14 in additional filter chain; firing Filter: 'CsrfFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 5 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/logout'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 6 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /hello' doesn't match 'POST /logout'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 7 of 14 in additional filter chain; firing Filter: 'SingleSignOutFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 8 of 14 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.s.HttpSessionRequestCache        : saved request doesn't match
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] 
o.s.s.w.a.AnonymousAuthenticationFilter  :
//还是创建匿名访问身份
Populated SecurityContextHolder with anonymous token: 'org.springframework.security.authentication.AnonymousAuthenticationToken@814986b7: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: null; Granted Authorities: ROLE_ANONYMOUS'
2019-04-12 11:41:11.021 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-04-12 11:41:11.022 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.session.SessionManagementFilter  : Requested session ID 2AFEE1F051B19D2C6E898A784BFF91B3 is invalid.
2019-04-12 11:41:11.022 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-04-12 11:41:11.022 DEBUG 13484 --- [nio-8083-exec-3] o.s.security.web.FilterChainProxy        : /hello at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
//它的作用是对request进行权限判断，允许访问或者抛出accessDenied异常。
主要的逻辑有两步：（1）查询出该访问路径所需的权限；（2）判断用户是否具有该权限
2019-04-12 11:41:11.022 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/hello'
2019-04-12 11:41:11.022 DEBUG 13484 --- [nio-8083-exec-3] 
o.s.s.w.a.i.FilterSecurityInterceptor    : 
//该路径所需权限
Secure object: FilterInvocation: URL: /hello; Attributes: [authenticated]
2019-04-12 11:41:11.023 DEBUG 13484 --- [nio-8083-exec-3] 
o.s.s.w.a.i.FilterSecurityInterceptor    : 
//获取出当前用户的权限【没有登录的用户，默认会有匿名角色】
Previously Authenticated: org.springframework.security.authentication.AnonymousAuthenticationToken@814986b7: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@b364: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: null; Granted Authorities: ROLE_ANONYMOUS
2019-04-12 11:41:11.029 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.access.vote.AffirmativeBased       : Voter: org.springframework.security.web.access.expression.WebExpressionVoter@5678defe, returned: -1
2019-04-12 11:41:11.034 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.a.ExceptionTranslationFilter     : Access is denied (user is anonymous); redirecting to authentication entry point
//由于当前用户为匿名身份，访问被拒绝，抛出AccessDeniedException异常
org.springframework.security.access.AccessDeniedException: Access is denied
	at org.springframework.security.access.vote.AffirmativeBased.decide(AffirmativeBased.java:84) ~[spring-security-core-5.1.4.RELEASE.jar:5.1.4.RELEASE]
	at org.springframework.security.access.intercept.AbstractSecurityInterceptor.beforeInvocation(AbstractSecurityInterceptor.java:233) ~[spring-security-core-5.1.4.RELEASE.jar:5.1.4.RELEASE]
	at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:124) ~[spring-security-web-5.1.4.RELEASE.jar:5.1.4.RELEASE]
	at 
	....
	....
	//这里省略异常抛出的递进

2019-04-12 11:41:11.040 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.util.matcher.AndRequestMatcher   : Trying to match using Ant [pattern='/**', GET]
2019-04-12 11:41:11.040 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request '/hello' matched by universal pattern '/**'
2019-04-12 11:41:11.040 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.util.matcher.AndRequestMatcher   : Trying to match using NegatedRequestMatcher [requestMatcher=Ant [pattern='/**/favicon.*']]
2019-04-12 11:41:11.040 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/**/favicon.*'
2019-04-12 11:41:11.043 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.NegatedRequestMatcher  : matches = true
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.util.matcher.AndRequestMatcher   : Trying to match using NegatedRequestMatcher [requestMatcher=MediaTypeRequestMatcher [contentNegotiationStrategy=org.springframework.web.accept.ContentNegotiationManager@4a954fec, matchingMediaTypes=[application/json], useEquals=false, ignoredMediaTypes=[*/*]]]
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : httpRequestMediaTypes=[text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing text/html
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith text/html = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing application/xhtml+xml
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith application/xhtml+xml = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing image/webp
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith image/webp = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing image/apng
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith image/apng = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing application/signed-exchange;v=b3
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith application/signed-exchange;v=b3 = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing application/xml;q=0.9
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : application/json .isCompatibleWith application/xml;q=0.9 = false
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Processing */*;q=0.8
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.m.MediaTypeRequestMatcher      : Ignoring
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] 
//没有匹配到任何静态资源访问请求
o.s.s.w.u.m.MediaTypeRequestMatcher      : Did not match any media types
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.NegatedRequestMatcher  : matches = true
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.util.matcher.AndRequestMatcher   : Trying to match using NegatedRequestMatcher [requestMatcher=RequestHeaderRequestMatcher [expectedHeaderName=X-Requested-With, expectedHeaderValue=XMLHttpRequest]]
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.u.matcher.NegatedRequestMatcher  : matches = true
2019-04-12 11:41:11.044 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.util.matcher.AndRequestMatcher   : All requestMatchers returned true
2019-04-12 11:41:11.051 DEBUG 13484 --- [nio-8083-exec-3] 
//保存当前的访问路径，，开始发起用户身份认证，当用户身份认证成功后，再跳转到之前的访问路径
o.s.s.w.s.HttpSessionRequestCache        : DefaultSavedRequest added to Session: DefaultSavedRequest[http://localhost:8083/hello]
2019-04-12 11:41:11.051 DEBUG 13484 --- [nio-8083-exec-3] 

o.s.s.w.a.ExceptionTranslationFilter     : Calling Authentication entry point.

2019-04-12 11:41:11.051 DEBUG 13484 --- [nio-8083-exec-3] o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@56fff598
2019-04-12 11:41:11.051 DEBUG 13484 --- [nio-8083-exec-3] w.c.HttpSessionSecurityContextRepository : SecurityContext is empty or contents are anonymous - context will not be stored in HttpSession.
2019-04-12 11:41:11.052 DEBUG 13484 --- [nio-8083-exec-3] s.s.w.c.SecurityContextPersistenceFilter : SecurityContextHolder now cleared, as request processing completed
//过滤器链走完

//ExceptionTranslationFilter开始发起用户身份认证
以下是cas server的日志信息：
2019-04-12 11:41:11,077 INFO [org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager] - <Audit trail record BEGIN
=============================================================
WHO: audit:unknown
WHAT: [event=success,timestamp=Fri Apr 12 11:41:11 CST 2019,source=RankedAuthenticationProviderWebflowEventResolver]
ACTION: AUTHENTICATION_EVENT_TRIGGERED
APPLICATION: CAS
WHEN: Fri Apr 12 11:41:11 CST 2019
CLIENT IP ADDRESS: 0:0:0:0:0:0:0:1
SERVER IP ADDRESS: 0:0:0:0:0:0:0:1
=============================================================

>
2019-04-12 11:41:19,432 INFO [org.apereo.cas.authentication.PolicyBasedAuthenticationManager] - <Authenticated principal [admin] with attributes [{}] via credentials [[admin]].>
2019-04-12 11:41:19,433 INFO [org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager] - <Audit trail record BEGIN
=============================================================
WHO: admin
WHAT: Supplied credentials: [admin]
ACTION: AUTHENTICATION_SUCCESS
APPLICATION: CAS
WHEN: Fri Apr 12 11:41:19 CST 2019
CLIENT IP ADDRESS: 0:0:0:0:0:0:0:1
SERVER IP ADDRESS: 0:0:0:0:0:0:0:1
=============================================================

>
2019-04-12 11:41:19,438 INFO [org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager] - <Audit trail record BEGIN
=============================================================
WHO: admin
WHAT: TGT-************R1P4Mn8oX7-PugBuWz9-kW14kYTFm-6skaqT7Y1-qVnZKujRU5g2noZH9mpP4-DESKTOP-GDU9JII
ACTION: TICKET_GRANTING_TICKET_CREATED
APPLICATION: CAS
WHEN: Fri Apr 12 11:41:19 CST 2019
CLIENT IP ADDRESS: 0:0:0:0:0:0:0:1
SERVER IP ADDRESS: 0:0:0:0:0:0:0:1
=============================================================

>
2019-04-12 11:41:19,445 INFO [org.apereo.cas.DefaultCentralAuthenticationService] - <Granted ticket [ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII] for service [http://localhost:8083/login/cas] and principal [admin]>
2019-04-12 11:41:19,445 INFO [org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager] - <Audit trail record BEGIN
=============================================================
WHO: admin
WHAT: ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII for http://localhost:8083/login/cas
ACTION: SERVICE_TICKET_CREATED
APPLICATION: CAS
WHEN: Fri Apr 12 11:41:19 CST 2019
CLIENT IP ADDRESS: 0:0:0:0:0:0:0:1
SERVER IP ADDRESS: 0:0:0:0:0:0:0:1
=============================================================

>
2019-04-12 11:41:19,937 INFO [org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager] - <Audit trail record BEGIN
=============================================================
WHO: admin
WHAT: ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII
ACTION: SERVICE_TICKET_VALIDATED
APPLICATION: CAS
WHEN: Fri Apr 12 11:41:19 CST 2019
CLIENT IP ADDRESS: 127.0.0.1
SERVER IP ADDRESS: 127.0.0.1
=============================================================
//当用户成功在cas server认证后，跳转到/login/cas端点，校验ticket

2019-04-12 11:41:19.470 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token']
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/oauth/token'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token_key']
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/oauth/token_key'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/check_token']
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/oauth/check_token'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.web.util.matcher.OrRequestMatcher  : No matches found
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        :
/login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] w.c.HttpSessionSecurityContextRepository : HttpSession returned null object for SPRING_SECURITY_CONTEXT
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] w.c.HttpSessionSecurityContextRepository : No SecurityContext was available from the HttpSession: org.apache.catalina.session.StandardSessionFacade@1e8a34fa. A new one will be created.
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 4 of 14 in additional filter chain; firing Filter: 'CsrfFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 5 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/logout'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 6 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /login/cas' doesn't match 'POST /logout'
2019-04-12 11:41:19.471 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 7 of 14 in additional filter chain; firing Filter: 'SingleSignOutFilter'
2019-04-12 11:41:19.482 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 8 of 14 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.s.DefaultSavedRequest            : pathInfo: both null (property equals)
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.s.DefaultSavedRequest            : queryString: arg1=null; arg2=ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII (property not equals)
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.s.HttpSessionRequestCache        : saved request doesn't match
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.a.AnonymousAuthenticationFilter  : Populated SecurityContextHolder with anonymous token: 'org.springframework.security.authentication.AnonymousAuthenticationToken@816e2647: Principal: anonymousUser; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ROLE_ANONYMOUS'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-04-12 11:41:19.483 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/hello'
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.a.i.FilterSecurityInterceptor    : Public object - authentication not attempted
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.security.web.FilterChainProxy        : /login/cas?ticket=ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII reached end of additional filter chain; proceeding with original chain
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/logout'
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/login/cas'
//前面/login/cas没有被spring security的过滤器链拦截，匹配到casAuthenticationFilter的端点，开始发起用户认证
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = true
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : requiresAuthentication = true
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : Request is to process authentication
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorConfigured = false
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorRequest = false
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/login/cas'
2019-04-12 11:41:19.484 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = true
2019-04-12 11:41:19.485 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.authentication.ProviderManager     : Authentication attempt using org.springframework.security.cas.authentication.CasAuthenticationProvider
2019-04-12 11:41:19.485 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.c.a.CasAuthenticationProvider      : serviceUrl = http://localhost:8083/login/cas
2019-04-12 11:41:28.092 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/login/cas'; against '/login/cas'
2019-04-12 11:41:28.092 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = true
2019-04-12 11:41:28.092 DEBUG 13484 --- [nio-8083-exec-4] 
//用户成功认证
o.s.s.cas.web.CasAuthenticationFilter    : Authentication success. Updating SecurityContextHolder to contain: org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII
2019-04-12 11:41:28.093 DEBUG 13484 --- [nio-8083-exec-4] 
//成功认证用户后，RequestAwareAuthenticationSuccessHandler跳转到之前的默认保存路径，这个路径之前保存到session中了
RequestAwareAuthenticationSuccessHandler : Redirecting to DefaultSavedRequest Url: http://localhost:8083/hello
2019-04-12 11:41:28.094 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.web.DefaultRedirectStrategy        : Redirecting to 'http://localhost:8083/hello'
2019-04-12 11:41:28.094 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@56fff598
2019-04-12 11:41:28.094 DEBUG 13484 --- [nio-8083-exec-4] w.c.HttpSessionSecurityContextRepository : SecurityContext 'org.springframework.security.core.context.SecurityContextImpl@557bc6a4: Authentication: org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII' stored to HttpSession: 'org.apache.catalina.session.StandardSessionFacade@1e8a34fa
2019-04-12 11:41:28.095 DEBUG 13484 --- [nio-8083-exec-4] o.s.s.w.a.ExceptionTranslationFilter     : Chain processed normally
2019-04-12 11:41:28.095 DEBUG 13484 --- [nio-8083-exec-4] s.s.w.c.SecurityContextPersistenceFilter : SecurityContextHolder now cleared, as request processing completed

//
2019-04-12 11:41:28.120 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token']
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/token'
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token_key']
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/token_key'
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/check_token']
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/oauth/check_token'
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.web.util.matcher.OrRequestMatcher  : No matches found
2019-04-12 11:41:28.121 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-04-12 11:41:28.122 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-04-12 11:41:28.122 DEBUG 13484 --- [nio-8083-exec-6] w.c.HttpSessionSecurityContextRepository : Obtained a valid SecurityContext from SPRING_SECURITY_CONTEXT: 'org.springframework.security.core.context.SecurityContextImpl@557bc6a4: Authentication: org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII'
2019-04-12 11:41:28.122 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-04-12 11:41:28.122 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 4 of 14 in additional filter chain; firing Filter: 'CsrfFilter'
2019-04-12 11:41:28.122 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 5 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/logout'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 6 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /hello' doesn't match 'POST /logout'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 7 of 14 in additional filter chain; firing Filter: 'SingleSignOutFilter'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 8 of 14 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : pathInfo: both null (property equals)
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : queryString: both null (property equals)
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : requestURI: arg1=/hello; arg2=/hello (property equals)
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : serverPort: arg1=8083; arg2=8083 (property equals)
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : requestURL: arg1=http://localhost:8083/hello; arg2=http://localhost:8083/hello (property equals)
2019-04-12 11:41:28.123 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : scheme: arg1=http; arg2=http (property equals)
2019-04-12 11:41:28.124 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : serverName: arg1=localhost; arg2=localhost (property equals)
2019-04-12 11:41:28.124 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : contextPath: arg1=; arg2= (property equals)
2019-04-12 11:41:28.124 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.DefaultSavedRequest            : servletPath: arg1=/hello; arg2=/hello (property equals)
2019-04-12 11:41:28.124 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.s.HttpSessionRequestCache        : Removing DefaultSavedRequest from session if present
2019-04-12 11:41:28.125 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-04-12 11:41:28.125 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2019-04-12 11:41:28.125 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.AnonymousAuthenticationFilter  : SecurityContextHolder not populated with anonymous token, as it already contained: 'org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII'
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/hello'
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.i.FilterSecurityInterceptor    : Secure object: FilterInvocation: URL: /hello; Attributes: [authenticated]
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.i.FilterSecurityInterceptor    : Previously Authenticated: org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.access.vote.AffirmativeBased       : Voter: org.springframework.security.web.access.expression.WebExpressionVoter@5678defe, returned: 1
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.i.FilterSecurityInterceptor    : Authorization successful
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.i.FilterSecurityInterceptor    : RunAsManager did not change Authentication object
2019-04-12 11:41:28.126 DEBUG 13484 --- [nio-8083-exec-6] o.s.security.web.FilterChainProxy        : /hello reached end of additional filter chain; proceeding with original chain
2019-04-12 11:41:28.128 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/logout'
2019-04-12 11:41:28.128 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/hello'; against '/login/cas'
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = false
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorConfigured = false
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorRequest = false
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.cas.web.CasAuthenticationFilter    : proxyTicketRequest = false
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.cas.web.CasAuthenticationFilter    : requiresAuthentication = false
2019-04-12 11:41:28.129 DEBUG 13484 --- [nio-8083-exec-6] o.s.web.servlet.DispatcherServlet        : GET "/hello", parameters={}
2019-04-12 11:41:28.131 DEBUG 13484 --- [nio-8083-exec-6] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to public java.lang.String com.hand.oauth.controller.HelloController.hello()
2019-04-12 11:41:28.132 DEBUG 13484 --- [nio-8083-exec-6] o.s.w.s.v.ContentNegotiatingViewResolver : Selected 'text/html' given [text/html, application/xhtml+xml, image/webp, image/apng, application/signed-exchange;v=b3, application/xml;q=0.9, */*;q=0.8]
2019-04-12 11:41:28.173 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@56fff598
2019-04-12 11:41:28.175 DEBUG 13484 --- [nio-8083-exec-6] o.s.web.servlet.DispatcherServlet        : Completed 200 OK



===
访问授权端点
/oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect
===
2019-04-12 11:41:28.176 DEBUG 13484 --- [nio-8083-exec-6] o.s.s.w.a.ExceptionTranslationFilter     : Chain processed normally
2019-04-12 11:41:28.176 DEBUG 13484 --- [nio-8083-exec-6] s.s.w.c.SecurityContextPersistenceFilter : SecurityContextHolder now cleared, as request processing completed
2019-04-12 11:41:43.857 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token']
2019-04-12 11:41:43.857 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/oauth/token'
2019-04-12 11:41:43.857 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/token_key']
2019-04-12 11:41:43.857 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/oauth/token_key'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.web.util.matcher.OrRequestMatcher  : Trying to match using Ant [pattern='/oauth/check_token']
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/oauth/check_token'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.web.util.matcher.OrRequestMatcher  : No matches found
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] w.c.HttpSessionSecurityContextRepository : Obtained a valid SecurityContext from SPRING_SECURITY_CONTEXT: 'org.springframework.security.core.context.SecurityContextImpl@557bc6a4: Authentication: org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 4 of 14 in additional filter chain; firing Filter: 'CsrfFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 5 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/logout'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 6 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Request 'GET /oauth/authorize' doesn't match 'POST /logout'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 7 of 14 in additional filter chain; firing Filter: 'SingleSignOutFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 8 of 14 in additional filter chain; firing Filter: 'BasicAuthenticationFilter'
2019-04-12 11:41:43.858 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.s.HttpSessionRequestCache        : saved request doesn't match
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.a.AnonymousAuthenticationFilter  : SecurityContextHolder not populated with anonymous token, as it already contained: 'org.springframework.security.cas.authentication.CasAuthenticationToken@557bc6a4: Principal: org.springframework.security.core.userdetails.User@586034f: Username: admin; Password: [PROTECTED]; Enabled: true; AccountNonExpired: true; credentialsNonExpired: true; AccountNonLocked: true; Granted Authorities: ADMIN,USER; Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@380f4: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: 740520174534D5BF8D076080492A2E5C; Granted Authorities: ADMIN, USER Assertion: org.jasig.cas.client.validation.AssertionImpl@4f253488 Credentials (Service/Proxy Ticket): ST-8-tNma6wiRM7YQvIPev4jP2sqQ9Fk-DESKTOP-GDU9JII'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/hello'
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.a.i.FilterSecurityInterceptor    : Public object - authentication not attempted
2019-04-12 11:41:43.859 DEBUG 13484 --- [nio-8083-exec-7] o.s.security.web.FilterChainProxy        : /oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect reached end of additional filter chain; proceeding with original chain
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/logout'
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.u.matcher.AntPathRequestMatcher  : Checking match of request : '/oauth/authorize'; against '/login/cas'
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.cas.web.CasAuthenticationFilter    : serviceTicketRequest = false
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorConfigured = false
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.cas.web.CasAuthenticationFilter    : proxyReceptorRequest = false
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.cas.web.CasAuthenticationFilter    : proxyTicketRequest = false
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.cas.web.CasAuthenticationFilter    : requiresAuthentication = false
2019-04-12 11:41:43.860 DEBUG 13484 --- [nio-8083-exec-7] o.s.web.servlet.DispatcherServlet        : GET "/oauth/authorize?client_id=iqiyi&response_type=token&redirect_uri=http://localhost:8081/iqiyi/qq/redirect", parameters={masked}
2019-04-12 11:41:43.867 DEBUG 13484 --- [nio-8083-exec-7] .s.o.p.e.FrameworkEndpointHandlerMapping : Mapped to public org.springframework.web.servlet.ModelAndView org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.authorize(java.util.Map<java.lang.String, java.lang.Object>,java.util.Map<java.lang.String, java.lang.String>,org.springframework.web.bind.support.SessionStatus,java.security.Principal)
2019-04-12 11:41:43.915 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.o.p.implicit.ImplicitTokenGranter  : Getting access token for: iqiyi
2019-04-12 11:41:43.934 DEBUG 13484 --- [nio-8083-exec-7] o.s.web.servlet.view.RedirectView        : View [RedirectView], model {}
2019-04-12 11:41:43.934 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.header.writers.HstsHeaderWriter  : Not injecting HSTS header since it did not match the requestMatcher org.springframework.security.web.header.writers.HstsHeaderWriter$SecureRequestMatcher@56fff598
2019-04-12 11:41:43.934 DEBUG 13484 --- [nio-8083-exec-7] o.s.web.servlet.DispatcherServlet        : Completed 302 FOUND
2019-04-12 11:41:43.935 DEBUG 13484 --- [nio-8083-exec-7] o.s.s.w.a.ExceptionTranslationFilter     : Chain processed normally
2019-04-12 11:41:43.935 DEBUG 13484 --- [nio-8083-exec-7] s.s.w.c.SecurityContextPersistenceFilter : SecurityContextHolder now cleared, as request processing completed


```

