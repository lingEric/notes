# Spring Security(一)--Architecture Overview

# 1 核心组件

这一节主要介绍一些在Spring Security中常见且核心的Java类，它们之间的依赖，构建起了整个框架。想要理解整个架构，最起码得对这些类眼熟。

### 1.1 SecurityContextHolder

`SecurityContextHolder`用于存储安全上下文（security context）的信息。当前操作的用户是谁，该用户是否已经被认证，他拥有哪些角色权限…这些都被保存在SecurityContextHolder中。`SecurityContextHolder`默认使用`ThreadLocal` 策略来存储认证信息。看到`ThreadLocal` 也就意味着，这是一种与线程绑定的策略。Spring Security在用户登录时自动绑定认证信息到当前线程，在用户退出时，自动清除当前线程的认证信息。但这一切的前提，是你在web场景下使用Spring Security，而如果是Swing界面，Spring也提供了支持，`SecurityContextHolder`的策略则需要被替换，鉴于我的初衷是基于web来介绍Spring Security，所以这里以及后续，非web的相关的内容都一笔带过。

#### 获取当前用户的信息

因为身份信息是与线程绑定的，所以可以在程序的任何地方使用静态方法获取用户信息。一个典型的获取当前登录用户的姓名的例子如下所示：

```
Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

if (principal instanceof UserDetails) {
String username = ((UserDetails)principal).getUsername();
} else {
String username = principal.toString();
}
```

getAuthentication()返回了认证信息，再次getPrincipal()返回了身份信息，UserDetails便是Spring对身份信息封装的一个接口。Authentication和UserDetails的介绍在下面的小节具体讲解，本节重要的内容是介绍SecurityContextHolder这个容器。



### 1.2 Authentication

先看看这个接口的源码长什么样：

```
package org.springframework.security.core;// <1>

public interface Authentication extends Principal, Serializable { // <1>
    Collection<? extends GrantedAuthority> getAuthorities(); // <2>

    Object getCredentials();// <2>

    Object getDetails();// <2>

    Object getPrincipal();// <2>

    boolean isAuthenticated();// <2>

    void setAuthenticated(boolean var1) throws IllegalArgumentException;
}
```

<1> Authentication是spring security包中的接口，直接继承自Principal类，而Principal是位于`java.security`包中的。可以见得，Authentication在spring security中是最高级别的身份/认证的抽象。

<2> 由这个顶级接口，我们可以得到用户拥有的权限信息列表，密码，用户细节信息，用户身份信息，认证信息。

还记得1.1节中，authentication.getPrincipal()返回了一个Object，我们将Principal强转成了Spring Security中最常用的UserDetails，这在Spring Security中非常常见，接口返回Object，使用instanceof判断类型，强转成对应的具体实现类。接口详细解读如下：

- getAuthorities()，权限信息列表，默认是GrantedAuthority接口的一些实现类，通常是代表权限信息的一系列字符串。
- getCredentials()，密码信息，用户输入的密码字符串，在认证过后通常会被移除，用于保障安全。
- getDetails()，细节信息，web应用中的实现接口通常为 WebAuthenticationDetails，它记录了访问者的ip地址和sessionId的值。
- getPrincipal()，敲黑板！！！最重要的身份信息，大部分情况下返回的是UserDetails接口的实现类，也是框架中的常用接口之一。UserDetails接口将会在下面的小节重点介绍。

#### Spring Security是如何完成身份认证的？

1 用户名和密码被过滤器获取到，封装成`Authentication`,通常情况下是`UsernamePasswordAuthenticationToken`这个实现类。

2 `AuthenticationManager` 身份管理器负责验证这个`Authentication`

3 认证成功后，`AuthenticationManager`身份管理器返回一个被填充满了信息的（包括上面提到的权限信息，身份信息，细节信息，但密码通常会被移除）`Authentication`实例。

4 `SecurityContextHolder`安全上下文容器将第3步填充了信息的`Authentication`，通过SecurityContextHolder.getContext().setAuthentication(…)方法，设置到其中。

这是一个抽象的认证流程，而整个过程中，如果不纠结于细节，其实只剩下一个`AuthenticationManager` 是我们没有接触过的了，这个身份管理器我们在后面的小节介绍。将上述的流程转换成代码，便是如下的流程：

```java
public class AuthenticationExample {
private static AuthenticationManager am = new SampleAuthenticationManager();

public static void main(String[] args) throws Exception {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	while(true) {
	System.out.println("Please enter your username:");
	String name = in.readLine();
	System.out.println("Please enter your password:");
	String password = in.readLine();
	try {
		Authentication request = new UsernamePasswordAuthenticationToken(name, password);
		Authentication result = am.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
		break;
	} catch(AuthenticationException e) {
		System.out.println("Authentication failed: " + e.getMessage());
	}
	}
	System.out.println("Successfully authenticated. Security context contains: " +
			SecurityContextHolder.getContext().getAuthentication());
}
}

class SampleAuthenticationManager implements AuthenticationManager {
static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();

static {
	AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
}

public Authentication authenticate(Authentication auth) throws AuthenticationException {
	if (auth.getName().equals(auth.getCredentials())) {
	return new UsernamePasswordAuthenticationToken(auth.getName(),
		auth.getCredentials(), AUTHORITIES);
	}
	throw new BadCredentialsException("Bad Credentials");
}
}
```

注意：上述这段代码只是为了让大家了解Spring Security的工作流程而写的，不是什么源码。在实际使用中，整个流程会变得更加的复杂，但是基本思想，和上述代码如出一辙。

### 1.3 AuthenticationManager

初次接触Spring Security的朋友相信会被`AuthenticationManager`，`ProviderManager`，`AuthenticationProvider` …这么多相似的Spring认证类搞得晕头转向，但只要稍微梳理一下就可以理解清楚它们的联系和设计者的用意。AuthenticationManager（接口）是认证相关的核心接口，也是发起认证的出发点，因为在实际需求中，我们可能会允许用户使用用户名+密码登录，同时允许用户使用邮箱+密码，手机号码+密码登录，甚至，可能允许用户使用指纹登录（还有这样的操作？没想到吧），所以说AuthenticationManager一般不直接认证，AuthenticationManager接口的常用实现类`ProviderManager` 内部会维护一个`List<AuthenticationProvider>`列表，存放多种认证方式，实际上这是委托者模式的应用（Delegate）。也就是说，核心的认证入口始终只有一个：AuthenticationManager，不同的认证方式：用户名+密码（UsernamePasswordAuthenticationToken），邮箱+密码，手机号码+密码登录则对应了三个AuthenticationProvider。这样一来四不四就好理解多了？熟悉shiro的朋友可以把AuthenticationProvider理解成Realm。在默认策略下，只需要通过一个AuthenticationProvider的认证，即可被认为是登录成功。

只保留了关键认证部分的ProviderManager源码：

```
public class ProviderManager implements AuthenticationManager, MessageSourceAware,
		InitializingBean {

    // 维护一个AuthenticationProvider列表
    private List<AuthenticationProvider> providers = Collections.emptyList();
          
    public Authentication authenticate(Authentication authentication)
          throws AuthenticationException {
       Class<? extends Authentication> toTest = authentication.getClass();
       AuthenticationException lastException = null;
       Authentication result = null;

       // 依次认证
       for (AuthenticationProvider provider : getProviders()) {
          if (!provider.supports(toTest)) {
             continue;
          }
          try {
             result = provider.authenticate(authentication);

             if (result != null) {
                copyDetails(authentication, result);
                break;
             }
          }
          ...
          catch (AuthenticationException e) {
             lastException = e;
          }
       }
       // 如果有Authentication信息，则直接返回
       if (result != null) {
			if (eraseCredentialsAfterAuthentication
					&& (result instanceof CredentialsContainer)) {
              	 //移除密码
				((CredentialsContainer) result).eraseCredentials();
			}
             //发布登录成功事件
			eventPublisher.publishAuthenticationSuccess(result);
			return result;
	   }
	   ...
       //执行到此，说明没有认证成功，包装异常信息
       if (lastException == null) {
          lastException = new ProviderNotFoundException(messages.getMessage(
                "ProviderManager.providerNotFound",
                new Object[] { toTest.getName() },
                "No AuthenticationProvider found for {0}"));
       }
       prepareException(lastException, authentication);
       throw lastException;
    }
}
```

`ProviderManager` 中的List，会依照次序去认证，认证成功则立即返回，若认证失败则返回null，下一个AuthenticationProvider会继续尝试认证，如果所有认证器都无法认证成功，则`ProviderManager` 会抛出一个ProviderNotFoundException异常。

到这里，如果不纠结于AuthenticationProvider的实现细节以及安全相关的过滤器，认证相关的核心类其实都已经介绍完毕了：身份信息的存放容器SecurityContextHolder，身份信息的抽象Authentication，身份认证器AuthenticationManager及其认证流程。姑且在这里做一个分隔线。下面来介绍下AuthenticationProvider接口的具体实现。

### 1.4 DaoAuthenticationProvider

AuthenticationProvider最最最常用的一个实现便是DaoAuthenticationProvider。顾名思义，Dao正是数据访问层的缩写，也暗示了这个身份认证器的实现思路。由于本文是一个Overview，姑且只给出其UML类图：

[![DaoAuthenticationProvider UML](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170919204228.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170919204228.png)DaoAuthenticationProvider UML

按照我们最直观的思路，怎么去认证一个用户呢？用户前台提交了用户名和密码，而数据库中保存了用户名和密码，认证便是负责比对同一个用户名，提交的密码和保存的密码是否相同便是了。在Spring Security中。提交的用户名和密码，被封装成了UsernamePasswordAuthenticationToken，而根据用户名加载用户的任务则是交给了UserDetailsService，在DaoAuthenticationProvider中，对应的方法便是retrieveUser，虽然有两个参数，但是retrieveUser只有第一个参数起主要作用，返回一个UserDetails。还需要完成UsernamePasswordAuthenticationToken和UserDetails密码的比对，这便是交给additionalAuthenticationChecks方法完成的，如果这个void方法没有抛异常，则认为比对成功。比对密码的过程，用到了PasswordEncoder和SaltSource，密码加密和盐的概念相信不用我赘述了，它们为保障安全而设计，都是比较基础的概念。

如果你已经被这些概念搞得晕头转向了，不妨这么理解DaoAuthenticationProvider：它获取用户提交的用户名和密码，比对其正确性，如果正确，返回一个数据库中的用户信息（假设用户信息被保存在数据库中）。

### 1.5 UserDetails与UserDetailsService

上面不断提到了UserDetails这个接口，它代表了最详细的用户信息，这个接口涵盖了一些必要的用户信息字段，具体的实现类对它进行了扩展。

```
public interface UserDetails extends Serializable {

   Collection<? extends GrantedAuthority> getAuthorities();

   String getPassword();

   String getUsername();

   boolean isAccountNonExpired();

   boolean isAccountNonLocked();

   boolean isCredentialsNonExpired();

   boolean isEnabled();
}
```

它和Authentication接口很类似，比如它们都拥有username，authorities，区分他们也是本文的重点内容之一。Authentication的getCredentials()与UserDetails中的getPassword()需要被区分对待，前者是用户提交的密码凭证，后者是用户正确的密码，认证器其实就是对这两者的比对。Authentication中的getAuthorities()实际是由UserDetails的getAuthorities()传递而形成的。还记得Authentication接口中的getUserDetails()方法吗？其中的UserDetails用户详细信息便是经过了AuthenticationProvider之后被填充的。

```
public interface UserDetailsService {
   UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

UserDetailsService和AuthenticationProvider两者的职责常常被人们搞混，关于他们的问题在文档的FAQ和issues中屡见不鲜。记住一点即可，敲黑板！！！UserDetailsService只负责从特定的地方（通常是数据库）加载用户信息，仅此而已，记住这一点，可以避免走很多弯路。UserDetailsService常见的实现类有JdbcDaoImpl，InMemoryUserDetailsManager，前者从数据库加载用户，后者从内存中加载用户，也可以自己实现UserDetailsService，通常这更加灵活。

### 1.6 架构概览图

为了更加形象的理解上述我介绍的这些核心类，附上一张按照我的理解，所画出Spring Security的一张非典型的UML图

[![架构概览图](http://kirito.iocoder.cn/spring%20security%20architecture.png)](http://kirito.iocoder.cn/spring%20security%20architecture.png)架构概览图

如果对Spring Security的这些概念感到理解不能，不用担心，因为这是Architecture First导致的必然结果，先过个眼熟。后续的文章会秉持Code First的理念，陆续详细地讲解这些实现类的使用场景，源码分析，以及最基本的：如何配置Spring Security，在后面的文章中可以不时翻看这篇文章，找到具体的类在整个架构中所处的位置，这也是本篇文章的定位。另外，一些Spring Security的过滤器还未囊括在架构概览中，如将表单信息包装成UsernamePasswordAuthenticationToken的过滤器，考虑到这些虽然也是架构的一部分，但是真正重写他们的可能性较小，所以打算放到后面的章节讲解。



# Spring Security(二)--Guides

## 2 Spring Security Guides

### 2.1 引入依赖

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
</dependencies>
```

由于我们集成了springboot，所以不需要显示的引入Spring Security文档中描述core，config依赖，只需要引入spring-boot-starter-security即可。



### 2.2 创建一个不受安全限制的web应用

这是一个首页，不受安全限制

```
src/main/resources/templates/home.html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example</title>
    </head>
    <body>
        <h1>Welcome!</h1>

        <p>Click <a th:href="@{/hello}">here</a> to see a greeting.</p>
    </body>
</html>
```

这个简单的页面上包含了一个链接，跳转到”/hello”。对应如下的页面

```
src/main/resources/templates/hello.html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello world!</h1>
    </body>
</html>
```

接下来配置Spring MVC，使得我们能够访问到页面。

```
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }

}
```

### 2.3 配置Spring Security

一个典型的安全配置如下所示：

```
@Configuration
@EnableWebSecurity <1>
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { <1>
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http <2>
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth <3>
            .inMemoryAuthentication()
                .withUser("admin").password("admin").roles("USER");
    }
}
```

<1> @EnableWebSecurity注解使得SpringMVC集成了Spring Security的web安全支持。另外，WebSecurityConfig配置类同时集成了WebSecurityConfigurerAdapter，重写了其中的特定方法，用于自定义Spring Security配置。整个Spring Security的工作量，其实都是集中在该配置类，不仅仅是这个guides，实际项目中也是如此。

<2> `configure(HttpSecurity)`定义了哪些URL路径应该被拦截，如字面意思所描述：”/“, “/home”允许所有人访问，”/login”作为登录入口，也被允许访问，而剩下的”/hello”则需要登陆后才可以访问。

<3> `configureGlobal(AuthenticationManagerBuilder)`在内存中配置一个用户，admin/admin分别是用户名和密码，这个用户拥有USER角色。

我们目前还没有登录页面，下面创建登录页面：

```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example </title>
    </head>
    <body>
        <div th:if="${param.error}">
            Invalid username and password.
        </div>
        <div th:if="${param.logout}">
            You have been logged out.
        </div>
        <form th:action="@{/login}" method="post">
            <div><label> User Name : <input type="text" name="username"/> </label></div>
            <div><label> Password: <input type="password" name="password"/> </label></div>
            <div><input type="submit" value="Sign In"/></div>
        </form>
    </body>
</html>
```

这个Thymeleaf模板提供了一个用于提交用户名和密码的表单,其中name=”username”，name=”password”是默认的表单值，并发送到“/ login”。 在默认配置中，Spring Security提供了一个拦截该请求并验证用户的过滤器。 如果验证失败，该页面将重定向到“/ login?error”，并显示相应的错误消息。 当用户选择注销，请求会被发送到“/ login?logout”。

最后，我们为hello.html添加一些内容，用于展示用户信息。

```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1 th:inline="text">Hello [[${#httpServletRequest.remoteUser}]]!</h1>
        <form th:action="@{/logout}" method="post">
            <input type="submit" value="Sign Out"/>
        </form>
    </body>
</html>
```

我们使用Spring Security之后，HttpServletRequest#getRemoteUser()可以用来获取用户名。 登出请求将被发送到“/ logout”。 成功注销后，会将用户重定向到“/ login?logout”。

### 2.4 添加启动类

```
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

}
```

### 2.5 测试

访问首页`http://localhost:8080/`:

[![home.html](http://kirito.iocoder.cn/home.png)](http://kirito.iocoder.cn/home.png)home.html

点击here，尝试访问受限的页面：`/hello`,由于未登录，结果被强制跳转到登录页面`/login`：

[![login.html](http://kirito.iocoder.cn/login.png)](http://kirito.iocoder.cn/login.png)login.html

输入正确的用户名和密码之后，跳转到之前想要访问的`/hello`:

[![hello.html](http://kirito.iocoder.cn/hello.png)](http://kirito.iocoder.cn/hello.png)hello.html

点击Sign out退出按钮，访问:`/logout`,回到登录页面:

[![logout.html](http://kirito.iocoder.cn/logout.png)](http://kirito.iocoder.cn/logout.png)logout.html

### 2.6 总结

本篇文章没有什么干货，基本算是翻译了Spring Security Guides的内容，稍微了解Spring Security的朋友都不会对这个翻译感到陌生。考虑到受众的问题，一个入门的例子是必须得有的，方便后续对Spring Security的自定义配置进行讲解。下一节，以此guides为例，讲解这些最简化的配置背后，Spring Security都帮我们做了什么工作。

本节所有的代码，可以直接在Spring的官方仓库下载得到，`git clone https://github.com/spring-guides/gs-securing-web.git`。不过，建议初学者根据文章先一步步配置，出了问题，再与demo进行对比。



# Spring Security(三)--核心配置解读

## 3 核心配置解读

### 3.1 功能介绍

这是Spring Security入门指南中的配置项：

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests()
              .antMatchers("/", "/home").permitAll()
              .anyRequest().authenticated()
              .and()
          .formLogin()
              .loginPage("/login")
              .permitAll()
              .and()
          .logout()
              .permitAll();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .inMemoryAuthentication()
              .withUser("admin").password("admin").roles("USER");
  }
}
```

当配置了上述的javaconfig之后，我们的应用便具备了如下的功能：

- 除了“/”,”/home”(首页),”/login”(登录),”/logout”(注销),之外，其他路径都需要认证。
- 指定“/login”该路径为登录页面，当未认证的用户尝试访问任何受保护的资源时，都会跳转到“/login”。
- 默认指定“/logout”为注销页面
- 配置一个内存中的用户认证器，使用admin/admin作为用户名和密码，具有USER角色

- 防止CSRF攻击
- [Session Fixation](https://en.wikipedia.org/wiki/Session_fixation) protection(可以参考我之前讲解Spring Session的文章，防止别人篡改sessionId)
- Security Header(添加一系列和Header相关的控制)
  - [HTTP Strict Transport Security](https://en.wikipedia.org/wiki/HTTP_Strict_Transport_Security) for secure requests
  - 集成X-Content-Type-Options
  - 缓存控制
  - 集成[X-XSS-Protection](https://msdn.microsoft.com/en-us/library/dd565647(v=vs.85).aspx)
  - X-Frame-Options integration to help prevent [Clickjacking](https://en.wikipedia.org/wiki/Clickjacking)(iframe被默认禁止使用)
- 为Servlet API集成了如下的几个方法
  - [HttpServletRequest#getRemoteUser()](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getRemoteUser())
  - [HttpServletRequest.html#getUserPrincipal()](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getUserPrincipal())
  - [HttpServletRequest.html#isUserInRole(java.lang.String)](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#isUserInRole(java.lang.String))
  - [HttpServletRequest.html#login(java.lang.String, java.lang.String)](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#login(java.lang.String,%20java.lang.String))
  - [HttpServletRequest.html#logout()](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#logout())

### 3.2 @EnableWebSecurity

我们自己定义的配置类WebSecurityConfig加上了@EnableWebSecurity注解，同时继承了WebSecurityConfigurerAdapter。你可能会在想谁的作用大一点，毫无疑问@EnableWebSecurity起到决定性的配置作用，它其实是个组合注解。

```
@Import({ WebSecurityConfiguration.class, // <2>
      SpringWebMvcImportSelector.class }) // <1>
@EnableGlobalAuthentication // <3>
@Configuration
public @interface EnableWebSecurity {
   boolean debug() default false;
}
```

@Import是springboot提供的用于引入外部的配置的注解，可以理解为：@EnableWebSecurity注解激活了@Import注解中包含的配置类。

<1> `SpringWebMvcImportSelector`的作用是判断当前的环境是否包含springmvc，因为spring security可以在非spring环境下使用，为了避免DispatcherServlet的重复配置，所以使用了这个注解来区分。

<2> `WebSecurityConfiguration`顾名思义，是用来配置web安全的，下面的小节会详细介绍。

<3> `@EnableGlobalAuthentication`注解的源码如下：

```
@Import(AuthenticationConfiguration.class)
@Configuration
public @interface EnableGlobalAuthentication {
}
```

注意点同样在@Import之中，它实际上激活了AuthenticationConfiguration这样的一个配置类，用来配置认证相关的核心类。

也就是说：@EnableWebSecurity完成的工作便是加载了WebSecurityConfiguration，AuthenticationConfiguration这两个核心配置类，也就此将spring security的职责划分为了配置安全信息，配置认证信息两部分。

#### WebSecurityConfiguration

在这个配置类中，有一个非常重要的Bean被注册了。

```
@Configuration
public class WebSecurityConfiguration {

	//DEFAULT_FILTER_NAME = "springSecurityFilterChain"
	@Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public Filter springSecurityFilterChain() throws Exception {
    	...
    }
   
 }
```

在未使用springboot之前，大多数人都应该对“springSecurityFilterChain”这个名词不会陌生，他是spring security的核心过滤器，是整个认证的入口。在曾经的XML配置中，想要启用spring security，需要在web.xml中进行如下配置：

```xml
<!-- Spring Security -->
<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>

<filter-mapping>
    <filter-name>springSecurityFilterChain</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

而在springboot集成之后，这样的XML被java配置取代。WebSecurityConfiguration中完成了声明springSecurityFilterChain的作用，并且最终交给DelegatingFilterProxy这个代理类，负责拦截请求（注意DelegatingFilterProxy这个类不是spring security包中的，而是存在于web包中，spring使用了代理模式来实现安全过滤的解耦）。

#### AuthenticationConfiguration

```
@Configuration
@Import(ObjectPostProcessorConfiguration.class)
public class AuthenticationConfiguration {

  	@Bean
	public AuthenticationManagerBuilder authenticationManagerBuilder(
			ObjectPostProcessor<Object> objectPostProcessor) {
		return new AuthenticationManagerBuilder(objectPostProcessor);
	}
  
  	public AuthenticationManager getAuthenticationManager() throws Exception {
    	...
    }

}
```

AuthenticationConfiguration的主要任务，便是负责生成全局的身份认证管理者AuthenticationManager。还记得在《Spring Security(一)–Architecture Overview》中，介绍了Spring Security的认证体系，AuthenticationManager便是最核心的身份认证管理器。

### 3.3 WebSecurityConfigurerAdapter

适配器模式在spring中被广泛的使用，在配置中使用Adapter的好处便是，我们可以选择性的配置想要修改的那一部分配置，而不用覆盖其他不相关的配置。WebSecurityConfigurerAdapter中我们可以选择自己想要修改的内容，来进行重写，而其提供了三个configure重载方法，是我们主要关心的：

[![WebSecurityConfigurerAdapter中的configure](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170924215436.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170924215436.png)WebSecurityConfigurerAdapter中的configure

由参数就可以知道，分别是对AuthenticationManagerBuilder，WebSecurity，HttpSecurity进行个性化的配置。

#### HttpSecurity常用配置

```
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {
  
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/**", "/signup", "/about").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .failureForwardUrl("/login?error")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
                .and()
            .httpBasic()
                .disable();
    }
}
```

上述是一个使用Java Configuration配置HttpSecurity的典型配置，其中http作为根开始配置，每一个and()对应了一个模块的配置（等同于xml配置中的结束标签），并且and()返回了HttpSecurity本身，于是可以连续进行配置。他们配置的含义也非常容易通过变量本身来推测，

- authorizeRequests()配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
- formLogin()对应表单认证相关的配置
- logout()对应了注销相关的配置
- httpBasic()可以配置basic登录
- etc

他们分别代表了http请求相关的安全配置，这些配置项无一例外的返回了Configurer类，而所有的http相关配置可以通过查看HttpSecurity的主要方法得知：

[![http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170924223252.png](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170924223252.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170924223252.png)

需要对http协议有一定的了解才能完全掌握所有的配置，不过，springboot和spring security的自动配置已经足够使用了。其中每一项Configurer（e.g.FormLoginConfigurer,CsrfConfigurer）都是HttpConfigurer的细化配置项。

#### WebSecurityBuilder

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/resources/**");
    }
}
```

以笔者的经验，这个配置中并不会出现太多的配置信息。

#### AuthenticationManagerBuilder

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("admin").password("admin").roles("USER");
    }
}
```

想要在WebSecurityConfigurerAdapter中进行认证相关的配置，可以使用configure(AuthenticationManagerBuilder auth)暴露一个AuthenticationManager的建造器：AuthenticationManagerBuilder 。如上所示，我们便完成了内存中用户的配置。

细心的朋友会发现，在前面的文章中我们配置内存中的用户时，似乎不是这么配置的，而是：

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("admin").password("admin").roles("USER");
    }
}
```

如果你的应用只有唯一一个WebSecurityConfigurerAdapter，那么他们之间的差距可以被忽略，从方法名可以看出两者的区别：使用@Autowired注入的AuthenticationManagerBuilder是全局的身份认证器，作用域可以跨越多个WebSecurityConfigurerAdapter，以及影响到基于Method的安全控制；而 `protected configure()`的方式则类似于一个匿名内部类，它的作用域局限于一个WebSecurityConfigurerAdapter内部。关于这一点的区别，可以参考我曾经提出的issue[spring-security#issues4571](https://github.com/spring-projects/spring-security/issues/4571)。官方文档中，也给出了配置多个WebSecurityConfigurerAdapter的场景以及demo，将在该系列的后续文章中解读。



# Spring Security(四)--核心过滤器源码分析

前面的部分，我们关注了Spring Security是如何完成认证工作的，但是另外一部分核心的内容：过滤器，一直没有提到，我们已经知道Spring Security使用了springSecurityFillterChian作为了安全过滤的入口，这一节主要分析一下这个过滤器链都包含了哪些关键的过滤器，并且各自的使命是什么。

## 4 过滤器详解

### 4.1 核心过滤器概述

由于过滤器链路中的过滤较多，即使是Spring Security的官方文档中也并未对所有的过滤器进行介绍，在之前，《Spring Security(二)–Guides》入门指南中我们配置了一个表单登录的demo，以此为例，来看看这过程中Spring Security都帮我们自动配置了哪些过滤器。

```
Creating filter chain: o.s.s.web.util.matcher.AnyRequestMatcher@1, 
[o.s.s.web.context.SecurityContextPersistenceFilter@8851ce1, 
o.s.s.web.header.HeaderWriterFilter@6a472566, o.s.s.web.csrf.CsrfFilter@61cd1c71, 
o.s.s.web.authentication.logout.LogoutFilter@5e1d03d7, 
o.s.s.web.authentication.UsernamePasswordAuthenticationFilter@122d6c22, 
o.s.s.web.savedrequest.RequestCacheAwareFilter@5ef6fd7f, 
o.s.s.web.servletapi.SecurityContextHolderAwareRequestFilter@4beaf6bd, 
o.s.s.web.authentication.AnonymousAuthenticationFilter@6edcad64, 
o.s.s.web.session.SessionManagementFilter@5e65afb6, 
o.s.s.web.access.ExceptionTranslationFilter@5b9396d3, 
o.s.s.web.access.intercept.FilterSecurityInterceptor@3c5dbdf8
]
```

上述的log信息是我从springboot启动的日志中CV所得，spring security的过滤器日志有一个特点：log打印顺序与实际配置顺序符合，也就意味着`SecurityContextPersistenceFilter`是整个过滤器链的第一个过滤器，而`FilterSecurityInterceptor`则是末置的过滤器。另外通过观察过滤器的名称，和所在的包名，可以大致地分析出他们各自的作用，如`UsernamePasswordAuthenticationFilter`明显便是与使用用户名和密码登录相关的过滤器，而`FilterSecurityInterceptor`我们似乎看不出它的作用，但是其位于`web.access`包下，大致可以分析出他与访问限制相关。第四篇文章主要就是介绍这些常用的过滤器，对其中关键的过滤器进行一些源码分析。先大致介绍下每个过滤器的作用：

- **SecurityContextPersistenceFilter** 两个主要职责：请求来临时，创建`SecurityContext`安全上下文信息，请求结束时清空`SecurityContextHolder`。
- HeaderWriterFilter (文档中并未介绍，非核心过滤器) 用来给http响应添加一些Header,比如X-Frame-Options, X-XSS-Protection*，X-Content-Type-Options.
- CsrfFilter 在spring4这个版本中被默认开启的一个过滤器，用于防止csrf攻击，了解前后端分离的人一定不会对这个攻击方式感到陌生，前后端使用json交互需要注意的一个问题。
- LogoutFilter 顾名思义，处理注销的过滤器
- **UsernamePasswordAuthenticationFilter** 这个会重点分析，表单提交了username和password，被封装成token进行一系列的认证，便是主要通过这个过滤器完成的，在表单认证的方法中，这是最最关键的过滤器。
- RequestCacheAwareFilter (文档中并未介绍，非核心过滤器) 内部维护了一个RequestCache，用于缓存request请求
- SecurityContextHolderAwareRequestFilter 此过滤器对ServletRequest进行了一次包装，使得request具有更加丰富的API
- **AnonymousAuthenticationFilter** 匿名身份过滤器，这个过滤器个人认为很重要，需要将它与UsernamePasswordAuthenticationFilter 放在一起比较理解，spring security为了兼容未登录的访问，也走了一套认证流程，只不过是一个匿名的身份。
- SessionManagementFilter 和session相关的过滤器，内部维护了一个SessionAuthenticationStrategy，两者组合使用，常用来防止`session-fixation protection attack`，以及限制同一用户开启多个会话的数量
- **ExceptionTranslationFilter** 直译成异常翻译过滤器，还是比较形象的，这个过滤器本身不处理异常，而是将认证过程中出现的异常交给内部维护的一些类去处理，具体是那些类下面详细介绍
- **FilterSecurityInterceptor** 这个过滤器决定了访问特定路径应该具备的权限，访问的用户的角色，权限是什么？访问的路径需要什么样的角色和权限？这些判断和处理都是由该类进行的。

其中加粗的过滤器可以被认为是Spring Security的核心过滤器，将在下面，一个过滤器对应一个小节来讲解。

### 4.2 SecurityContextPersistenceFilter

试想一下，如果我们不使用Spring Security，如果保存用户信息呢，大多数情况下会考虑使用Session对吧？在Spring Security中也是如此，用户在登录过一次之后，后续的访问便是通过sessionId来识别，从而认为用户已经被认证。具体在何处存放用户信息，便是第一篇文章中提到的SecurityContextHolder；认证相关的信息是如何被存放到其中的，便是通过SecurityContextPersistenceFilter。在4.1概述中也提到了，SecurityContextPersistenceFilter的两个主要作用便是请求来临时，创建`SecurityContext`安全上下文信息和请求结束时清空`SecurityContextHolder`。顺带提一下：微服务的一个设计理念需要实现服务通信的无状态，而http协议中的无状态意味着不允许存在session，这可以通过`setAllowSessionCreation(false)` 实现，这并不意味着SecurityContextPersistenceFilter变得无用，因为它还需要负责清除用户信息。在Spring Security中，虽然安全上下文信息被存储于Session中，但我们在实际使用中不应该直接操作Session，而应当使用SecurityContextHolder。

#### 源码分析

```
org.springframework.security.web.context.SecurityContextPersistenceFilter
public class SecurityContextPersistenceFilter extends GenericFilterBean {

   static final String FILTER_APPLIED = "__spring_security_scpf_applied";
   //安全上下文存储的仓库
   private SecurityContextRepository repo;
  
   public SecurityContextPersistenceFilter() {
      //HttpSessionSecurityContextRepository是SecurityContextRepository接口的一个实现类
      //使用HttpSession来存储SecurityContext
      this(new HttpSessionSecurityContextRepository());
   }

   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
         throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      if (request.getAttribute(FILTER_APPLIED) != null) {
         // ensure that filter is only applied once per request
         chain.doFilter(request, response);
         return;
      }
      request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
      //包装request，response
      HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request,
            response);
      //从Session中获取安全上下文信息
      SecurityContext contextBeforeChainExecution = repo.loadContext(holder);
      try {
         //请求开始时，设置安全上下文信息，这样就避免了用户直接从Session中获取安全上下文信息
         SecurityContextHolder.setContext(contextBeforeChainExecution);
         chain.doFilter(holder.getRequest(), holder.getResponse());
      }
      finally {
         //请求结束后，清空安全上下文信息
         SecurityContext contextAfterChainExecution = SecurityContextHolder
               .getContext();
         SecurityContextHolder.clearContext();
         repo.saveContext(contextAfterChainExecution, holder.getRequest(),
               holder.getResponse());
         request.removeAttribute(FILTER_APPLIED);
         if (debug) {
            logger.debug("SecurityContextHolder now cleared, as request processing completed");
         }
      }
   }

}
```

过滤器一般负责核心的处理流程，而具体的业务实现，通常交给其中聚合的其他实体类，这在Filter的设计中很常见，同时也符合职责分离模式。例如存储安全上下文和读取安全上下文的工作完全委托给了HttpSessionSecurityContextRepository去处理，而这个类中也有几个方法可以稍微解读下，方便我们理解内部的工作流程

```
org.springframework.security.web.context.HttpSessionSecurityContextRepository
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {
   // 'SPRING_SECURITY_CONTEXT'是安全上下文默认存储在Session中的键值
   public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
   ...
   private final Object contextObject = SecurityContextHolder.createEmptyContext();
   private boolean allowSessionCreation = true;
   private boolean disableUrlRewriting = false;
   private String springSecurityContextKey = SPRING_SECURITY_CONTEXT_KEY;

   private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

   //从当前request中取出安全上下文，如果session为空，则会返回一个新的安全上下文
   public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
      HttpServletRequest request = requestResponseHolder.getRequest();
      HttpServletResponse response = requestResponseHolder.getResponse();
      HttpSession httpSession = request.getSession(false);
      SecurityContext context = readSecurityContextFromSession(httpSession);
      if (context == null) {
         context = generateNewContext();
      }
      ...
      return context;
   }

   ...

   public boolean containsContext(HttpServletRequest request) {
      HttpSession session = request.getSession(false);
      if (session == null) {
         return false;
      }
      return session.getAttribute(springSecurityContextKey) != null;
   }

   private SecurityContext readSecurityContextFromSession(HttpSession httpSession) {
      if (httpSession == null) {
         return null;
      }
      ...
      // Session存在的情况下，尝试获取其中的SecurityContext
      Object contextFromSession = httpSession.getAttribute(springSecurityContextKey);
      if (contextFromSession == null) {
         return null;
      }
      ...
      return (SecurityContext) contextFromSession;
   }

   //初次请求时创建一个新的SecurityContext实例
   protected SecurityContext generateNewContext() {
      return SecurityContextHolder.createEmptyContext();
   }

}
```

SecurityContextPersistenceFilter和HttpSessionSecurityContextRepository配合使用，构成了Spring Security整个调用链路的入口，为什么将它放在最开始的地方也是显而易见的，后续的过滤器中大概率会依赖Session信息和安全上下文信息。

### 4.3 UsernamePasswordAuthenticationFilter

表单认证是最常用的一个认证方式，一个最直观的业务场景便是允许用户在表单中输入用户名和密码进行登录，而这背后的UsernamePasswordAuthenticationFilter，在整个Spring Security的认证体系中则扮演着至关重要的角色。

[![http://kirito.iocoder.cn/2011121410543010.jpg](http://kirito.iocoder.cn/2011121410543010.jpg)](http://kirito.iocoder.cn/2011121410543010.jpg)http://kirito.iocoder.cn/2011121410543010.jpg

上述的时序图，可以看出UsernamePasswordAuthenticationFilter主要肩负起了调用身份认证器，校验身份的作用，至于认证的细节，在前面几章花了很大篇幅进行了介绍，到这里，其实Spring Security的基本流程就已经走通了。

#### 源码分析

```
org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication
public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
   //获取表单中的用户名和密码
   String username = obtainUsername(request);
   String password = obtainPassword(request);
   ...
   username = username.trim();
   //组装成username+password形式的token
   UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
         username, password);
   // Allow subclasses to set the "details" property
   setDetails(request, authRequest);
   //交给内部的AuthenticationManager去认证，并返回认证信息
   return this.getAuthenticationManager().authenticate(authRequest);
}
```

`UsernamePasswordAuthenticationFilter`本身的代码只包含了上述这么一个方法，非常简略，而在其父类`AbstractAuthenticationProcessingFilter`中包含了大量的细节，值得我们分析：

```
public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean
      implements ApplicationEventPublisherAware, MessageSourceAware {
	//包含了一个身份认证器
	private AuthenticationManager authenticationManager;
	//用于实现remeberMe
	private RememberMeServices rememberMeServices = new NullRememberMeServices();
	private RequestMatcher requiresAuthenticationRequestMatcher;
	//这两个Handler很关键，分别代表了认证成功和失败相应的处理器
	private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		...
		Authentication authResult;
		try {
			//此处实际上就是调用UsernamePasswordAuthenticationFilter的attemptAuthentication方法
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				//子类未完成认证，立刻返回
				return;
			}
			sessionStrategy.onAuthentication(authResult, request, response);
		}
		//在认证过程中可以直接抛出异常，在过滤器中，就像此处一样，进行捕获
		catch (InternalAuthenticationServiceException failed) {
			//内部服务异常
			unsuccessfulAuthentication(request, response, failed);
			return;
		}
		catch (AuthenticationException failed) {
			//认证失败
			unsuccessfulAuthentication(request, response, failed);
			return;
		}
		//认证成功
		if (continueChainBeforeSuccessfulAuthentication) {
			chain.doFilter(request, response);
		}
		//注意，认证成功后过滤器把authResult结果也传递给了成功处理器
		successfulAuthentication(request, response, chain, authResult);
	}
	
}
```

整个流程理解起来也并不难，主要就是内部调用了authenticationManager完成认证，根据认证结果执行successfulAuthentication或者unsuccessfulAuthentication，无论成功失败，一般的实现都是转发或者重定向等处理，不再细究AuthenticationSuccessHandler和AuthenticationFailureHandler，有兴趣的朋友，可以去看看两者的实现类。

### 4.4 AnonymousAuthenticationFilter

匿名认证过滤器，可能有人会想：匿名了还有身份？我自己对于Anonymous匿名身份的理解是Spirng Security为了整体逻辑的统一性，即使是未通过认证的用户，也给予了一个匿名身份。而`AnonymousAuthenticationFilter`该过滤器的位置也是非常的科学的，它位于常用的身份认证过滤器（如`UsernamePasswordAuthenticationFilter`、`BasicAuthenticationFilter`、`RememberMeAuthenticationFilter`）之后，意味着只有在上述身份过滤器执行完毕后，SecurityContext依旧没有用户信息，`AnonymousAuthenticationFilter`该过滤器才会有意义—-基于用户一个匿名身份。

#### 源码分析

```
org.springframework.security.web.authentication.AnonymousAuthenticationFilter
public class AnonymousAuthenticationFilter extends GenericFilterBean implements
      InitializingBean {

   private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
   private String key;
   private Object principal;
   private List<GrantedAuthority> authorities;


   //自动创建一个"anonymousUser"的匿名用户,其具有ANONYMOUS角色
   public AnonymousAuthenticationFilter(String key) {
      this(key, "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
   }

   /**
    *
    * @param key key用来识别该过滤器创建的身份
    * @param principal principal代表匿名用户的身份
    * @param authorities authorities代表匿名用户的权限集合
    */
   public AnonymousAuthenticationFilter(String key, Object principal,
         List<GrantedAuthority> authorities) {
      Assert.hasLength(key, "key cannot be null or empty");
      Assert.notNull(principal, "Anonymous authentication principal must be set");
      Assert.notNull(authorities, "Anonymous authorities must be set");
      this.key = key;
      this.principal = principal;
      this.authorities = authorities;
   }

   ...

   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
         throws IOException, ServletException {
      //过滤器链都执行到匿名认证过滤器这儿了还没有身份信息，塞一个匿名身份进去
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
         SecurityContextHolder.getContext().setAuthentication(
               createAuthentication((HttpServletRequest) req));
      }
      chain.doFilter(req, res);
   }

   protected Authentication createAuthentication(HttpServletRequest request) {
     //创建一个AnonymousAuthenticationToken
      AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(key,
            principal, authorities);
      auth.setDetails(authenticationDetailsSource.buildDetails(request));

      return auth;
   }
   ...
}
```

其实对比AnonymousAuthenticationFilter和UsernamePasswordAuthenticationFilter就可以发现一些门道了，UsernamePasswordAuthenticationToken对应AnonymousAuthenticationToken，他们都是Authentication的实现类，而Authentication则是被SecurityContextHolder(SecurityContext)持有的，一切都被串联在了一起。

### 4.5 ExceptionTranslationFilter

ExceptionTranslationFilter异常转换过滤器位于整个springSecurityFilterChain的后方，用来转换整个链路中出现的异常，将其转化，顾名思义，转化以意味本身并不处理。一般其只处理两大类异常：AccessDeniedException访问异常和AuthenticationException认证异常。

这个过滤器非常重要，因为它将Java中的异常和HTTP的响应连接在了一起，这样在处理异常时，我们不用考虑密码错误该跳到什么页面，账号锁定该如何，只需要关注自己的业务逻辑，抛出相应的异常便可。如果该过滤器检测到AuthenticationException，则将会交给内部的AuthenticationEntryPoint去处理，如果检测到AccessDeniedException，需要先判断当前用户是不是匿名用户，如果是匿名访问，则和前面一样运行AuthenticationEntryPoint，否则会委托给AccessDeniedHandler去处理，而AccessDeniedHandler的默认实现，是AccessDeniedHandlerImpl。所以ExceptionTranslationFilter内部的AuthenticationEntryPoint是至关重要的，顾名思义：认证的入口点。

#### 源码分析

```
public class ExceptionTranslationFilter extends GenericFilterBean {
  //处理异常转换的核心方法
  private void handleSpringSecurityException(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, RuntimeException exception)
        throws IOException, ServletException {
     if (exception instanceof AuthenticationException) {
       	//重定向到登录端点
        sendStartAuthentication(request, response, chain,
              (AuthenticationException) exception);
     }
     else if (exception instanceof AccessDeniedException) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationTrustResolver.isAnonymous(authentication) || authenticationTrustResolver.isRememberMe(authentication)) {
		  //重定向到登录端点
           sendStartAuthentication(
                 request,
                 response,
                 chain,
                 new InsufficientAuthenticationException(
                       "Full authentication is required to access this resource"));
        }
        else {
           //交给accessDeniedHandler处理
           accessDeniedHandler.handle(request, response,
                 (AccessDeniedException) exception);
        }
     }
  }
}
```

剩下的便是要搞懂AuthenticationEntryPoint和AccessDeniedHandler就可以了。

[![AuthenticationEntryPoint](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170929231608.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720170929231608.png)AuthenticationEntryPoint

选择了几个常用的登录端点，以其中第一个为例来介绍，看名字就能猜到是认证失败之后，让用户跳转到登录页面。还记得我们一开始怎么配置表单登录页面的吗？

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()//FormLoginConfigurer
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
}
```

我们顺着formLogin返回的FormLoginConfigurer往下找，看看能发现什么，最终在FormLoginConfigurer的父类AbstractAuthenticationFilterConfigurer中有了不小的收获：

```
public abstract class AbstractAuthenticationFilterConfigurer extends ...{
   ...
   //formLogin不出所料配置了AuthenticationEntryPoint
   private LoginUrlAuthenticationEntryPoint authenticationEntryPoint;
   //认证失败的处理器
   private AuthenticationFailureHandler failureHandler;
   ...
}
```

具体如何配置的就不看了，我们得出了结论，formLogin()配置了之后最起码做了两件事，其一，为UsernamePasswordAuthenticationFilter设置了相关的配置，其二配置了AuthenticationEntryPoint。

登录端点还有Http401AuthenticationEntryPoint，Http403ForbiddenEntryPoint这些都是很简单的实现，有时候我们访问受限页面，又没有配置登录，就看到了一个空荡荡的默认错误页面，上面显示着401,403，就是这两个入口起了作用。

还剩下一个AccessDeniedHandler访问决策器未被讲解，简单提一下：AccessDeniedHandlerImpl这个默认实现类会根据errorPage和状态码来判断，最终决定跳转的页面

```
org.springframework.security.web.access.AccessDeniedHandlerImpl#handle
public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException,
      ServletException {
   if (!response.isCommitted()) {
      if (errorPage != null) {
         // Put exception into request scope (perhaps of use to a view)
         request.setAttribute(WebAttributes.ACCESS_DENIED_403,
               accessDeniedException);
         // Set the 403 status code.
         response.setStatus(HttpServletResponse.SC_FORBIDDEN);
         // forward to error page.
         RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
         dispatcher.forward(request, response);
      }
      else {
         response.sendError(HttpServletResponse.SC_FORBIDDEN,
               accessDeniedException.getMessage());
      }
   }
}
```

### 4.6 FilterSecurityInterceptor

想想整个认证安全控制流程还缺了什么？我们已经有了认证，有了请求的封装，有了Session的关联…还缺一个：由什么控制哪些资源是受限的，这些受限的资源需要什么权限，需要什么角色…这一切和访问控制相关的操作，都是由FilterSecurityInterceptor完成的。

FilterSecurityInterceptor的工作流程用笔者的理解可以理解如下：FilterSecurityInterceptor从SecurityContextHolder中获取Authentication对象，然后比对用户拥有的权限和资源所需的权限。前者可以通过Authentication对象直接获得，而后者则需要引入我们之前一直未提到过的两个类：SecurityMetadataSource，AccessDecisionManager。理解清楚决策管理器的整个创建流程和SecurityMetadataSource的作用需要花很大一笔功夫，这里，暂时只介绍其大概的作用。

在JavaConfig的配置中，我们通常如下配置路径的访问控制：

```
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
		.authorizeRequests()
			.antMatchers("/resources/**", "/signup", "/about").permitAll()
             .antMatchers("/admin/**").hasRole("ADMIN")
             .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
             .anyRequest().authenticated()
			.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
				public <O extends FilterSecurityInterceptor> O postProcess(
						O fsi) {
					fsi.setPublishAuthorizationSuccess(true);
					return fsi;
				}
			});
}
```

在ObjectPostProcessor的泛型中看到了FilterSecurityInterceptor，以笔者的经验，目前并没有太多机会需要修改FilterSecurityInterceptor的配置。

### 总结

本篇文章在介绍过滤器时，顺便进行了一些源码的分析，目的是方便理解整个Spring Security的工作流。伴随着整个过滤器链的介绍，安全框架的轮廓应该已经浮出水面了，下面的章节，主要打算通过自定义一些需求，再次分析其他组件的源码，学习应该如何改造Spring Security，为我们所用。



# Spring Security(五)--动手实现一个IP_Login

在开始这篇文章之前，我们似乎应该思考下为什么需要搞清楚Spring Security的内部工作原理？按照第二篇文章中的配置，一个简单的表单认证不就达成了吗？更有甚者，为什么我们不自己写一个表单认证，用过滤器即可完成，大费周章引入Spring Security，看起来也并没有方便多少。对的，在引入Spring Security之前，我们得首先想到，是什么需求让我们引入了Spring Security，以及为什么是Spring Security，而不是shiro等等其他安全框架。我的理解是有如下几点：

1 在前文的介绍中，Spring Security支持防止csrf攻击，session-fixation protection，支持表单认证，basic认证，rememberMe…等等一些特性，有很多是开箱即用的功能，而大多特性都可以通过配置灵活的变更，这是它的强大之处。

2 Spring Security的兄弟的项目Spring Security SSO，OAuth2等支持了多种协议，而这些都是基于Spring Security的，方便了项目的扩展。

3 SpringBoot的支持，更加保证了Spring Security的开箱即用。

4 为什么需要理解其内部工作原理?一个有自我追求的程序员都不会满足于浅尝辄止，如果一个开源技术在我们的日常工作中十分常用，那么我偏向于阅读其源码，这样可以让我们即使排查不期而至的问题，也方便日后需求扩展。

5 Spring及其子项目的官方文档是我见过的最良心的文档！~~相比较于Apache的部分文档~~

这一节，为了对之前分析的Spring Security源码和组件有一个清晰的认识，介绍一个使用IP完成登录的简单demo。



## 5 动手实现一个IP_Login

### 5.1 定义需求

在表单登录中，一般使用数据库中配置的用户表，权限表，角色表，权限组表…这取决于你的权限粒度，但本质都是借助了一个持久化存储，维护了用户的角色权限，而后给出一个/login作为登录端点，使用表单提交用户名和密码，而后完成登录后可自由访问受限页面。

在我们的IP登录demo中，也是类似的，使用IP地址作为身份，内存中的一个ConcurrentHashMap维护IP地址和权限的映射，如果在认证时找不到相应的权限，则认为认证失败。

实际上，在表单登录中，用户的IP地址已经被存放在Authentication.getDetails()中了，完全可以只重写一个AuthenticationProvider认证这个IP地址即可，但是，本demo是为了厘清Spring Security内部工作原理而设置，为了设计到更多的类，我完全重写了IP过滤器。

### 5.2 设计概述

我们的参考完全是表单认证，在之前章节中，已经了解了表单认证相关的核心流程，将此图再贴一遍：

[![http://kirito.iocoder.cn/2011121410543010.jpg](http://kirito.iocoder.cn/2011121410543010.jpg)](http://kirito.iocoder.cn/2011121410543010.jpg)http://kirito.iocoder.cn/2011121410543010.jpg

在IP登录的demo中，使用IpAuthenticationProcessingFilter拦截IP登录请求，同样使用ProviderManager作为全局AuthenticationManager接口的实现类，将ProviderManager内部的DaoAuthenticationProvider替换为IpAuthenticationProvider，而UserDetailsService则使用一个ConcurrentHashMap代替。更详细一点的设计：

1. IpAuthenticationProcessingFilter–>UsernamePasswordAuthenticationFilter
2. IpAuthenticationToken–>UsernamePasswordAuthenticationToken
3. ProviderManager–>ProviderManager
4. IpAuthenticationProvider–>DaoAuthenticationProvider
5. ConcurrentHashMap–>UserDetailsService

### 5.3 IpAuthenticationToken

```
public class IpAuthenticationToken extends AbstractAuthenticationToken {

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public IpAuthenticationToken(String ip) {
        super(null);
        this.ip = ip;
        super.setAuthenticated(false);//注意这个构造方法是认证时使用的
    }

    public IpAuthenticationToken(String ip, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.ip = ip;
        super.setAuthenticated(true);//注意这个构造方法是认证成功后使用的

    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.ip;
    }

}
```

两个构造方法需要引起我们的注意，这里设计的用意是模仿的UsernamePasswordAuthenticationToken，第一个构造器是用于认证之前，传递给认证器使用的，所以只有IP地址，自然是未认证；第二个构造器用于认证成功之后，封装认证用户的信息，此时需要将权限也设置到其中，并且setAuthenticated(true)。这样的设计在诸多的Token类设计中很常见。

### 5.4 IpAuthenticationProcessingFilter

```
public class IpAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    //使用/ipVerify该端点进行ip认证
    IpAuthenticationProcessingFilter() {
        super(new AntPathRequestMatcher("/ipVerify"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //获取host信息
        String host = request.getRemoteHost();
        //交给内部的AuthenticationManager去认证，实现解耦
        return getAuthenticationManager().authenticate(new IpAuthenticationToken(host));
    }
}
```

1. AbstractAuthenticationProcessingFilter这个过滤器在前面一节介绍过，是UsernamePasswordAuthenticationFilter的父类，我们的IpAuthenticationProcessingFilter也继承了它
2. 构造器中传入了/ipVerify作为IP登录的端点
3. attemptAuthentication()方法中加载请求的IP地址，之后交给内部的AuthenticationManager去认证

### 5.5 IpAuthenticationProvider

```
public class IpAuthenticationProvider implements AuthenticationProvider {
	final static Map<String, SimpleGrantedAuthority> ipAuthorityMap = new ConcurrenHashMap();
    //维护一个ip白名单列表，每个ip对应一定的权限
    static {
        ipAuthorityMap.put("127.0.0.1", new SimpleGrantedAuthority("ADMIN"));
        ipAuthorityMap.put("10.236.69.103", new SimpleGrantedAuthority("ADMIN"));
        ipAuthorityMap.put("10.236.69.104", new SimpleGrantedAuthority("FRIEND"));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IpAuthenticationToken ipAuthenticationToken = (IpAuthenticationToken) authentication;
        String ip = ipAuthenticationToken.getIp();
        SimpleGrantedAuthority simpleGrantedAuthority = ipAuthorityMap.get(ip);
        //不在白名单列表中
        if (simpleGrantedAuthority == null) {
            return null;
        } else {
            //封装权限信息，并且此时身份已经被认证
            return new IpAuthenticationToken(ip, Arrays.asList(simpleGrantedAuthority));
        }
    }

    //只支持IpAuthenticationToken该身份
    @Override
    public boolean supports(Class<?> authentication) {
        return (IpAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
}
```

`return new IpAuthenticationToken(ip, Arrays.asList(simpleGrantedAuthority));`使用了IpAuthenticationToken的第二个构造器，返回了一个已经经过认证的IpAuthenticationToken。

### 5.6 配置WebSecurityConfigAdapter

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //ip认证者配置
    @Bean
    IpAuthenticationProvider ipAuthenticationProvider() {
        return new IpAuthenticationProvider();
    }

    //配置封装ipAuthenticationToken的过滤器
    IpAuthenticationProcessingFilter ipAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        IpAuthenticationProcessingFilter ipAuthenticationProcessingFilter = new IpAuthenticationProcessingFilter();
        //为过滤器添加认证器
        ipAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        //重写认证失败时的跳转页面
        ipAuthenticationProcessingFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/ipLogin?error"));
        return ipAuthenticationProcessingFilter;
    }

    //配置登录端点
    @Bean
    LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint(){
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint
                ("/ipLogin");
        return loginUrlAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/ipLogin").permitAll()
                .anyRequest().authenticated()
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
            .exceptionHandling()
                .accessDeniedPage("/ipLogin")
                .authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
        ;

        //注册IpAuthenticationProcessingFilter  注意放置的顺序 这很关键
        http.addFilterBefore(ipAuthenticationProcessingFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ipAuthenticationProvider());
    }

}
```

WebSecurityConfigAdapter提供了我们很大的便利，不需要关注AuthenticationManager什么时候被创建，只需要使用其暴露的`configure(AuthenticationManagerBuilder auth)`便可以添加我们自定义的ipAuthenticationProvider。剩下的一些细节，注释中基本都写了出来。

### 5.7 配置SpringMVC

```
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/ip").setViewName("ipHello");
        registry.addViewController("/ipLogin").setViewName("ipLogin");

    }

}
```

页面的具体内容和表单登录基本一致，可以在文末的源码中查看。

### 5.8 运行效果

### 成功的流程

- `http://127.0.0.1:8080/`访问首页，其中here链接到的地址为：`http://127.0.0.1:8080/hello`

[![首页](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144410.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144410.png)首页

- 点击here，由于`http://127.0.0.1:8080/hello`是受保护资源，所以跳转到了校验IP的页面。此时若点击Sign In by IP按钮，将会提交到/ipVerify端点，进行IP的认证。

[![登录](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144520.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144520.png)登录

- 登录校验成功之后，页面被成功重定向到了原先访问的

[![受保护的hello页](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144800.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144800.png)受保护的hello页

### 失败的流程

- 注意此时已经注销了上次的登录，并且，使用了localhost(localhost和127.0.0.1是两个不同的IP地址，我们的内存中只有127.0.0.1的用户,没有localhost的用户)

[![首页](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144949.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002144949.png)首页

- 点击here后，由于没有认证过，依旧跳转到登录页面

  [![登录](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002145344.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002145344.png)登录

- 此时，我们发现使用localhost，并没有认证成功，符合我们的预期

[![认证失败](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002145209.png)](http://kirito.iocoder.cn/QQ%E5%9B%BE%E7%89%8720171002145209.png)认证失败

### 5.9 总结

一个简单的使用Spring Security来进行验证IP地址的登录demo就已经完成了，这个demo主要是为了更加清晰地阐释Spring Security内部工作的原理设置的，其本身没有实际的项目意义，认证IP其实也不应该通过Spring Security的过滤器去做，退一步也应该交给Filter去做（这个Filter不存在于Spring Security的过滤器链中），而真正项目中，如果真正要做黑白名单这样的功能，一般选择在网关层或者nginx的扩展模块中做。再次特地强调下，怕大家误解。



# 该如何设计你的 PasswordEncoder?

### 缘起

前端时间将一个集成了 spring-security-oauth2 的旧项目改造了一番，将 springboot 升级成了 springboot 2.0，众所周知 springboot 2.0 依赖的是 spring5，并且许多相关的依赖都发生了较大的改动，与本文相关的改动罗列如下，有兴趣的同学可以看看：[Spring Security 5.0 New Features](https://docs.spring.io/spring-security/site/docs/5.0.4.RELEASE/reference/htmlsingle/#new) ，增强了 oauth2 集成的功能以及和一个比较有意思的改动—重构了密码编码器的实现（Password Encoding，由于大多数 PasswordEncoder 相关的算法是 hash 算法，所以本文将 PasswordEncoder 翻译成‘密码编码器’和并非‘密码加密器’）官方称之为

[Modernized Password Encoding](https://docs.spring.io/spring-security/site/docs/5.0.4.RELEASE/reference/htmlsingle/#core-services-password-encoding) — 现代化的密码编码方式

另外，springboot2.0 的自动配置也做了一些调整，其中也有几点和 spring-security 相关，戳这里看所有细节 [springboot2.0 迁移指南](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide)

一开始，我仅仅修改了依赖，将

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.4.RELEASE</version>
</parent>
```

升级成了

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.1.RELEASE</version>
</parent>
```

不出意料出现了兼容性的问题，我在尝试登陆时，出现了如下的报错

```
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

原因也很明显，正如 spring security 的更新文档中描述的那样，spring security 5 对 PasswordEncoder 做了相关的重构，原先默认配置的 PlainTextPasswordEncoder（明文密码）被移除了。这引起了我的兴趣，spring security 在新版本中对于 passwordEncoder 进行了哪些改造，这些改造背后又是出于什么样的目的呢？卖个关子，先从远古时期的案例来一步步演化出所谓的“现代化密码编码方式”。

### 密码存储演进史

自从互联网有了用户的那一刻起，存储用户密码这件事便成为了一个健全的系统不得不面对的一件事。远古时期，明文存储密码可能还不被认为是一个很大的系统缺陷（事实上这是一件很恐怖的事）。提及明文存储密码，我立刻联想到的是 CSDN 社区在 2011 年末发生的 600 万用户密码泄露的事件，谁也不会想到这个和程序员密切相关的网站会犯如此低级的错误。明文存储密码使得恶意用户可以通过 sql 注入等攻击方式来获取用户名和密码，虽然安全框架和良好的编码规范可以规避很多类似的攻击，但依旧避免不了系统管理员，DBA 有途径获取用户密码这一事实。事实上，不用明文存储存储密码，程序员们早在 n 多年前就已经达成了共识。

不能明文存储，一些 hash 算法便被广泛用做密码的编码器，对密码进行单向 hash 处理后存储数据库，当用户登录时，计算用户输入的密码的 hash 值，将两者进行比对。单向 hash 算法，顾名思义，它无法（或者用不能轻易更为合适）被反向解析还原出原密码。这杜绝了管理员直接获取密码的途径，可仅仅依赖于普通的 hash 算法（如 md5，sha256）是不合适的，他主要有 3 个特点：

1. 同一密码生成的 hash 值一定相同
2. 不同密码的生成的 hash 值可能相同（md5 的碰撞问题相比 sha256 还要严重）
3. 计算速度快。

以上三点结合在一起，破解此类算法成了不是那么困难的一件事，尤其是第三点，会在下文中再次提到，多快才算非常快？按照相关资料的说法：

> modern hardware perform billions of hash calculations a second.

考虑到大多数用户使用的密码多为数字+字母+特殊符号的组合，攻击者将常用的密码进行枚举，甚至通过排列组合来暴力破解，这被称为 rainbow table。算法爱好者能够立刻看懂到上述的方案，这被亲切地称之为—打表，一种暴力美学，这张表是可以被复用的。

虽然仅仅依赖于传统 hash 算法的思路被否决了，但这种 hash 后比对的思路，几乎被后续所有的优化方案继承。

hash 方案迎来的第一个改造是对引入一个“随机的因子”来掺杂进明文中进行 hash 计算，这样的随机因子通常被称之为盐 （salt）。salt 一般是用户相关的，每个用户持有各自的 salt。此时狗蛋和二丫的密码即使相同，由于 salt 的影响，存储在数据库中的密码也是不同的，除非…为每个用户单独建议一张 rainbow table。很明显 salted hash 相比普通的单向 hash 方案加大了 hacker 攻击的难度。但了解过 GPU 并行计算能力之强大的童鞋，都能够意识到，虽然破解 salted hash 比较麻烦，却并非不可行，勤劳勇敢的安全专家似乎也对这个方案不够满意。

为解决上述 salted hash 仍然存在的问题，一些新型的单向 hash 算法被研究了出来。其中就包括：Bcrypt，PBKDF2，Scrypt，Argon2。为什么这些 hash 算法能保证密码存储的安全性？因为他们足够慢，恰到好处的慢。这么说不严谨，只是为了给大家留个深刻的映像：慢。这类算法有一个特点，存在一个影响因子，可以用来控制计算强度，这直接决定了破解密码所需要的资源和时间，直观的体会可以见下图，在一年内破解如下算法所需要的硬件资源花费（折算成美元）

[![一年内破解如下算法所需要的硬件资源花费](http://kirito.iocoder.cn/1_QdbniDuZiiF1N7ArNJChOA.png)](http://kirito.iocoder.cn/1_QdbniDuZiiF1N7ArNJChOA.png)一年内破解如下算法所需要的硬件资源花费

这使得破解成了一件极其困难的事，并且，其中的计算强度因子是可控的，这样，即使未来量子计算机的计算能力爆表，也可以通过其控制计算强度以防破解。注意，普通的验证过程只需要计算一次 hash 计算，使用此类 hash 算法并不会影响到用户体验。

### 慢 hash 算法真的安全吗？

Bcrypt，Scrypt，PBKDF2 这些慢 hash 算法是目前最为推崇的 password encoding 方式，好奇心驱使我思考了这样一个问题：慢 hash 算法真的安全吗？

我暂时还没有精力仔细去研究他们中每一个算法的具体实现，只能通过一些文章来拾人牙慧，简单看看这几个算法的原理和安全性。

PBKDF2 被设计的很简单，它的基本原理是通过一个伪随机函数（例如 HMAC 函数），把明文和一个盐值作为输入参数，然后按照设置的计算强度因子重复进行运算，并最终产生密钥。这样的重复 hash 已经被认为足够安全，但也有人提出了不同意见，此类算法对于传统的 CPU 来说的确是足够安全，但 GPU 被搬了出来，前文提到过 GPU 的并行计算能力非常强大。

Bcrypt 强大的一点在于，其不仅仅是 CPU 密集型，还是 RAM 密集型！双重的限制因素，导致 GPU，ASIC（专用集成电路）无法应对 Bcrypt 带来的破解困境。

然后…看了 Scrypt 的相关资料之后我才意识到这个坑有多深。一个熟悉又陌生的词出现在了我面前：FPGA（现场可编程逻辑门阵列），这货就比较厉害了。现成的芯片指令结构如传统的 CPU，GPU，ASIC 都无法破解 Bcrypt，但是 FPGA 支持烧录逻辑门（如AND、OR、XOR、NOT），通过编程的方式烧录指令集的这一特性使得可以定制硬件来破解 Bcrypt。尽管我不认为懂这个技术的人会去想办法破解真正的系统，但，只要这是一个可能性，就总有方法会被发明出来与之对抗。Scrypt 比 Bcrypt 额外考虑到的就是大规模的[自定义硬件攻击](https://zh.wikipedia.org/w/index.php?title=%E5%AE%A2%E8%A3%BD%E7%A1%AC%E9%AB%94%E6%94%BB%E6%93%8A&action=edit&redlink=1) ，从而刻意设计需要大量内存运算。

理论终归是理论，实际上 Bcrypt 算法被发明至今 18 年，使用范围广，且从未因为安全问题而被修改，其有限性是已经被验证过的，相比之下 Scrypt 据我看到的文章显示是 9 年的历史，没有 Bcrypt 使用的广泛。从破解成本和权威性的角度来看，Bcrypt 用作密码编码器是不错的选择。

### spring security 废弃的接口

回到文档中，spring security 5 对 PasswordEncoder 做了相关的重构，原先默认配置的 PlainTextPasswordEncoder（明文密码）被移除了，想要做到明文存储密码，只能使用一个过期的类来过渡

```
@Bean
PasswordEncoder passwordEncoder(){
    return NoOpPasswordEncoder.getInstance();
}
```

实际上，spring security 提供了 BCryptPasswordEncoder 来进行密码编码，并作为了相关配置的默认配置，只不过没有暴露为全局的 Bean。使用明文存储的风险在文章一开始就已经强调过，NoOpPasswordEncoder 只能存在于 demo 中。

```
@Bean
PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}
```

别忘了对你数据库中的密码进行同样的编码，否则无法对应。

### 更深层的思考

实际上，spring security 5 的另一个设计是促使我写成本文的初衷。

不知道有没有读者产生跟我相同的困扰：

1. 如果我要设计一个 QPS 很高的登录系统，使用 spring security 推荐的 BCrypt 会不会存在性能问题？
2. spring security 怎么这么坑，原来的密码编码器都给改了，我需要怎么迁移旧密码编码的应用程序？
3. 万一以后出了更高效的加密算法，这种笨重的硬编码方式配置密码编码器是不是不够灵活？

在 spring security 5 提供了这样一个思路，应该将密码编码之后的 hash 值和加密方式一起存储，并提供了一个 DelegatingPasswordEncoder 来作为众多密码密码编码方式的集合。

```
@Bean
PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

负责生产 DelegatingPasswordEncoder 的工厂方法：

```
public class PasswordEncoderFactories {

   public static PasswordEncoder createDelegatingPasswordEncoder() {
      String encodingId = "bcrypt";
      Map<String, PasswordEncoder> encoders = new HashMap<>();
      encoders.put(encodingId, new BCryptPasswordEncoder());
      encoders.put("ldap", new LdapShaPasswordEncoder());
      encoders.put("MD4", new Md4PasswordEncoder());
      encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
      encoders.put("noop", NoOpPasswordEncoder.getInstance());
      encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
      encoders.put("scrypt", new SCryptPasswordEncoder());
      encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
      encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
      encoders.put("sha256", new StandardPasswordEncoder());

      return new DelegatingPasswordEncoder(encodingId, encoders);
   }

   private PasswordEncoderFactories() {}
}
```

如此注入 PasswordEncoder 之后，我们在数据库中需要这么存储数据：

```
{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG 
{noop}password 
{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc 
{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=  
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0
```

还记得文章开始的报错吗？

```
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

这个 id 就是因为我们没有为数据库中的密码添加 {bcrypt} 此类的前缀导致的。

> 你会不会担心密码泄露后，{bcrypt}，{pbkdf2}，{scrypt}，{sha256} 此类前缀会直接暴露密码的编码方式？其实这个考虑是多余的，因为密码存储的依赖算法并不是一个秘密。大多数能搞到你密码的 hacker 都可以轻松的知道你用的是什么算法，例如，bcrypt 算法通常以 \$2a$ 开头

稍微思考下，前面的三个疑问就可以迎刃而解，这就是文档中所谓的：**能够自适应服务器性能的现代化密码编码方案**。

### 参考

[Password Hashing: PBKDF2, Scrypt, Bcrypt](https://medium.com/@mpreziuso/password-hashing-pbkdf2-scrypt-bcrypt-1ef4bb9c19b3)

[core-services-password-encoding](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#core-services-password-encoding)

### show me the code

spring security oauth2 的 github 代码示例，体会下 spring security 4 -> spring security 5 的相关变化。

<https://github.com/lexburner/oauth2-demo>



# Spring Security(六)—SpringSecurityFilterChain加载流程深度解析

SpringSecurityFilterChain 作为 SpringSecurity 的核心过滤器链在整个认证授权过程中起着举足轻重的地位，每个请求到来，都会经过该过滤器链，前文[《Spring Security(四)–核心过滤器源码分析》](https://www.cnkirito.moe/spring-security-4/) 中我们分析了 SpringSecurityFilterChain 的构成，但还有很多疑问可能没有解开：

1. 这个 SpringSecurityFilterChain 是怎么注册到 web 环境中的？
2. 有读者发出这样的疑问：”SpringSecurityFilterChain 的实现类到底是什么，我知道它是一个 Filter，但是在很多配置类中看到了 BeanName=SpringSecurityFilterChain 相关的类，比如 DelegatingFilterProxy，FilterChainProxy，SecurityFilterChain，他们的的名称实在太相似了，到底哪个才是真正的实现，SpringSecurity 又为什么要这么设计？“
3. 我们貌似一直在配置 WebSecurity ，但没有对 SpringSecurityFilterChain 进行什么配置，WebSecurity 相关配置是怎么和 SpringSecurityFilterChain 结合在一起的？

以上是个人 YY 的一些 SpringSecurityFilterChain 相关的问题，因为我当初研究了一段时间 SpringSecurity 源码，依旧没有理清这么多错综复杂的类。那么本文就主要围绕 SpringSecurityFilterChain 展开我们的探索。

### 6.1 SpringSecurityFilterChain是怎么注册的？

这个问题并不容易解释，因为 SpringSecurity 仅仅在 web 环境下（SpringSecurity 还支持非 web 环境）就有非常多的支持形式：

**Java 配置方式**

1. 作为独立的 SpringSecurity 依赖提供给朴素的 java web 项目使用，并且项目不使用 Spring！没错，仅仅使用 servlet，jsp 的情况下也是可以集成 SpringSecurity 的。
2. 提供给包含 SpringMVC 项目使用。
3. 提供给具备 Servlet3.0+ 的 web 项目使用。
4. SpringBoot 内嵌容器环境下使用 SpringSecurity，并且包含了一定程度的自动配置。

**XML 配置方式**

1. 使用 XML 中的命名空间配置 SpringSecurity。

注意，以上条件可能存在交集，比如我的项目是一个使用 servlet3.0 的 web 项目同时使用了 SpringMVC；也有可能使用了 SpringBoot 同时配合 SpringMVC；还有可能使用了 SpringBoot，却打成了 war 包，部署在外置的支持 Servlet3.0+ 规范的应用容器中…各种组合方式会导致配置 SpringSecurityFilterChain 的注册方式产生差异，所以，这个问题说复杂还真有点，需要根据你的环境来分析。我主要分析几种较为常见的注册方式。

SpringSecurityFilterChain 抽象概念里最重要的三个类：DelegatingFilterProxy，FilterChainProxy 和 SecurityFilterChain，对这三个类的源码分析和设计将会贯彻本文。不同环境下 DelegatingFilterProxy 的注册方式区别较大，但 FilterChainProxy 和 SecurityFilterChain 的差异不大，所以重点就是分析 DelegatingFilterProxy 的注册方式。它们三者的分析会放到下一节中。

#### 6.1.1 servlet3.0+环境下SpringSecurity的java config方式

这是一个比较常见的场景，你可能还没有使用 SpringBoot 内嵌的容器，将项目打成 war 包部署在外置的应用容器中，比如最常见的 tomcat，一般很少 web 项目低于 servlet3.0 版本的，并且该场景摒弃了 XML 配置。

```
import org.springframework.security.web.context.*;

public class SecurityWebApplicationInitializer
	extends AbstractSecurityWebApplicationInitializer {

}
```

主要自定义一个 SecurityWebApplicationInitializer 并且让其继承自 AbstractSecurityWebApplicationInitializer 即可。如此简单的一个继承背后又经历了 Spring 怎样的封装呢？自然要去 AbstractSecurityWebApplicationInitializer 中去一探究竟。经过删减后的源码如下

```
public abstract class AbstractSecurityWebApplicationInitializer
      implements WebApplicationInitializer {//<1>

   public static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";

   // <1> 父类WebApplicationInitializer的加载入口
   public final void onStartup(ServletContext servletContext) throws ServletException {
      beforeSpringSecurityFilterChain(servletContext);
      if (this.configurationClasses != null) {
         AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
         rootAppContext.register(this.configurationClasses);
         servletContext.addListener(new ContextLoaderListener(rootAppContext));
      }
      if (enableHttpSessionEventPublisher()) {
         servletContext.addListener(
               "org.springframework.security.web.session.HttpSessionEventPublisher");
      }
      servletContext.setSessionTrackingModes(getSessionTrackingModes());
      insertSpringSecurityFilterChain(servletContext);//<2>
      afterSpringSecurityFilterChain(servletContext);
   }
   
    // <2> 在这儿初始化了关键的DelegatingFilterProxy
    private void insertSpringSecurityFilterChain(ServletContext servletContext) {
		String filterName = DEFAULT_FILTER_NAME;
        // <2> 该方法中最关键的一个步骤，DelegatingFilterProxy在此被创建
		DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy(
				filterName);
		String contextAttribute = getWebApplicationContextAttribute();
		if (contextAttribute != null) {
			springSecurityFilterChain.setContextAttribute(contextAttribute);
		}
		registerFilter(servletContext, true, filterName, springSecurityFilterChain);
	}
    
    // <3> 使用servlet3.0的新特性，动态注册springSecurityFilterChain(实际上注册的是springSecurityFilterChain代理类)
    private final void registerFilter(ServletContext servletContext,
			boolean insertBeforeOtherFilters, String filterName, Filter filter) {
		Dynamic registration = servletContext.addFilter(filterName, filter);
		registration.setAsyncSupported(isAsyncSecuritySupported());
		EnumSet<DispatcherType> dispatcherTypes = getSecurityDispatcherTypes();
		registration.addMappingForUrlPatterns(dispatcherTypes, !insertBeforeOtherFilters,
				"/*");
	}

}
```

<1><3> 放在一起讲，因为他们都和 servlet3.0 新特性以及 spring 对 servlet3.0 的支持相关，这也是为什么在场景描述中我特地强调了需要 servlet3.0 环境。如果你对 servlet3.0 的新特性不了解，这儿准备了一篇详细的介绍为你阐述[《Spring揭秘–寻找遗失的web.xml》](https://www.cnkirito.moe/servlet-explore/)。得益于 Spring 的封装，在 servlet3.0 环境下，web 容器启动时会自行去寻找类路径下所有实现了 WebApplicationInitializer 接口的 Initializer 实例，并调用他们的 onStartup 方法。所以，我们只需要继承 AbstractSecurityWebApplicationInitializer ，便可以自动触发 web 容器的加载，进而配置和 SpringSecurityFilterChain 第一个密切相关的类，第<2>步中的 DelegatingFilterProxy。

<2> DelegatingFilterProxy 在此被实例化出来。在第<3>步中，它作为一个 Filter 正式注册到了 web 容器中。

#### 6.1.2 XML 配置

这个真的是简单易懂，因为它是被指名道姓配置成一个 Filter 的。

```
web.xml
<filter>
	<filter-name>springSecurityFilterChain</filter-name>
	<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>

<filter-mapping>
	<filter-name>springSecurityFilterChain</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

`web.xml` 的存在注定了其无所谓当前环境是不是 servlet3.0+，虽然我个人不太喜欢 xml 的配置方式，但不得不说，这样真的很简单粗暴。

#### 6.1.3 SpringBoot 内嵌应用容器并且使用自动配置

[《Spring揭秘–寻找遗失的web.xml》](https://www.cnkirito.moe/servlet-explore/)中我曾经得出一个结论，内嵌容器是完全不会使用 SPI 机制加载 servlet3.0 新特性的那些 Initializer 的，springboot 又推崇 java configuration，所以上述两种方案完全被抛弃了。那么 SpringBoot 如何注册 DelegatingFilterProxy 呢？

```
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties
@ConditionalOnClass({ AbstractSecurityWebApplicationInitializer.class,
      SessionCreationPolicy.class })
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class SecurityFilterAutoConfiguration {

   private static final String DEFAULT_FILTER_NAME = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;//springSecurityFilterChain

    // <1>
   @Bean
   @ConditionalOnBean(name = DEFAULT_FILTER_NAME)
   public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration(
         SecurityProperties securityProperties) {
      DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(
            DEFAULT_FILTER_NAME);
      registration.setOrder(securityProperties.getFilterOrder());
      registration.setDispatcherTypes(getDispatcherTypes(securityProperties));
      return registration;
   }

   @Bean
   @ConditionalOnMissingBean
   public SecurityProperties securityProperties() {
      return new SecurityProperties();
   }
}
```

<1> DelegatingFilterProxyRegistrationBean 的分析在之前那篇文章中也有详细的介绍，其作用便是在 SpringBoot 环境下通过 TomcatStarter 等内嵌容器启动类来注册一个 DelegatingFilterProxy。这下，和前面两种配置方式都对应上了。

\###SpringSecurityFilterChain三个核心类的源码分析

理解 SpringSecurityFilterChain 的工作流程必须搞懂三个类：`org.springframework.web.filter.DelegatingFilterProxy`，`org.springframework.security.web.FilterChainProxy` ， `org.springframework.security.web.SecurityFilterChain`

#### DelegatingFilterProxy

上面一节主要就是介绍 DelegatingFilterProxy 在不同环境下的注册方式，可以很明显的发现，DelegatingFilterProxy 是 SpringSecurity 的“门面”，注意它的包结构：org.springframework.web.filter，它本身是 Spring Web 包中的类，并不是 SpringSecurity 中的类。因为 Spring 考虑到了多种使用场景，自然希望将侵入性降到最低，所以使用了这个委托代理类来代理真正的 SpringSecurityFilterChain。DelegatingFilterProxy 实现了 javax.servlet.Filter 接口，使得它可以作为一个 java web 的标准过滤器，其职责也很简单，只负责调用真正的 SpringSecurityFilterChain。

删减掉非重要代码后的 DelegatingFilterProxy：

```
public class DelegatingFilterProxy extends GenericFilterBean {

   private WebApplicationContext webApplicationContext;
   // springSecurityFilterChain
   private String targetBeanName;
   // <1> 关键点
   private volatile Filter delegate;
   private final Object delegateMonitor = new Object();

   public DelegatingFilterProxy(String targetBeanName, WebApplicationContext wac) {
      Assert.hasText(targetBeanName, "Target Filter bean name must not be null or empty");
      this.setTargetBeanName(targetBeanName);
      this.webApplicationContext = wac;
      if (wac != null) {
         this.setEnvironment(wac.getEnvironment());
      }
   }

   @Override
   protected void initFilterBean() throws ServletException {
      synchronized (this.delegateMonitor) {
         if (this.delegate == null) {
            if (this.targetBeanName == null) {
               this.targetBeanName = getFilterName();
            }
            // Fetch Spring root application context and initialize the delegate early,
            // if possible. If the root application context will be started after this
            // filter proxy, we'll have to resort to lazy initialization.
            WebApplicationContext wac = findWebApplicationContext();
            if (wac != null) {
               this.delegate = initDelegate(wac);
            }
         }
      }
   }

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {

      // 过滤器代理支持懒加载
      Filter delegateToUse = this.delegate;
      if (delegateToUse == null) {
         synchronized (this.delegateMonitor) {
            delegateToUse = this.delegate;
            if (delegateToUse == null) {
               WebApplicationContext wac = findWebApplicationContext();
               delegateToUse = initDelegate(wac);
            }
            this.delegate = delegateToUse;
         }
      }

      // 让代理过滤器执行实际的过滤行为
      invokeDelegate(delegateToUse, request, response, filterChain);
   }

   // 初始化过滤器代理
   // <2>
   protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
      Filter delegate = wac.getBean(getTargetBeanName(), Filter.class);
      if (isTargetFilterLifecycle()) {
         delegate.init(getFilterConfig());
      }
      return delegate;
   }


   // 调用代理过滤器	
   protected void invokeDelegate(
         Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {
      delegate.doFilter(request, response, filterChain);
   }

}
```

<1> 可以发现整个 DelegatingFilterProxy 的逻辑就是为了调用 `private volatile Filter delegate;`那么问题来了，这个 delegate 的真正实现是什么呢？

<2> 可以看到，DelegatingFilterProxy 尝试去容器中获取名为 targetBeanName 的类，而 targetBeanName 的默认值便是 Filter 的名称，也就是 springSecurityFilterChain！也就是说，DelegatingFilterProxy 只是名称和 targetBeanName 叫 springSecurityFilterChain，真正容器中的 Bean(name=”springSecurityFilterChain”) 其实另有其人（这里springboot稍微有点区别，不过不影响理解，我们不纠结这个细节了）。通过 debug，我们发现了真正的 springSecurityFilterChain — FilterChainProxy。

[![delegate](http://kirito.iocoder.cn/C811CC2A-9434-49C8-9240-15BD0EE5A21E.png)](http://kirito.iocoder.cn/C811CC2A-9434-49C8-9240-15BD0EE5A21E.png)delegate

#### FilterChainProxy和SecurityFilterChain

`org.springframework.security.web.FilterChainProxy` 已经是 SpringSecurity 提供的类了，原来它才是真正的 springSecurityFilterChain，我们来看看它的源码（有删减，不影响理解）。

```
public class FilterChainProxy extends GenericFilterBean {
   // <1> 包含了多个SecurityFilterChain
   private List<SecurityFilterChain> filterChains;

   public FilterChainProxy(SecurityFilterChain chain) {
      this(Arrays.asList(chain));
   }

   public FilterChainProxy(List<SecurityFilterChain> filterChains) {
      this.filterChains = filterChains;
   }

   @Override
   public void afterPropertiesSet() {
      filterChainValidator.validate(this);
   }

   public void doFilter(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException {
         doFilterInternal(request, response, chain);
   }

   private void doFilterInternal(ServletRequest request, ServletResponse response,
         FilterChain chain) throws IOException, ServletException {

      FirewalledRequest fwRequest = firewall
            .getFirewalledRequest((HttpServletRequest) request);
      HttpServletResponse fwResponse = firewall
            .getFirewalledResponse((HttpServletResponse) response);
	  // <1>	
      List<Filter> filters = getFilters(fwRequest);

      if (filters == null || filters.size() == 0) {
         fwRequest.reset();
         chain.doFilter(fwRequest, fwResponse);
         return;
      }

      VirtualFilterChain vfc = new VirtualFilterChain(fwRequest, chain, filters);
      vfc.doFilter(fwRequest, fwResponse);
   }

   /**
    * <1> 可能会有多个过滤器链，返回第一个和请求URL匹配的过滤器链
    */
   private List<Filter> getFilters(HttpServletRequest request) {
      for (SecurityFilterChain chain : filterChains) {
         if (chain.matches(request)) {
            return chain.getFilters();
         }
      }
      return null;
   }

}
```

看 FilterChainProxy 的名字就可以发现，它依旧不是真正实施过滤的类，它内部维护了一个 SecurityFilterChain，这个过滤器链才是请求真正对应的过滤器链，并且同一个 Spring 环境下，可能同时存在多个安全过滤器链，如 private List filterChains 所示，需要经过 chain.matches(request) 判断到底哪个过滤器链匹配成功，每个 request 最多只会经过一个 SecurityFilterChain。为何要这么设计？因为 Web 环境下可能有多种安全保护策略，每种策略都需要有自己的一条链路，比如我曾经设计过 Oauth2 服务，在极端条件下，可能同一个服务本身既是资源服务器，又是认证服务器，还需要做 Web 安全！

[![多个SecurityFilterChain](http://kirito.iocoder.cn/F0EAD340-B206-4FB0-A660-4CEB28AB8609.png)](http://kirito.iocoder.cn/F0EAD340-B206-4FB0-A660-4CEB28AB8609.png)多个SecurityFilterChain

如上图，4 个 SecurityFilterChain 存在于 FilterChainProxy 中，值得再次强调：实际每次请求，最多只有一个安全过滤器链被返回。

SecurityFilterChain 才是真正意义上的 SpringSecurityFilterChain：

```
public final class DefaultSecurityFilterChain implements SecurityFilterChain {
   private final RequestMatcher requestMatcher;
   private final List<Filter> filters;

   public List<Filter> getFilters() {
      return filters;
   }

   public boolean matches(HttpServletRequest request) {
      return requestMatcher.matches(request);
   }
}
```

其中的 List filters 就是我们在 [《Spring Security(四)–核心过滤器源码分析》](https://www.cnkirito.moe/spring-security-4/) 中分析的诸多核心过滤器，包含了 UsernamePasswordAuthenticationFilter，SecurityContextPersistenceFilter，FilterSecurityInterceptor 等之前就介绍过的 Filter。

\###SecurityFilterChain的注册过程

还记得 DelegatingFilterProxy 从 Spring 容器中寻找了一个 targetBeanName=springSecurityFilterChain 的 Bean 吗？我们通过 debug 直接定位到了其实现是 SecurityFilterChain，但它又是什么时候被放进去的呢？

这就得说到老朋友 WebSecurity 了，还记得一般我们都会选择使用 @EnableWebSecurity 和 WebSecurityConfigurerAdapter 来进行 web 安全配置吗，来到 WebSecurity 的源码：

```
public final class WebSecurity extends
      AbstractConfiguredSecurityBuilder<Filter, WebSecurity> implements
      SecurityBuilder<Filter>, ApplicationContextAware {
    
    @Override
	protected Filter performBuild() throws Exception {
		int chainSize = ignoredRequests.size() + securityFilterChainBuilders.size();
		List<SecurityFilterChain> securityFilterChains = new ArrayList<SecurityFilterChain>(
				chainSize);
		for (RequestMatcher ignoredRequest : ignoredRequests) {
			securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest));
		}
		for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : securityFilterChainBuilders) {
			securityFilterChains.add(securityFilterChainBuilder.build());
		}
        // <1> FilterChainProxy 由 WebSecurity 构建
		FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
		if (httpFirewall != null) {
			filterChainProxy.setFirewall(httpFirewall);
		}
		filterChainProxy.afterPropertiesSet();

		Filter result = filterChainProxy;
		postBuildAction.run();
		return result;
	}
}
```

<1> 最终定位到 WebSecurity 的 performBuild 方法，我们之前配置了一堆参数的 WebSecurity 最终帮助我们构建了 FilterChainProxy。

[![WebSecurityConfiguration](http://kirito.iocoder.cn/8E09B17E-EC83-4824-9ED9-AF2814AC6B3A.png)](http://kirito.iocoder.cn/8E09B17E-EC83-4824-9ED9-AF2814AC6B3A.png)WebSecurityConfiguration

并且，最终在 `org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration`中被注册为默认名称为 SpringSecurityFilterChain。

### 总结

一个名称 SpringSecurityFilterChain，借助于 Spring 的 IOC 容器，完成了 DelegatingFilterProxy 到 FilterChainProxy 的连接，并借助于 FilterChainProxy 内部维护的 List 中的某一个 SecurityFilterChain 来完成最终的过滤。

