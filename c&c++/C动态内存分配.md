# C动态内存分配

数组是固定数量的值的集合。在声明数组的大小之后，将无法更改它。有时，数组大小可能不够，需要动态扩容。解决此问题，可以在运行时手动分配内存。这在C编程中称为**动态内存分配**。

动态分配存储器涉及到的库函数有

- `malloc()`

- `calloc()`

- `realloc()`

- `free()`

这些函数在`<stdlib.h>`头文件中定义。

------

##  1.malloc（）

名称“ malloc”代表内存分配，memory allocation。

该`malloc()`函数保留指定字节数的内存块。并且，它返回一个**指针**的`void`可铸造成任何形式的指针。

------

### malloc（）的语法

```c
ptr = (castType*) malloc(size);
```

**例**

```c
ptr = (int*) malloc(100 * sizeof(float));
```

上面的语句分配了400个字节的内存。这是因为`float`的大小为4个字节。并且，**指针ptr**保存分配的存储器中的第一个字节的内存地址。

如果无法分配内存，则表达式将产生一个`NULL`指针。

------

## 2.calloc（）

名称“ calloc”代表连续分配，contiguous allocation。

`malloc()`函数分配内存，但不初始化内存。而`calloc()` 函数分配内存并将所有位初始化为零。

------

### calloc（）的语法

```c
ptr = (castType*)calloc(n, size);
```

**例：**

```c
ptr = (float*) calloc(25, sizeof(float));
```

上面的语句为`float`类型的25个元素在内存中分配了连续的空间。

------

## 3.free（）

使用`calloc()`或`malloc()`不单独释放创建的动态分配内存，必须明确使用`free()`释放空间。

------

### free（）的语法

```c
free(ptr);
```

该语句释放由指向的内存中分配的空间`ptr`。

------

### 示例1： malloc（）和free（）

```c
// Program to calculate the sum of n numbers entered by the user
#include <stdio.h>
#include <stdlib.h>
int main()
{
    int n, i, *ptr, sum = 0;
    printf("Enter number of elements: ");
    scanf("%d", &n);
    ptr = (int*) malloc(n * sizeof(int));
 
    // if memory cannot be allocated
    if(ptr == NULL)                     
    {
        printf("Error! memory not allocated.");
        exit(0);
    }
    printf("Enter elements: ");
    for(i = 0; i < n; ++i)
    {
        scanf("%d", ptr + i);
        sum += *(ptr + i);
    }
    printf("Sum = %d", sum);
  
    // deallocating the memory
    free(ptr);
    return 0;
}
```

在这里，我们已为n个数字动态分配了内存

------

### 示例2： calloc（）和free（）

```c
// Program to calculate the sum of n numbers entered by the user
#include <stdio.h>
#include <stdlib.h>
int main()
{
    int n, i, *ptr, sum = 0;
    printf("Enter number of elements: ");
    scanf("%d", &n);
    ptr = (int*) calloc(n, sizeof(int));
    if(ptr == NULL)
    {
        printf("Error! memory not allocated.");
        exit(0);
    }
    printf("Enter elements: ");
    for(i = 0; i < n; ++i)
    {
        scanf("%d", ptr + i);
        sum += *(ptr + i);
    }
    printf("Sum = %d", sum);
    free(ptr);
    return 0;
}
```

------

## 4.realloc（）

如果动态分配的内存不足或超出要求，则可以使用该`realloc()`功能更改以前分配的内存的大小。

------

### realloc（）的语法

```c
ptr = realloc(ptr, x);
```

在这里，ptr以新的大小x重新分配。

------

### 示例3： realloc（）

```c
#include <stdio.h>
#include <stdlib.h>
int main()
{
    int *ptr, i , n1, n2;
    printf("Enter size: ");
    scanf("%d", &n1);
    ptr = (int*) malloc(n1 * sizeof(int));
    printf("Addresses of previously allocated memory: ");
    for(i = 0; i < n1; ++i)
         printf("%u\n",ptr + i);
    printf("\nEnter the new size: ");
    scanf("%d", &n2);
    // rellocating the memory
    ptr = realloc(ptr, n2 * sizeof(int));
    printf("Addresses of newly allocated memory: ");
    for(i = 0; i < n2; ++i)
         printf("%u\n", ptr + i);
  
    free(ptr);
    return 0;
}
```

运行该程序时，输出为：

```
输入大小：2
先前分配的内存的地址：26855472
26855476

输入新的尺寸：4
新分配的内存地址：26855472
26855476
26855480
26855484
```

------

## 总结

malloc动态分配内存，不初始化

```c
int n, *ptr = 0;
printf("Enter number of elements: ");
scanf("%d", &n);
ptr = (int*) malloc(n * sizeof(int));

```

> 如果无法分配内存，则返回NULL

calloc动态分配内存，初始化所有bit为0

```c
int n, *ptr = 0;
printf("Enter number of elements: ");
scanf("%d", &n);
ptr = (int*) calloc(n, sizeof(int));
```

> 如果无法分配内存，则返回NULL

free释放内存

```c
free(ptr);
```



realloc重新分配内存

```c
ptr = realloc(ptr, n * sizeof(int));
```

