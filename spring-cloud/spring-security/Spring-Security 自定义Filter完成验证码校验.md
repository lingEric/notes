# Spring-Security 自定义Filter完成验证码校验



　Spring-Security的功能主要是由一堆Filter构成过滤器链来实现，每个Filter都会完成自己的一部分工作。我今天要做的是对UsernamePasswordAuthenticationFilter进行扩展，新增一个Filter，完成对登录页面的校验码的验证。下面先给一张过滤器的说明，接下来讲自定义的登录验证Filter。

​    <https://docs.spring.io/spring-security/site/docs/3.2.8.RELEASE/reference/htmlsingle/#ns-web-advanced>

   ![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213140425894-1019207698.png)

   **一、扩展AbstractAuthenticationProcessingFilter，实现MyUsernamePasswordAuthenticationFilter。**



```
package simm.spring.web.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter  {
     // 是否开启验证码功能
    private boolean isOpenValidateCode = true;

    public static final String VALIDATE_CODE = "validateCode";
    
    public MyUsernamePasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher("/user/login.do", "POST"));
        SimpleUrlAuthenticationFailureHandler failedHandler = (SimpleUrlAuthenticationFailureHandler)getFailureHandler();
        failedHandler.setDefaultFailureUrl("/user/login.do?validerror");
    }
    
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  
        HttpServletRequest req = (HttpServletRequest) request;  
        HttpServletResponse res=(HttpServletResponse)response;
        
        if (!requiresAuthentication(req, res)) {
            chain.doFilter(request, response);
            return;
        }
        if (isOpenValidateCode) {
            if(!checkValidateCode(req, res))return;
        }
        //保存一些session信息
        HttpSession session = req.getSession();
        session.setAttribute(VALIDATE_CODE, "mytest");
        chain.doFilter(request,response);  
    }  
    
    /**
     * 覆盖授权验证方法，这里可以做一些自己需要的session设置操作
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return null;
    }

    protected boolean checkValidateCode(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String sessionValidateCode = obtainSessionValidateCode(session);
        sessionValidateCode = "1234";// 做个假的验证码；
        // 让上一次的验证码失效
        session.setAttribute(VALIDATE_CODE, null);
        String validateCodeParameter = obtainValidateCodeParameter(request);
        if (StringUtils.isEmpty(validateCodeParameter) || !sessionValidateCode.equalsIgnoreCase(validateCodeParameter)) {
            unsuccessfulAuthentication(request, response, new InsufficientAuthenticationException("输入的验证码不正确"));  
            return false;
        }
        return true;
    }

    private String obtainValidateCodeParameter(HttpServletRequest request) {
        Object obj = request.getParameter(VALIDATE_CODE);
        return null == obj ? "" : obj.toString();
    }

    protected String obtainSessionValidateCode(HttpSession session) {
        Object obj = session.getAttribute(VALIDATE_CODE);
        return null == obj ? "" : obj.toString();
    }
}
```



　　**代码解读**

　　1、为Filter指定请求地址过滤器，用于拦截登录请求。调用AbstractAuthenticationProcessingFilter.requiresAuthentication方法。

​             ![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213143416316-1187865027.png)

　　2、指定验证失败的跳转页面

　　![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213143551238-511518208.png)

　　3、验证码的测试代码。假的验证码1234，与页面参数比对后，如果不相等则抛出"输入的验证码不正确"的异常。

　　![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213143925566-794505933.png)

　　4、验证通过，继续执行后续的Filter链。否则退出请求处理逻辑。这个Filter只处理验证码的校验逻辑，用户名密码的验证交给后面的UsernamePasswordAuthenticationFilter来处理。

　　![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213144241144-1820011260.png)

   **二、向HttpSecurity的Filter链上插入自定义的Filter,插入到UsernamePasswordAuthenticationFilter的位置上。插入方法有addFilterBefore,addFilterAt,addFilterAfter。这个地方需要注意使用addFilterAt并不是说能替换掉原有的Filter,事实上框架原有的Filter在启动HttpSecurity配置的过程中，都由框架完成了其一定程度上固定的配置，是不允许更改替换的。根据测试结果来看，调用addFilterAt方法插入的Filter，会在这个位置上的原有Filter之前执行。**

```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        String doUrl = "/**/*.do";
        http
         .addFilterAt(new MyUsernamePasswordAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
```

　 **三、登录方法添加对验证码错误回调的拦截**



```
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "validerror", required = false) String validerror,
            @RequestParam(value = "logout", required = false) String logout,Model model) {
        if (error != null) {
            model.addAttribute("msg", "用户名或密码错误！");
        }
        if(validerror!=null){
            model.addAttribute("msg", "验证码错误！");
        }
        if (logout != null) {
            model.addAttribute("msg", "成功退出！");
        }
        return "user/login";
    }
```



​     **四、测试结果展示**

　　![img](https://images2017.cnblogs.com/blog/465907/201712/465907-20171213150017488-2114934738.png)