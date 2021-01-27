使用memset对数组进行初始化

memset的用法如下：

```
memset(数组名，赋的初值，sizeof(数组名));
//用法举例
memset(a, 0, sizeof(a));
```

其作用为把数组a的所有元素初始化为0。需要注意两点

- 第三个参数并非填写数组长度，而是数组的大小，这里的大小是指以字节为单位
- 这个方法赋初值只能赋0和-1，因为他们的补码表示为全0和全1，所以**按字节赋值**不会出错，如果赋其他的值，则会出错

你可以使用如下代码来检验，实际上，因为1和2的补码表示为`0000 0001`和`0000 0002`，所以使用memset赋值之后对应的元素内容分别为`0000 0001 0000 0001 0000 0001 0000 0001`和`0000 0002 0000 0002 0000 0002 0000 0002`，对应的十进制则是 16843009和33686018

```c++
#include<stdio.h>
#include<string.h>

void demo1() {
	int a[5] = { 1,2,3,4,5 };
	//memset(a, 0, 20);
	//memset(a, 0, sizeof(a));
	for (int i = 0; i < 5; i++) {
		printf("%d ", a[i]);
	}
	printf("\n");
	memset(a, -1, sizeof(a));
	for (int i = 0; i < 5; i++) {
		printf("%d ", a[i]);
	}
	printf("\n");
}

void demo2() {
	int a[5] = { 1,2,3,4,5 };
	memset(a, 1, sizeof(a));
    //memset(a, 2, sizeof(a));
	for (int i = 0; i < 5; i++) {
		printf("%d ", a[i]);
	}
	printf("\n");
}

int main() {
	//demo1();
	demo2();
	return 0;
}	
```

输出结果如下所示

![](https://gitee.com/ericling666/imgbed/raw/master/img/20210113174717.png)