# Mybatis中@Param注解的使用



在SSM框架中，@Param主要是用来注解dao【即mapper】类中方法的参数，便于在对应的dao.xml文件中引用，如：在userDAO类中有这么一个函数：

public User selectByNameAndPwd(@Param("userName") String name,@Param("password") String )

在其对应的dao.xml文件中的查询语句则为：

select  username,password from user where username=${userName} and  password=${password}

注：在不使用@Param注解的时候，函数的参数**只能为一个**，并且在查询语句取值时**只能用#{}**，且其所属的类**必须为Javabean**,而使用@Param注解则可以**使用多个参数**，在查询语句中使用时可以使用#{}或者${}

