## 依赖范围scope

共5种，compile (编译)、test (测试)、runtime (运行时)、provided、system

不指定，则依赖范围默认为compile.

 

compile:编译依赖范围，在编译，测试，运行时都需要。

test: 测试依赖范围，测试时需要。编译和运行不需要。如Junit

runtime: 运行时依赖范围，测试和运行时需要。编译不需要。如JDBC驱动包

provided:已提供依赖范围，编译和测试时需要。运行时不需要。如servlet-api

system:系统依赖范围。本地依赖，不在maven中央仓库。

 

 

## 依赖的传递

A->B(compile)     第一关系: a依赖b   compile

B->C(compile)     第二关系: b依赖c   compile

 

当在A中配置

```xml
<dependency>  
    <groupId>com.B</groupId>  
    <artifactId>B</artifactId>  
    <version>1.0</version>  
</dependency>
```

则会自动导入c包。关系传递如下表：

 

| **第一**          **第二** | **compile**  | **test** | **provided** | **runtime**  |
| -------------------------- | ------------ | -------- | ------------ | ------------ |
| **compile**                | **compile**  | **-**    | **-**        | **runtime**  |
| **test**                   | **test**     | **-**    | **-**        | **test**     |
| **provided**               | **provided** | **-**    | **provided** | **provided** |
| **runtime**                | **runtime**  | **-**    | **-**        | **runtime**  |

## 依赖冲突的调节

A->B->C->X(1.0)

A->D->X(2.0)

由于只能引入一个版本的包，此时Maven按照最短路径选择导入x(2.0)

 

A->B->X(1.0)

A->D->X(2.0)

路径长度一致，则优先选择第一个，此时导入x(1.0)

 

## 排除依赖

A->B->C(1.0)

此时在A项目中，不想使用C(1.0)，而使用C(2.0)

则需要使用exclusion排除B对C(1.0)的依赖。并在A中引入C(2.0).

 

pom.xml中配置

```xml
<!--排除B对C的依赖-->

<dependency>  
    <groupId>B</groupId>  
    <artifactId>B</artifactId>  
    <version>0.1</version>  
    <exclusions>
        <exclusion>
            <groupId>C</groupId>  
            <artifactId>C</artifactId><!--无需指定要排除项目的版本号-->
        </exclusion>
    </exclusions>
</dependency> 

<!---在A中引入C(2.0)-->

<dependency>  
    <groupId>C</groupId>  
    <artifactId>C</artifactId>  
    <version>2.0</version>  
</dependency> 
```

## 依赖关系的查看

cmd进入工程根目录，执行  mvn dependency:tree

会列出依赖关系树及各依赖关系

 

mvn dependency:analyze    分析依赖关系