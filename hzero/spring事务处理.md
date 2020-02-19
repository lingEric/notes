# spring事务处理

在默认情况下，大部分使用spring的事务都是使用代理的模式，代理实现的事务有一定的局限性：仅有在公有方法上标记的@Transactional有效；仅有外部方法调用过程才会被代理截获，事务才会有效，也就是说，一个方法调用本对象的另一个方法，没有通过代理类，事务也就无法生效。

## 问题重现

首先描述下类内部方法互相调用，事务不生效的情况

```java
//UserService测试接口类
package cn.sw.study.web.service;

public interface UserService {
    void addInfo();
    void addOne();
}

//UserServiceImpl测试实现类
package cn.sw.study.web.service.impl;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;
    public void addInfo() {
        addOne();
    }
    
    @Transactional
    public void addOne() {
        User record = new User();
        record.setLoginName("tom");
        record.setPwd("111111");
        record.setMobile("13913913913");
        record.setUsable(1);
        record.setCreateTime(new Date());
        userMapper.insertSelective(record);
        int i = 1/0;    // 测试事务的回滚
    }
}
```

addInfo()方法上没有事务注解，addOne()方法上有事务注解，此时运行addInfo调用addOne方法，不会产生事务，测试数据遇到异常没有回滚。如果从外部类直接调用addOne方法，则事务是可以正常生效的。



## 解决

`AopProxy`：

```java
/**
 * 封装self()方法便于获取自身接口代理类
 */
public interface AopProxy <T>{

    default T self() {
        return (T) AopContext.currentProxy();
    }
    
}

//AopContext.currentProxy();核心代码
```



提供self()方法便于获取自身接口代理对象，常用在一个事务方法里调用当前类的其它事务方法，如果不使用代理对象调用方法，本质使用的是原始对象，因而可能导致事务或AOP拦截不生效。 

![img](http://hzerodoc.saas.hand-china.com/img/docs/development-component/starter/1539668576.jpg)





## 参考API

```java
//是否为代理对象
AopUtils.isAopProxy();

//是否为JDK动态代理对象
AopUtils.isJdkDynamicProxy();

//是否为CGlib代理对象
AopUtils.isCglibProxy();
```

