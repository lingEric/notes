# MySQL查询优化记录

https://blog.csdn.net/qq_37939251/article/details/83022638





## 1.exists关键字

首先是exists和in的区别



```mysql
SELECT
	count( payOrderID ) order_num,
	sum( amount ) order_amount 
FROM
	isp_his_order_account hisorder 
WHERE
	NOT EXISTS ( SELECT remark FROM isp_medical_settlement med WHERE med.remark = hisorder.payOrderID ) 
	AND hisorder.payTime BETWEEN '2019-09-19 00:00:00' 
	AND '2019-09-25 23:59:59';

SELECT
	count( payOrderID ) order_num,
	sum( amount ) order_amount 
FROM
	isp_his_order_account hisorder
	JOIN isp_medical_settlement med ON med.remark = hisorder.payOrderID 
WHERE
	med.remark IS NULL 
	AND hisorder.payTime BETWEEN '2019-09-19 00:00:00' 
	AND '2019-09-25 23:59:59';
```

## 2.解释查询语句



## 3.查询唯一记录时限制记录条数



## 4.使用索引

为join添加索引？？



## 5.enum类型

## 6.使用过程分析

analysis()



## 7.not null

## 8.将IP存储为无符号int

## 9.使用静态表

也即固定长度表，谨慎使用VARCHAR,TEXT,BLOB等数据类型

固定长度的表可以提高性能，因为MySQL引擎查找记录的速度更快。当它想读取表中的特定行时，它可以快速计算它的位置。如果行大小不是固定的，则每次需要查找时，都必须参考主键索引。



## 10.垂直划分

## 11.对大批量的删除或者插入进行拆分

## 12.对分页查询进行范围限定

## 13.使用较小的列

## 14.选择正确的存储引擎

## 15.