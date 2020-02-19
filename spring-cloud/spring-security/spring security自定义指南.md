## 序

本文主要研究一下几种自定义spring security的方式

## 主要方式

- 自定义UserDetailsService
- 自定义passwordEncoder
- 自定义filter
- 自定义AuthenticationProvider
- 自定义AccessDecisionManager
- 自定义securityMetadataSource
- 自定义access访问控制
- 自定义authenticationEntryPoint
- 自定义多个WebSecurityConfigurerAdapter

## 自定义UserDetailsService

```
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //......
    @Bean
    @Override
    protected UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("demoUser1").password("123456")
                .authorities("ROLE_USER","read_x").build());
        manager.createUser(User.withUsername("admin").password("123456")
                .authorities("ROLE_ADMIN").build());
        return manager;
    }
}
```

> 通过重写userDetailsService()方法自定义userDetailsService。这里展示的是InMemoryUserDetailsManager。
> spring security内置了JdbcUserDetailsManager，可以自行扩展

## 自定义passwordEncoder

> 自定义密码的加密方式，实例如下

```
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //......

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }
}
```

## 自定义filter

> 自定义filter离不开对spring security内置filter的顺序的认知：

### Standard Filter Aliases and Ordering

spring security内置的各种filter顺序如下：

| Alias                        | Filter Class                                        | Namespace Element or Attribute         |
| :--------------------------- | :-------------------------------------------------- | :------------------------------------- |
| CHANNEL_FILTER               | ChannelProcessingFilter                             | http/intercept-url@requires-channel    |
| SECURITY_CONTEXT_FILTER      | SecurityContextPersistenceFilter                    | http                                   |
| CONCURRENT_SESSION_FILTER    | ConcurrentSessionFilter                             | session-management/concurrency-control |
| HEADERS_FILTER               | HeaderWriterFilter                                  | http/headers                           |
| CSRF_FILTER                  | CsrfFilter                                          | http/csrf                              |
| LOGOUT_FILTER                | LogoutFilter                                        | http/logout                            |
| X509_FILTER                  | X509AuthenticationFilter                            | http/x509                              |
| PRE_AUTH_FILTER              | AbstractPreAuthenticatedProcessingFilter Subclasses | N/A                                    |
| CAS_FILTER                   | CasAuthenticationFilter                             | N/A                                    |
| FORM_LOGIN_FILTER            | UsernamePasswordAuthenticationFilter                | http/form-login                        |
| BASIC_AUTH_FILTER            | BasicAuthenticationFilter                           | http/http-basic                        |
| SERVLET_API_SUPPORT_FILTER   | SecurityContextHolderAwareRequestFilter             | http/@servlet-api-provision            |
| JAAS_API_SUPPORT_FILTER      | JaasApiIntegrationFilter                            | http/@jaas-api-provision               |
| REMEMBER_ME_FILTER           | RememberMeAuthenticationFilter                      | http/remember-me                       |
| ANONYMOUS_FILTER             | AnonymousAuthenticationFilter                       | http/anonymous                         |
| SESSION_MANAGEMENT_FILTER    | SessionManagementFilter                             | session-management                     |
| EXCEPTION_TRANSLATION_FILTER | ExceptionTranslationFilter                          | http                                   |
| FILTER_SECURITY_INTERCEPTOR  | FilterSecurityInterceptor                           | http                                   |
| SWITCH_USER_FILTER           | SwitchUserFilter                                    | N/A                                    |

### 内置的认证filter

- UsernamePasswordAuthenticationFilter

> 参数有username,password的，走UsernamePasswordAuthenticationFilter，提取参数构造UsernamePasswordAuthenticationToken进行认证，成功则填充SecurityContextHolder的Authentication

- BasicAuthenticationFilter

> header里头有Authorization，而且value是以Basic开头的，则走BasicAuthenticationFilter，提取参数构造UsernamePasswordAuthenticationToken进行认证，成功则填充SecurityContextHolder的Authentication

- AnonymousAuthenticationFilter

> 给没有登陆的用户，填充AnonymousAuthenticationToken到SecurityContextHolder的Authentication

### 定义自己的filter

> 可以像UsernamePasswordAuthenticationFilter或者AnonymousAuthenticationFilter继承GenericFilterBean，或者像BasicAuthenticationFilter继承OncePerRequestFilter。
> 关于GenericFilterBean与OncePerRequestFilter的区别可以见这篇[spring mvc中的几类拦截器对比](https://segmentfault.com/a/1190000011230591)

### 自定义filter主要完成功能如下：

- 提取认证参数
- 调用认证，成功则填充SecurityContextHolder的Authentication，失败则抛出异常

### 实例

```
public class DemoAuthFilter extends GenericFilterBean {

    private final AuthenticationManager authenticationManager;

    public DemoAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String token = httpServletRequest.getHeader("app_token");
        if(StringUtils.isEmpty(token)){
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
            return ;
        }

        try {
            Authentication auth = authenticationManager.authenticate(new WebToken(token));
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (AuthenticationException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
```

### 设置filter顺序

> 上面定义完filter之后，然后就要将它放置到filterChain中

```
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //......
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new DemoAuthFilter(authenticationManager()), BasicAuthenticationFilter.class);
        http.csrf().disable();
        http.logout().disable();
        http.sessionManagement().disable();
    }
}
```

> 这里把他添加在BasicAuthenticationFilter之前，当然可以根据情况直接替换UsernamePasswordAuthenticationFilter

```
http.addFilterAt(newDemoAuthFilter(authenticationManager()),UsernamePasswordAuthenticationFilter.class);
```

## 自定义AuthenticationProvider

AuthenticationManager接口有个实现ProviderManager相当于一个provider chain，它里头有个List<AuthenticationProvider> providers，通过provider来实现认证。

```
public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        boolean debug = logger.isDebugEnabled();

        for (AuthenticationProvider provider : getProviders()) {
            if (!provider.supports(toTest)) {
                continue;
            }

            //......
            try {
                result = provider.authenticate(authentication);

                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            }
            catch (AccountStatusException e) {
                prepareException(e, authentication);
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw e;
            }
            catch (InternalAuthenticationServiceException e) {
                prepareException(e, authentication);
                throw e;
            }
            catch (AuthenticationException e) {
                lastException = e;
            }
        }

        //......
    }
```

> AuthenticationProvider通过supports方法来标识它是否能够处理这个类型的Authentication。
> AnonymousAuthenticationFilter构造的是AnonymousAuthenticationToken，由AnonymousAuthenticationProvider来处理

```
public class AnonymousAuthenticationProvider implements AuthenticationProvider,
        MessageSourceAware {
        //......
        public boolean supports(Class<?> authentication) {
            return (AnonymousAuthenticationToken.class.isAssignableFrom(authentication));
        }
}        
```

> UsernamePasswordAuthenticationFilter，BasicAuthenticationFilter构造的是UsernamePasswordAuthenticationToken，由DaoAuthenticationProvider(其父类为AbstractUserDetailsAuthenticationProvider)来处理

```
public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean, MessageSourceAware {
        //......
        public boolean supports(Class<?> authentication) {
            return (UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication));
        }
}            
```

像上面我们自定义了WebToken，其实例如下：

> 可以实现Authentication接口，或者继承AbstractAuthenticationToken

```
public class WebToken extends AbstractAuthenticationToken {

    private final String token;

    public WebToken(String token) {
        super(null);
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
```

这里就自定义一下支持这类WebToken的AuthenticationProvider

> AuthenticationProvider要实现的功能就是根据参数来校验是否可以登录通过，不通过则抛出异常；通过则获取其GrantedAuthority填充到authentication中
> 如果是继承了AbstractAuthenticationToken，则是填充其authorities属性
> 前面自定义的DemoAuthFilter会在登陆成功之后，将authentication写入到SecurityContextHolder的context中
> 可以实现AuthenticationProvider接口，或者继承AbstractUserDetailsAuthenticationProvider(`默认集成了preAuthenticationChecks以及postAuthenticationChecks`)

```
@Service
public class MyAuthProvider implements AuthenticationProvider {
    //...
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //......
    }
    @Override
    public boolean supports(Class<?> authenticationClass) {
        return return (WebToken.class
                .isAssignableFrom(authenticationClass));
    }
}
```

## 自定义AccessDecisionManager

前面有filter处理了登录问题，接下来是否可访问指定资源的问题就由FilterSecurityInterceptor来处理了。而FilterSecurityInterceptor是用了AccessDecisionManager来进行鉴权。

### AccessDecisionManager的几个实现：

- AffirmativeBased(`spring security默认使用`)

> 只要有投通过（ACCESS_GRANTED）票，则直接判为通过。如果没有投通过票且反对（ACCESS_DENIED）票在1个及其以上的，则直接判为不通过。

- ConsensusBased(`少数服从多数`)

> 通过的票数大于反对的票数则判为通过;通过的票数小于反对的票数则判为不通过;通过的票数和反对的票数相等，则可根据配置allowIfEqualGrantedDeniedDecisions（默认为true）进行判断是否通过。

- UnanimousBased(`反对票优先`)

> 无论多少投票者投了多少通过（ACCESS_GRANTED）票，只要有反对票（ACCESS_DENIED），那都判为不通过;如果没有反对票且有投票者投了通过票，那么就判为通过.

### 实例

其自定义方式之一可以参考[聊聊spring security的role hierarchy](https://segmentfault.com/a/1190000012545851)，展示了如何自定义AccessDecisionVoter。

## 自定义securityMetadataSource

主要是通过ObjectPostProcessor来实现自定义，具体实例可参考[spring security动态配置url权限](https://segmentfault.com/a/1190000010672041)

## 自定义access访问控制

对authorizeRequests的控制，可以使用permitAll，anonymous，authenticated，hasAuthority，hasRole等等

```
                .antMatchers("/login","/css/**", "/js/**","/fonts/**","/file/**").permitAll()
                .antMatchers("/anonymous*").anonymous()
                .antMatchers("/session").authenticated()
                .antMatchers("/login/impersonate").hasAuthority("ROLE_ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/auth/*").hasAnyRole("ADMIN","USER")
```

> 这些都是利用spring security内置的表达式。像hasAuthority等，他们内部还是使用access方法来实现的。因此我们也可以直接使用access，来实现最大限度的自定义。

### 实例

```
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login/**","/logout/**")
                .permitAll()
                .anyRequest().access("@authService.canAccess(request,authentication)");
    }
}
```

这个就有点像使用spring EL表达式，实现实例如下

```
@Component
public class AuthService {

    public boolean canAccess(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if(principal == null){
            return false;
        }

        if(authentication instanceof AnonymousAuthenticationToken){
            //check if this uri can be access by anonymous
            //return
        }

        Set<String> roles = authentication.getAuthorities()
                .stream()
                .map(e -> e.getAuthority())
                .collect(Collectors.toSet());
        String uri = request.getRequestURI();
        //check this uri can be access by this role

        return true;

    }
}
```

## 自定义authenticationEntryPoint

比如你想给basic认证换个realmName，除了再spring security配置中指定

```
security.basic.realm=myrealm
```

也可以这样

```
    httpBasic().authenticationEntryPoint(createBasicAuthEntryPoint("myrealm"))

    public static BasicAuthenticationEntryPoint createBasicAuthEntryPoint(String realmName){
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName(realmName);
        return entryPoint;
    }
```

## 自定义多个WebSecurityConfigurerAdapter

spring security使用antMatchers不支持not的情况，因此可以自定义多个WebSecurityConfigurerAdapter，利用order优先级来实现匹配的覆盖，具体可以参考这篇文章[Multiple Entry Points in Spring Security](http://www.baeldung.com/spring-security-multiple-entry-points)

## 小结

还有其他自定义的方式，等后续有发现再补上。

## doc

- [Spring Security password hashing example](http://www.mkyong.com/spring-security/spring-security-password-hashing-example/)
- [spring mvc中的几类拦截器对比](https://segmentfault.com/a/1190000011230591)
- [spring security 自定义认证](https://segmentfault.com/a/1190000005616465)
- [Spring Security Tutorial](http://www.mkyong.com/tutorials/spring-security-tutorials/)
- [Security with Spring](http://www.baeldung.com/security-spring)
- [话说Spring Security权限管理（源码）](http://www.cnblogs.com/dongying/p/6106855.html)