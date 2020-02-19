# c/c++求最大公约数和最小公倍数

## 最大公约数GCD(Greatest Common Divisor)

最常见的求两个数的最大公约数的算法是[辗转相除法，也叫欧几里得算法](https://zh.wikipedia.org/wiki/%E8%BC%BE%E8%BD%89%E7%9B%B8%E9%99%A4%E6%B3%95)

该算法的c++语言实现如下：

```c
#include<iostream>
using namespace std;
int gcd(int a,int b){
    return b==0?a:gcd(b,a%b);
}
int main(){
    int a=45,b=10;
    cout<<gcd(10,45);
}
```

Output

```
5
```





## 最小公倍数LCM(Lowest Common Multiple)

最大公倍数=a*b/最大公约数；

它的c++语言实现如下:

```c
#include<iostream>
using namespace std;
int gcd(int a,int b){
    return b==0?a:gcd(b,a%b);
}
int lcm(int a,int b){
 	return a*b/gcd(a,b);
}
int main(){
    int a=45,b=10;
    cout<<gcd(10,45)<<endl;
    cout<<lcm(10,45);
    return 0;
}
```

Output

```
5
90
```



