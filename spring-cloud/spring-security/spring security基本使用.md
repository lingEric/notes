# **Spring Security 基本介绍**

这里就不对Spring Security进行过多的介绍了，具体的可以参考[官方文档](https://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/)

我就只说下SpringSecurity核心功能:

1. 认证（你是谁）
2. 授权（你能干什么）
3. 攻击防护（防止伪造身份）

## **基本环境搭建**

这里我们以SpringBoot作为项目的基本框架，我这里使用的是maven的方式来进行的包管理，所以这里先给出集成Spring Security的方式

```xml
<dependencies>
  ...
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
  ...
</dependencies>
```

然后建立一个Web层请求接口

```
@RestController
@RequestMapping("/user")
public class UserController {
  @GetMapping
  public String getUsers() {    
    return "Hello Spring Security";
  }
}
```

接下来可以直接进行项目的运行，并进行接口的调用看看效果了。

### **通过网页的调用**

我们首先通过浏览器进行接口的调用，直接访问http://localhost:8080/user，如果接口能正常访问，那么应该显示“Hello Spring Security”。 

但是我们是没法正常访问的，出现了下图的身份验证输入框 

[![img](https://files.jb51.net/file_images/article/201805/2018518151144430.jpg?2018418151157)](https://files.jb51.net/file_images/article/201805/2018518151144430.jpg?2018418151157)

这是因为在SpringBoot中，默认的Spring Security就是生效了的，此时的接口都是被保护的，我们需要通过验证才能正常的访问。 Spring Security提供了一个默认的用户，用户名是user，而密码则是启动项目的时候自动生成的。

我们查看项目启动的日志，会发现如下的一段Log

> Using default security password: 62ccf9ca-9fbe-4993-8566-8468cc33c28c

当然你看到的password肯定和我是不一样的，我们直接用user和启动日志中的密码进行登录。

登录成功后，就跳转到了接口正常调用的页面了。 

如果不想一开始就使能Spring Security，可以在配置文件中做如下的配置：

```
# security 使能
security.basic.enabled = false
```

刚才看到的登录框是SpringSecurity是框架自己提供的，被称为httpBasicLogin。显示它不是我们产品上想要的，我们前端一般是通过表单提交的方式进行用户登录验证的，所以我们就需要自定义自己的认证逻辑了。

## **自定义用户认证逻辑**

每个系统肯定是有自己的一套用户体系的，所以我们需要自定义自己的认证逻辑以及登录界面。 
这里我们需要先对SpringSecurity进行相应的配置

```
@Configuration
public class BrowerSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.formLogin()          // 定义当需要用户登录时候，转到的登录页面。
        .and()
        .authorizeRequests()    // 定义哪些URL需要被保护、哪些不需要被保护
        .anyRequest()        // 任何请求,登录后可以访问
        .authenticated();
  }
}
```

接下来再配置用户认证逻辑，因为我们是有自己的一套用户体系的

```
@Component
public class MyUserDetailsService implements UserDetailsService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("用户的用户名: {}", username);
    // TODO 根据用户名，查找到对应的密码，与权限

    // 封装用户信息，并返回。参数分别是：用户名，密码，用户权限
    User user = new User(userame, "123456",
              AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    return user;
 }
}
```

这里我们没有进行过多的校验，用户名可以随意的填写，但是密码必须得是“123456”，这样才能登录成功。 

同时可以看到，这里User对象的第三个参数，它表示的是当前用户的权限，我们将它设置为”admin”。

运行一下程序进行测试，会发现登录界面有所改变 

![img](https://files.jb51.net/file_images/article/201805/2018518151258688.jpg?201841815139)

这是因为我们在配置文件中配置了`http.formLogin()`

我们这里随便填写一个User，然后Password写填写一个错误的（非123456）的。这时会提示校验错误： 

![img](https://files.jb51.net/file_images/article/201805/2018518151328236.jpg?2018418151338)

同时在控制台，也会打印出刚才登录时填写的user

现在我们再来使用正确的密码进行登录试试，可以发现就会通过校验，跳转到正确的接口调用页面了。

## **UserDetails**

刚刚我们在写`MyUserDetailsService`的时候，里面实现了一个方法，并返回了一个`UserDetails`。这个UserDetails 就是封装了用户信息的对象，里面包含了七个方法

```
public interface UserDetails extends Serializable {
  // 封装了权限信息
  Collection<? extends GrantedAuthority> getAuthorities();
  // 密码信息
  String getPassword();
  // 登录用户名
  String getUsername();
  // 帐户是否过期
  boolean isAccountNonExpired();
  // 帐户是否被冻结
  boolean isAccountNonLocked();
  // 帐户密码是否过期，一般有的密码要求性高的系统会使用到，比较每隔一段时间就要求用户重置密码
  boolean isCredentialsNonExpired();
  // 帐号是否可用
  boolean isEnabled();
}
```

我们在返回UserDetails的实现类User的时候，可以通过User的构造方法，设置对应的参数

## **密码加密解密**

SpringSecurity中有一个PasswordEncoder接口

```
public interface PasswordEncoder {
  // 对密码进行加密
  String encode(CharSequence var1);
  // 对密码进行判断匹配
  boolean matches(CharSequence var1, String var2);
}
```

我们只需要自己实现这个接口，并在配置文件中配置一下就可以了。 

这里我暂时以默认提供的一个实现类进行测试

```
// BrowerSecurityConfig 
@Bean
public PasswordEncoder passwordEncoder() {
   return new BCryptPasswordEncoder();
 }
```

加密使用：

```
@Component
public class MyUserDetailsService implements UserDetailsService {


  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.info("用户的用户名: {}", username);

    String password = passwordEncoder.encode("123456");
    logger.info("password: {}", password);

    // 参数分别是：用户名，密码，用户权限
    User user = new User(username, password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    return user;
  }
}
```

这里简单的对123456进行了加密的处理。我们可以进行测试，发现每次打印出来的password都是不一样的，这就是配置的BCryptPasswordEncoder所起到的作用。

## **个性化用户认证逻辑**

### **自定义登录页面**

在之前的测试中，一直都是使用的默认的登录界面，我相信每个产品都是有自己的登录界面设计的，所以我们这一节了解一下如何自定义登录页面。 

我们先写一个简单的登录页面

```
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>登录页面</title>
</head>
<body>
  <h2>自定义登录页面</h2>
  <form action="/user/login" method="post">
    <table>
      <tr>
        <td>用户名：</td>
        <td><input type="text" name="username"></td>
      </tr>
      <tr>
        <td>密码：</td>
        <td><input type="password" name="password"></td>
      </tr>
      <tr>
        <td colspan="2"><button type="submit">登录</button></td>
      </tr>
    </table>
  </form>
</body>
</html>
```

完成了登录页面之后，就需要将它配置进行SpringSecurity

```
// BrowerSecurityConfig.java
@Override
protected void configure(HttpSecurity http) throws Exception {
  http.formLogin()          // 定义当需要用户登录时候，转到的登录页面。
      .loginPage("/login.html")      // 设置登录页面
      .loginProcessingUrl("/user/login") // 自定义的登录接口
      .and()
      .authorizeRequests()    // 定义哪些URL需要被保护、哪些不需要被保护
      .antMatchers("/login.html").permitAll()   // 设置所有人都可以访问登录页面
      .anyRequest()        // 任何请求,登录后可以访问
      .authenticated()
      .and()
      .csrf().disable();     // 关闭csrf防护
}
```

这样，每当我们访问被保护的接口的时候，就会调转到login.html页面

### **处理不同类型的请求**

因为现在一般都前后端分离了，后端提供接口供前端调用，返回JSON格式的数据给前端。刚才那样，调用了被保护的接口，直接进行了页面的跳转，在web端还可以接受，但是在App端就不行了， 所以我们还需要做进一步的处理。 

这里做一下简单的思路整理 

![img](https://files.jb51.net/file_images/article/201805/2018518151529411.jpg?2018418151541)

首先来写自定义的Controller，当需要身份认证的时候就跳转过来

```
@RestController
public class BrowserSecurityController {

  private Logger logger = LoggerFactory.getLogger(getClass());

  // 原请求信息的缓存及恢复
  private RequestCache requestCache = new HttpSessionRequestCache();

  // 用于重定向
  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  /**
   * 当需要身份认证的时候，跳转过来
   * @param request
   * @param response
   * @return
   */
  @RequestMapping("/authentication/require")
  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  public BaseResponse requireAuthenication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    SavedRequest savedRequest = requestCache.getRequest(request, response);

    if (savedRequest != null) {
      String targetUrl = savedRequest.getRedirectUrl();
      logger.info("引发跳转的请求是:" + targetUrl);
      if (StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {
        redirectStrategy.sendRedirect(request, response, "/login.html");
      }
    }

    return new BaseResponse("访问的服务需要身份认证，请引导用户到登录页");
  }
}
```

当然还需要将配置文件进行相应的修改， 这里我就不贴代码了。 就是将该接口开放出来 。

**扩展：** 

这里我们是写死了如果是从网页访问的接口，那么就跳转到”/login.html”页面，其实我们可以扩展一下，将该跳转地址配置到配置文件中，这样会更方便的。

### **自定义处理登录成功/失败**

在之前的测试中，登录成功了都是进行了页面的跳转。 

在前后端分离的情况下，我们登录成功了可能需要向前端返回用户的个人信息，而不是直接进行跳转。登录失败也是同样的道理。

这里涉及到了Spring Security中的两个接口`AuthenticationSuccessHandler`和`AuthenticationFailureHandler`。我们可以实现这个接口，并进行相应的配置就可以了。 当然框架是有默认的实现类的，我们可以继承这个实现类再来自定义自己的业务

```
@Component("myAuthenctiationSuccessHandler")
public class MyAuthenctiationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {

    logger.info("登录成功");

    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(authentication));
  }
}
```

这里我们通过response返回一个JSON字符串回去。 

这个方法中的第三个参数`Authentication`，它里面包含了登录后的用户信息（UserDetails），Session的信息，登录信息等。

```
@Component("myAuthenctiationFailureHandler")
public class MyAuthenctiationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {

    logger.info("登录失败");

    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(new BaseResponse(exception.getMessage())));
  }
}
```

这个方法中的第三个参数`AuthenticationException`，包括了登录失败的信息。

同样的，还是需要在配置文件中进行配置，这里就不贴出全部的代码了，只贴出相应的语句

```
.successHandler(myAuthenticationSuccessHandler) // 自定义登录成功处理 
.failureHandler(myAuthenticationFailureHandler) // 自定义登录失败处理
```

## **代码**

完整的代码可以[点我查看](https://github.com/whyalwaysmea/Spring-Security)