**简介**

 使用jpa进行update操作主要有两种方式：

1. 调用保存实体的方法

​      1）保存一个实体：repository.save(T entity)

​      2）保存多个实体：repository.save(Iterable<T> entities)

​      3）保存并立即刷新一个实体：repository.saveAndFlush(T entity)

注：若是更改，entity中必须设置了主键字段，不然不能对应上[数据库](http://lib.csdn.net/base/mysql)中的记录，变成新增（数据库自动生成主键）或报错（数据库不自动生成主键）了

2. @Query注解，自己写JPQL语句

​    例：

   @Modifying
   @Query("update ShopCoupon sc set sc.deleted = true where sc.id in :ids")
   public void deleteByIds(@Param(value = "ids") List<String> ids);

   注：

   1）update或delete时必须使用@Modifying对方法进行注解，才能使得ORM知道现在要执行的是写操作

   2）有时候不加@Param注解参数，可能会报如下异常：

   org.springframework.dao.InvalidDataAccessApiUsageException: Name must not be null or empty!; nested exception is [Java](http://lib.csdn.net/base/java).lang.IllegalArgumentException: Name must not be null or empty! 



3. jpa实现update操作 字段有值就更新,没值就用原来的

```java
/**
     *复杂JPA操作  使用@Query()自定义sql语句  根据业务id UId去更新整个实体
     * 删除和更新操作，需要@Modifying和@Transactional注解的支持
     *
     * 更新操作中 如果某个字段为null则不更新，否则更新【注意符号和空格位置】
     *
     * @param huaYangArea   传入实体，分别取实体字段进行set
     * @return  更新操作返回sql作用条数
     */
    @Modifying
    @Transactional
    @Query("update HuaYangArea hy set " +
            "hy.areaName = CASE WHEN :#{#huaYangArea.areaName} IS NULL THEN hy.areaName ELSE :#{#huaYangArea.areaName} END ," +
            "hy.areaPerson = CASE WHEN :#{#huaYangArea.areaPerson} IS NULL THEN hy.areaPerson ELSE :#{#huaYangArea.areaPerson} END ," +
            "hy.updateDate = CASE WHEN :#{#huaYangArea.updateDate} IS NULL THEN hy.updateDate ELSE :#{#huaYangArea.updateDate} END ," +
            "hy.updateId =  CASE WHEN :#{#huaYangArea.updateId} IS NULL THEN hy.updateId ELSE :#{#huaYangArea.updateId} END " +
            "where hy.uid = :#{#huaYangArea.uid}")
    int update(@Param("huaYangArea") HuaYangArea huaYangArea);
```

