# 前言

本文主要讲解的知识点有以下：

- 权限管理的基础知识
  - 模型
  - 粗粒度和细粒度的概念
- 回顾URL拦截的实现
- Shiro的介绍与简单入门

# 一、Shiro基础知识

在学习Shiro这个框架之前，首先我们要先了解Shiro需要的基础知识：**权限管理**

## 1.1什么是权限管理？

**只要有用户参与的系统一般都要有权限管理**，权限管理实现对用户访问系统的控制，按照安全规则或者安全策略**控制用户可以访问而且只能访问自己被授权的资源**。

对权限的管理又分为两大类别：

- **用户认证**
- **用户授权**

### 1.1.1用户认证

用户认证，**用户去访问系统，系统要验证用户身份的合法性**

最常用的用户身份验证的方法：1、用户名密码方式、2、指纹打卡机、3、基于证书验证方法。。**系统验证用户身份合法，用户方可访问系统的资源。**

举个例子：

- 当我们输入了自己的淘宝的账户和密码，才能打开购物车

用户认证的流程：

- **判断该资源能否不认证就能访问【登陆页面、首页】**
- **如果该资源需要认证后才能访问，那么判断该访问者是否认证了**
- **如果还没有认证，那么需要返回到【登陆页面】进行认证**
- **认证通过后才能访问资源**

![这里写图片描述](https://segmentfault.com/img/remote/1460000013875095?w=748&h=681)

从用户认证我们可以抽取出这么几个概念

- subject主体：**理解为用户,可能是程序，都要去访问系统的资源，系统需要对subject进行身份认证**
- principal身份信息：**通常是唯一的，一个主体还有多个身份信息**，但是都有一个主身份信息（primary principal）【我们可以选择身份证认证、学生证认证等等都是我们的身份信息】
- credential凭证信息：**可以是密码 、证书、指纹。**

总结：**主体在进行身份认证时需要提供身份信息和凭证信息。**

### 1.1.2用户授权

用户授权，**简单理解为访问控制**，在用户认证通过后，**系统对用户访问资源进行控制，用户具有资源的访问权限方可访问**。

用户授权的流程

- 到达了用户授权环节，当然是需要用户认证之后了
- **用户访问资源，系统判断该用户是否有权限去操作该资源**
- **如果该用户有权限才能够访问，如果没有权限就不能访问了**

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

授权的过程可以简单理解为：**主体认证之后，系统进行访问控制**

subject必须具备资源的访问权限才可访问该资源..

权限/许可(permission) ：**针对资源的权限或许可，subject具有permission访问资源，如何访问/操作需要定义permission**，权限比如：用户添加、用户修改、商品删除

资源可以分为两种

- 资源类型:**系统的用户信息就是资源类型，相当于java类。**
- 资源实例:**系统中id为001的用户就是资源实例，相当于new的java对象。**

## 1.2权限管理模型

一般地，我们可以抽取出这么几个模型：

- 主体（账号、密码）
- 资源（资源名称、访问地址）
- 权限（权限名称、资源id）
- 角色（角色名称）
- 角色和权限关系（角色id、权限id）
- 主体和角色关系（主体id、角色id）

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

通常企业开发中将资源和权限表合并为一张权限表，如下：

- 资源（资源名称、访问地址）
- 权限（权限名称、资源id）

合并为：

- **权限（权限名称、资源名称、资源访问地址）**

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

## 1.3分配权限

**用户需要分配相应的权限才可访问相应的资源。权限是对于资源的操作许可。**

通常给**用户分配资源权限需要将权限信息持久化，比如存储在关系数据库中**。把用户信息、权限管理、用户分配的权限信息写到数据库（权限数据模型）

### 1.3.1基于角色访问控制

**RBAC(role based access control)，基于角色的访问控制。**

```
//如果该user是部门经理则可以访问if中的代码
if(user.hasRole('部门经理')){
    //系统资源内容
    //用户报表查看
}
```

角色针对人划分的，**人作为用户在系统中属于活动内容，如果该 角色可以访问的资源出现变更，需要修改你的代码了**，

```
if(user.hasRole('部门经理') || user.hasRole('总经理')  ){
    //系统资源内容
    //用户报表查看
}
```

**基于角色的访问控制是不利于系统维护(可扩展性不强)。**

### 1.3.2基于资源的访问控制

**RBAC(Resource based access control)，基于资源的访问控制。**

资源在系统中是不变的，比如资源有：**类中的方法，页面中的按钮。**

```
对资源的访问需要具有permission权限，代码可以写为：

if(user.hasPermission ('用户报表查看（权限标识符）')){
    //系统资源内容
    //用户报表查看
}
```

**建议使用基于资源的访问控制实现权限管理**。

------

# 二、 粗粒度和细粒度权限

细粒度权限管理：对资源实例的权限管理。资源实例就资源类型的具体化，比如：用户id为001的修改连接，1110班的用户信息、行政部的员工。**细粒度权限管理就是数据级别的权限管理。**

粗粒度权限管理比如：超级管理员可以访问户添加页面、用户信息等全部页面。部门管理员可以访问用户信息页面包括 页面中所有按钮。

**粗粒度和细粒度例子**：

```
系统有一个用户列表查询页面，对用户列表查询分权限，

如果粗颗粒管理，张三和李四都有用户列表查询的权限，张三和李四都可以访问用户列表查询。

进一步进行细颗粒管理，张三（行政部）和李四(开发部)只可以查询自己本部门的用户信息。

张三只能查看行政部 的用户信息，李四只能查看开发部门的用户信息。

细粒度权限管理就是数据级别的权限管理。
```

## 2.1如何实现粗粒度权限管理？

粗粒度权限管理比较容易**将权限管理的代码抽取出来在系统架构级别统一处理**。比如：通过**springmvc的拦截器实现授权**。

对细粒度权限管理在**数据级别是没有共性可言**，针对细粒度权限管理就是**系统业务逻辑的一部分**，**在业务层去处理相对比较简单**

比如：**部门经理只查询本部门员工信息，在service接口提供一个部门id的参数，controller中根据当前用户的信息得到该 用户属于哪个部门，调用service时将部门id传入service，实现该用户只查询本部门的员工。**

### 2.1.1基于URL拦截

基于url拦截的方式实现在实际开发中比较常用的一种方式。

**对于web系统，通过filter过虑器实现url拦截，也可以springmvc的拦截器实现基于url的拦截。**

### 2.2.2使用权限管理框架实现

对于粗粒度权限管理，建议使用优秀权限管理框架来实现，节省开发成功，提高开发效率。

**shiro就是一个优秀权限管理框架。**

# 三、回顾URL拦截

我们在学习的路途上也是使用过几次URL对权限进行拦截的

当时我们做了权限的增删该查的管理系统，**但是在权限表中是没有把资源添加进去，我们使用的是Map集合来进行替代的**。
<http://blog.csdn.net/hon_3y/article/details/61926175>

随后，我们学习了动态代理和注解，我们也做了一个基于注解的拦截

- **在Controller得到service对象的时候，service工厂返回的是一个动态代理对象回去**
- **Controller拿着代理对象去调用方法，代理对象就会去解析该方法上是否有注解**
- **如果有注解，那么就需要我们进行判断该主体是否认证了，如果认证了就判断该主体是否有权限**
- **当我们解析出该主体的权限和我们注解的权限是一致的时候，才放行！**

<http://blog.csdn.net/hon_3y/article/details/70767050>

流程:

![这里写图片描述](https://segmentfault.com/img/remote/1460000013875099?w=689&h=670)

## 3.1认证的JavaBean

我们之前认证都是放在默认的Javabean对象上的，现在既然我们准备学Shiro了，我们就得专业一点，弄一个**专门存储认证信息的JavaBean**

```
/**
 * 用户身份信息，存入session 由于tomcat将session会序列化在本地硬盘上，所以使用Serializable接口
 * 
 * @author Thinkpad
 * 
 */
public class ActiveUser implements java.io.Serializable {
    private String userid;//用户id（主键）
    private String usercode;// 用户账号
    private String username;// 用户名称

    private List<SysPermission> menus;// 菜单
    private List<SysPermission> permissions;// 权限

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<SysPermission> getMenus() {
        return menus;
    }

    public void setMenus(List<SysPermission> menus) {
        this.menus = menus;
    }

    public List<SysPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SysPermission> permissions) {
        this.permissions = permissions;
    }

    
}
```

认证的服务

```
    @Override
    public ActiveUser authenticat(String userCode, String password)
            throws Exception {
        /**
    认证过程：
    根据用户身份（账号）查询数据库，如果查询不到用户不存在
    对输入的密码 和数据库密码 进行比对，如果一致，认证通过
         */
        //根据用户账号查询数据库
        SysUser sysUser = this.findSysUserByUserCode(userCode);
        
        if(sysUser == null){
            //抛出异常
            throw new CustomException("用户账号不存在");
        }
        
        //数据库密码 (md5密码 )
        String password_db = sysUser.getPassword();
        
        //对输入的密码 和数据库密码 进行比对，如果一致，认证通过
        //对页面输入的密码 进行md5加密 
        String password_input_md5 = new MD5().getMD5ofStr(password);
        if(!password_input_md5.equalsIgnoreCase(password_db)){
            //抛出异常
            throw new CustomException("用户名或密码 错误");
        }
        //得到用户id
        String userid = sysUser.getId();
        //根据用户id查询菜单 
        List<SysPermission> menus =this.findMenuListByUserId(userid);
        
        //根据用户id查询权限url
        List<SysPermission> permissions = this.findPermissionListByUserId(userid);
        
        //认证通过，返回用户身份信息
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUserid(sysUser.getId());
        activeUser.setUsercode(userCode);
        activeUser.setUsername(sysUser.getUsername());//用户名称
        
        //放入权限范围的菜单和url
        activeUser.setMenus(menus);
        activeUser.setPermissions(permissions);
        
        return activeUser;
    }
```

Controller处理认证，**如果身份认证成功，那么把认证信息存储在Session中**

```
    @RequestMapping("/login")
    public String login(HttpSession session, String randomcode,String usercode,String password)throws Exception{
        //校验验证码，防止恶性攻击
        //从session获取正确验证码
        String validateCode = (String) session.getAttribute("validateCode");
        
        //输入的验证和session中的验证进行对比 
        if(!randomcode.equals(validateCode)){
            //抛出异常
            throw new CustomException("验证码输入错误");
        }
        
        //调用service校验用户账号和密码的正确性
        ActiveUser activeUser = sysService.authenticat(usercode, password);
        
        //如果service校验通过，将用户身份记录到session
        session.setAttribute("activeUser", activeUser);
        //重定向到商品查询页面
        return "redirect:/first.action";
    }
    
```

身份认证拦截器

```
    //在执行handler之前来执行的
    //用于用户认证校验、用户权限校验
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        
        //得到请求的url
        String url = request.getRequestURI();
        //判断是否是公开 地址
        //实际开发中需要公开 地址配置在配置文件中
        //从配置中取逆名访问url
        List<String> open_urls = ResourcesUtil.gekeyList("anonymousURL");
        //遍历公开 地址，如果是公开 地址则放行
        for(String open_url:open_urls){
            if(url.indexOf(open_url)>=0){
                //如果是公开 地址则放行
                return true;
            }
        }
        //判断用户身份在session中是否存在
        HttpSession session = request.getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        //如果用户身份在session中存在放行
        if(activeUser!=null){
            return true;
        }
        //执行到这里拦截，跳转到登陆页面，用户进行身份认证
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        
        //如果返回false表示拦截不继续执行handler，如果返回true表示放行
        return false;
    }
```

授权拦截器

```
    //在执行handler之前来执行的
    //用于用户认证校验、用户权限校验
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        
        //得到请求的url
        String url = request.getRequestURI();
        //判断是否是公开 地址
        //实际开发中需要公开 地址配置在配置文件中
        //从配置中取逆名访问url
        
        List<String> open_urls = ResourcesUtil.gekeyList("anonymousURL");
        //遍历公开 地址，如果是公开 地址则放行
        for(String open_url:open_urls){
            if(url.indexOf(open_url)>=0){
                //如果是公开 地址则放行
                return true;
            }
        }
        //从配置文件中获取公共访问地址
        List<String> common_urls = ResourcesUtil.gekeyList("commonURL");
        //遍历公用 地址，如果是公用 地址则放行
        for(String common_url:common_urls){
            if(url.indexOf(common_url)>=0){
                //如果是公开 地址则放行
                return true;
            }
        }
        //获取session
        HttpSession session = request.getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        //从session中取权限范围的url
        List<SysPermission> permissions = activeUser.getPermissions();
        for(SysPermission sysPermission:permissions){
            //权限的url
            String permission_url = sysPermission.getUrl();
            if(url.indexOf(permission_url)>=0){
                //如果是权限的url 地址则放行
                return true;
            }
        }
        
        //执行到这里拦截，跳转到无权访问的提示页面
        request.getRequestDispatcher("/WEB-INF/jsp/refuse.jsp").forward(request, response);
        
        //如果返回false表示拦截不继续执行handler，如果返回true表示放行
        return false;
    }
```

拦截器配置：

```
    <!--拦截器 -->
    <mvc:interceptors>

        <mvc:interceptor>
            <!-- 用户认证拦截 -->
            <mvc:mapping path="/**" />
            <bean class="cn.itcast.ssm.controller.interceptor.LoginInterceptor"></bean>
        </mvc:interceptor>
        <mvc:interceptor>
            <!-- 授权拦截 -->
            <mvc:mapping path="/**" />
            <bean class="cn.itcast.ssm.controller.interceptor.PermissionInterceptor"></bean>
        </mvc:interceptor>
    </mvc:interceptors>
```

# 四、什么是Shiro

shiro是apache的一个开源框架，**是一个权限管理的框架，实现 用户认证、用户授权**。

spring中有spring security (原名Acegi)，是一个权限框架，它和spring依赖过于紧密，没有shiro使用简单。
shiro不依赖于spring，shiro不仅可以实现 web应用的权限管理，还可以实现c/s系统，分布式系统权限管理，**shiro属于轻量框架，越来越多企业项目开始使用shiro。**

Shiro架构：

![这里写图片描述](https://segmentfault.com/img/remote/1460000013875100?w=854&h=575)

- subject：主体，可以是用户也可以是程序，主体要访问系统，系统需要对主体进行认证、授权。
- securityManager：安全管理器，主体进行认证和授权都 是通过securityManager进行。
- authenticator：认证器，主体进行认证最终通过authenticator进行的。
- authorizer：授权器，主体进行授权最终通过authorizer进行的。
- sessionManager：web应用中一般是用web容器对session进行管理，shiro也提供一套session管理的方式。
- SessionDao： 通过SessionDao管理session数据，针对个性化的session数据存储需要使用sessionDao。
- cache Manager：缓存管理器，主要对session和授权数据进行缓存，比如将授权数据通过cacheManager进行缓存管理，和ehcache整合对缓存数据进行管理。
- realm：域，领域，相当于数据源，**通过realm存取认证、授权相关数据。**

**cryptography：密码管理，提供了一套加密/解密的组件，方便开发。比如提供常用的散列、加/解密等功能。**

- 比如md5散列算法。

# 五、为什么使用Shiro

我们在使用URL拦截的时候，**要将所有的URL都配置起来，繁琐、不易维护**

而我们的Shiro实现**系统的权限管理，有效提高开发效率，从而降低开发成本。**

# 六、Shiro认证

## 6.1导入jar包

我们使用的是Maven的坐标就行了

```
    <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-core</artifactId>
            <version>1.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-web</artifactId>
            <version>1.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-ehcache</artifactId>
            <version>1.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-quartz</artifactId>
            <version>1.2.3</version>
        </dependency>
```

当然了，我们也可以把Shiro相关的jar包全部导入进去

```
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-all</artifactId>
    <version>1.2.3</version>
</dependency>
```

## 6.2Shiro认证流程

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

### 6.2.1通过配置文件创建工厂

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

```
    // 用户登陆和退出
    @Test
    public void testLoginAndLogout() {

        // 创建securityManager工厂，通过ini配置文件创建securityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(
                "classpath:shiro-first.ini");

        // 创建SecurityManager
        SecurityManager securityManager = factory.getInstance();

        // 将securityManager设置当前的运行环境中
        SecurityUtils.setSecurityManager(securityManager);

        // 从SecurityUtils里边创建一个subject
        Subject subject = SecurityUtils.getSubject();

        // 在认证提交前准备token（令牌）
        // 这里的账号和密码 将来是由用户输入进去
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan",
                "111111");
        try {
            // 执行认证提交
            subject.login(token);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 是否认证通过
        boolean isAuthenticated = subject.isAuthenticated();

        System.out.println("是否认证通过：" + isAuthenticated);

        // 退出操作
        subject.logout();

        // 是否认证通过
        isAuthenticated = subject.isAuthenticated();

        System.out.println("是否认证通过：" + isAuthenticated);

    }
```

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

### 6.3小结

ModularRealmAuthenticator作用进行认证，**需要调用realm查询用户信息（在数据库中存在用户信息）**
ModularRealmAuthenticator进行密码对比（认证过程）。
realm：需要根据token中的身份信息去查询数据库（入门程序使用ini配置文件），**如果查到用户返回认证信息，如果查询不到返回null**。

## 6.4自定义realm

从第一个认证程序我们可以看见，我们所说的流程，**是认证器去找realm去查询我们相对应的数据**。而默认的realm是直接去与配置文件来比对的，一般地，**我们在开发中都是让realm去数据库中比对。**
因此，我们需要自定义realm

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

```
public class CustomRealm extends AuthorizingRealm {

    // 设置realm的名称
    @Override
    public void setName(String name) {
        super.setName("customRealm");
    }

    // 用于认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {

        // token是用户输入的
        // 第一步从token中取出身份信息
        String userCode = (String) token.getPrincipal();

        // 第二步：根据用户输入的userCode从数据库查询
        // ....
    

        // 如果查询不到返回null
        //数据库中用户账号是zhangsansan
        /*if(!userCode.equals("zhangsansan")){//
            return null;
        }*/
        
        
        // 模拟从数据库查询到密码
        String password = "111112";

        // 如果查询到返回认证信息AuthenticationInfo

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                userCode, password, this.getName());

        return simpleAuthenticationInfo;
    }

    // 用于授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        // TODO Auto-generated method stub
        return null;
    }

}
```

## 6.5配置realm

**需要在shiro-realm.ini配置realm注入到securityManager中。**

![这里写图片描述](https://static.segmentfault.com/v-5c8b4d77/global/img/squares.svg)

## 6.6测试自定义realm

同上边的入门程序，需要更改ini配置文件路径：

```
同上边的入门程序，需要更改ini配置文件路径：
Factory<SecurityManager> factory = new IniSecurityManagerFactory(
                "classpath:shiro-realm.ini");
```

## 6.7散列算法

我们如果知道md5，我们就会知道md5是不可逆的，但是如果设置了一些安全性比较低的密码：111111...即时是不可逆的，但还是可以通过暴力算法来得到md5对应的明文...

**建议对md5进行散列时加salt（盐），进行加密相当 于对原始密码+盐进行散列。**\

正常使用时散列方法：

- 在程序中对原始密码+盐进行散列，将散列值存储到数据库中，并且还要将盐也要存储在数据库中。

测试：

```
public class MD5Test {
    
    public static void main(String[] args) {
        
        //原始 密码 
        String source = "111111";
        //盐
        String salt = "qwerty";
        //散列次数
        int hashIterations = 2;
        //上边散列1次：f3694f162729b7d0254c6e40260bf15c
        //上边散列2次：36f2dfa24d0a9fa97276abbe13e596fc
        
        
        //构造方法中：
        //第一个参数：明文，原始密码 
        //第二个参数：盐，通过使用随机数
        //第三个参数：散列的次数，比如散列两次，相当 于md5(md5(''))
        Md5Hash md5Hash = new Md5Hash(source, salt, hashIterations);
        
        String password_md5 =  md5Hash.toString();
        System.out.println(password_md5);
        //第一个参数：散列算法 
        SimpleHash simpleHash = new SimpleHash("md5", source, salt, hashIterations);
        System.out.println(simpleHash.toString());
    }

}
```

## 6.8自定义realm支持md5

**自定义realm**

```
public class CustomRealmMd5 extends AuthorizingRealm {

    // 设置realm的名称
    @Override
    public void setName(String name) {
        super.setName("customRealmMd5");
    }

    // 用于认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {

        // token是用户输入的
        // 第一步从token中取出身份信息
        String userCode = (String) token.getPrincipal();

        // 第二步：根据用户输入的userCode从数据库查询
        // ....

        // 如果查询不到返回null
        // 数据库中用户账号是zhangsansan
        /*
         * if(!userCode.equals("zhangsansan")){// return null; }
         */

        // 模拟从数据库查询到密码,散列值
        String password = "f3694f162729b7d0254c6e40260bf15c";
        // 从数据库获取salt
        String salt = "qwerty";
        //上边散列值和盐对应的明文：111111

        // 如果查询到返回认证信息AuthenticationInfo
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                userCode, password, ByteSource.Util.bytes(salt), this.getName());

        return simpleAuthenticationInfo;
    }

    // 用于授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        // TODO Auto-generated method stub
        return null;
    }

}
```

配置文件：

![这里写图片描述](https://segmentfault.com/img/remote/1460000013875106?w=737&h=276)

测试：

```
// 自定义realm实现散列值匹配
    @Test
    public void testCustomRealmMd5() {

        // 创建securityManager工厂，通过ini配置文件创建securityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(
                "classpath:shiro-realm-md5.ini");

        // 创建SecurityManager
        SecurityManager securityManager = factory.getInstance();

        // 将securityManager设置当前的运行环境中
        SecurityUtils.setSecurityManager(securityManager);

        // 从SecurityUtils里边创建一个subject
        Subject subject = SecurityUtils.getSubject();

        // 在认证提交前准备token（令牌）
        // 这里的账号和密码 将来是由用户输入进去
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan",
                "222222");

        try {
            // 执行认证提交
            subject.login(token);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 是否认证通过
        boolean isAuthenticated = subject.isAuthenticated();

        System.out.println("是否认证通过：" + isAuthenticated);

    }
```

# 七、总结

- 用户认证和用户授权是Shiro的基础，用户认证其实上就是登陆操作、用户授权实际上就是对资源拦截的操作。
- 权限管理的模型一般我们都将资源放在权限表中进行管理起来。
- 我们可以基于角色拦截，也可以基于资源拦截。要是基于角色拦截的话，那么如果角色的权限发生变化了，那就需要修改代码了**。推荐使用基于资源进行拦截**
- **这次URL拦截，我们使用一个JavaBean来封装所有的认证信息。当用户登陆了之后，我们就把用户对菜单栏的访问、对资源的访问权限都封装到该JavaBean中**
- 当使用拦截器进行用户认证的时候，我们只要判断Session域有没有JavaBen对象即可了。
- 当时候拦截器进行用户授权的时候，我们要判断JavaBean中的权限是否能够访问该资源。
- 以前URL拦截的方式需要把所有的URL都在数据库进行管理。非常麻烦，不易维护。
- **我们希望Shiro去认证的时候是通过realm去数据库查询数据的。而我们reaml默认是查询配置文件的数据的。**
- 因此，我们需要自定义reaml，使得它是去数据库查询数据。只要继承AuthorizingRealm类就行了。
- **当然了，自定义后的reaml也需要在配置文件中写上我们的自定义reaml的位置的。**
- 散列算法就是为了让密码不被别人给破解。**我们可对原始的密码加盐再进行散列，这就加大了破解的难度了。**
- 自定义的reaml也是支持散列算法的，相同的，还是需要我们在配置文件中配置一下就好了。