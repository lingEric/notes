# spring security之httpSecurity使用示例

原文地址：<https://www.cnblogs.com/davidwang456/p/4549344.html>

httpSecurity

   类似于spring security的xml配置文件命名空间配置中的<http>元素。它允许对特定的http请求基于安全考虑进行配置。默认情况下，适用于所有的请求，但可以使用requestMatcher(RequestMatcher)或者其它相似的方法进行限制。

## 最简示例

最基本的基于表单的配置如下。该配置将所有的url访问权限设定为角色名称为"ROLE_USER".同时也定义了内存认证模式：使用用户名"user"和密码“password”,角色"ROLE_USER"来认证。

```java
@Configuration
@EnableWebSecurity
public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
				//基于角色配置
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }
}
```


##  配置基于openId的认证方式

 basic示例，不使用attribute exchange

```java
@Configuration
@EnableWebSecurity
public class OpenIDLoginConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .openidLogin()
			  .permitAll();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  auth
			  .inMemoryAuthentication()
				  // the username must match the OpenID of the user you are
				  // logging in with
				  .withUser("https://www.google.com/accounts/o8/id?id=lmkCn9xzPdsxVwG7pjYMuDgNNdASFmobNkcRPaWU")
					  .password("password")
					  .roles("USER");
	}
}
```

### 使用attribute exchange

下面展示一个更高级的示例，使用attribute exchange

```java
@Configuration
@EnableWebSecurity
public class OpenIDLoginConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .openidLogin()
			  .loginPage("/login")
			  .permitAll()
			  .authenticationUserDetailsService(new AutoProvisioningUserDetailsService())
				  .attributeExchange("https://www.google.com/.")
					  .attribute("email")
						  .type("http://axschema.org/contact/email")
						  .required(true)
						  .and()
					  .attribute("firstname")
						  .type("http://axschema.org/namePerson/first")
						  .required(true)
						  .and()
					  .attribute("lastname")
						  .type("http://axschema.org/namePerson/last")
						  .required(true)
						  .and()
					  .and()
				  .attributeExchange(".yahoo.com.")
					  .attribute("email")
						  .type("http://schema.openid.net/contact/email")
						  .required(true)
						  .and()
					  .attribute("fullname")
						  .type("http://axschema.org/namePerson")
						  .required(true)
						  .and()
					  .and()
				  .attributeExchange(".myopenid.com.")
					  .attribute("email")
						  .type("http://schema.openid.net/contact/email")
						  .required(true)
						  .and()
					  .attribute("fullname")
						  .type("http://schema.openid.net/namePerson")
						  .required(true);
  }
}

public class AutoProvisioningUserDetailsService implements
	  AuthenticationUserDetailsService<OpenIDAuthenticationToken>; {
  public UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {
	  return new User(token.getName(), "NOTUSED", AuthorityUtils.createAuthorityList("ROLE_USER"));
  }
}
```



## 增加响应安全报文头

默认情况下当使用WebSecuirtyConfigAdapter的默认构造函数时激活。

仅触发Headers()方法而不触发其它方法或者接受WebSecurityConfigureerAdater默认的，等同于：

```java
@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http
		  .headers()
			  .contentTypeOptions();
			  .xssProtection()
			  .cacheControl()
			  .httpStrictTransportSecurity()
			  .frameOptions()
			  .and()
		  ...;
	}
}
```



### 取消安全报文头



```java
@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.headers().disable()
        	...;
    }
}
```



### 使用部分安全报文头

触发headers()方法的返回结果，例如，只使用HeaderConfigurer的cacheControll()方法和HeadersConfigurer的frameOptions()方法.



```java
@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .headers()
            .cacheControl()
            .frameOptions()
            .and()
            ...;
    }
}
```



## 配置session管理

下面的配置展示了只允许认证用户在同一时间只有一个实例是如何配置的。若一个用户使用用户名为"user"认证并且没有退出，同一个名为“user”的试图再次认证时，第一个用户的session将会强制销毁，并设置到"/login?expired"的url。

```java
@Configuration
@EnableWebSecurity
public class SessionManagementSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http
		 .authorizeRequests()
			  .anyRequest().hasRole("USER")
			  .and()
		 .formLogin()
			  .permitAll()
			  .and()
		 .sessionManagement()
			  .maximumSessions(1)
			  .expiredUrl("/login?expired");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth.
		  inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
	}
}
```



当使用SessionManagementConfigurer的maximumSessions(int)时不要忘记为应用配置HttpSessionEventPublisher，这样能保证过期的session能够被清除。

在web.xml中可以这样配置：

```
      <listener>
           <listener-class>org.springframework.security.web.session.HttpSessionEventPublisher</listener-class>;
      </listener>
```

## 配置PortMapper

允许配置一个从HttpSecurity的getSharedObject(Class)方法中获取的PortMapper。当http请求跳转到https或者https请求跳转到http请求时(例如我们和requiresChanenl一起使用时)，别的提供的SecurityConfigurer对象使用P诶账户的PortMapper作为默认的PortMapper。默认情况下，spring security使用PortMapperImpl来映射http端口8080到https端口8443，并且将http端口的80映射到https的端口443.

配置示例如下，下面的配置将确保在spring security中的http请求端口9090跳转到https端口9443 并且将http端口80跳转到https443端口。

```java
@Configuration
@EnableWebSecurity
public class PortMapperSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin()
			  .permitAll()
			  .and()
			  // Example portMapper() configuration
			  .portMapper()
				  .http(9090).mapsTo(9443)
				  .http(80).mapsTo(443);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	  auth
		  .inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
  }
}
```



## 配置基于容器的预认证

在这个场景中，servlet容器管理认证。

配置示例：

下面的配置使用HttpServletRequest中的principal，若用户的角色是“ROLE_USER”或者"ROLE_ADMIN"，将会返回Authentication结果。

```java
@Configuration
@EnableWebSecurity
public class JeeSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  // Example jee() configuration
		  .jee()
			  .mappableRoles("ROLE_USER", "ROLE_ADMIN");
	}
}
```

开发者希望使用基于容器预认证时，需要在web.xml中配置安全限制。例如：

```xml
	
<login-config>
    <auth-method>FORM</auth-method>
    <form-login-config>
        <form-login-page>/login</form-login-page>
        <form-error-page>/login?error</form-error-page>
    </form-login-config>
</login-config>

<security-role>
    <role-name>ROLE_USER</role-name>
</security-role>

<security-constraint>
    <web-resource-collection>
        <web-resource-name>Public</web-resource-name>
        <description>Matches unconstrained pages</description>
        <url-pattern>/login</url-pattern>
        <url-pattern>/logout</url-pattern>
        <url-pattern>/resources/</url-pattern>
    </web-resource-collection>
</security-constraint>
<security-constraint>
    <web-resource-collection>
        <web-resource-name>Secured Areas</web-resource-name>
        <url-pattern>/</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>ROLE_USER</role-name>
    </auth-constraint>
</security-constraint>
```



## 配置基于X509的预认证

配置示例，下面的配置试图从X509证书中提取用户名，注意，为完成这个工作，客户端请求证书需要配置到servlet容器中。

```java
@Configuration
@EnableWebSecurity
public class X509SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  // Example x509() configuration
		  .x509();
  }
}     
```



##　配置Remember-me服务

配置示例，下面的配置展示了如何允许基于token的remember-me的认证。若http参数中包含一个名为“remember-me”的参数，不管session是否过期，用户记录将会被记保存下来。

```java
@Configuration
@EnableWebSecurity
public class RememberMeSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin()
			  .permitAll()
			  .and()
		   // Example Remember Me Configuration
		  .rememberMe();
  }
}
```



## 限制HttpServletRequest的请求访问

配置示例，最基本的示例是配置所有的url访问都需要角色"ROLE_USER".下面的配置要求每一个url的访问都需要认证，并且授权访问权限给用户"admin"和"user".

```Java
@Configuration
@EnableWebSecurity
public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER")
					 .and()
				.withUser("adminr")
					 .password("password")
					 .roles("ADMIN","USER");
	}
}
```

同样，也可以配置多个url。下面的配置要求以/admin/开始的url访问权限为“admin”用户。

```java
@Configuration
@EnableWebSecurity
public class AuthorizeUrlsSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/admin/**").hasRole("ADMIN")
			  .antMatchers("/**").hasRole("USER")
			  .and()
		  .formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER")
					 .and()
				.withUser("adminr")
					 .password("password")
					 .roles("ADMIN","USER");
  }
}
```

注意：匹配起效是按照顺序来的。因此如果下面的配置是无效的，因为满足第一个规则后将不会检查第二条规则：

```Java
http
  .authorizeRequests()
	  .antMatchers("/**").hasRole("USER")
	  .antMatchers("/admin/**").hasRole("ADMIN")；
```

## 增加CSRF支持

默认情况下，当使用WebSecurityConfigurerAdapter时的默认构造方法时CSRF是激活的。你可以使用如下方法关闭它：

```Java
@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .csrf().disable()
		  ...;
  }
}
```



## 增加logout支持

默认支持，当使用WebSecurityConfigurerAdapter时Logout是支持的。当用户发出“/logout”请求时，系统将会销毁session并且清空配置的rememberMe()认证，然后清除SecurityContextHolder，最后跳向logout成功页面或者登陆页面。

```Java
@Configuration
@EnableWebSecurity
public class LogoutSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin()
			  .and()
		  // sample logout customization
		  .logout()
			  .logout()
				 .deleteCookies("remove")
				 .invalidateHttpSession(false)
				 .logoutUrl("/custom-logout")
				 .logoutSuccessUrl("/logout-success");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }
}
```



## 匿名用户控制

使用WebSecurityConfigurerAdapter时自动绑定。默认情况下，匿名用户有一个AnonymousAuthenticationToken标示，包含角色"ROLE_ANONYMOUS"。

下面的配置展示了如何指定匿名用户应该包含"ROLE_ANON".

```Java
@Configuration
@EnableWebSecurity
public class AnononymousSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin()
			  .and()
		  // sample anonymous customization
		  .anonymous()
			  .authorities("ROLE_ANON");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
	  throws Exception {
		auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
	}
}
```



## 基于表单的认证

若FormLoginConfigurer的loginpage（String）没有指定，将会产生一个默认的login页面。

示例配置：

```Java
@Configuration
@EnableWebSecurity
public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER")
			  .and()
		  .formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }
}
```

下面的示例展示了自定义的表单认证：

```Java
@Configuration
@EnableWebSecurity
public class FormLoginSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/").hasRole("USER")
			  .and()
		  .formLogin()
				 .usernameParameter("j_username") // default is username
				 .passwordParameter("j_password") // default is password
				 .loginPage("/authentication/login") // default is /login with an HTTP get
				 .failureUrl("/authentication/login?failed") // default is /login?error
				 .loginProcessingUrl("/authentication/login/process"); // default is /login with an HTTP post
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }
}
```

## 配置安全通道

为使配置生效，需至少配置一个通道的映射。

配置示例：

下面例子展示了如何将每个请求都使用https通道。

```Java
@Configuration
@EnableWebSecurity
public class ChannelSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER")
			  .and()
		  .formLogin()
			  .and()
		  .channelSecurity()
			  .anyRequest().requiresSecure();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		   .inMemoryAuthentication()
				.withUser("user")
					 .password("password")
					 .roles("USER");
  }
}
```

## 配置http 基本认证

配置示例：

```Java
@Configuration
@EnableWebSecurity
public class HttpBasicSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER").and()
			  .httpBasic();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		  .inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
  }
}
```



配置要触发的HttpRequest

重写RequestMatcher方法、antMatcher()、regexMatcher()等。

配置示例

下面的配置使HttpSecurity接收以"/api/","/oauth/"开头请求。



```Java
@Configuration
@EnableWebSecurity
public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .requestMatchers()
			  .antMatchers("/api/**","/oauth/**")
			  .and()
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER").and()
			  .httpBasic();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		  .inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
  }
}
```



下面的配置和上面的相同：



```Java
@Configuration
@EnableWebSecurity
public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .requestMatchers()
			  .antMatchers("/api/**")
			  .antMatchers("/oauth/**")
			  .and()
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER").and()
			  .httpBasic();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		  .inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
  }
}
```

同样也可以这样使用：

```Java
@Configuration
@EnableWebSecurity
public class RequestMatchersSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  http
		  .requestMatchers()
			  .antMatchers("/api/**")
			  .and()
		  .requestMatchers()
			  .antMatchers("/oauth/**")
			  .and()
		  .authorizeRequests()
			  .antMatchers("/**").hasRole("USER").and()
			  .httpBasic();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth)
		  throws Exception {
	  auth
		  .inMemoryAuthentication()
			  .withUser("user")
				  .password("password")
				  .roles("USER");
  }
}
```