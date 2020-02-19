# 使用DEV C++调试代码

## 0.序言

本片博客旨在记录通过DEV C++工具调试C/C++代码，在这之前需要对以下知识了解或掌握。

- C/C++代码的完整编译过程，可[参考文章](https://www.cnblogs.com/ericling/articles/11736681.html)
- GCC,gcc,g++,gdb的区别和联系，可[参考文章](https://www.cnblogs.com/ericling/p/11736326.html)

## 1.开发工具配置

我所使用的DEV C++版本为5.11，默认设置就已经支持调试了，看其他博客经常会提到默认设置无法调试，可能是使用的旧版本4.xx。这里记录一下他们修改默认设置为可调试的步骤

- 添加std标准库版本设置

  工具》编译选项》编译器》编译时加入以下命令：

  `-g3`

  

- 打开调试信息

  还是这个窗口，打开`代码生成/优化`选项卡》连接器》

  修改产生调试信息为Yes

经过测试，上述配置会在编译过程添加这些选项

```bash
# 预处理->生成xxx.i文件->编译->生成xxx.s文件->汇编->生成xxx.o文件
# 
g++.exe -D__DEBUG__ -c main.cpp -o main.o -I"D:/software/Dev-Cpp/MinGW64/include" -I"D:/software/Dev-Cpp/MinGW64/x86_64-w64-mingw32/include" -I"D:/software/Dev-Cpp/MinGW64/lib/gcc/x86_64-w64-mingw32/4.9.2/include" -I"D:/software/Dev-Cpp/MinGW64/lib/gcc/x86_64-w64-mingw32/4.9.2/include/c++" -g3 -std=c++11

# 链接
g++.exe -D__DEBUG__ main.o -o "hello world.exe" -L"D:/software/Dev-Cpp/MinGW64/lib" -L"D:/software/Dev-Cpp/MinGW64/x86_64-w64-mingw32/lib" -static-libgcc -g3

```

更多完整的选项可以参考官网的[文档说明](https://gcc.gnu.org/onlinedocs/gcc-9.2.0/gcc/Debugging-Options.html#Debugging-Options)

## 2.调试所需源代码

```c
#include <iostream>
using namespace std;

int add (int a , int b){
	return a+b;
}

int main(int argc, char** argv) {
	int a = 1+1;
	cout << "hello world!";
	int b = 1+2;
	int c = add(a,b);
	int d = 1+3;
	int e = 1+3;
	return 0;
}
```

代码内容很简单，这里就不展开解释。

## 3.调试过程详解

可以去菜单栏**运行**下面的子菜单，了解一下每一个菜单的作用

![Snipaste_2019-10-25_11-47-50.png](http://ww1.sinaimg.cn/large/006edVQGgy1g8aawcc3xij309h0ehdi8.jpg)

你也可以通过工具栏的图标来实现同样的效果。

![Snipaste_2019-10-25_11-48-31.png](http://ww1.sinaimg.cn/large/006edVQGgy1g8aax0mf8pj308p010mx2.jpg)

点击全部重新编译（快捷键F12），可以看到编译日志记录如下：

```
重新生成整个项目...
--------
- 项目文件名: D:\otherworkspace\devcppworkspace\hello world\hello world.dev
- 编译器名: TDM-GCC 4.9.2 64-bit Debug

生成 makefile...
--------
- 文件名: D:\otherworkspace\devcppworkspace\hello world\Makefile.win

正在处理makefile...
--------
- makefile处理器: D:\software\Dev-Cpp\MinGW64\bin\mingw32-make.exe
- 命令: mingw32-make.exe -f "D:\otherworkspace\devcppworkspace\hello world\Makefile.win" clean all

rm.exe -f main.o "hello world.exe"

g++.exe -D__DEBUG__ -c main.cpp -o main.o -I"D:/software/Dev-Cpp/MinGW64/include" -I"D:/software/Dev-Cpp/MinGW64/x86_64-w64-mingw32/include" -I"D:/software/Dev-Cpp/MinGW64/lib/gcc/x86_64-w64-mingw32/4.9.2/include" -I"D:/software/Dev-Cpp/MinGW64/lib/gcc/x86_64-w64-mingw32/4.9.2/include/c++" -g3

g++.exe -D__DEBUG__ main.o -o "hello world.exe" -L"D:/software/Dev-Cpp/MinGW64/lib" -L"D:/software/Dev-Cpp/MinGW64/x86_64-w64-mingw32/lib" -static-libgcc -g3

g++.exe -D__DEBUG__ main.o -o "hello world.exe" -L"D:/software/Dev-Cpp/MinGW64/lib" -L"D:/software/Dev-Cpp/MinGW64/x86_64-w64-mingw32/lib" -static-libgcc -g3


编译结果...
--------
- 错误: 0
- 警告: 0
- 输出文件名: D:\otherworkspace\devcppworkspace\hello world\hello world.exe
- 输出大小: 1.88690853118896 MiB
- 编译时间: 2.42s

```

开始调试代码：

添加以下断点

![Snipaste_2019-10-25_11-50-34.png](http://ww1.sinaimg.cn/large/006edVQGgy1g8aazcmx0pj31a60bawgd.jpg)

点击开始调试按钮之后，可以看到当前运行行背景颜色为蓝色。

调试相关窗口说明如下图。

最常见的按钮就是调试，单步进入和停止执行

![Snipaste_2019-10-25_11-53-40.png](http://ww1.sinaimg.cn/large/006edVQGgy1g8ab2gllumj31hc0b8goh.jpg)