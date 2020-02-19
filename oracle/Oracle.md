# Oracle数据库命令备份

## 表空间和用户操作
```sql
/*
步骤：
1、创建表空间
2、创建用户
3、用户授权
*/

/*创建表空间*/
create tablespace QCJ_TABLESPACE
/*表空间物理文件名称*/
datafile 'QCJ_TABLESPACE.dbf' 
-- 这种方式指定表空间物理文件位置
-- datafile 'F:\APP\QIUCHANGJIN\ORADATA\ORCL\QCJ_TABLESPACE.dbf' 
-- 大小 500M，每次 5M 自动增大，最大不限制
size 500M autoextend on next 5M maxsize unlimited; 

/* 创建用户*/
create user qiuchangjin 
IDENTIFIED BY root --用户密码
default tablespace QCJ_TABLESPACE-- 表空间是上面创建的
temporary tablespace TEMP -- 临时表空间默认 TEMP
profile DEFAULT;
--password expire;
/*密码过期需要重设密码,意思是当你用这个新建立的密码过期用户首次登录后，系统会提示你重新输入新密码，不然会拒绝你登陆，重设新密码后就可以登录，该用户的密码就是你新设的密码，相当于首次登录修改密码这样的选项。*/

/* 用户授权_1*/
grant connect,resource,dba to qiuchangjin;

/* 用户授权_2*/
grant connect to qiuchangjin with admin option;
grant dba to qiuchangjin with admin option;
grant resource to qiuchangjin with admin option;

/*查询所有表空间物理位置*/
select name from v$datafile;
/*查询当前用户的表空间*/
select username,default_tablespace from user_users;
/*修改用户的默认表空间*/
alter user 用户名 default tablespace 新表空间; 
/*查询所有的表空间*/
select * from user_tablespaces; 

/* 删除表空间*/
alter tablespace QCJ_TABLESPACE offline;
drop tablespace QCJ_TABLESPACE including contents and datafiles;


-- 查看当前用户拥有的角色权限信息：
select * from role_sys_privs;

-- 查看当前用户的详细信息：
select * from user_users;

-- 查看当前用户的角色信息：
select * from user_role_privs;

-- 修改用户密码：
alter user 用户名 identified by 新密码；

-- 设置Oracle用户密码为无期限
ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;

```

## 表空间操作

```sql
-- 查看表空间物理文件路径
select * from dba_data_files

-- 修改表空间物理文件路径
-- 先登录sqlplus：
    C:\Documents and Settings\chezh>sqlplus  system/password as sysdba

-- 修改表空间为Offline： 
     SQL> alter tablespace users offline; 

-- 拷贝表空间文件 
     拷贝  C:\oracle\product\10.2.0\oradata\orcl\USERS01.DBF 到 D:\oracledata\orcl\USERS01.DBF

-- 修改oracle表空间指向地址 

    alter database rename file ‘原路径\USERS01.DBF' to '文件新路径\USERS01.DBF'; 
    SQL> alter database rename file 'C:\oracle\product\10.2.0\oradata\orcl\USERS01.DBF' to 'D:\oracledata\orcl\USERS01.DBF'

-- 手动删除表空间物理文件
     删除c:下的USERS01.DBF文件，并且以后数据全部会放在D:\oracledata

-- 修改表空间为Online 
     SQL> alter tablespace users online; 
```

## 密码操作

```sql
--结构：(profile, resource_name, resource_type, limited)
desc dba_profiles;

--password默认180天过期
select * from dba_profiles where profile='DEFAULT' and resource_name='PASSWORD_LIFE_TIME';

--设置密码不会过期
alter profile default limit password_life_time unlimited;

--建议定期修改密码，登陆sqlplus会提示输入新密码
sqlplus user/pass@orcl

--修改用户密码 
alter user [username] identified by [password] 

--下次登录时提示修改密码 
alter user [username] password expired 

--锁住用户 
alter user [username] account lock  

--解锁锁用户 
alter user [username] account unlock  

```





## 初始化数据

```sql
--表countries
create table countries (COUNTRY_ID CHAR(2) NOT NULL,COUNTRY_NAME VARCHAR2(40),REGION_ID NUMBER);
insert into countries values('CA','Canada',2);
insert into countries values('DE','Germany',1);
insert into countries values('UK','United Kingdom',1);
insert into countries values('US','United States of America',2);


--表departments
create table departments(DEPARTMENT_ID NUMBER(4) NOT NULL, DEPARTMENT_NAME VARCHAR2(30) NOT NULL,MANAGER_ID NUMBER(6),LOCATION_ID NUMBER(4));
insert into departments values(10,'Administartion',200,1700);
insert into departments values(20,'Marketing',201,1800);
insert into departments values(50,'Shipping',124,1500);
insert into departments values(60,'IT',103,1400);
insert into departments values(80,'Sales',149,2500);
insert into departments values(90,'Executive',100,1700);
insert into departments values(110,'Accounting',205,1700);
insert into departments values(190,'Contracting',null,1700);

--表employees
create table employees
(EMPLOYEE_ID NUMBER(6) NOT NULL,
FIRST_NAME VARCHAR2(20),
LAST_NAME VARCHAR2(25) NOT NULL,
EMAIL VARCHAR2(25) NOT NULL,
PHONE_NUMBER VARCHAR(20),
HIRE_DATE DATE NOT NULL,
JOB_ID VARCHAR2(10) NOT NULL,
SALARY NUMBER(8,2),
COMMISSION_PCT NUMBER(2,2),
MANAGER_ID NUMBER(6),
DEPARTMENT_ID NUMBER(4));

insert into employees values(100,'Steven','King','SKING','515.123.4567',to_date('1987-06-17','YYYY-MM-DD'),'AD_PRES',24000,NULL,NULL,90);
insert into employees values(101,'Neena','Kochhar','NKOCHHAR','515.123.4568',to_date('1989-09-21','YYYY-MM-DD'),'AD_VP',17000,NULL,100,90);
insert into employees values(102,'Lex','De Haan','LDEHAAN','515.123.4569',to_date('1993-01-13','YYYY-MM-DD'),'AD_VP',17000,NULL,100,90);
insert into employees values(103,'Alexander','Hunold','AHUNOLD','590.423.4567',to_date('1990-01-03','YYYY-MM-DD'),'IT_PROG',9000,NULL,102,60);
insert into employees values(104,'Bruce','Ernst','BERNST','590.423.4568',to_date('1991-05-21','YYYY-MM-DD'),'IT_PROG',6000,NULL,103,60);
insert into employees values(107,'Diana','Lorentz','DLORENTZ','590.423.5567',to_date('1999-02-07','YYYY-MM-DD'),'IT_PROG',4200,NULL,103,60);
insert into employees values(124,'Kevin','Mourgos','KMOURGOS','650.123.5234',to_date('1999-11-16','YYYY-MM-DD'),'ST_MAN',5800,NULL,100,50);
insert into employees values(141,'Trenna','Rajs','TRAJS','650.121.8009',to_date('1995-10-17','YYYY-MM-DD'),'ST_CLERK',3500,NULL,124,50);
insert into employees values(142,'Curtis','Davies','CDAVIES','650.121.2994',to_date('1997-01-29','YYYY-MM-DD'),'ST_CLERK',3100,NULL,124,50);
insert into employees values(143,'Randall','Matos','RMATOS','650.121.2874',to_date('1998-05-15','YYYY-MM-DD'),'ST_CLERK',2600,NULL,124,50);
insert into employees values(144,'Peter','Vargas','PVARGAS','650.121.2004',to_date('1998-07-09','YYYY-MM-DD'),'ST_CLERK',2500,NULL,124,50);
insert into employees values(149,'Eleni','Zlotkey','EZLOTKEY','011.44.1344.429018',to_date('2000-06-29','YYYY-MM-DD'),'SA_MAN',10500,.2,100,80);
insert into employees values(174,'Ellen','Abel','EABEL','011.44.1644.429267',to_date('1996-05-11','YYYY-MM-DD'),'SA_REP',11000,.3,149,80);
insert into employees values(176,'Jonathon','Taylor','JTAYLOR','011.44.1644.429265',to_date('1998-05-24','YYYY-MM-DD'),'SA_REP',8600,.2,149,80);
insert into employees values(178,'Kimberely','Grant','KGRANT','011.44.1644.429263',to_date('1999-05-24','YYYY-MM-DD'),'SA_REP',7000,.15,149,NULL);
insert into employees values(200,'Jennifer','Whalen','JWHALEN','515.123.4444',to_date('1987-09-17','YYYY-MM-DD'),'AD_ASST',4400,NULL,101,10);
insert into employees values(201,'Michael','Hartstein','MHARTSTE','515.123.5555',to_date('1996-02-17','YYYY-MM-DD'),'MK_MAN',13000,NULL,100,20);
insert into employees values(202,'Pat','Fay','PFAY','603.123.6666',to_date('1997-08-17','YYYY-MM-DD'),'MK_REP',6000,NULL,201,20);
insert into employees values(205,'Shelley','Higgins','SHIGGINS','515.123.8080',to_date('1994-06-07','YYYY-MM-DD'),'AC_MGR',12000,NULL,101,110);
insert into employees values(206,'William','Gietz','WGIETZ','515.123.8181',to_date('1994-06-07','YYYY-MM-DD'),'AC_ACCOUNT',8300,NULL,205,110);


--表 jobs
create table jobs(JOB_ID VARCHAR2(10) NOT NULL,JOB_TITLE VARCHAR2(35) NOT NULL,MIN_SALARY NUMBER(6),MAX_SALARY NUMBER(6));
insert into jobs values('AD_PRES','President',20000,40000);
insert into jobs values('AD_VP','Administration Vice President',15000,30000);
insert into jobs values('AD_ASST','Administration Assistant',3000,6000);
insert into jobs values('AC_MGR','Accounting Manager',8200,16000);
insert into jobs values('AC_ACCOUNT','Public Accountant',4200,9000);
insert into jobs values('SA_MAN','Sales Manager',10000,20000);
insert into jobs values('SA_REP','Sales Representative',6000,12000);
insert into jobs values('ST_MAN','Stock Manager',5500,8500);
insert into jobs values('ST_CLERK','Stock Clerk',2000,5000);
insert into jobs values('IT_PROG','Programmer',4000,10000);
insert into jobs values('MK_MAN','Marketing Manager',9000,15000);
insert into jobs values('MK_REP','Marketing Representative',4000,9000);

--表 job_grades
create table job_grades(GRADE_LEVEL VARCHAR2(3),LOWEST_SAL NUMBER,HIGHEST_SAL NUMBER);
insert into job_grades values('A',1000,2999);
insert into job_grades values('B',3000,5999);
insert into job_grades values('C',6000,9999);
insert into job_grades values('D',10000,14999);
insert into job_grades values('E',15000,24999);
insert into job_grades values('F',25000,40000);

--表 job_history
create table job_history(EMPLOYEE_ID NUMBER(6) NOT NULL,START_DATE DATE NOT NULL,END_DATE DATE NOT NULL,JOB_ID VARCHAR2(10) NOT NULL,DEPARTMENT_ID NUMBER(4));
insert into job_history values(102,to_date('1993-06-13','YYYY-MM-DD'),to_date('1998-07-24','YYYY-MM-DD'),'IT_PROG',60);
insert into job_history values(101,to_date('1989-09-21','YYYY-MM-DD'),to_date('1993-10-27','YYYY-MM-DD'),'AC_ACCOUNT',110);
insert into job_history values(101,to_date('1993-10-28','YYYY-MM-DD'),to_date('1997-05-15','YYYY-MM-DD'),'AC_MGR',110);
insert into job_history values(201,to_date('1996-02-17','YYYY-MM-DD'),to_date('1999-12-19','YYYY-MM-DD'),'MK_REP',20);
insert into job_history values(114,to_date('1998-05-24','YYYY-MM-DD'),to_date('1999-12-31','YYYY-MM-DD'),'ST_CLERK',50);
insert into job_history values(122,to_date('1999-01-01','YYYY-MM-DD'),to_date('1999-12-31','YYYY-MM-DD'),'ST_CLERK',50);
insert into job_history values(200,to_date('1987-09-18','YYYY-MM-DD'),to_date('1993-06-17','YYYY-MM-DD'),'AS_ASST',90);
insert into job_history values(176,to_date('1998-05-24','YYYY-MM-DD'),to_date('1998-12-31','YYYY-MM-DD'),'SA_REP',80);
insert into job_history values(176,to_date('1999-06-01','YYYY-MM-DD'),to_date('1999-12-31','YYYY-MM-DD'),'SA_MAN',80);
insert into job_history values(200,to_date('1994-07-01','YYYY-MM-DD'),to_date('1998-12-31','YYYY-MM-DD'),'AC_ACCOUNT',90);

--表 locations
create table locations(LOCATION_ID NUMBER(4) NOT NULL,STREER_ADDRESS VARCHAR2(40),POSTAL_CODE VARCHAR2(12),CITY VARCHAR2(30) NOT NULL,STATE_PROVINCE VARCHAR2(25),COUNTRY_ID CHAR(2));
insert into locations values(1400,'2014 Jabberwocky Rd','26192','Southlake','Texas','US');
insert into locations values(1500,'2011 Interiors Blvd','99236','South San Franciscon','California','US');
insert into locations values(1700,'2004 Charade Rd','98199','Seattle','Washington','US');
insert into locations values(1800,'460 Bloor St.W.','ON M5S 1X8','Toronto','Ontario','CA');
insert into locations values(2500,'Magdalen Centre,The Oxford Science Park','OX9 OZB','Oxford','Oxford','UK');

--表 regions
create table regions(REGION_ID NUMBER NOT NULL,REGION_NAME VARCHAR2(25));
insert into regions values(1,'Europe');
insert into regions values(2,'Americas');
insert into regions values(3,'Asia');
insert into regions values(4,'Middle East and Africa');

--表 sales_reps;
create table sales_reps
(ID NUMBER(6) NOT NULL,
NAME VARCHAR2(25)  NOT NULL,
SALARY NUMBER(8,2),
COMMISSION_PCT NUMBER(2,2));

select * from employees;
select last_name from employees;
select last_name as lastname, salary "sala",salary+300 from employees order by salary desc;
select last_name||' is a '||job_id from employees
select DISTINCT department_id from employees
SELECT employee_id,last_name,department_id from employees where department_id=90
SELECT employee_id,last_name,department_id , salary from employees where salary between 2500 and 3000
SELECT employee_id,last_name , salary from employees where salary in(2500,3000,5000,6000)
select employee_id, last_name from employees where last_name LIKE '%a%'
select employee_id ,manager_id from employees where manager_id is not null;
select last_name from employees where LOWER(last_name)='hunold'
select round(45.926,2) from dual
select trunc(45.926,2) from dual
select job_id from employees
select job_id,last_name,salary ,
CASE job_id
  WHEN 'IT_PROG' THEN 1.10*salary
    WHEN 'ST_CERK' THEN 1.15*salary
      ELSE salary
        END "REVISED_SALARY"
FROM employees 
SELECT last_name
      ,job_id
      ,salary
      ,CASE job_id
         WHEN 'IT_PROG' THEN
          1.10 * salary
         WHEN 'ST_CLERK' THEN
          1.15 * salary
         WHEN 'SA_REP' THEN
          1.20 * salary
         ELSE
          salary
       END   "REVISED_SALARY"
  FROM employees;
  
SELECT e.employee_id
      ,e.department_id
      ,d.department_name

  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id;

SELECT e.last_name
      ,e.department_id
      ,d.department_name
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id(+);

SELECT e.last_name
      ,e.department_id
      ,d.department_name
  FROM employees   e
      ,departments d
 WHERE e.department_id(+) = d.department_id;

```

## 查询操作

```sql
--select查询
select * from employees;

SELECT last_name, salary FROM employees;

SELECT e.*,d.department_name
  FROM employees e
      ,departments d
 WHERE e.department_id = d.department_id;
 
--表达式
SELECT last_name, salary, salary + 300, SALARY *2 FROM employees;
 

--关于NULL【初始数据为NULL，则执行表达式运算后依然是NULL】
SELECT e.last_name
      ,e.commission_pct
      ,e.commission_pct + 0.1
      ,e.commission_pct * 2
  FROM employees e;


--列别名【as 可以省略】
SELECT last_name as Name, commission_pct comm FROM employees;

SELECT last_name as "NamE", salary * 12  "Annual Salary" FROM employees;


--连接【||连接符号，不做其它运算】
SELECT last_name||job_id AS "Employees" FROM employees;

SELECT last_name || ' is a ' || job_id AS "Employee Details"
FROM employees;


--distinct 去重【对查询之后的结果去重】
select department_id from employees;

select distinct department_id from employees;


select last_name,department_id from employees;

select distinct last_name, department_id from employees;

--条件查询
select *
from employees e
where e.salary >=4400
and e.salary <=9000;

select *
from employees e
where e.department_id in (90,60); 

--模糊查询【_表示单个任意字符，%表示任意个字符】
select *
from employees e
where e.last_name like '_o%';

select *
from employees e
where e.last_name like 'k%';/*区分大小写*/

--is null
SELECT * FROM EMPLOYees e
where e.department_id is null;

--and 
select *  from employees e
where e.salary > 10000
and e.job_id like '%MAN%';

--or
select *  from employees e
where e.salary > 10000
or e.job_id like '%MAN%';

--is not null
SELECT * FROM EMPLOYees e
where e.department_id is not null;

-- not in
SELECT * FROM EMPLOYees e
where e.department_id not in (90);
```

## 替代变量

```plsql
-- 代替列名
select &column from departments; -- &columen是替代变量，相当于要查询表中的任意一个字段

-- 代替表名
select department_id from &table; --弹窗输入需要查询的表格

-- 用作变量，特别注意，如果是字符串类型，需要使用单引号，即select * from departments where department_name = '&department_name';
select * from departments where department_id = &id; --输入指定的值

-- 用作模糊查询
select * from departments where department_name like '%&department_name%'; --输入模糊关键字

```



## 字符操作

```sql
create table t_char(A varchar(200));
insert into t_char values('a_b');
insert into t_char values('acb');
insert into t_char values('ab');
insert into t_char values('a%b');
insert into t_char values('a''b');
insert into t_char values('a/b');
insert into t_char values('a\b');
insert into t_char values('%');
insert into t_char values('_');
insert into t_char values('%');
insert into t_char values('a');


select * from t_char;

select * from t_char 
where a like '%a%';

--通过escape指定转义字符
select * from t_char
where a like '%\%%' escape '\';

select * from t_char
where a like '%K%%' escape 'K'; /*查询的结果和前面的一样*/

```

## 排序

- asc:升序
- desc:降序

```sql
select last_name,job_id,department_id,hire_date
from employees 
order by hire_date asc;

SELECT employee_id, last_name, salary*12 annsal 
FROM employees 
ORDER BY annsal; 

SELECT last_name, department_id, salary 
FROM employees 
ORDER BY department_id , salary DESC; 
```

## 单行函数

- LOWER(last_name)
- UPPER(last_name)
- CONCAT(first_name,last_name)
- LENGTH(last_name)
- INSTR(last_name, 'a') "Contains 'a'?" 区分大小写
- SUBSTR(job_id, 4) = 'REP'; 
- ROUND(45.923,2)
- TRUNC(45.923,2)
- MOD(salary, 5000)



- MONTHS_BETWEEN('11-JAN-94','01-SEP-95')
- NEXT_DAY ('01-SEP-95','FRIDAY')
- NEXT_DAY ('01-SEP-95',1)
- ROUND('25-SEP-17','yyyy')
- ROUND(sysdate,'yyyy')



- sysdate
- TO_DATE('2016/07/01','yyyy/mm/dd HH24:MI:SS')
- to_char(sysdate,'YYYY/MM/DD HH:MI:SS')
- TO_NUMBER('$4,456,455.000','$9,999.999' )
- nvl(null,1)
- nvl2(null,'yes','no')
- NULLIF（expr1，expr2），如果两个参数相等，返回null，否则返回第一个。第一个参数不可指定为空。对于非数字类型参数，数据类型必须一致。
- COALESCE (expression_1, expression_2, ...,expression_n)依次参考各参数表达式，遇到非null值即停止并返回该值。如果所有的表达式都是空值，最终将返回一个空值。



- CASE job_id
           WHEN 'IT_PROG' THEN
            1.10 * salary
           WHEN 'ST_CLERK' THEN
            1.15 * salary
           WHEN 'SA_REP' THEN
            1.20 * salary
           ELSE
            salary
         END   "REVISED_SALARY"
- decode(job_id , 'IT_PROG' , 1.10 * salary , 'ST_CLERK' , 1.15 * salary , 'SA_REP' , 1.20 *salary ,  salary) revised_salary

```sql
SELECT employee_id, last_name, department_id 
FROM employees 
WHERE last_name = 'higgins'; /*Oracle数据库中的数据是大小写敏感的*/

SELECT employee_id, last_name, department_id 
FROM employees 
WHERE LOWER(last_name) = 'higgins'; 

SELECT employee_id, CONCAT(first_name, last_name) NAME,  job_id, 
LENGTH (last_name),  INSTR(last_name, 'a') "Contains 'a'?" /*INSTR区分大小写*/
FROM employees 
WHERE SUBSTR(job_id, 4) = 'REP'; 

SELECT ROUND(45.923,2), ROUND(45.923,0),  ROUND(45.923,-1)  FROM DUAL; /*ROUND(X[,Y])返回四舍五入后的值*/

SELECT TRUNC(45.923,2), TRUNC(45.923), TRUNC(45.923,-2)  FROM DUAL; /*TRUNC(X[,Y])返回x按精度y截取后的值*/

SELECT last_name, salary, MOD(salary, 5000) FROM employees  WHERE job_id = 'SA_REP'; /*取余*/


--日期和字符串的转换

'YYYY/MM/DD';
'YYYY/MM/DD HH24:MI:SS';
'YYYY/MM/DD HH:MI:SS';

'MM-DD-YYYY'

'DD-MMM-RR'
'DD-MMM-YY';



select MONTHS_BETWEEN('11-JAN-94','01-SEP-95') from dual;

select NEXT_DAY ('01-SEP-95','FRIDAY') from dual;

select NEXT_DAY ('01-SEP-95',1) from dual;

select ROUND('25-SEP-17','yyyy') from dual;

select ROUND(sysdate,'yyyy') from dual;



select sysdate from dual;

--to_char：日期——》字符串
--to_date: 字符串——》日期


SELECT TO_DATE('2016/07/01','yyyy/mm/dd HH24:MI:SS') from dual;

select to_char(sysdate,'YYYY/MM/DD HH:MI:SS') FROM DUAL;

select to_char(sysdate,'YYYY') FROM DUAL;

select months_between(sysdate + 31, sysdate) from dual;

select next_day(sysdate ,1) from dual;

select next_day(sysdate,'sunday') from dual;

select NEXT_DAY ('01-SEP-95',1) from dual;


SELECT NEXT_DAY (TO_DATE('1995-09-01','YYYY-MM-DD'),1) FROM DUAL;


select ROUND('25-JUL-95','MONTH') from DUAL;


select ROUND(SYSDATE ,'MONTH') from DUAL;

select ROUND(SYSDATE ,'YYYY') from DUAL;


------------------日期运算
SELECT last_name
     ,SYSDATE - hire_date
      ,(SYSDATE - hire_date) / 7 AS weeks
      ,SYSDATE + 1 AS tomorrow
      ,hire_date + 8/24
  FROM employees
 WHERE department_id = 90;



--格式转换函数
--to_date
--to_char
--to_number


select to_char(1234) from dual;



SELECT salary, TO_CHAR(salary, 'L99,999.00') SALARY FROM employees
WHERE last_name = 'Ernst';

alter session set NLS_CURRENCY = '￥';



SELECT salary, TO_CHAR(salary, '$99,999.00') SALARY FROM employees
WHERE last_name = 'Ernst';

select TO_NUMBER('$4,456','$9,999' )from dual;

select TO_NUMBER('$4,456,455.000','$9,999.999' )from dual;

--

--nvl,nvl2,nullif,colaesce
select null, nvl(null,1) from dual;


select NULLIF (1, 1),NULLIF(1, 2) from dual;

select COALESCE(null,null,1,null,2 ) from dual;


--条件表达式
SELECT last_name
      ,job_id
      ,salary
      ,CASE job_id
         WHEN 'IT_PROG' THEN
          1.10 * salary
         WHEN 'ST_CLERK' THEN
          1.15 * salary
         WHEN 'SA_REP' THEN
          1.20 * salary
         ELSE
          salary
       END   "REVISED_SALARY"
  FROM employees;

SELECT last_name
      ,job_id
      ,salary
      ,decode(job_id
             , 'IT_PROG'
             , 1.10 * salary
             
             , 'ST_CLERK'
             , 1.15 * salary
             
             , 'SA_REP'             
             , 1.20 * salary
             
             , salary) revised_salary
  FROM employees;
```

## 多表关联

```sql
--多表关联查询
--内连接
SELECT e.employee_id
      ,e.department_id
      ,d.department_name
      ,d.location_id
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id;

select * from employees   e;

select * from departments d;

--左连接，e表为主表
SELECT e.last_name
      ,e.department_id
      ,d.department_name
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id(+);
--右连接，d表为主表 
 SELECT e.last_name
      ,e.department_id
      ,d.department_id
      ,d.department_name
  FROM employees   e
      ,departments d
 WHERE e.department_id(+) = d.department_id;
 
select * from job_grades j;

SELECT e.last_name
      ,e.salary
      ,j.grade_level
  FROM employees  e
      ,job_grades j
 WHERE e.salary BETWEEN j.lowest_sal AND j.highest_sal;
 
 SELECT worker.last_name || ' works for ' || manager.last_name,worker.employee_id,worker.manager_id
FROM employees worker, employees manager
WHERE worker.manager_id = manager.employee_id ;



SELECT e.employee_id
      ,e.last_name
      ,d.location_id
  FROM employees e
  JOIN departments d
 USING (department_id);
 
 SELECT e.employee_id
      ,e.department_id
      ,d.department_name
      ,d.location_id
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id;


SELECT e.last_name
      ,e.department_id
      ,d.department_name
  FROM employees e
  FULL OUTER JOIN departments d
    ON (e.department_id = d.department_id);

```



## 分组计算和group by

```sql
--分组函数和group by子句

select *
  FROM employees
 WHERE job_id LIKE '%REP%';

11000.00
8600.00
7000.00
6000.00


SELECT AVG(salary)
      ,MAX(salary)
      ,MIN(salary)
      ,SUM(salary)
  FROM employees
 WHERE job_id LIKE '%REP%';
 
 select * from employees e;
 
 select count(e.department_id) from employees e;
 
 select count(1) from employees e;
 
 select count(*) from employees e;
 
 select distinct e.department_id from  employees e;


 select count(distinct e.department_id) from  employees e;
 
 select e.department_id from  employees e group by e.department_id;
 
 
 select count(*) from (select e.department_id from  employees e group by e.department_id);
 
 
 select count(department_id) from (select e.department_id from  employees e group by e.department_id);
 
 
 select sum(commission_pct) from employees;
 select count(*) from employees ;
 
 select (select sum(commission_pct) from employees)/(select count(*) from employees ) from dual
;--0.0425
select count(commission_pct) from employees;
select (select sum(commission_pct) from employees)/(select count(commission_pct) from employees ) from dual;
--0.2125

SELECT AVG(NVL(commission_pct, 0))
FROM employees;
--------group by

SELECT AVG(salary),department_id FROM employees
GROUP BY department_id ;

SELECT department_id dept_id
      ,job_id
      ,SUM(salary)
  FROM employees
 GROUP BY department_id
         ,job_id;
         

SELECT AVG(salary)
      ,department_id
  FROM employees
 GROUP BY department_id
 having AVG(salary) > 10000;

SELECT department_id, MAX(AVG(salary)) FROM employees GROUP BY department_id;

```



## 子查询

```sql
--子查询
SELECT last_name FROM employees WHERE salary > (SELECT salary FROM employees WHERE last_name = 'Abel');

SELECT employee_id
      ,last_name
      ,job_id
      ,salary
  FROM employees
 WHERE salary < ANY (SELECT salary FROM employees WHERE job_id = 'IT_PROG')
   AND job_id <> 'IT_PROG';


SELECT employee_id
      ,last_name
      ,job_id
      ,salary
  FROM employees
 WHERE salary < ALL (SELECT salary FROM employees WHERE job_id = 'IT_PROG')
   AND job_id <> 'IT_PROG';

```



## DML语句

```sql
--insert
INSERT INTO departments
  (department_id
  ,department_name)
VALUES
  (30
  ,'Purchasing');
  
select * from departments;  


INSERT INTO departments
VALUES (100, 'Finance', NULL, NULL);

select * from sales_reps;

INSERT INTO sales_reps
  (id
  ,NAME
  ,salary
  ,commission_pct)
  SELECT employee_id
        ,last_name
        ,salary
        ,commission_pct
    FROM employees
   WHERE job_id LIKE '%REP%';

select * from sales_reps;


INSERT INTO
  (SELECT employee_id
         ,last_name
         ,email
         ,hire_date
         ,job_id
         ,salary
         ,department_id
     FROM employees
    WHERE department_id = 50)
VALUES
  (99999
  ,'Taylor'
  ,'DTAYLOR'
  ,to_date('1999-07-07', 'YYYY-MM-DD')
  ,'ST_CLERK'
  ,5000
  ,50);
  

INSERT INTO
  (SELECT employee_id
         ,last_name
         ,email
         ,hire_date
         ,job_id
         ,salary
     FROM employees
    WHERE department_id = 50 WITH CHECK OPTION)
VALUES
  (99998
  ,'Smith'
  ,'JSMITH'
  ,to_date('1999-06-07', 'YYYY-MM-DD')
  ,'ST_CLERK'
  ,5000);
  
--UPDATE
SELECT  * FROM employees;
UPDATE employees SET department_id = 90 WHERE employee_id = 100;


--DELETE 
DELETE FROM departments
WHERE department_name = 'Finance';


SELECT *  FROM departments
WHERE department_name = 'Finance';

TRUNCATE TABLE departments;


--merge
MERGE INTO copy_emp c
USING employees e
ON (c.employee_id = e.employee_id)
WHEN MATCHED THEN
  UPDATE
     SET c.first_name = e.first_name
        ,c.last_name  = e.last_name
        ,c.email =e.email
        ,c.phone_number =e.phone_number
        ,c.hire_date = e.hire_date
        ,c.job_id = e.job_id
        ,c.salary = e.salary
        ,c.commission_pct = e.commission_pct
        ,c.manager_id = e.manager_id
        ,c.department_id = e.department_id
WHEN NOT MATCHED THEN
  INSERT
  VALUES
    (e.employee_id
    ,e.first_name
    ,e.last_name
    ,e.email
    ,e.phone_number
    ,e.hire_date
    ,e.job_id
    ,e.salary
    ,e.commission_pct
    ,e.manager_id
    ,e.department_id);

create table copy_emp as select * from employees where 1=2;

select * from copy_emp ;

```



## 锁

```sql
create table testtab3
( Pk1 number ,
field1 varchar2(200)
);

ALTER TABLE testtab3 ADD CONSTRAINT testtab3_PK PRIMARY KEY(Pk1) ;

insert into testtab3 values (1, 'AAA');

select * from testtab3 for update;

update testtab3 set field1 = 'AAAA' WHERE PK1 = 1;


SELECT a.*
      ,c.type
      ,c.lmode
  FROM v$locked_object a
      ,all_objects     b
      ,v$lock          c
 WHERE a.object_id = b.object_id
   AND a.session_id = c.sid
   AND b.object_name = 'TESTTAB3';

```

## 表

```sql
create table cux_table_tech_test
(test_id number,
test_name varchar2(100));

select * from cux_table_tech_test;

insert into cux_table_tech_test values (1,'aaa');

CREATE table cux_table_tech_test1 as select * from cux_table_tech_test;

select * from cux_table_tech_test1;


CREATE table cux_table_tech_test2 as select * from cux_table_tech_test where 1=2;

select * from cux_table_tech_test2;


---
ALTER TABLE cux_table_tech_test
ADD (test_code varchar2(30));

select * from cux_table_tech_test;

ALTER TABLE cux_table_tech_test
MODIFY (test_code NUMBER);

INSERT INTO cux_table_tech_test values (2,'BBB',10);

ALTER TABLE cux_table_tech_test
DROP (test_code)

```



## 约束

- 主键约束
- 外键约束
- 非空约束
- 唯一性约束
- 自定义约束

```sql
--约束
--drop table employees;
create table employees
(EMPLOYEE_ID NUMBER(6) NOT NULL, --非空约束
FIRST_NAME VARCHAR2(20),
LAST_NAME VARCHAR2(25) NOT NULL,
EMAIL VARCHAR2(25) NOT NULL,
PHONE_NUMBER VARCHAR(20),
HIRE_DATE DATE CONSTRAINT emp_hire_date_nn NOT NULL,
JOB_ID VARCHAR2(10) NOT NULL,
SALARY NUMBER(8,2) CONSTRAINT emp_salary_min CHECK(salary >1000), --自定义约束
COMMISSION_PCT NUMBER(2,2),
MANAGER_ID NUMBER(6),
DEPARTMENT_ID NUMBER(4),
--主键约束
CONSTRAINT emp_emp_id_pk PRIMARY KEY (EMPLOYEE_ID),
--唯一性约束 
CONSTRAINT emp_email_uk UNIQUE(email),
--外键约束 
CONSTRAINT emp_dept_fk FOREIGN KEY(department_id) REFERENCES departments(department_id)

);

ALTER TABLE employees ADD CONSTRAINT emp_emp_id_pk PRIMARY KEY(EMPLOYEE_ID);


---drop 
ALTER TABLE employees DROP CONSTRAINT emp_manager_fk;

ALTER TABLE departments DROP PRIMARY KEY CASCADE;  --department_id  employees

--
drop table  test_t1;
CREATE TABLE test_t1 (
 pk NUMBER PRIMARY KEY,
 fk NUMBER,
 col1 NUMBER,
 col2 NUMBER,
 CONSTRAINT fk_constraint FOREIGN KEY (fk) REFERENCES test_t1,
 CONSTRAINT ck1 CHECK (pk > 0 and col1 > 0),
 CONSTRAINT ck2 CHECK (col2 > 0));
 
ALTER TABLE test_t1 DROP (pk) CASCADE CONSTRAINTS;


SELECT constraint_name
      ,constraint_type
      ,search_condition
  FROM user_constraints
 WHERE table_name = 'TEST_T1';

```

## 视图

```sql
--创建视图的语法
create view viewName as <自定义查询>
```

```sql
--视图

SELECT employee_id, last_name, salary
FROM employees
WHERE department_id = 80;

CREATE VIEW empvu80 AS 
SELECT employee_id, last_name, salary
FROM employees
WHERE department_id = 80;

select * from empvu80;


--
CREATE VIEW dept_sum_vu
(name, minsal, maxsal, avgsal)
AS 
SELECT d.department_name
      ,MIN(e.salary)
      ,MAX(e.salary)
      ,AVG(e.salary)
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id
 GROUP BY d.department_name;
 
 select * from departments;
 
 select * from dept_sum_vu;
 
 
 DROP VIEW empvu80;
 

SELECT salary
      ,last_name
      ,e_rownum
      ,rownum
  FROM (SELECT rownum e_rownum
              ,e.salary
              ,e.last_name
          FROM employees e
         ORDER BY e.salary)
 WHERE rownum <= 3;

```



## 序列 索引 同义词

```sql
--创建序列语法
CREATE SEQUENCE sequence
[INCREMENT BY 1]
[START WITH 1000]
[{MAXVALUE 9999999999999999 | NOMAXVALUE}]
[{MINVALUE 1000 | NOMINVALUE}]
[{CYCLE | NOCYCLE}]
[{CACHE 20 | NOCACHE}];

1000
1001
1002
;

create SEQUENCE dept_deptid_seq
INCREMENT BY 50
MAXVALUE 999999
NOCACHE NOCYCLE;

--查询指定序列的下一个value
SELECT dept_deptid_seq.nextval
FROM dual;
--查询指定序列的当前value
SELECT dept_deptid_seq.currval
FROM dual;

INSERT INTO departments
  (department_id
  ,department_name
  ,location_id)
VALUES
  (dept_deptid_seq.nextval
  ,'Support'
  ,2500);


SELECT dept_deptid_seq.CURRVAL
FROM dual;

ALTER SEQUENCE dept_deptid_seq
INCREMENT BY 20
MAXVALUE 999999
NOCACHE NOCYCLE;

--索引
--创建索引的语法
CREATE INDEX emp_last_name_idx ON employees(last_name);


--同义词
create table cux.cux_order_headers_all(header_id number, order_number varchar2(50));

select * from cux_order_headers_all;

--创建同义词
CREATE SYNONYM cux_order_headers_all for cux.cux_order_headers_all;

select * from cux_order_headers_all;

```

## 控制用户权限

```sql
REM =======================================================
REM cleanup section
REM =======================================================
REM DROP USER HPOS CASCADE;
spool create_HPOS_schema
REM =======================================================
REM create user
REM =======================================================
CREATE USER HPOS IDENTIFIED BY HPOS;
ALTER USER HPOS DEFAULT TABLESPACE HPOS_DATA QUOTA UNLIMITED ON HPOS_DATA;
ALTER USER HPOS TEMPORARY TABLESPACE temp;
GRANT create session
, create table
, create procedure
, create sequence
, create trigger
, create view
, create synonym
, alter session
TO HPOS;
GRANT resource to HPOS;
exit;



GRANT create table, create view to a;

GRANT create table, create view to b;

GRANT create table, create view to c;
---=>
CREATE ROLE manager ;
GRANT create table, create view to manager;

GRANT manager to a, b,c;

--
GRANT update (department_name, location_id)
ON departments
TO scott, manager;


--
GRANT select, insert
ON departments
TO scott
WITH GRANT OPTION;

GRANT select
ON alice.departments
TO PUBLIC;


REVOKE select
ON departments
FROM scott;

--A数据库 想访问 B 数据库中的emp表
--在A 数据库中创建一个DBLINK
CREATE PUBLIC DATABASE LINK hq.acme.com
USING 'sales';

--A 数据库中执行
SELECT *
FROM emp@HQ.ACME.COM;

```



## group by 子句增强

```sql
--ROLLUP
SELECT department_id
      ,job_id
      ,SUM(salary)
  FROM employees
 WHERE department_id < 60
 GROUP BY ROLLUP(department_id, job_id);
 
--CUBE
SELECT department_id
      ,job_id
      ,SUM(salary)
  FROM employees
 WHERE department_id < 60
 GROUP BY CUBE(department_id, job_id);
 
--GROUPING
SELECT department_id deptid
      ,job_id job
      ,SUM(salary)
      ,GROUPING(department_id) grp_dept
      ,GROUPING(job_id) grp_job
  FROM employees
 WHERE department_id < 50
 GROUP BY ROLLUP(department_id, job_id);

--GROUPING
SELECT department_id deptid
      ,job_id job
      ,SUM(salary)
      ,GROUPING(department_id) grp_dept
      ,GROUPING(job_id) grp_job
  FROM employees
 WHERE department_id < 50
 GROUP BY cube(department_id, job_id); 
 
 
--GROUPING SETS
SELECT department_id
      ,job_id
      ,manager_id
      ,AVG(salary)
  FROM employees
 GROUP BY GROUPING SETS((department_id, job_id),(job_id, manager_id));
 

SELECT department_id
      ,job_id
      ,null manager_id
      ,AVG(salary)
  FROM employees
 GROUP BY department_id, job_id; 
 
 
 
SELECT null department_id
      ,job_id
      ,manager_id
      ,AVG(salary)
  FROM employees
 GROUP BY job_id, manager_id;
 
 
 
SELECT department_id
      ,job_id
      ,null manager_id
      ,AVG(salary)
  FROM employees
 GROUP BY department_id, job_id 
 union 
SELECT null department_id
      ,job_id
      ,manager_id
      ,AVG(salary)
  FROM employees
 GROUP BY job_id, manager_id;

```



## 子查询进阶

```sql
SELECT a.last_name
      ,a.salary
      ,a.department_id
      ,b.salavg
  FROM employees a
      ,(SELECT department_id
              ,AVG(salary) salavg
          FROM employees
         GROUP BY department_id) b
 WHERE a.department_id = b.department_id
   AND a.salary > b.salavg;
   
SELECT department_id
              ,AVG(salary) salavg
          FROM employees
         GROUP BY department_id ;
         

SELECT last_name
      ,salary
      ,department_id
  FROM employees  OUTER 
 WHERE salary > (SELECT AVG(salary) FROM employees WHERE department_id = outer.department_id);
 
--exists
SELECT employee_id
      ,last_name
      ,job_id
      ,department_id
  FROM employees  OUTER 
WHERE EXISTS (SELECT 'X' FROM employees WHERE manager_id = outer.employee_id);


SELECT employee_id
      ,last_name
      ,job_id
      ,department_id
  FROM employees
 WHERE employee_id IN (SELECT manager_id FROM employees WHERE manager_id IS NOT NULL);
 
--NOT EXISTS
SELECT department_id
      ,department_name
  FROM departments d
 WHERE NOT EXISTS (SELECT 'X' FROM employees WHERE department_id = d.department_id);

select * from employees;



SELECT department_id
      ,department_name
  FROM departments
 WHERE department_id NOT IN (SELECT department_id FROM employees where department_id is not null);



ALTER TABLE employees
ADD(department_name VARCHAR2(14));

select * from employees;

UPDATE employees e
   SET department_name =
       (SELECT department_name FROM departments d WHERE e.department_id = d.department_id);
       
--

DELETE FROM job_history jh
 WHERE employee_id =
       (SELECT employee_id
          FROM employees e
         WHERE jh.employee_id = e.employee_id
           AND start_date = (SELECT MIN(start_date) FROM job_history jh WHERE jh.employee_id = e.employee_id)
           AND 5 >
               (SELECT COUNT(*) FROM job_history jh WHERE jh.employee_id = e.employee_id GROUP BY employee_id HAVING COUNT(*) >= 4));



select * FROM job_history jh
 WHERE employee_id =
       (SELECT employee_id
          FROM employees e
         WHERE jh.employee_id = e.employee_id
           AND jh.start_date = (SELECT MIN(start_date) FROM job_history jh WHERE jh.employee_id = e.employee_id)
           AND 5 >
               (SELECT COUNT(*) FROM job_history jh WHERE jh.employee_id = e.employee_id GROUP BY employee_id HAVING COUNT(*) >= 4));

select * FROM job_history jh
 WHERE employee_id =
       (SELECT employee_id
          FROM employees e
         WHERE jh.employee_id = e.employee_id
           AND jh.start_date = (SELECT MIN(start_date) FROM job_history jh WHERE jh.employee_id = e.employee_id));
               
               
SELECT COUNT(*) FROM job_history jh  GROUP BY employee_id HAVING COUNT(*) >= 4;


---with

WITH dept_costs AS
 (SELECT d.department_name
        ,SUM(e.salary) AS dept_total
    FROM employees   e
        ,departments d
   WHERE e.department_id = d.department_id
   GROUP BY d.department_name),
   
avg_cost AS
 (SELECT SUM(dept_total) / COUNT(*) AS dept_avg FROM dept_costs)
 
SELECT * FROM dept_costs WHERE dept_total > (SELECT dept_avg FROM avg_cost) ORDER BY department_name;

SELECT d.department_name
      ,SUM(e.salary) AS dept_total
  FROM employees   e
      ,departments d
 WHERE e.department_id = d.department_id
 GROUP BY d.department_name;
 
 
 SELECT SUM(dept_total) / COUNT(*) AS dept_avg FROM dept_costs

```



## 递归查询

```sql
SELECT last_name || ' reports to ' || PRIOR last_name "Walk Top Down"
  FROM employees
 START WITH last_name = 'King'
CONNECT BY PRIOR employee_id = manager_id;


SELECT employee_id
      ,last_name
      ,job_id
      ,manager_id
  FROM employees
 START WITH employee_id = 101
CONNECT BY PRIOR manager_id = employee_id;


--

SELECT lpad(last_name, length(last_name) + (LEVEL * 2) - 2, '-') AS org_chart, LEVEL
  FROM employees
 START WITH last_name = 'King'
CONNECT BY PRIOR employee_id = manager_id

```



## insert增强

```sql
INSERT ALL INTO sal_history
VALUES
  (empid
  ,hiredate
  ,sal)
  
INTO mgr_history
VALUES
  (empid
  ,mgr
  ,sal)
  
  SELECT employee_id empid
        ,hire_date   hiredate
        ,salary      sal
        ,manager_id  mgr
    FROM employees
   WHERE employee_id > 200;
   
   

----
INSERT FIRST WHEN sal > 25000 THEN INTO special_sal
VALUES
  (deptid
  ,sal) 
  
  WHEN hiredate LIKE  ('%00%') THEN INTO hiredate_history_00
VALUES
  (deptid
  ,hiredate) 
  
  WHEN hiredate LIKE  ('%99%') THEN INTO hiredate_history_99
VALUES
  (deptid
  ,hiredate) 
  
  ELSE INTO hiredate_history
VALUES
  (deptid
  ,hiredate)
  SELECT department_id deptid
        ,SUM(salary) sal
        ,MAX(hire_date) hiredate
    FROM employees
   GROUP BY department_id;
   
create table special_sal(dept_id number ,sal number);


create table  hiredate_history_00(dept_id number ,hiredate date);


create table  hiredate_history_99(dept_id number ,hiredate date);


create table  hiredate_history(dept_id number ,hiredate date);

SELECT * FROM special_sal;

SELECT * FROM hiredate_history_00;

SELECT * FROM hiredate_history_99;

SELECT * FROM hiredate_history;


---列转行，一行变多行
INSERT ALL 
INTO sales_info VALUES (employee_id,week_id,sales_MON)
INTO sales_info VALUES (employee_id,week_id,sales_TUE) 
INTO sales_info VALUES (employee_id,week_id,sales_WED) 
INTO sales_info VALUES (employee_id,week_id,sales_THUR) 
INTO sales_info VALUES (employee_id,week_id, sales_FRI) 

SELECT EMPLOYEE_ID, week_id, sales_MON, sales_TUE, sales_WED, sales_THUR,sales_FRI FROM sales_source_data;

create table sales_source_data
(EMPLOYEE_ID number,
 week_id number, 
 sales_MON number, 
 sales_TUE number, 
 sales_WED number, 
 sales_THUR number,
 sales_FRI number);
 
create table sales_info(employee_id number,week_id number, sales_everyday number);
 
insert into sales_source_data values (149,1,10000,12000,15000,11000,6000);
 
select * from employees;
 
select * from sales_source_data;

select * from sales_info;

```

## sql进阶功能

```sql
SELECT e.last_name
      ,e.salary
      ,d.department_name
      ,AVG(e.salary) over(PARTITION BY d.department_name) department_avg_salary
      ,MAX(e.salary) over(PARTITION BY d.department_name) department_max_salary
      ,MIN(e.salary) over(PARTITION BY d.department_name) department_min_salary
  FROM employees   e
      ,departments d
 WHERE 1 = 1
   AND e.department_id = d.department_id;



SELECT e.last_name
      ,e.salary
      ,
      d.department_name
      ,AVG(e.salary)-- over(PARTITION BY d.department_name) department_avg_salary
      ,MAX(e.salary)-- over(PARTITION BY d.department_name) department_max_salary
      ,MIN(e.salary)-- over(PARTITION BY d.department_name) department_min_salary
  FROM employees   e
      ,departments d
 WHERE 1 = 1
   AND e.department_id = d.department_id
   
   group by e.last_name
      ,e.salary,d.department_name;

SELECT d.department_name
      ,e.last_name
      ,e.salary
      ,rank() over(PARTITION BY d.department_name ORDER BY e.salary DESC) dept_salary_rank1
      ,dense_rank() over(PARTITION BY d.department_name ORDER BY e.salary DESC) dept_salary_rank2
      ,row_number() over(PARTITION BY d.department_name ORDER BY e.salary DESC) dept_salary_rank3
  FROM employees   e
      ,departments d
 WHERE 1 = 1
   AND e.department_id = d.department_id;
   
   
---

select * from departments;

DELETE FROM departments
WHERE department_name = 'Shipping';

COMMIT;

SELECT * FROM departments WHERE department_name = 'Shipping'; 

insert into departments
SELECT * FROM departments AS OF TIMESTAMP SYSDATE -5/(24*60)
WHERE department_name = 'Shipping';   

---

CREATE GLOBAL TEMPORARY TABLE temp_table_session1 (header_id number) ON COMMIT PRESERVE ROWS;


CREATE GLOBAL TEMPORARY TABLE temp_table_session2 (header_id number) ON COMMIT delete  ROWS;

insert into temp_table_session1 values(1);

select * from temp_table_session1;


insert into temp_table_session2 values(1);

select * from temp_table_session2;


---===================================物化视图
create materialized view log on employees with PRIMARY KEY;

CREATE MATERIALIZED VIEW employees_90 --创建物化视图
BUILD IMMEDIATE --在视图编写好后创建  
REFRESH FAST WITH PRIMARY KEY --根据主表主键增量刷新（FAST，增量） 
ON DEMAND --在用户需要时，由用户刷新
ENABLE QUERY REWRITE --查询重写
AS
SELECT * FROM employees where department_id = 90;



--刷新


INSERT INTO employees
VALUES
  (208
  ,'Steven'
  ,'Lee'
  ,'LEE'
  ,'123.123.1234'
  ,to_date('1998-06-13', 'YYYY-MM-DD')
  ,'AD_VP'
  ,16000
  ,NULL
  ,100
  ,90
  ,'Executive');
  
select * from employees_90;

begin  
DBMS_MVIEW.REFRESH('EMPLOYEES_90','f');
end;


--删除

DROP MATERIALIZED VIEW LOG ON employees;

DROP MATERIALIZED VIEW employees_90; 


----------

SELECT length('中国') FROM dual;
SELECT lengthb('中国') FROM dual; --UTF-8汉字 3个字节

SELECT substr('上海汉得', 2, 2) FROM dual;
SELECT substr('上海汉得', 3, 2) FROM dual;

SELECT substrb('上海汉得', 2, 2) FROM dual;

SELECT substrb('上海汉得', 3, 2) FROM dual;

SELECT substrb('上海汉得', 4, 9) FROM dual;

---===================================VPD




--1）创建策略函数 Function:

CREATE OR REPLACE FUNCTION emp_vpd_test(p_schema VARCHAR2
                                       ,p_object VARCHAR2) RETURN VARCHAR2 AS
BEGIN
  RETURN 'employee_id >= 200';
END;


--2）对数据库对象应用策略函数：

BEGIN
  dbms_rls.add_policy(object_schema   => 'APPS'
                     ,object_name     => 'EMPLOYEES'
                     ,policy_name     => 'VPD_TEST'
                     ,function_schema => 'APPS'
                     ,policy_function => 'EMP_VPD_TEST');
END;


--3）通过查询数据字典，确认数据库对象上是否有策略函数：

SELECT * FROM dba_policies t WHERE t.object_name = 'EMPLOYEES';

SELECT * FROM EMPLOYEES;

--4) 失效策略

BEGIN
  dbms_rls.drop_policy(object_schema   => 'APPS'
                     ,object_name     => 'EMPLOYEES'
                     ,policy_name     => 'VPD_TEST'
                     --,function_schema => 'APPS'
                     --,policy_function => 'EMP_VPD_TEST'
                     );
END;


```

