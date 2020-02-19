[原文链接](https://www.cnblogs.com/CarpenterLee/p/5994681.html#top)

C语言程序从源代码到二进制程序都经历了那些过程？本文以Linux下C语言的编译过程为例，讲解C语言程序的编译过程。

编写hello world C程序：

```c
// hello.c
#include <stdio.h>
int main(){
    printf("hello world!\n");
}
```

编译过程只需：

```bash
$ gcc hello.c # 编译
$ ./a.out # 执行
hello world!
```

这个过程如此熟悉，以至于大家觉得编译事件很简单的事。事实真的如此吗？我们来细看一下C语言的编译过程到底是怎样的。

上述gcc命令其实依次执行了四步操作：

1.预处理(Preprocessing), 

2.编译(Compilation), 

3.汇编(Assemble), 

4.链接(Linking)。

![C_complie](https://www3.ntu.edu.sg/home/ehchua/programming/cpp/images/GCC_CompilationProcess.png)

# 示例

为了下面步骤讲解的方便，我们需要一个稍微复杂一点的例子。假设我们自己定义了一个头文件`mymath.h`，实现一些自己的数学函数，并把具体实现放在`mymath.c`当中。然后写一个`test.c`程序使用这些函数。程序目录结构如下：

```
├── test.c
└── inc
    ├── mymath.h
    └── mymath.c
```

程序代码如下：

```c
// test.c
#include <stdio.h>
#include "mymath.h"// 自定义头文件
int main(){
    int a = 2;
    int b = 3;
    int sum = add(a, b); 
    printf("a=%d, b=%d, a+b=%d\n", a, b, sum);
}
```

头文件定义：

```c
// mymath.h
#ifndef MYMATH_H
#define MYMATH_H
int add(int a, int b);
int sum(int a, int b);
#endif
```

头文件实现：

```c
// mymath.c
int add(int a, int b){
    return a+b;
}
int sub(int a, int b){
    return a-b;
}
```

# 1.预处理(Preprocessing)

**预处理用于将所有的#include头文件以及宏定义替换成其真正的内容**，预处理之后得到的仍然是文本文件，但文件体积会大很多。`gcc`的预处理是预处理器`cpp`来完成的，你可以通过如下命令对`test.c`进行预处理：

```c
gcc -E -I./inc test.c -o test.i
```

或者直接调用`cpp`命令

```c
$ cpp test.c -I./inc -o test.i
```

上述命令中`-E`是让编译器在预处理之后就退出，不进行后续编译过程；`-I`指定头文件目录，这里指定的是我们自定义的头文件目录；`-o`指定输出文件名。

经过预处理之后代码体积会大很多：

| X        | 文件名 | 文件大小 | 代码行数 |
| -------- | ------ | -------- | -------- |
| 预处理前 | test.c | 146B     | 9        |
| 预处理后 | test.i | 17691B   | 857      |

预处理之后的程序还是文本，可以用文本编辑器打开。

# 2.编译(Compilation)

这里的编译不是指程序从源文件到二进制程序的全部过程，而是指**将经过预处理之后的程序转换成特定汇编代码(assembly code)的过程**。编译的指定如下：

```bash
$ gcc -S -I./inc test.c -o test.s
```

上述命令中`-S`让编译器在编译之后停止，不进行后续过程。编译过程完成后，将生成程序的汇编代码`test.s`，这也是文本文件，内容如下：

```
// test.c汇编之后的结果test.s
    .file   "test.c"
    .section    .rodata
.LC0:
    .string "a=%d, b=%d, a+b=%d\n"
    .text
    .globl  main
    .type   main, @function
main:
.LFB0:
    .cfi_startproc
    pushl   %ebp
    .cfi_def_cfa_offset 8
    .cfi_offset 5, -8
    movl    %esp, %ebp
    .cfi_def_cfa_register 5
    andl    $-16, %esp
    subl    $32, %esp
    movl    $2, 20(%esp)
    movl    $3, 24(%esp)
    movl    24(%esp), %eax
    movl    %eax, 4(%esp)
    movl    20(%esp), %eax
    movl    %eax, (%esp)
    call    add 
    movl    %eax, 28(%esp)
    movl    28(%esp), %eax
    movl    %eax, 12(%esp)
    movl    24(%esp), %eax
    movl    %eax, 8(%esp)
    movl    20(%esp), %eax
    movl    %eax, 4(%esp)
    movl    $.LC0, (%esp)
    call    printf
    leave
    .cfi_restore 5
    .cfi_def_cfa 4, 4
    ret 
    .cfi_endproc
.LFE0:
    .size   main, .-main
    .ident  "GCC: (Ubuntu 4.8.2-19ubuntu1) 4.8.2"
    .section    .note.GNU-stack,"",@progbits
```

请不要问我上述代码是什么意思!-_-

# 3.汇编(Assemble)

**汇编过程将上一步的汇编代码转换成机器码(machine code)**，这一步产生的文件叫做**目标文件**，是二进制格式。`gcc`汇编过程通过`as`命令完成：

```
$ as test.s -o test.o
```

等价于：

```
gcc -c test.s -o test.o
```

这一步会**为每一个源文件产生一个目标文件**。因此`mymath.c`也需要产生一个`mymath.o`文件

# 4.链接(Linking)

**链接过程将多个目标文件以及所需的库文件(.so等)链接成最终的可执行文件(executable file)**。

命令大致如下：

```
$ ld -o test.out test.o inc/mymath.o ...libraries...
```

# 结语

经过以上分析，我们发现编译过程并不像想象的那么简单，而是要经过预处理、编译、汇编、链接。尽管我们平时使用`gcc`命令的时候没有关心中间结果，但每次程序的编译都少不了这几个步骤。也不用为上述繁琐过程而烦恼，因为你仍然可以：

```
$ gcc hello.c # 编译
$ ./a.out # 执行
```

# 参考文献

1.https://www3.ntu.edu.sg/home/ehchua/programming/cpp/gcc_make.html
2.http://www.trilithium.com/johan/2005/08/linux-gate/
3.https://gcc.gnu.org/onlinedocs/gccint/Collect2.html