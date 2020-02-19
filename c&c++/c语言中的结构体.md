# c语言中的结构体

## 1.结构体是什么

在C编程中，结构体是单个名称下的变量（可以是不同类型）的集合。

### 1.1定义结构体

```
struct structureName 
{
    dataType member1;
    dataType member2;
    ...
};
```

代码示例：

```c
struct Person
{
    char name[50];
    int citNo;
    float salary;
};
```

### 1.2创建结构体

```c
struct Person
{
    char name[50];
    int citNo;
    float salary;
};

int main()
{
    struct Person person1, person2, p[20];
    return 0;
}
```

也可以这样创建

```c
struct Person
{
    char name[50];
    int citNo;
    float salary;
} person1, person2, p[20];
```

### 1.3 访问结构体的属性

你可以使用两种操作符来访问结构体的属性：

- `.` member operator
- `->` Structure pointer operator 

代码示例：

```c
//结构体属性的访问
#include<stdio.h>

struct Person {
	char name[50];
	int citNo;
	float salary;
};

int main(void) {
	//首先使用结构体语法定义一个person变量，以及一个指向person变量的指针
	struct Person *personPtr, person;
	//指针指向person1变量
	personPtr = &person;
	
	
	printf("Enter name: ");
    scanf("%s", &personPtr->name);
    
    printf("Enter citNo: ");
    scanf("%d", &personPtr->citNo);
    
    printf("Enter salary: ");
    scanf("%f", &personPtr->salary);
    
	//1.使用指针访问属性
	//1.1使用->操作符
	printf("%s\n",personPtr->name);
	printf("%d\n",personPtr->citNo);
	printf("%f\n",personPtr->salary);

	//1.2使用.操作符
	printf("%s\n",(*personPtr).name);
	printf("%d\n",(*personPtr).citNo);
	printf("%f\n",(*personPtr).salary);

	//2.使用变量访问属性
	printf("%s\n",person.name);
	printf("%d\n",person.citNo);
	printf("%f\n",person.salary);
	
}
```

> 注意：这里使用变量访问属性的时候，并没有使用`->`操作符，因为变量（这里就是默认值结构体变量）不支持该操作符，可以参考如下图片：

![Snipaste_2019-10-29_20-40-57.png](http://ww1.sinaimg.cn/large/008048Tsgy1g8fcs9e6d1j30ue085mz4.jpg)

Output

```
Enter name: eric
Enter citNo: 1
Enter salary: 1.1
eric
1
1.100000
eric
1
1.100000
eric
1
1.100000
```

## 2.`typedef`关键字

这个关键字可以用来给结构体起别名。

**下列代码**

```c
struct Distance{
    int feet;
    float inch;
};

int main() {
    struct Distance d1, d2;
}
```

**和以下代码效果一致**

```c
typedef struct Distance{
    int feet;
    float inch;
} distances;

int main() {
    distances d1, d2;
}
```

## 3.结构体嵌套

你可以在一个结构体里面定义子结构体属性，代码示例如下：

```c
struct complex
{
 int imag;
 float real;
};

struct number
{
   struct complex comp;
   int integers;
} num1, num2;

//这样来访问子结构体的属性
num2.comp.imag = 11;
```

## 4.结构体中的动态内存分配

在继续阅读之前，可以先参考之前的一篇文章[C语言动态内存分配](https://www.cnblogs.com/ericling/p/11746972.html) 

代码示例：

```c
#include <stdio.h>
#include <stdlib.h>
struct person {
   int age;
   float weight;
   char name[30];
};
int main()
{
   struct person *ptr;
   int i, n;
   printf("Enter the number of persons: ");
   scanf("%d", &n);
   // allocating memory for n numbers of struct person
   ptr = (struct person*) malloc(n * sizeof(struct person));
   for(i = 0; i < n; ++i)
   {
       printf("Enter first name and age respectively: ");
       scanf("%s %d", &(ptr+i)->name, &(ptr+i)->age);
   }
   printf("Displaying Information:\n");
   for(i = 0; i < n; ++i)
       printf("Name: %s\tAge: %d\n", (ptr+i)->name, (ptr+i)->age);
   return 0;
}
```

Output

```c
Enter the number of persons: 3
Enter first name and age respectively: ericling 22
Enter first name and age respectively: bob 23
Enter first name and age respectively: lucy 24
Displaying Information:
Name: ericling  Age: 22
Name: bob       Age: 23
Name: lucy      Age: 24
```

## 5.结构体变量传参和返参

### 5.1使用结构体变量来传递参数

代码示例：

```c
#include <stdio.h>
struct student
{
    char name[50];
    int age;
};
// function prototype
void display(struct student s);
int main()
{
    struct student s1;
    printf("Enter name: ");
    scanf("%[^\n]%*c", s1.name);
    printf("Enter age: ");
    scanf("%d", &s1.age);
    
    display(s1);   // passing struct as an argument
    
    return 0;
}
void display(struct student s) 
{
  printf("\nDisplaying information\n");
  printf("Name: %s", s.name);
  printf("\nAge: %d", s.age);
}
```

Output

```c
Enter name: eric
Enter age: 23

Displaying information
Name: eric
Age: 23
```

### 5.2函数返参结构体变量

代码示例

```c
//函数返回结构体变量 
#include <stdio.h>
struct student
{
    char name[50];
    int age;
};
// function prototype
struct student getInformation();
int main()
{
    struct student s;
    s = getInformation();
    printf("\nDisplaying information\n");
    printf("Name: %s", s.name);
    printf("\nRoll: %d", s.age);
    
    return 0;
}
struct student getInformation() 
{
  struct student s1;
  printf("Enter name: ");
  scanf ("%[^\n]%*c", s1.name);
  printf("Enter age: ");
  scanf("%d", &s1.age);
  
  return s1;
}	
```

Output:

```c
Enter name: eric
Enter age: 23

Displaying information
Name: eric
Roll: 23

```

### 5.3结构体变量参数的引用传递

代码示例：

```c
#include <stdio.h>
typedef struct Complex
{
    float real;
    float imag;
} complex;
void addNumbers(complex c1, complex c2, complex *result); 
int main()
{
    complex c1, c2, result;
    printf("For first number,\n");
    printf("Enter real part: ");
    scanf("%f", &c1.real);
    printf("Enter imaginary part: ");
    scanf("%f", &c1.imag);
    
    printf("For second number, \n");
    printf("Enter real part: ");
    scanf("%f", &c2.real);
    printf("Enter imaginary part: ");
    scanf("%f", &c2.imag);
    
    //值传递测试，c1和c2都是属于值传递，result传递的是地址
    addNumbers(c1, c2, &result); 
    printf("\nresult.real = %.1f\n", result.real);
    printf("result.imag = %.1f", result.imag);
    
    //打印值传递的两个结构体的属性，查看是否被修改
	printf("\nc1.real = %.1f", c1.real);
	printf("\nc2.real = %.1f\n", c2.real);
    
    return 0;
}
void addNumbers(complex c1, complex c2, complex *result) 
{
     result->real = c1.real + c2.real;
     result->imag = c1.imag + c2.imag; 
     c1.real = 1.1111;
     c2.real = 2.2222;
}
```

Output:

```c
For first number,
Enter real part: 3
Enter imaginary part: 1.3
For second number,
Enter real part: 4
Enter imaginary part: 1.4

result.real = 7.0
result.imag = 2.7
c1.real = 3.0
c2.real = 4.0
```

可以看到，引用传递的result的两个属性都已经修改了，而值传递的c1，c2的real属性还是用户输入的数据。

## 6.`union`关键字

`union`关键字的用法和`struct`关键字非常像，代码示例如下：

### 6.1定义union

```c
union car
{
  char name[50];
  int price;
};
```



### 6.2创建union

```c
union car
{
  char name[50];
  int price;
};
int main()
{
  union car car1, car2, *car3;
  return 0;
}
```

或者这样

```c
union car
{
  char name[50];
  int price;
} car1, car2, *car3;
 
```



### 6.3访问属性

和`struct`一致，使用.操作符来访问变量属性，使用->操作符来访问指针属性。

### 6.4`union`和`struct`的不同

看来看去，好像union和struct并没有什么不同，看一下下面的代码

```c
#include <stdio.h>
union unionJob
{
   //defining a union
   char name[32];
   float salary;
   int workerNo;
} uJob;

struct structJob
{
   char name[32];
   float salary;
   int workerNo;
} sJob;

int main()
{
   printf("size of union = %d bytes", sizeof(uJob));
   printf("\nsize of structure = %d bytes", sizeof(sJob));
   return 0;
}
```

Output

```c
size of union = 32
size of structure = 40
```

sJob可以这样累加 

- the size of `name[32]` is 32 bytes
- the size of `salary` is 4 bytes
- the size of `workerNo` is 4 bytes

那为什么uJob 是32 bytes?

因为它获取的是最大的一个元素的bytes，在这里也就是32

最关键的原因是什么？

You can only access a single member of a union at one time.

你一次只可以访问一个union属性，不理解的话，可以看一下下面的这段代码

```c
#include <stdio.h>
union Job
{
   float salary;
   int workerNo;
} j;
int main()
{
   j.salary = 12.3;
   j.workerNo = 100;
   printf("Salary = %.1f\n", j.salary);
   printf("Number of workers = %d", j.workerNo);
   return 0;
}
```

**Output**

```c
Salary = 0.0
Number of workers = 100
```

12.3不见了！这是因为，后面访问workerNo属性时，因为一次只能访问一个属性，所以就可以理解为被覆盖。你可以试试把两个赋值语句调换顺序，看结果怎么样。