# PL/SQL

## 变量和块

```plsql
alter session set nls_date_format = 'yyyy/mm/dd';

DECLARE

  v_hiredate DATE;
  v_deptno   NUMBER(2) NOT NULL := 10;
  v_location VARCHAR2(13) := 'Atlanta';

  c_comm CONSTANT NUMBER := 1400;

BEGIN
  v_hiredate := SYSDATE;
  dbms_output.put_line('the variable of v_hiredate is ' || v_hiredate);
EXCEPTION
  WHEN OTHERS THEN
    dbms_output.put_line('there is an exception in this program');
END;

--
DECLARE
  v_job       VARCHAR2(9);
  v_count     BINARY_INTEGER := 0;
  v_total_sal NUMBER(9, 2) := 0;
  v_orderdate DATE := SYSDATE + 7;
  c_tax_rate CONSTANT NUMBER(3, 2) := 8.25;
  v_valid BOOLEAN := FALSE;
BEGIN
  NULL;
END;


--
DECLARE
  v_salary employees.salary%TYPE;
BEGIN
  SELECT salary INTO v_salary FROM employees WHERE employee_id = 178;
  dbms_output.put_line('v_salary ' || v_salary);
END;

SELECT salary FROM employees WHERE employee_id = 178;


DECLARE
  v_sal NUMBER(9, 2) := &p_annual_sal;
BEGIN
  --  v_sal := v_sal / 0;
  v_sal := v_sal / 12;
  dbms_output.put_line('The monthly salary is ' || to_char(v_sal));

EXCEPTION
  WHEN OTHERS THEN
    dbms_output.put_line('there is some error in this program that you should go check out of!!');
END;


DECLARE
  v_name VARCHAR2(30) := 'AABBCC';
  v_temp VARCHAR2(30);

BEGIN
  IF length(v_name) < 10 THEN
    dbms_output.put_line(length(v_name));
  
  END IF;
  SELECT decode(v_name,
                'AA',
                'X',
                'BB',
                'Y',
                'AABBCC',
                'Z',
                'XYZ')
    INTO v_temp
    FROM dual;

  IF v_temp = 'Z' THEN
    --区分大小写！！！
    dbms_output.put_line(length(v_name));
  
  ELSE
    dbms_output.put_line(v_temp);
  END IF;

END;


--
BEGIN

  <<outer>>
  DECLARE
    birthdate DATE;
  
  BEGIN
    DECLARE
      birthdate DATE;
    BEGIN
      outer.birthdate := to_date('1975/06/01',
                                 'yyyy/MM/dd');
      dbms_output.put_line(outer.birthdate);
    
    END;
  
  END;

END;

SELECT SYSDATE,
       current_date,
       systimestamp,
       current_timestamp
  FROM dual;

----
BEGIN
  <<outer>>
  DECLARE
    v_sal     NUMBER(7, 2) := 60000;
    v_comm    NUMBER(7, 2) := v_sal * 0.20;
    v_message VARCHAR2(255) := ' eligible for commission';
  BEGIN
    DECLARE
      v_sal        NUMBER(7, 2) := 50000;
      v_comm       NUMBER(7, 2) := 0;
      v_total_comp NUMBER(7, 2) := v_sal + v_comm;
    BEGIN
      v_message    := 'CLERK not' || v_message;
      outer.v_comm := v_sal * 0.30;
    
      dbms_output.put_line('position1 V_MESSAGE : ' || v_message);
    END;
    v_message := 'SALESMAN' || v_message;
  END;

END;

```

## PL/SQL中的SQL语句

```plsql
--SET SERVEROUTPUT ON;
DECLARE
  v_sum_sal NUMBER(10,2);
  v_deptno  NUMBER NOT NULL := 60;
BEGIN
  SELECT SUM(salary) -- group function
    INTO v_sum_sal
    FROM employees
   WHERE department_id = v_deptno;

  dbms_output.put_line('The sum salary is ' || to_char(v_sum_sal));
END;
/


SELECT SUM(salary) -- group function
-- INTO v_sum_sal
  FROM employees
 WHERE department_id = 60;

-- 查询序列
select * from dba_sequences where sequence_name like '%emp%';

BEGIN
  INSERT INTO employees
    (employee_id,
     first_name,
     last_name,
     email,
     hire_date,
     job_id,
     salary)
  VALUES
    (employees_seq.nextval,
     'Ruth',
     'Cores',
     'RCORES',
     SYSDATE,
     'AD_ASST',
     4000);
END;
/
--update

DECLARE
  v_sal_increase employees.salary%TYPE := 800;
BEGIN
  UPDATE employees SET salary = salary + v_sal_increase WHERE job_id = 'ST_CLERK';
END;

select * from employees WHERE job_id = 'ST_CLERK';
/

UPDATE employees SET salary = salary + 800 WHERE job_id = 'ST_CLERK';
 


DECLARE
  v_deptno employees.department_id%TYPE := 90;
BEGIN
  DELETE FROM employees WHERE department_id = v_deptno;
    dbms_output.put_line('affected row '||sql%rowcount);
END;
/

DELETE FROM employees WHERE department_id = 10;
select * from employees where department_id = 90;

--

```

## PL/SQL中的控制语句

```plsql
IF 2>1 THEN
statements;
[ELSIF condition THEN
statements;]
[ELSE
statements;]
END IF;




--loop

DECLARE
  v_number NUMBER := 10;
BEGIN
  LOOP
  
    dbms_output.put_line('v_number : ' || v_number);
    v_number := v_number - 1;
    EXIT WHEN v_number < 6;
  END LOOP;
END;


DECLARE
  v_number NUMBER := 10;
BEGIN
  WHILE v_number > 6 LOOP
    dbms_output.put_line('v_number : '||v_number);
    v_number := v_number -1;
  
  END LOOP;
END;

--for 循环
--1、指定循环 1..10,
--2、游标循环

DECLARE
  --v_number NUMBER := 10;
BEGIN
  FOR i IN 1 .. 10 LOOP
  
    dbms_output.put_line('i*2 :' || i * 2);
  END LOOP;

END;


--嵌套循环
DECLARE
  v_counter       NUMBER := 0;
  v_counter_inner NUMBER;
BEGIN

  LOOP
    v_counter       := v_counter + 1;
    v_counter_inner := 10;  
    EXIT WHEN v_counter > 10;
    LOOP
      v_counter_inner := v_counter_inner - 1;    
      EXIT WHEN v_counter_inner < 8;    
      dbms_output.put_line('v_counter: ' || v_counter || ';   v_counter_inner : ' || v_counter_inner);    
    END LOOP;  
  END LOOP;
END;

--嵌套循环输出99乘法口诀表

BEGIN
  FOR i IN 1 .. 9 LOOP
    FOR j IN i .. 9 LOOP
      dbms_output.put_line(i || '*' || j || '=' || i * j);
    END LOOP;
  END LOOP;
END;

```

## PL/SQL中复杂自定义数据类型

```plsql
-- %TYPE
DECLARE
  v_employee_id         employees.employee_id%TYPE;
  v_employee_first_name employees.first_name%TYPE;
BEGIN
  FOR cursor_employee_id_list IN (SELECT e.employee_id FROM employees e) LOOP
    dbms_output.put_line(cursor_employee_id_list.employee_id);
    SELECT first_name
      INTO v_employee_first_name
      FROM employees
     WHERE employee_id = cursor_employee_id_list.employee_id;
    dbms_output.put_line(v_employee_first_name);
  END LOOP;
END;


-- TYPE <type_name> IS RECORD(XX XXX,...);
DECLARE
  TYPE emp_record_type IS RECORD(
    last_name VARCHAR2(25),
    job_id    VARCHAR2(10),
    salary    NUMBER(8, 2));

  emp_record emp_record_type;

BEGIN
  NULL;
END;

---%ROWTYPE

DECLARE
  emp_rec employees%ROWTYPE;
BEGIN
  SELECT * INTO emp_rec FROM employees WHERE employee_id = &employee_number;
  
  INSERT INTO retired_emps
    (empno
    ,ename
    ,job
    ,mgr
    ,hiredate
    ,leavedate
    ,sal
    ,comm
    ,deptno)
  VALUES
    (emp_rec.employee_id
    ,emp_rec.last_name
    ,emp_rec.job_id
    ,emp_rec.manager_id
    ,emp_rec.hire_date
    ,SYSDATE
    ,emp_rec.salary
    ,emp_rec.commission_pct
    ,emp_rec.department_id);
  COMMIT;
END;
/

--类似于数组的概念
DECLARE

  TYPE ename_table_type IS TABLE OF employees.last_name%TYPE INDEX BY BINARY_INTEGER;
  
  ename_table ename_table_type;
  
BEGIN

  NULL;
END;


DECLARE
  TYPE ename_table_type IS TABLE OF employees.last_name%TYPE INDEX BY BINARY_INTEGER;
  
  TYPE hiredate_table_type IS TABLE OF DATE INDEX BY BINARY_INTEGER;
  
  ename_table    ename_table_type;
  hiredate_table hiredate_table_type;
  
BEGIN
  ename_table(1) := 'CAMERON';
  hiredate_table(8) := SYSDATE + 7;
  IF ename_table.exists(1) THEN
    NULL;
  
  END IF;
END;
/

DECLARE
  TYPE emp_table_type IS TABLE OF employees%ROWTYPE INDEX BY BINARY_INTEGER;
  
  my_emp_table emp_table_type;
  v_count      NUMBER(3) := 104;
  
BEGIN
  FOR i IN 100 .. v_count LOOP
    SELECT * INTO my_emp_table(i) FROM employees WHERE employee_id = i;
  END LOOP;
  
  FOR i IN my_emp_table.first .. my_emp_table.last LOOP
    dbms_output.put_line(my_emp_table(i).last_name);
  END LOOP;  
END;

SELECT * FROM employees;

```

## 游标

### 隐式游标

- SQL%ROWCOUNT			受最近SQL语句影响的行数
- SQL%FOUND                     最近的SQL语句是否影响了一行以上的数据
- SQL%NOTFOUND             最近的SQL语句是否未影响任何数据
- SQL%ISOPEN                     对于隐式游标而言永远为FALSE



举例

```plsql
VARIABLE rows_deleted VARCHAR2(30)
DECLARE
	v_employee_id employees.employee_id%TYPE := 176;
BEGIN
	DELETE FROM employees
	WHERE employee_id = v_employee_id;
    :rows_deleted := (SQL%ROWCOUNT ||' row deleted.');
END;
/
PRINT rows_deleted
```



### 显式游标

1. 一行一行的处理返回的数据。
2. 保持当前处理行的一个跟踪，像一个指针一样指示当前的处理的记录。
3. 允许程序员在PLSQL 块中人为的控制游标的开启、关闭、上下移动

举例

**游标的一般写法**

```plsql
DECLARE
    v_empno employees.employee_id%TYPE;
    v_ename employees.last_name%TYPE;
    CURSOR emp_cursor IS	--定义游标
    SELECT employee_id, last_name
	FROM employees;
BEGIN
	OPEN emp_cursor;		--开启游标
	LOOP
        FETCH emp_cursor INTO v_empno, v_ename;		--提取当前行到变量
        EXIT WHEN emp_cursor%ROWCOUNT > 10 OR emp_cursor%NOTFOUND;	
        DBMS_OUTPUT.PUT_LINE (TO_CHAR(v_empno)||' '|| v_ename);
	END LOOP;
	CLOSE emp_cursor;		--关闭游标
END ;
```

**游标的简便写法**

省略游标的定义，只能使用一次

```plsql
BEGIN
    FOR emp_record IN (SELECT last_name, department_id FROM employees) LOOP
    -- implicit open and implicit fetch occur
    IF emp_record.department_id = 80 THEN
    END LOOP; -- implicit close occurs
END;
```

也可以写成【下面这种写法，可以多次使用游标】

```plsql
DECLARE
    CURSOR emp_cursor IS
    SELECT last_name, department_id
    FROM employees;
BEGIN
    FOR emp_record IN emp_cursor LOOP
    -- implicit open and implicit fetch occur
    IF emp_record.department_id = 80 THEN
    ...
	END LOOP; -- implicit close occurs
END;
```

### 带参数游标

类似于函数，可以通过给定不同实参，来查询相应的结果

```plsql
DECLARE
	CURSOR emp_cursor(p_deptno NUMBER, p_job VARCHAR2) IS
	SELECT employee_id, last_name
	FROM employees
	WHERE department_id = p_deptno AND job_id = p_job;
BEGIN
	OPEN emp_cursor (80, 'SA_REP'); --使用游标的时候，带上参数即可
	...
	CLOSE emp_cursor;
	
	OPEN emp_cursor (60, 'IT_PROG');
	...
END;
```

> FOR UPDATE NOWAIT 语句：
>
> 有的时候我们打开一个游标是为了更新或者删除一些记录，这种情况下我们希望在打开游标的时候即锁定相关记录，应该使用for update nowait 语句，倘若锁定失败我们就停止不再继续，避免出现长时间等待资源的死锁情况

```plsql
select * from employees;

---
DECLARE
 -- 定义游标
  CURSOR emp_cursor IS
    SELECT employee_id
          ,last_name
          ,departments.department_name
      FROM employees
          ,departments
     WHERE employees.department_id = departments.department_id
       AND employees.department_id = 90
    FOR UPDATE OF salary NOWAIT  --避免死锁
    ;

BEGIN
  FOR emp_data IN emp_cursor LOOP
    UPDATE employees e
       SET e.salary = 29999
    --where e.employee_id = emp_data.employee_id
     WHERE CURRENT OF emp_cursor; 
  END LOOP;

END;


--VARIABLE rows_deleted VARCHAR2(30)
DECLARE
rows_deleted VARCHAR2(30);
v_employee_id employees.employee_id%TYPE := 176;
BEGIN
DELETE FROM employees
WHERE employee_id = v_employee_id;
rows_deleted := (SQL%ROWCOUNT ||
' row deleted.');
dbms_output.put_line(rows_deleted);

if SQL%NOTFOUND then
  
dbms_output.put_line(
'SQL%NOTFOUND');
end if;

END;
/

select * from employees;


DECLARE
  v_empno employees.employee_id%TYPE;
  v_ename employees.last_name%TYPE;
  CURSOR emp_cursor IS
    SELECT employee_id
          ,last_name
      FROM employees;
BEGIN
  OPEN emp_cursor;
  LOOP
    FETCH emp_cursor
      INTO v_empno
          ,v_ename;
    EXIT WHEN emp_cursor%ROWCOUNT > 3 OR emp_cursor%NOTFOUND;
    dbms_output.put_line(to_char(v_empno) || ' ' || v_ename);
  END LOOP;
  
  CLOSE emp_cursor;
END;




select * from employees;

BEGIN
  FOR emp_record IN (SELECT last_name
                           ,department_id
                       FROM employees) LOOP
    -- implicit open and implicit fetch occur
    IF emp_record.department_id = 90 THEN
    dbms_output.put_line(emp_record.last_name);
    END IF;
  END LOOP; -- implicit close occurs
END;


DECLARE
  CURSOR emp_cursor IS
    SELECT last_name
          ,department_id
      FROM employees;
BEGIN
  FOR emp_record IN emp_cursor LOOP
    -- implicit open and implicit fetch occur
    IF emp_record.department_id = 90 THEN
      dbms_output.put_line(emp_record.last_name);
    END IF;
  END LOOP; -- implicit close occurs
  
  FOR emp_record2 IN emp_cursor LOOP
     dbms_output.put_line(emp_record2.last_name);
  end loop;
  
END;


---
DECLARE
  CURSOR emp_cursor IS
    SELECT employee_id
          ,last_name
          ,departments.department_name
      FROM employees
          ,departments
     WHERE employees.department_id = departments.department_id
       AND employees.department_id = 90
       --FOR UPDATE OF salary NOWAIT
       ;

BEGIN
  FOR emp_data IN emp_cursor LOOP
    update employees e set e.salary = 19999
    where e.employee_id = emp_data.employee_id;
  END LOOP;

END;



select * from employees;

```

## 例外处理

例外处理一般语法：

```plsql
EXCEPTION
    WHEN exception1 [OR exception2 . . .] THEN
    statement1;
    statement2;
    . . .
    [WHEN exception3 [OR exception4 . . .] THEN
    statement1;
    statement2;
    . . .]
    [WHEN OTHERS THEN
    statement1;
    statement2;
    . . .]
```

### 一些常用的预定义例外

参考链接：https://docs.oracle.com/cd/A97630_01/appdev.920/a96624/07_errs.htm#725

```plsql
NO_DATA_FOUND
TOO_MANY_ROWS
INVALID_CURSOR
ZERO_DIVIDE
DUP_VAL_ON_INDEX
```

NO_DATA_FOUND 和 TOO_MANY_ROWS  是最常见的例外，大多数Block 中都建议对这两种例外有处理。

### 例外处理

Oracle提供了两个内置函数，分别用来返回例外代码和描述信息

- SQLCODE
- SQLERRM

```plsql
DECLARE
  v_error_code    NUMBER;
  v_error_message VARCHAR2(255);
BEGIN
  ...
EXCEPTION 
  ...
  WHEN OTHERS THEN ROLLBACK;
  v_error_code    := SQLCODE;
  v_error_message := SQLERRM;
  INSERT INTO errors
  VALUES
    (v_error_code,
     v_error_message);
END;
```

### 自定义例外和已有预定义例外关联

Oracle支持自定义例外，并通过错误代码关联预定义例外

```plsql
DECLARE
  p_deptno NUMBER := 10;
  e_emps_remaining EXCEPTION;
  PRAGMA EXCEPTION_INIT(e_emps_remaining,-2292); --和已有预定义例外关联
BEGIN
  DELETE FROM departments WHERE department_id = &p_deptno;
  COMMIT;
EXCEPTION
  WHEN e_emps_remaining THEN
    dbms_output.put_line('Cannot remove dept ' || to_char(&p_deptno) || '. Employees exist. ');
END;

```

### 忽略自定义例外定义，直接抛出

RAISE_APPLICATION_ERROR()  函数：对于用户自定义的业务错误，如果觉得先定义再使用很麻烦，那么也可以简单的使用raise_application_error()  来简化处理。它可以无需预先定义错误，而在需要抛出错误的地方直接使用此函数抛出例外。

```plsql
BEGIN
	...
	DELETE FROM employees
	WHERE manager_id = v_mgr;
	IF SQL%NOTFOUND THEN
	RAISE_APPLICATION_ERROR(-20202,'This is not a valid manager');
	END IF;
```

## 存储过程

语法

```plsql
CREATE [OR REPLACE] PROCEDURE procedure_name[(parameter1 [mode1] datatype1,parameter2 [mode2] datatype2,. . .)]
IS|AS
PL/SQL Block;
```

举例

```plsql
CREATE OR REPLACE PROCEDURE raise_salary(p_id IN employees.employee_id%TYPE) IS
BEGIN
  UPDATE employees SET salary = salary * 1.10 WHERE employee_id = p_id;
END raise_salary;
```

### 存储过程的参数模式

| IN                           | OUT                      | IN OUT                             |
| ---------------------------- | ------------------------ | ---------------------------------- |
| 默认模式                     | 必须显式指定             | 必须显式指定                       |
| 把值传给过程                 | 把值从过程返回给调用环境 | 把变量传递给过程，并返回给调用环境 |
| 参数可以是常数、变量、表达式 | 必须是个变量             | 必须是个变量                       |
| 可以赋予默认值               | 不能赋予默认值           | 不能赋予默认值                     |

举例：

```plsql
--存储过程创建【使用Procedure窗口来创建】
CREATE OR REPLACE PROCEDURE query_emp
(
  p_id     IN employees.employee_id%TYPE,
  p_name   OUT employees.last_name%TYPE,
  p_salary OUT employees.salary%TYPE,
  p_comm   OUT employees.commission_pct%TYPE
) IS
BEGIN
  SELECT last_name,
         salary,
         commission_pct
    INTO p_name,
         p_salary,
         p_comm
    FROM employees
   WHERE employee_id = p_id;
END query_emp;


--调用
DECLARE
  v_id     employees.employee_id%TYPE := 100;
  v_name   employees.first_name%TYPE;
  v_salary employees.salary%TYPE;
  v_comm   employees.commission_pct%TYPE;
BEGIN
  query_emp(p_id     => v_id,
            p_name   => v_name,
            p_salary => v_salary,
            p_comm   => v_comm);
  dbms_output.put_line('v_id ' || v_id || ' v_name ' || v_name || ' v_salary ' || v_salary || ' v_comm ' || v_comm);
EXCEPTION
  WHEN OTHERS THEN
    dbms_output.put_line('SQLCODE ' || SQLCODE || ' SQLERRM ' || SQLERRM);
END;

SELECT * FROM EMPLOYEES;
```

### 存储过程调用

参数传递方式有两种：

- 按顺序传递
- 使用=>符合传递【英文符号】【推荐用法】

举例

```plsql
--过程定义
CREATE OR REPLACE PROCEDURE add_dept
(
  p_name IN departments.department_name%TYPE DEFAULT 'unknown', --in参数类型可以赋予默认值
  p_loc  IN departments.location_id%TYPE DEFAULT 1700
) IS
BEGIN
  INSERT INTO departments
    (department_id,
     department_name,
     location_id)
  VALUES
    (departments_seq.nextval,
     p_name,
     p_loc);
END add_dept;

--过程被调用
BEGIN
  add_dept; --使用默认值
  add_dept('TRAINING',
           2500);  --按照顺序传递
  add_dept(p_loc  => 2400,
           p_name => 'EDUCATION');   --通过符号=>传递，可以忽略顺序
  add_dept(p_loc => 1200);   --只传入单个，另外一个参数取默认值
END;

```

## 存储函数

语法：

```plsql
CREATE [OR REPLACE] FUNCTION function_name
[(parameter1 [mode1] datatype1,
parameter2 [mode2] datatype2,
. . .)]
RETURN datatype
IS|AS
PL/SQL Block;
```

举例：

```plsql
CREATE OR REPLACE FUNCTION get_sal(p_id IN employees.employee_id%TYPE) 
	RETURN NUMBER --与存储过程的差异
	IS
  	v_salary employees.salary%TYPE := 0;
BEGIN
  SELECT salary INTO v_salary FROM employees WHERE employee_id = p_id;
  RETURN v_salary; --与存储过程的差异
END get_sal;
```

## 包

package包括package specification 和package body

```plsql
CREATE OR REPLACE PACKAGE taxes
IS
--定义全局变量tax
tax NUMBER;
... -- declare all public procedures/functions
END taxes;
```

```plsql
CREATE OR REPLACE PACKAGE BODY taxes
IS
... -- declare all private variables
... -- define public/private procedures/functions
BEGIN
	--这里是对全局变量tax进行初始化，只会在session被加载时执行一次，一般用于复杂变量的初始化，如果没有这个需求，可以直接在这里写个NULL;即可
    SELECT rate_value
    INTO tax
    FROM tax_rates
    WHERE rate_name = 'TAX';
END taxes;
```

在**同一**session中，包中的公有变量可以被共享，只能通过函数来查询【指的是在包外或者命令行中，不能直接select公有变量】。



## 内置工具包

### 动态SQL

动态SQL 可以使用Oracle  内置包 **DBMS_SQL**  来执行，也可以使用**EXECUTE IMMEDIATE** 语句来执行。

举例：

```plsql
CREATE OR REPLACE PROCEDURE delete_all_rows
(p_tab_name IN VARCHAR2, p_rows_del OUT NUMBER)
IS
	cursor_name INTEGER;		--定义局部变量cursor_name
BEGIN
	cursor_name := DBMS_SQL.OPEN_CURSOR;	--工具包DBMS_SQL.OPEN_CURSOR;
	DBMS_SQL.PARSE(cursor_name, 'DELETE FROM '||p_tab_name,
	DBMS_SQL.NATIVE );		--动态sql编译
	p_rows_del := DBMS_SQL.EXECUTE (cursor_name); --将sql执行结果赋值给形参p_rows_del并返回
	DBMS_SQL.CLOSE_CURSOR(cursor_name);		--关闭？
END;
```

EXECUTE IMMEDIATE 执行例子

```plsql
CREATE PROCEDURE del_rows
(p_table_name IN VARCHAR2,
p_rows_deld OUT NUMBER)
IS
BEGIN
    EXECUTE IMMEDIATE 'delete from '||p_table_name;
    p_rows_deld := SQL%ROWCOUNT;
END;
```

### 程序中执行DDL

如果想在程序中执行DDL, 可使用Oracle  内置包：DBMS_DDL

比如在程序中执行编译命令

```plsq
DBMS_DDL.ALTER_COMPILE('PROCEDURE','A_USER','QUERY_EMP')
```

比如在程序中执行数据收集命令

```plsql
DBMS_DDL.ANALYZE_OBJECT('TABLE','A_USER','JOBS','COMPUTE')
```



### JOB

Oracle 数据库JOB ：定义JOB  可以定期执行某个程序。
应用场景：比如每隔一周对某些表进行数据收集，以确保CBO 正确，又比如在消息处理机制中，每隔5 分钟对
消息队列进行扫描处理等。 
Oracle 提供内置包 DBMS_JOB ，可完成JOB 的定义、提交、更改、停止、移除。

例子：提交一个JOB  每隔1 天执行一次

```plsql
DECLARE
	jobno NUMBER;
BEGIN
    DBMS_JOB.SUBMIT (
    job => jobno ,
    what => 'OVER_PACK.ADD_DEPT(''EDUCATION'',2710);',
    next_date => TRUNC(SYSDATE + 1),
    interval => 'TRUNC(SYSDATE + 1)'
    );
    dbms_output.put_line('job_no ='|| jobno )
    COMMIT;
END
```

例子 ：更改JOB 的执行频率为：每4 小时执行一次

```plsql
BEGIN
	DBMS_JOB.CHANGE(1, NULL, TRUNC(SYSDATE+1)+6/24, 'SYSDATE+4/24');
END;
```

如何找到自己提交的JOB号：

```plsql
SELECT job, log_user, next_date, next_sec, broken, what FROM DBA_JOBS;
```



## 触发器

创建Trigger ：

- 时机：Before 或者 After 或 Instead of

- 事件：Insert  或 Update 或 Delete

- 对象：表名（或视图名）

- 类型：Row 或者 Statement 级；

- 条件：满足特定Where 条件才执行；

- 内容：通常是一段PLSQL 块代码；

  

重点注意：
  Instead of :  用Trigger 的内容替换事件本身的动作
  Row 级：SQL 语句影响到的每一行都会引发Trigger
  Statement 级：一句SQL 语句引发一次，不管它影响多少行（甚至0 行）



创建statement级别trigger语法：

```plsql
CREATE [OR REPLACE] TRIGGER trigger_name
	timing
		event1 [OR event2 OR event3]
			ON table_name
trigger_body
```

- 单一事件Trigger

```plsql
CREATE OR REPLACE TRIGGER secure_emp
	BEFORE INSERT ON employees
BEGIN
   IF (TO_CHAR(SYSDATE,'DY') IN ('SAT','SUN')) OR
   (TO_CHAR(SYSDATE,'HH24:MI') NOT BETWEEN '08:00' AND '18:00')
   THEN RAISE_APPLICATION_ERROR (-20500,'You may insert into EMPLOYEES table only during business hours.');
   END IF;
END;
```

- 多事件Trigger，在Trigger Body 中判断具体事件

```plsql
CREATE OR REPLACE TRIGGER secure_emp
BEFORE INSERT OR UPDATE OR DELETE ON employees
BEGIN
    IF (TO_CHAR (SYSDATE,'DY') IN ('SAT','SUN')) OR
    (TO_CHAR (SYSDATE, 'HH24') NOT BETWEEN '08' AND '18')
        THEN
        IF DELETING THEN
        	RAISE_APPLICATION_ERROR (-20502,'You may delete from EMPLOYEES
        	table only during business hours.');
        ELSIF INSERTING THEN
        	RAISE_APPLICATION_ERROR (-20500,'You may insert into
        	EMPLOYEES table only during business hours.');
        ELSIF UPDATING ('SALARY') THEN
        	RAISE_APPLICATION_ERROR (-20503,'You may update
        	SALARY only during business hours.');
        ELSE
        	RAISE_APPLICATION_ERROR (-20504,'You may update
        	EMPLOYEES table only during normal hours.');
        END IF;
    END IF;
END;
```

创建Row 级别Trigger  语法

```plsql
CREATE [OR REPLACE] TRIGGER trigger_name
	timing
	event1 [OR event2 OR event3]
	ON table_name
	[REFERENCING OLD AS old | NEW AS new]
	FOR EACH ROW
	[WHEN (condition)]
	trigger_body
```

举例：

```plsql
CREATE OR REPLACE TRIGGER restrict_salary
	BEFORE INSERT OR UPDATE OF salary ON employees
	FOR EACH ROW
BEGIN
    IF NOT (:NEW.job_id IN ('AD_PRES', 'AD_VP')) AND :NEW.salary > 15000
    THEN
    	RAISE_APPLICATION_ERROR (-20202,'Employee cannot earn this amount');
    END IF;
END;
```

