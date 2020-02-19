# MySQL按照时间分组查询

```mysql
select DATE_FORMAT( paytime, "%Y-%m-%d %H" ),sum(t.amount) amount_sum,count(t.his_order_id) num_sum from isp_his_order_account t GROUP BY DATE_FORMAT( paytime, "%Y-%m-%d %H" );
```

查询语句示例如上所示，该语句会把时间格式化到小时，即数据库记录分组依据为小时。

修改"%Y-%m-%d %H"的格式，可以调整为按照年份/月份/日期/小时来分组查询。

1）按天统计：

select DATE_FORMAT(start_time,'%Y%m%d') days,count(product_no) count from test group by days; 

2）按周统计：

select DATE_FORMAT(start_time,'%Y%u') weeks,count(product_no) count from test group by weeks; 

3）按月统计:

select DATE_FORMAT(start_time,'%Y%m') months,count(product_no) count from test group by months; 



参考：

- %M 月名字(January……December)
- %W 星期名字(Sunday……Saturday)
- %D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。）
- %Y 年, 数字, 4 位
- %y 年, 数字, 2 位
- %a 缩写的星期名字(Sun……Sat)
- %d 月份中的天数, 数字(00……31)
- %e 月份中的天数, 数字(0……31)
- %m 月, 数字(01……12)
- %c 月, 数字(1……12)
- %b 缩写的月份名字(Jan……Dec)
- %j 一年中的天数(001……366)
- %H 小时(00……23)
- %k 小时(0……23)
- %h 小时(01……12)
- %I 小时(01……12)
- %l 小时(1……12)
- %i 分钟, 数字(00……59)
- %r 时间,12 小时(hh:mm:ss [AP]M)
- %T 时间,24 小时(hh:mm:ss)
- %S 秒(00……59)
- %s 秒(00……59)
- %p AM或PM
- %w 一个星期中的天数(0=Sunday ……6=Saturday ）
- %U 星期(0……52), 这里星期天是星期的第一天
- %u 星期(0……52), 这里星期一是星期的第一天
- %% 一个文字“%”。