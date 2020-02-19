# SpringSecurityFilterChain三个核心类的源码分析

理解 SpringSecurityFilterChain 的工作流程必须搞懂三个类：`org.springframework.web.filter.DelegatingFilterProxy`，`org.springframework.security.web.FilterChainProxy` ， `org.springframework.security.web.SecurityFilterChain`

## DelegatingFilterProxy

上面一节主要就是介绍 DelegatingFilterProxy 在不同环境下的注册方式，可以很明显的发现，DelegatingFilterProxy 是 SpringSecurity 的“门面”，注意它的包结构：org.springframework.web.filter，它本身是 Spring Web 包中的类，并不是 SpringSecurity 中的类。因为 Spring 考虑到了多种使用场景，自然希望将侵入性降到最低，所以使用了这个委托代理类来代理真正的 SpringSecurityFilterChain。DelegatingFilterProxy 实现了 javax.servlet.Filter 接口，使得它可以作为一个 java web 的标准过滤器，其职责也很简单，只负责调用真正的 SpringSecurityFilterChain。

删减掉非重要代码后的 DelegatingFilterProxy：

```java
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

## FilterChainProxy和SecurityFilterChain

`org.springframework.security.web.FilterChainProxy` 已经是 SpringSecurity 提供的类了，原来它才是真正的 springSecurityFilterChain，我们来看看它的源码（有删减，不影响理解）。

```java
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

## SecurityFilterChain的注册过程

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

## 总结

一个名称 SpringSecurityFilterChain，借助于 Spring 的 IOC 容器，完成了 DelegatingFilterProxy 到 FilterChainProxy 的连接，并借助于 FilterChainProxy 内部维护的 List 中的某一个 SecurityFilterChain 来完成最终的过滤。