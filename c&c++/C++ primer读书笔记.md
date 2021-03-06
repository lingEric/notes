C++ primer读书笔记

- 使用istream对象作为条件时,其效果是检测流的状态.如果流是有效的,即流未遇到错误,那么检测成功,返回`true` ,如果遇到文件结束符(end of file ),或遇到一个无效输入时(例如输入的值不是一个整数),istream对象的状态会变为无效.处于无效状态的istream对象使条件变为假

- windows操作系统中,输入文件结束符的方法是`ctrl+z` ,然后回车.UNIX系统中包括mac osx系统,输入`ctrl+D`

- 标准库头文件通常不带后缀,自定义的头文件根据其中定义的类的名字命名,自定义类首字母大写,用下划线连接

- C语言中的库,在C++中都可以导入,格式为`c+原库名` ,如`cstdio`

- 类类型的变量如果未指定初值,则按类定义指定的方式进行初始化.定义在函数内部的内置类型变量默认是不初始化的,除非有显式的初始化语句.全局变量默认初始化.

- 基本数据类型的尺寸(即所占的比特数)在不同的机器上有所差别,C++标准规定尺寸的最小值,同时允许编译器赋予这些类型更大的尺寸.

- 类型所能表示的值的范围决定了类型转换的过程

  - 非布尔类型算术值赋给布尔类型时,初始值为0结果为false,否则结果为true
  - 布尔值赋给非布尔类型,初始值为false,结果为0,否则为1
  - 浮点数赋给整数类型,小数部分记为0,整数所占空间超出浮点类型的容量,精度可能丢失,保留低位,高位截去
  - 赋给无符号类型一个超出它表示范围的值时,结果是初始值对无符号类型表示数值总数取模后的余数.
  - 赋给带符号类型一个超出它表示范围的值时,结果是未定义的(undefined)
  - 有符号<<==>>无符号(均是补码表示):对原数据内容不变,解释方式改变(即第一位是否为符号位,正负数有不同的解释方式)
  - 长整数<<==>>短整数:短变长,符号扩展,长变短,高位截断,低位保留

- 编译器在每个字符串的结尾处添加一个空字符('\0'),因此字符串字面值的实际长度要比他的内容多1.

- nullptr是指针字面值

- C++中的每个变量都有其数据类型,数据类型决定变量所占内存空间的大小和布局方式,该空间能存储的值的范围,以及变量能参与的运算.

- 在C++语言中,**初始化和赋值是两个完全不同的操作**,初始化不是赋值,初始化的含义是创建变量时赋予其一个初始值,赋值的含义是把对象的当前值擦除,而以一个新值来替代.

- 如果定义变量时没有指定初值,则变量被默认初始化,此时变量被赋予默认值**,默认值到底是什么由变量类型决定,同时定义变量的位置也会对此有影响**.如果是内置类型的变量未被显式初始化,它的值由定义的位置决定.**定义于任何函数体之外的变量被初始化为0,定义在函数体内部的内置类型变量将不被初始化**.一个未被初始化的内置类型变量的值是未定义的,如果试图拷贝或以其他形式访问此类值将引发错误.每个类各自决定其初始化对象的方式.

- 为了支持分离式编译,C++语言将声明和定义区分开.声明(declaration)使得名字为程序所知,**一个文件如果想使用别处定义的名字则必须包含对那个名字的声明**.而定义(definition)负责创建与名字关联的实体.**变量声明规定了变量的类型和名字**,在这一点上定义与之相同,但是除此之外,**定义还申请存储空间,也可能会为变量赋一个初始值**.如果想声明一个变量而非定义它,就在变量名前添加关键字extern,而且不要显式初始化变量:

  ```c++
  extern int i;//声明i而非定义i
  int j;//声明并定义j
  ```

  任何包含了显式初始化的声明即成为定义,可以给extern关键字标记的变量赋一个初始值,但是这么做抵消了extern的作用.extern语句如果包含初始值就不再是声明,而是定义:

  ```C++
  extern double pi = 3.1415;//定义
  ```

  变量能且只能被定义一次,但是可以被多次声明.

- 引用为对象起了另外一个名字,引用类型引用另外一种类型.通过将声明符写成&d的形式来定义引用类型.一般在初始化变量时,初始值会被拷贝到新建的对象中,然而定义引用时,程序把引用和它的初始值绑定(bind)在一起,而不是将初始值拷贝给引用.一旦初始化完成,引用将和它的初始值对象一直绑定在一起.**因为无法令引用重新绑定到另外一个对象,因此引用必须初始化.**

- 引用并非对象,他只是一个已经存在的对象所起的另外一个名字.定义了一个引用之后,对其进行的所有操作都是在与之绑定的对象上进行的.**引用只能绑定到对象上,而不能与字面值或某个表达式的计算结果绑定在一起**

- **指针本身就是一个对象,允许对指针赋值和拷贝,而且在指针的生命周期内它可以先后指向几个不同的对象.指针无需在定义时赋初值.和其他内置类型一样,在块作用域内定义的指针如果没有被初始化,也将拥有一个不确定的值.**

- 任何非0指针对应的条件值都是true

- `void*`是一种特殊的指针类型,可用于存放任意对象的地址.一个`void*`指针存放着一个地址,我们对该地址中到底是个什么类型的对象并不了解.利用`void*`指针能做的事比较有限,拿它和别的指针比较,作为函数的输入或输出,赋给另外一个`void*`指针,不能直接操作`void*` 指针所指的对象,因为我们并不知道这个对象到底是什么类型,也就无法确定能在这个对象上做哪些操作.概括说来,以`void*`的视角来看内存空间也就仅仅是内存空间,没办法访问内存空间中所存的对象.

- 理解复合类型的声明

  ```c++
  //i是一个int型的数,p是一个int型指针,r是一个int型引用(要理解类型修饰符只是声明符的一部分)
  int i = 1024 , *p = &i, &r = i;
  
  //定义多个变量
  //p1是指向int的指针,p2是int类型
  int* p1,p2;
  ```

- 面对一条比较复杂的指针或者引用的声明语句时,**从右向左**阅读有助于弄清楚它的真实含义.

- 因为const对象一经创建后其值就不能再改变,所以**const对象必须初始化.**

- 有两种方法可以用于定义类型别名

  ```c++
  typedef double wages;	//wages是double的同义词
  typedef wages base,*p;	//base是double的同义词,p是double*的同义词
  using SI = Sales_item;	//SI是Sales_item的同义词
  ```

- `auto`让编译器通过初始值来推算变量的类型.显然auto定义的变量必须有初始值,使用auto也能在一条语句中声明多个变量,因为**一条声明语句只能有一个基本数据类型,所以该语句中所有变量的初始基本数据类型都必须一样**.

- c++11新标准规定,可以为结构体的数据成员提供一个**类内初始值**,创建对象时,类内初始值将用于初始化数据成员,没有初始值的成员将被默认初始化.

- **预处理器是在编译之前执行的一段程序,可以部分的改变我们所写的程序**,如当预处理器看到`#include`标记时就会用指定的头文件的内容代替`#include`

- 一个标准的自定义头文件结构

  ```c++
  #ifndef SALES_DATA_H
  #define SALES_DATA_H
  #include<string>
  
  struct Sales_data{
      std::string bookNo;
      unsigned units_sold = 0;
      double revenue = 0.0;
  };
  #endif
  ```

  使用这些功能可以有效地防止重复包含头文件