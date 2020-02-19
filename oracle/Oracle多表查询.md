# Oracle多表查询

- 内连接 

- 外连接 

- 子查询 

- 分页 

- 集合运算

## 内连接

```sql
/*
笛卡尔积： 两张表进行关联查询，得到的结果是两张表的乘积
如何去除笛卡尔积： 通过条件【外键】

主表：
从表：外键在这里，外键的值来源于主表的主键

--内连接：完全符合条件的数据才会显示
*/
select *
from emp e,dept d
where e.deptno = d.deptno;

--案例：查询员工与他领导的信息
select *
from emp e,emp m
where e.mgr = m.empno;

--案例：在上面的基础上，查询员工的部门信息
select e.empno,e.ename,e.deptno,d.dname 员工部门名称, m.empno,m.ename 领导名称
from emp e,emp m,dept d
where e.mgr = m.empno
      and e.deptno=d.deptno;

--案例：在上面的基础上，查询领导的部门信息
select e.empno,e.ename,e.deptno,d.dname 员工部门名称, m.empno,m.ename 领导名称,m.deptno ,d2.dname 领导部门名称
from emp e,emp m,dept d,dept d2
where e.mgr = m.empno
      and e.deptno=d.deptno
      and m.deptno=d2.deptno;

--案例：在上面的基础上，显示员工的薪资级别
select e.empno,e.ename,e.deptno,d.dname 员工部门名称,e.sal,s.grade 员工薪资等级,
       m.empno,m.ename 领导名称,m.deptno ,d2.dname 领导部门名称
from emp e,emp m,dept d,dept d2,salgrade s
where e.mgr = m.empno
      and e.deptno=d.deptno
      and m.deptno=d2.deptno
      and e.sal between s.losal and s.hisal;

--案例：在上面的基础上，把薪资等级显示为中文  一级,二级.....
select e.empno,e.ename,e.deptno,d.dname 员工部门名称,e.sal,s.grade 员工薪资等级,
       decode(s.grade,1,'一级',2,'二级',3,'三级','大boss级') as 中文等级,
       m.empno,m.ename 领导名称,m.deptno ,d2.dname 领导部门名称
from emp e,emp m,dept d,dept d2,salgrade s
where e.mgr = m.empno
      and e.deptno=d.deptno
      and m.deptno=d2.deptno
      and e.sal between s.losal and s.hisal;

select * from emp;
select * from dept;
select * from salgrade;
```

## 外连接

```sql
/*
外链接查询中，最关键是找基准表，如果是基准表，那么他的数据全部显示

外链接：
        左外连接： select * from tab1 left join tab2 on 条件      ： tab1是基准表
        右外链接： select * from tab1 right join tab2 on 条件     ： tab2是基准表

*/
-- 案例： 查询员工与部门表的信息
select * from emp e left join dept d on e.deptno = d.deptno;

-- 案例： 查询员工与他的薪资级别
select * from emp e left join salgrade s on e.sal between s.losal and s.hisal;

-- 案例： 查询员工与领导的信息
-- 左外链接
select * from emp e left join emp m on e.mgr = m.empno;
-- 右外链接
select * from emp m right join emp e on e.mgr = m.empno;

--内连接：
select * 
from emp e,emp m
where e.mgr = m.empno;

--oracle中独有外链接： 在内连接的基础上，使用  + 号
-- 口诀： + 号在那边，对面的一边就是基准表
select * 
from emp e,emp m
where e.mgr = m.empno(+);
```

## 子查询

```sql
/*
子查询:把一个查询的结果作为另外一个查询的 条件或者表

*/
--案例： 查询比SCOTT 用户工资高的员工
--1)查询scott用户的工资
select * from emp where ename = 'SCOTT';
--2)把上述的查询作为一个条件
select * 
from emp
where sal > (select sal from emp where ename = 'SCOTT')


/*
分类：
      单行子查询： 返回一行
      多行子查询： 返回多行
*/

--范例：查询出比雇员 7654 的工资高，同时从事和 7788 的工作一样的员工
--1）查询7654的工资
select sal from emp where empno = 7654;
--2）查询7788的工作
select job from emp where empno = 7788;
--3）把上述的查询作为条件
select * 
from emp
where sal > (select sal from emp where empno = 7654)
      and
      job = (select job from emp where empno = 7788);

--空值问题
--单行子查询 : 没有数据，但是不会报错
select *
from emp
where job = (select job from emp where empno = 8888);


--多行子查询： 
--查询是领导的员工
select * 
from emp
where empno in (select distinct mgr from emp) ; -- 查询：相当于用  empno = 7902 or empno = 7698 

--查询不是领导的员工: 使用not in 一定要去空
select * 
from emp
where empno not in (select distinct mgr from emp where mgr is not null);-- 相当于：  empno=7902  and  empno=7698 and empno = null


--非法使用子查询的问题 ： 使用等于号的时候，返回的结果应该只有一条，下面的查询会报错
select * 
from emp
where job = (select job from emp);

```

## exists的用法

```sql
/*
exists(子查询) ：
          1）当子查询有返回值，那么就返回true
          2）当子查询没有返回值，返回false  
*/
select * from emp where 1=1;
select * from emp where 1=2;

--exists 使用
select * from emp where exists(select * from dept where deptno=10);--返回true
select * from emp where exists(select * from dept where deptno=100);--返回false


--案例: 查询有员工的部门的信息
--使用in
select * 
from dept d
where d.deptno in(select distinct deptno from emp);

--使用exists
select * 
from dept d
where exists(select * from emp e where d.deptno = e.deptno);

--in exists区别: in会进行全表扫描； exists不会
--在查询的数据量很大【百万级】的时候，exists的效率高
```

## 分页

```sql
/*
伪列：
      ROWNUM:表示行号，实际上只是一个列,但是这个列是一个伪列,此列可以在每张表中出现。
      ROWID:表中每行数据指向磁盘上的物理地址。

      for(int i=1;i<10;i++){
              System.out.println(i);
      }
      1 2 3 4 ......9
*/
--案例： 找出员工表中的第一页数据： pageSize = 3
select rownum,e.*
from emp e
where rownum <4;


--案例： 找出员工表中的第二页数据： pageSize = 3  : rownum不能使用大于号 ： 没查询一条数据就分配一个行号
--下面的查询是没有数据的
select rownum,e.*
from emp e
where rownum >=4  and rownum <7;

--1、先找到小于7的数据
select rownum,e.*
from emp e
where rownum <7;
--2、把上述的查询当成一张表
select *
from (
      select rownum as rnum,e.*
      from emp e
      where rownum <7
      ) t
where t.rnum >=4;

--======案例：对员工表的工资排序，由大到小，取出第二页，pageSize=3
--先排序
select *
from emp
order by sal desc;
--把上述的查询当成一张表
select rownum,t.* 
from (
     select *
     from emp
     order by sal desc
     ) t
where rownum < 7 ;
--把上面的查询当成一张表
select *
from (
      select rownum as rnum,t.* 
      from (
           select *
           from emp
           order by sal desc
           ) t
      where rownum < 7 
    ) t2
where t2.rnum >=4;
```

## 集合运算

```sql
/*
要求:
    两个集合返回的列数、列的类型、列名要一致
*/
-- 并集
--案例： 查询工资大于1500 或者 在20号部门下的员工
select * 
from emp 
where sal > 1500 or deptno=20;


--先找到大于1500
-- 把两个集合联合起来
--再找到20号部门的员工
select * from emp where sal > 1500
union all  -- 有可能有重复的数据
select * from emp where deptno = 20;

select * from emp where sal > 1500
union  -- 把两个集合联合起来，还会去重
select * from emp where deptno = 20;


-- 交集
-- 案例： 找出工资大于1500 并且是 20号部门的员工
select * from emp where sal > 1500
intersect
select * from emp where deptno = 20;


-- 差集
---范例： 1981 年入职的普通员工（不包括总裁和经理）（差集）
--1）查询1981年入职的所有员工
select * from emp where to_char(hiredate,'yyyy') = '1981'; 
--2）查询是总裁和经理的员工
select * from emp where job = 'MANAGER' or job = 'PRESIDENT';

--3）第一步减去第二步的集合
select * from emp where to_char(hiredate,'yyyy') = '1981'
minus
select * from emp where job = 'MANAGER' or job = 'PRESIDENT';
```

