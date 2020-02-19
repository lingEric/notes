# spring注入service时有多个实现类的解决

## 注解区分

- @Autowired
- @Resource
- @Qualifier

1. @Autowired 是通过 byType 的方式去注入的， 使用该注解，要求接口只能有一个实现类。

2. @Resource 可以通过 byName 和 byType的方式注入， 默认先按 byName的方式进行匹配，如果匹配不到，再按 byType的方式进行匹配。

3. @Qualifier 注解可以按名称注入， 但是注意是 **类名**

例子：

interface IAnimal

```java
public Interface IAnimal{
    ......
}
```

-------|CatImpl impliments IAnimal

```java
@Service("catImpl")
public class CatImpl impliments IAnimal{
    ...
}
```

-------|DaoImpl impliments IAnimal

```java
@Service("dogImpl")
public class DaoImpl impliments IAnimal{
    ...
}
```

AnimalController

```java
public class AnimalController {
    @Resource(name="dogImpl")        //@Service注解中标定的名称
    private IAnimal dogImpl;
    ...
}
```

或

```java
public class AnimalController {
    @Qualifier("DaoImpl")        //注意区分与@Resource(name="dogImpl") 的区别。
    private IAnimal dogImpl;
    ...
}
```

注意区分@Resource和@Qualifier注解

