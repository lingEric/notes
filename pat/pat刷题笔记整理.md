- 浮点数统一采用double类型，scanf采用`"%lf"`，printf采用`"%f"`或者`"%lf"`

- 给1～n的数，要求每13个分为一类，问某一数num是第几类中的第几个数：例如扑克牌1~13，14~26，这样分类的数字，求第几类：用num / 13的方法，要考虑当num == 13的时候其实还是在第一个类别里面，但却由0变成了1。解决办法是：num = num – 1后，这样使得num变成了0~12，13~25，…再进行num / 13，正好符合除以得到的结果分类。求是第几个数：用num % 13 ，不需要先num = num – 1。需要的是num % 13 + 1，因为取余得到的结果是0~12，所以要在最后取余后的结果之后加1满足条件。

- 如果觉得数组下标0开始麻烦，考虑舍弃0位，从1下标开始存储数据。

- 如果对于不同的结果输出不同的字母，最好考虑字符数组或者字符串数组预先存储好，避免多个printf语句导致的错误率增加。

- 处理最后一个结果末尾没有空格的情况，考虑在for语句里面添加if语句的方法处理：`if(i != n - 1) printf(" ");`

- 如果输入一组数据和输入好多个组数据的得到的同一组答案不一样，考虑下是不是int n后忘记写了输入语句。。。(mdzz。。。)

- substr只有两种用法：

  - substr(4)表示从下标4开始一直到结束。要想截取末尾3个数字，用substr(a.size()-3);
  - substr(5, 3)表示从下标5开始，3个字符

- 关于pair：

  ```c
  typedef pair<string, int> p;
  vector<p> v;
  p temp = make_pair("abc", 123);
  v.push_back(temp);
  cout << temp.first << " " << temp.second << endl;
  cout << v[0].first << " " << v[0].second << endl;
  ```

- string s = “abc”;char *c = s.c_str();
  把字符串变成字符数组，在头文件`<string>`里面

- map映射find函数:
  map和set都是有find函数的，用的时候so easy：
  if(m.find(“ABC”) != m.end())
  cout << “can found it”;

- abs()在头文件stdlib.h里面

- find方法：

  - `#include <string>`
    

    - string返回的是下标的值，如果没有，用 `== string::npos`

    - int index = s.find(‘a’); //返回a这个字母第一次出现的下标（从0开始）

    - int index = s.find(‘a’, 5); //从下标5开始，寻找a第一次出现的下标

    - 如果找不到，会出现一个随机值，可以这么写：if(s.find(‘a’, 5) != string::npos)

      int index = s.find(‘a’, 5);

     

  - `#include <map>`
  
    - map.find()返回的是迭代器
  
  - `#include <set>`
  
    - set同上，返回的是迭代器，找不到用 == s.end()表示
  
- 按照名称的升序排序，因为strcmp比较的是ACSII码，所以A < Z。写cmp函数的时候`return strcmp(a.name, b.name) <= 0;` return语句返回的是true或者false的值，所以要写 <= 0 这样的形式。比较ACSII码的大小，strcmp(‘a’, ‘z’)返回负值，因为a<z a – z < 0

- 如果name为10个字符，注意char name[]要开至少11个字符。

- 不能`char *str; puts(str);` 必须`char str[100000]; puts(str);`

  ```c
  #include <iostream>
  string str1;
  char str2[100];
  cin >> str1;
  cin >> str2;//cin以空格和回车为结束符
  
  char ch;
  cin.get(ch);//用来接收一个字符
  
  char str[20];
  cin.get(str, 20);//接收一行字符串，可以接收空格
  
  char str[20];
  cin.getline(str, 20);//接收一行字符串，可以接收空格
  cin.getline(str, 20, '#');//接收一行字符串，可以接收空格，判断直到遇到'#'为止
  ```

- ```c
  #include <iostream>
  string str1;
  char str2[100];
  cin >> str1;
  cin >> str2;//cin以空格和回车为结束符
  
  char ch;
  cin.get(ch);//用来接收一个字符
  
  char str[20];
  cin.get(str, 20);//接收一行字符串，可以接收空格
  
  char str[20];
  cin.getline(str, 20);//接收一行字符串，可以接收空格
  cin.getline(str, 20, '#');//接收一行字符串，可以接收空格，判断直到遇到'#'为止
  ```

- bool变量在main函数里面记得要初始化，bool flag[256] = {false}; 在main函数外面会被自动初始化为false~

- unordered_map和multimap

  - unordered_map是不排序的map，主要以key，下标法来访问
  - 在内部unordered_map的元素不以键值或映射的元素作任何特定的顺序排序，其存储位置取决于哈希值允许直接通过其键值为快速访问单个元素(所以也不是你输入的顺序)
  - multimap是可重复的map，因为可重复，所以一般用迭代器遍历

- `vector<int> v[n]`——建立一个存储int类型数组的数组，即二维数组

- 段错误：数组越界，访问了非法内存

- 将n转化为d进制，最后需要将arr数组倒置

  ```c
  do{
  	arr[len++] = n % d;
  	n = n / d;
  }while(n != 0);
  ```

- 判断是否为素数

  ```c
  bool isprime(int n) {
  	if(n <= 1) return false;
  	int sqr = int(sqrt(n * 1.0));
  	for(int i = 2; i <= sqr; i++) {
  		if(n % i == 0)
  			return false;
  	}
  	return true;
  }
  ```

- vector可以pop_back()删除最后一个元素

- multiset在头文件set里面

- 如果有将近一半的答案错误，多半是输出语句里面的字母大小写写错了~

- define不要加分号啊啊啊啊否则会编译失败而且是在程序里面失败啊啊啊expected expression

- 用了strlen一定要记得写头文件cstring（也就是string.h）

- index、swap、value、count、now、cmp、friend是关键字不能重复定义。（xcode）

- 段错误也有可能是因为数组a没有初始化，导致b[a[2]]这种形式访问了非法内存

- scanf的%c可以读入空格和换行

- 如果程序在输入数据之后异常退出，要考虑是不是scanf使用的时候漏写&

- 如果程序在输入数据之后异常退出，要考虑是不是scanf使用的时候漏写&

- 如果程序在输入数据之后异常退出，要考虑是不是scanf使用的时候漏写&

- scanf(“%s”, str); str是一个字符数组，str名表示了指针所以这个不用写&

- double类型变量，输出格式是%f，scanf中却是%lf

- `%5d`占5位，右对齐格式

- `%05d`占5位，不足前面补0——在某些情况下非常有用

- `getchar()`用来输入单个字符（包括空格和换行符），`putchar()`用来输出单个字符

- `<cmath>`里面的函数：

  - `fabs(double x)`取绝对值
  - `floor(double x)`向下取整
  - `ceil(double x)`向上取整
  - `pow(double r, double p)`r和p都是double类型
  - `sqrt(double x)`x是double类型
  - `log(double x)` 自然对数为底的对数(ln x)，C语言中没有对任意底数求对数的函数，用换底公式才行
  - `sin(double x)` `cos(double x)` `tan(double x)`
  - `pi = acos(-1.0)`
  - `asin(double x)` `acos(double x)` `atan(double x)`
  - `round(double x)` 四舍五入，传入double型返回double型，最后要进行取整（5.123456 = 5.0000）

- `memset(数组名, 值, sizeof(数组名));` memset只能赋值0或者-1，因为memset是按字节赋值的。（对二维数组和多维数组赋值也一样，只要写数组明即可）。memset在头文件`string.h`里面。如果要赋值其他数字（如1），那么用fill函数

memset：作用是在一段内存块中填充某个给定的值，它是对较大的结构体或数组进行清零操作的一种最快方法。

可以用作int型数组（只能0或者-1），也可以用作char型数组（记得sizeof(char) * (n – 1)）

不推荐使用memeset，推荐使用更好用的fill函数，在头文件algorithm里面

- scanf中的%s识别空格作为字符串的结尾

- `gets` 用来输入一行字符串，以\n为输入结束。所以scanf完一个整数后，如果要使用gets，必须先`getchar();`

- `puts(str);`**用来输出一行字符串并且自动加上\n**

- `<string.h>`头文件

  - `strlen(str)`返回不包括`\0`的长度
  - `strcmp(str1, str2)`按字典序比较（比较的是ASCII，大写字母在前，小写字母在后）。<返回负数（不一定是-1），==返回0，>返回正数（不一定是1）
  - `strcat(str1, str2)`把str2接到str1后面

- `sscanf(str, "%d", &n);`把字符数组str里面的内容以%d形式输入到变量n里面//sscanf还支持正则表达式`sprintf(str, "%d", n);`把n以%d的形式写到str字符数组中

- 指针是个unsigned类型的整数

- 两个int类型的指针相减，等价于在求两个指针之间相差了几个int。如&a[0]和&a[5]之间相差了5个int，会输出5

- 用指针进行swap: 传入地址，子函数参数定义为指针`int *a`和`int *b`，使用int temp交换a和b中的内容（`*a`和`*b`）

  ```c
  void swap1(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
  }
  swap1(&a, &b);
  ```

- 用引用进行swap：传入a和b，子函数参数定义为引用，用int temp交换a和b

  ```c
  void swap1(int &a, int &b) {
    int temp = a;
    a = b;
    b = temp;
  }
  swap1(a, b);
  ```

- swap函数定义在了`using namespace std;`里面 ，无需特殊的头文件。为了避免冲突请自己写的时候用swap1

- 结构体中元素的访问，->和.访问的区别：

  ```c
  struct node stu, *p;
  stu.id = 1001;
  
  (*p).id = 1001; 
  //为了简化结构体指针变量的(*p).的复杂写法，又可以用p->id来直接访问。
  p->id = 1001;
  //也就是说，使用->的前面是一个指向结构体的指针变量p，.的前面是一个结构体变量stu
  ```

- 使用自定义eps = 1e-8 进行浮点数的比较

  ```c
  const double eps = 1e-8;
  #define Equ(a, b) ((fabs((a) - (b))) < (eps))
  #define More(a, b) (((a) - (b)) > (eps))
  #define Less(a, b) (((a) - (b)) < (-eps))
  #define LessEqu(a, b) (((a) - (b)) < (eps))
  
  if(Equ(a, b)) {
    cout << "true";
  }
  ```

- 时间复杂度：O(1) < O(log n) < O(n) < O(nlogn)< O(n^2)< O(n^3)< O(n^k)< O(2^n)

- 输入的具体个数不明确

  ```c
  while(scanf("%d", n) != EOF) {
    
  }
  //因为EOF一般为-1，所以~按位取反-1正好是0，就可以退出循环了
  //所以也写成下面这种情况
  while(~scanf("%d", &n)) {
    
  }
  ```

- 要求最后一组数据没有空格：

  ```c
  while(T--) {
    if(T > 0) 
      printf(" ");
  }
   
  for(int i = 0; i < n; i++) {
    if(i < n - 1)
      printf(" ");
  }
  ```

- sort对于vector也可以`sort(v.begin() + 1, v.end()-1);`这种取第2个到倒数第二个数排序的形式

- friend也是关键字啊啊啊（xcode适用）

- queue、stack、priority_queue是push和pop

- vector、string、deque是push_back

- push和pop只是一个动作，而queue是用front和back访问第一个和最后一个元素的，stack使用top访问最上面的一个元素的

- stack、vector、queue、set、map作为容器，所以都有size

  ```c
  //根据所给序列构建一个二叉搜索树
  #include <cstdio>
  #include <vector>
  using namespace std;
  struct node {
      int v;
      struct node *left, *right;
  };
  node* build(node *root, int v) {
      if(root == NULL) {
          root = new node();
          root->v = v;
          root->left = root->right = NULL;
      } else if(v <= root->v)
          root->left = build(root->left, v);
      else
          root->right = build(root->right, v);
      return root;
  }
  int main() {
      int n, t;
      scanf("%d", &n);
      node *root = NULL;
      for(int i = 0; i < n; i++) {
          scanf("%d", &t);
          root = build(root, t);
      }
      return 0;
  }
  ```

- 计算sort的区间，是`(a.begin(), a.begin() +n);`的形式，因为 n – 0 = n个数字，所以要使中间多n个数字就要+n，a.end()是最后一个数字的后一位，也就是说，第二个参数应该是要排序序列的最后一个元素的后一位~~

  ```c
  从字符数组的下标1开始赋值，从下标1开始输出
  #include <cstdio>
  #include <cstring>
  using namespace std;
  int main() {
      char a[100];
      scanf("%s", a + 1);
      printf("print a:%s\nprint a+1:%s\n", a, a+1);
      int lena = strlen(a+1); //从a+1开始计算str的长度
      printf("strlen(a+1) = %d", lena);
      return 0;
  }
  /*
   1234
   print a:
   print a+1:1234
   strlen(a+1) = 4
  */
  ```

- 素数表的建立

  ```c
  vector<int> prime(500000, 1);
  for(int i = 2; i * i < 500000; i++)
      for(int j = 2; j * i < 500000; j++)
          prime[j * i] = 0;
  ```

- 在scanf接收完后一定要getchar();之后再使用getline(cin, s); 否则getline得到的是空字符。getline与getline之间无需使用getchar(); 可以在scanf完一个整数后后面接着的字符串使用getline，接收数字后面跟着的同一行剩下的字符串~~~

- 传参是拷贝，所以用引用的话更快，传参拷贝可能会超时~

- swap函数在using namespace std;里，不需要头文件

- %取余和除号/的优先级等同，优先级等同的时候从左到右运算~~~

- %c是会读入空格和回车的，可以通过在scanf里面手动添加空格的方式避免。scanf(“%d %c %d”, &a, &b, &c);

- 在algorithm头文件里面，有reverse函数，reverse(s.begin(), s.end()); reverse(v.begin(), v.end()); 直接改变字符串本身，没有返回值，不能通过赋值string t = reverse(s.begin(), s.end()); 这样是不对的

- dijkstra千万不要先把起点标记为true（已确定）。。因为找最小的边是找的所有false（不确定）的结点中距离最小的。。。

- 使用fill初始化二维数组是fill(e[0], e[0] + 510 * 510, inf);。。。。不是e，e + 510 * 510。。。。。因为二维数组在内存里面存储是线性的连续的一段。。。从e[0]的内存开始一直到n * n大小的内存。。。

- 循环无限输出考虑一下是不是i–变成了i++

- 判断一个图的连通分量数

  - 对所有结点（中未被访问的结点）进行深度优先遍历（此处计算连通分量的个数，每dfs一次就可以累加一次cnt++）
  - 深度优先遍历中
    - 置当前结点visit[u] = true
    - （此处可计算当前连通分量里面的结点个数memberNum++）
    - 对所有结点（且`e[u][v]`可达的结点，中的未被访问的结点 &&关系）深度优先遍历

- 树的层序遍历

  - DFS方法
    - 用vector的push.back方法存储每个结点的孩子们
    - 两个参数，结点下标index，和当前层数depth
    - 如果想求每一层的结点个数，用cnt[depth]++存储当前层数拥有的结点个数
    - 从根结点开始遍历，当一个结点已经没有任何孩子们（就是说它是叶子结点，到底了的时候）return，并更新cnt[depth]的值
    - 递归式是：对当前结点的所有孩子结点dfs，index为孩子结点的下标，层数为depth+1
  - BFS方法
    - 设立两个数组，第一个level保存i结点的层数，（bfs的时候当前结点的层数是它父亲结点的层数+1）；第二个数组cnt[i]，保存i层拥有的叶子结点的个数
    - 同样使用vector的push.back方法存储每个结点的孩子们
    - 先把根结点进入队列，如果要计算每一层拥有的叶子结点个数，只要当前出队的是叶子结点，就把它的cnt[level[i]]++，然后继续遍历访问他的孩子结点们

- 最大连续子序列和

  sum为要求的最大和，temp为临时最大和，left和right为所求的子序列的下标，tempindex标记left的临时下标。temp = temp + v[i]，当temp比sum大，就更新sum的值、left和right的值；当temp < 0，那么后面不管来什么值，都应该舍弃temp < 0前面的内容，因为负数对于总和只可能拉低总和，不可能增加总和，还不如舍弃；舍弃后，直接令temp = 0，并且同时更新left的临时值tempindex。因为对于所有的值都为负数的情况要输出0，第一个值，最后一个值，所以在输入的时候用flag判断是不是所有的数字都是小于0的，如果是，要在输入的时候特殊处理。
  
  ```c
  #include <cstdio>
  #include <vector>
  using namespace std;
  int main() {
      int n, flag = 0, sum = -1, temp = 0, left = 0, right = 0, tempindex = 0;
      scanf("%d", &n);
      vector<int> v(n);
      for(int i = 0; i < n; i++) {
          scanf("%d", &v[i]);
          if(v[i] >= 0)
              flag = 1;
          temp = temp + v[i];
          if(temp > sum) {
              sum = temp;
              left = tempindex;
              right = i;
          } else if(temp < 0) {
              temp = 0;
              tempindex = i + 1;
          }
      }
      if(flag == 0)
          printf("0 %d %d", v[0], v[n - 1]);
      else
          printf("%d %d %d", sum, v[left], v[right]);
      return 0;
  }
  ```
  
    
  
- 根据前序和中序输出后序

  - root为前序中当前的根结点的下标，**start为当前需要打印的子树在中序中的最左边的下标，end为当前需要打印的子树在中序中最右边的下标。**

  - **递归打印这棵树的后序，递归出口为start > end。i为root所表示的值在中序中的下标，所以i即是分隔中序中对应root结点的左子树和右子树的下标。**
    **先打印左子树，后打印右子树，最后输出当前根结点pre[root]的值。**

    ```c
    #include <cstdio>
    using namespace std;
    int pre[] = {1, 2, 3, 4, 5, 6};
    int in[] = {3, 2, 4, 1, 6, 5};
    void post(int root, int start, int end) {
        if(start > end) 
            return ;
        int i = start;
        while(i < end && in[i] != pre[root]) i++;
        post(root + 1, start, i - 1);
        post(root + 1 + i - start, i + 1, end);
        printf("%d ", pre[root]);
    }
    
    int main() {
        post(0, 0, 5);
        return 0;
    }
    ```

- **已知后序与中序输出前序（先序）：**

  因为后序的最后一个总是根结点，令i在中序中找到该根结点，则i把中序分为两部分，左边是左子树，右边是右子树。因为是输出先序（根左右），所以先打印出当前根结点，然后打印左子树，再打印右子树。左子树在后序中的根结点为root – (end – i + 1)，即为当前根结点-右子树的个数。左子树在中序中的起始点start为start，末尾end点为i – 1.右子树的根结点为当前根结点的前一个结点root – 1，右子树的起始点start为i+1，末尾end点为end。

  ```c
  #include <cstdio>
  using namespace std;
  int post[] = {3, 4, 2, 6, 5, 1};
  int in[] = {3, 2, 4, 1, 6, 5};
  void pre(int root, int start, int end) {
      if(start > end) return ;
      int i = start;
      while(i < end && in[i] != post[root]) i++;
      printf("%d ", post[root]);
      pre(root - 1 - end + i, start, i - 1);
      pre(root - 1, i + 1, end);
  }
  
  int main() {
      pre(5, 0, 5);
      return 0;
  }
  ```

- 树状数组

  - lowbitlowbit = x & (-x)
  - lowbit(x)也可以理解为能整除x的最大的2的幂次
  - c[i]存放的是在i号之前（包括i号）lowbit(i)个整数的和（即：c[i]的覆盖长度是lowbit(i) ）
  - 树状数组的下标必须从1开始

  经典应用：统计序列中在元素左边比该元素小的元素个数

  ```c
  #include <cstdio>
  #include <cstring>
  const int maxn = 10010;
  #define lowbit(i) ((i) & (-i))
  int c[maxn];
  void update(int x, int v) {
    for(int i = x; i < maxn; i += lowbit(i))
      c[i] += v;
  }
  int getsum(int x) {
    int sum = 0;
    for(int i = x; i >= 1; i -= lowbit(i))
      sum += c[i];
    return sum;
  }
  int main() {
    int n, x;
    scanf("%d", &n);
    for(int i = 0; i < n; i++) {
      scanf("%d", &x);
      update(x, 1);
      printf("%d\n", getsum(x - 1));
    }
    return 0;
  }
  ```

  

- scanf中的%d和%c之间一定要有分隔符的主动scanf输入

修改code completion快捷键位CTRL+ENTER，帮助提示函数名称

修改命令行提示符的属性，开启快速编辑模式，方便调试

添加c++11语言标准支持

开启代码调试功能

对输入的字符串进行切割时，可以使用scanf按照指定格式分别输入达到切割效果，比如：

```c
//对于这样的输入
3-10 99
11-5 87
102-1 0
//对于这种格式不绝对统一的字符串，可以自己构造相应的scanf分割对应的数据
scanf("%d-%d %d", &t, &num, &score);    
```

对于需要进行除法运算的变量，并且有精确度要求的时候，可以这样

```c
double grade =0;
//其中fullscore数组为int类型，在*1.0小数之后，自动转换为浮点型
grade += fullscore[j] * 1.0 / 2;
//或者这样强转
grade += (double)fullscore[j]/2;
```

如果在getline读取行之前还有其它的输入字符（不管是字符还是整数），则需要保证在调用getline函数之前，使用getchar()函数读取一次回车符，然后再调用

```c
#include<iostream>
using namespace std;
int main() {
	char n;
	scanf("%c",&n);
	getchar();
	string str;
	getline(cin,str);
	cout<<str;
}
```

散列表的应用：在需要进行遍历数组内容，判断某一个元素是否存在时，可以使用一个大数组，初始化为0，录入数据时，相应数据（把下标看成数据）的内容修改为1，这样判断的时候直接判断a[element]==1即可，其中element为录入的数据。这样就把O(n)级别的时间复杂度降到了O(1)

atoi&stoi(stol)&strtol区别

参考：[https://www.ibm.com/developerworks/community/blogs/12bb75c9-dfec-42f5-8b55-b669cc56ad76/entry/c_11%25e4%25b8%25ad%25e7%259a%2584string_atoi_itoa?lang=en](https://www.ibm.com/developerworks/community/blogs/12bb75c9-dfec-42f5-8b55-b669cc56ad76/entry/c_11%e4%b8%ad%e7%9a%84string_atoi_itoa?lang=en)

即使不记得也没关系，只要记住以下用法即可

```c
//数字型变量转字符串
to_string();//c++全局函数

//字符串转数字
stoi();//to int
stol();//to long 
stof();//to float
//都需要导入#include <string>     
```

四舍五入，可以先double +0.5然后转int取整数部分，也可以用`cmath`的函数`round()`

数组，字符串或其它常用stl容器大规模清零或者填充数据可以使用`fill()`函数

```c
// fill algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::fill
#include <vector>       // std::vector

int main () {
  std::vector<int> myvector (8);                       // myvector: 0 0 0 0 0 0 0 0

  std::fill (myvector.begin(),myvector.begin()+4,5);   // myvector: 5 5 5 5 0 0 0 0
  std::fill (myvector.begin()+3,myvector.end()-2,8);   // myvector: 5 5 5 8 8 8 0 0

  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output

```c
myvector contains: 5 5 5 8 8 8 0 0
```



将十进制a转换为b进制数，当a不为0时，将a%b从后往前倒序保存下来，每次保存后将a/b。这样倒序保存的数就是十进制a在b进制下的结果。

[进制互转](https://www.cnblogs.com/ericling/p/11813387.html)代码参考如下

```c
#include<iostream>
#include<cstdlib>
using namespace std;
int main(){
    //把8进制的17转化为10进制打印输出
    string str = "17";
    char *tmp ;
    long result = strtol(str.c_str(),&tmp,8);
    cout<<result;
    return 0;
}

#include<iostream>
#include<algorithm>
using namespace std;
//digital为10进制数，r为需要转换的目标进制，返回目标进制数
string dtox(int digital,int r){
    string result="";
    const char s[37]="0123456789abcdefghijklmnopqrstuvwxyz";
    if(digital==0){
        return "0";
    }
    while(digital!=0){
        int tmp =digital%r;
        result+=s[tmp];
        digital/=r;
    }
    reverse(result.begin(),result.end());
    return result;
}
int main(){
    cout<<"十进制10转为16进制结果："<<dtox(10,16)<<endl;
    cout<<"十进制10转为8进制结果："<<dtox(10,8)<<endl;
    cout<<"十进制10转为2进制结果："<<dtox(10,2)<<endl;
    cout<<"十进制10转为10进制结果："<<dtox(10,10)<<endl;
}
```

尽量使用c++的string类

读取整行的代码如下

```c
string str;
getline(cin,str);//该函数在std标准库中，不需要引入string头文件
```

c++的string类可以转化为c的字符数组，`str.c_str();`

[最大公约数和最小公倍数求解](https://www.cnblogs.com/ericling/p/11794490.html)

```c
int gcd(int a,int b){
    return b==0?a:gcd(b,a%b);
}
int lcm(int a,int b){
    return a*b/gcd(a,b);
}
```

对于一些可以**事先进行预处理然后用散列表**来求解的问题，可以大大减少时间复杂度。比如一个需要大量查询斐波那契数列的问题中，我们可以事先把前N个斐波那契数列求解出来然后放到散列表中进行保存，之后的查询就都是O(1)的时间复杂度。

▲多[利用位运算](https://www.cnblogs.com/ericling/p/11826619.html)来求解交集，并集，差集，可以大大减少时间复杂度，降低编码难度。

素数的判断

```c
bool isPrime(int n){
    if(n <= 1) return false;
    int sqr = (int)sqrt(1.0*n);
    for(int i = 2;i<=sqr; i++){
        if(n%i==0) return false;
    }
    return true;
}
```

常用C++ STL容器

**C++ container**

- Sequence containers
	- [string](https://embeddedartistry.com/blog/2017/7/24/stdstring-vs-c-strings)
	- [array](http://en.cppreference.com/w/cpp/container/array)
	- [vector](http://en.cppreference.com/w/cpp/container/vector)
	- [forward_list](http://en.cppreference.com/w/cpp/container/forward_list)
	- [list](http://en.cppreference.com/w/cpp/container/list)
	- [deque](http://en.cppreference.com/w/cpp/container/deque)
- Sequence container adapters
	- [stack](http://en.cppreference.com/w/cpp/container/stack)
	- [queue](http://en.cppreference.com/w/cpp/container/queue)
	- [priority_queue](http://en.cppreference.com/w/cpp/container/priority_queue)
- Associative containers
	- unique key
		- [set](http://en.cppreference.com/w/cpp/container/set)
		- [map](http://en.cppreference.com/w/cpp/container/map)
		  - [pair](https://en.cppreference.com/w/cpp/utility/pair)
	- same key
		- [multiset](http://en.cppreference.com/w/cpp/container/multiset)
		- [multimap](http://en.cppreference.com/w/cpp/container/multimap)
- Unordered associative containers
	- unique key
		- [unordered_set](http://en.cppreference.com/w/cpp/container/unordered_set)
		- [unordered_map](http://en.cppreference.com/w/cpp/container/unordered_map)
	- same key
		- [unordered_multiset](http://en.cppreference.com/w/cpp/container/unordered_multiset)
		- [unordered_multimap](http://en.cppreference.com/w/cpp/container/unordered_multimap)

algorithm头文件常用函数

algorithm
- max
- min
- abs
- swap
- reverse
- is_permutation
- next_permutation
- prev_permutation
- fill
- sort
- lower_bound
- upper_bound

大数组必须定义为全局变量

字符数组要多开一个单位

string.find()返回的是下标的值，没有找到用==string::npos

dev c++调试代码时，查看vector容器的内容
![查看vector容器的内容](https://img2018.cnblogs.com/blog/1668551/201911/1668551-20191120152309368-315300911.png)


同时存在边权和点权的最短路径问题，求**最短路径条数**以及**最大点权和**、**打印最短路径**？

- 用一遍Dijkstra算法即可
- dis[i]表示从出发点到i结点最短路径的**路径长度**
- num[i]表示从出发点到i结点**最短路径的条数**
- w[i]表示从出发点到i结点**点权的数目之和**
- path[i]表示从出发点到i结点的前驱结点，利用栈（或者递归）打印路径即可

图的初始化

- 如果是**无向图**，需要注意初始化边的时候，是**有两条边**的，不能漏掉了

- 没有边的情况（包括两种情况，1.顶点v，w之间不存在边，2.顶点v本身没有到达自己的边），初始化为0，如果是初始化为-1，则**判断边是否存在的时候需要注意G\[i][j]<0而不是直接if(G\[i][j])**，这两种判断一个是判断0，一个是判断<0

- 图的初始化，可以用fill函数，具体用法可以参考如下

  ```cpp
  //必须添加algorithm头文件
  #include<algorithm>
  
  //在内存地址区间[first,last)范围内，填充x
  fill(first,last,x);
  
  //但是特别注意二维数组的填充
  int arr[2][2];
  fill(arr[0],arr[0]+4,0);
  /*
  这里需要注意的是，arr[0]表示的才是整个二维数组的起始地址，而不是arr，另外，这个区间是左闭右开区间！！！
  */
  ```

  不仅是图的初始化， 其它的辅助数组也可以用fill函数初始化，比如说path数组（用于记录顶点w的前驱顶点，打印最短路径），dist数组（用于记录出发点到顶点w的最短路径），num数组（用于记录从出发点到顶点w的最短路径条数），w数组（用于记录从出发点到顶点w的点权之和）等。
  
- 在真正进行Dijkstra算法之前，先检查下面这些数据是否都已经初始化好了

  ```c
  1.初始化图
  2.初始化路径
  3.初始化距离
  4.初始化收录情况
  5.初始化点
  6.初始化出发点，以及出发点的邻接点的路径和距离信息
  ```

**最短路径扩展问题**

- 要求数最短路径有多少条
  - count[s] = 1;
  - 如果找到更短路：count[W]=count[V];
  - 如果找到等长路：count[W]+=count[V];
  
- 要求边数最少的最短路
  
  - count[s] = 0;
  - 如果找到更短路：count[W]=count[V]+1;
  - 如果找到等长路：count[W]=count[V]+1;
  
- 存在点权

  比如救火问题，多条最短路径，选择点权最大的那条

  ```cpp
  for(int i = 0; i < n; i++) {
  	int u = -1, minn = inf;
  	for(int j = 0; j < n; j++) {
  		if(visit[j] == false && dis[j] < minn) {
  			u = j;
  			minn = dis[j];
  		}
  	}
  	if(u == -1) break;
  	visit[u] = true;
  	for(int v = 0; v < n; v++) {
  		if(visit[v] == false && e[u][v] != inf) {
  			if(dis[u] + e[u][v] < dis[v]) {
  				dis[v] = dis[u] + e[u][v];
  				num[v] = num[u];
  				w[v] = w[u] + weight[v];
  			} else if(dis[u] + e[u][v] == dis[v]) {
  				num[v] = num[v] + num[u];
  				if(w[u] + weight[v] > w[v])
  					w[v] = w[u] + weight[v];
  			}
  		}
  	}
  }
  ```

  

- 边权不唯一

  比如旅游规划问题，存在多条最短路径时，选择花费最少的

  ```cpp
  void Dijkstra( Vertex s ) {
  	while (1) {
  		V = 未收录顶点中dist最小者;
  		if ( 这样的V不存在)
  			break;
  		collected[V] = true;
  		for ( V 的每个邻接点W )
  			if ( collected[W] == false )
  				if ( dist[V]+E<V,W> < dist[W] ) {
  					dist[W] = dist[V] + E<V,W> ;
  					path[W] = V;
  					cost[W] = cost[V] + C<V,W> ;
  				} else if ( (dist[V]+E<V,W> == dist[W])
  				            && (cost[V]+C<V,W> < cost[W]) ) {
  					cost[W] = cost[V] + C<V,W> ;
  					path[W] = V;
  				}
  	}
  }
  ```

algorithm常用函数补充
所有的range都是左闭右开区间
//判断range1是否为range2（长度length2>=length1）的子序列，可以完全相等
is_permutation(first1,last1,first2);

//查找range2在range1中的位置
search(first1,last1,first2,last2);

//交换a和b的值
swap(a,b);

//填充range
fill(first,last,value);

//reverse反转一个range
reverse(first,last);

//判断range是否有序【升序asc】
is_sorted(first,last);

//获取从那个位置开始无序
is_sorted_until(foo.begin(),foo.end());

//一个有序range插入新元素x的最小插入位置
lower_bound (v.begin(), v.end(), 20);

//一个有序range插入新元素x的最大插入位置
upper_bound (v.begin(), v.end(), 20);

//合并两个range到一个新容器中
std::merge (first,first+5,second,second+5,v.begin());

//获取min
std::min(1,2)

//获取max
std::max(1,2)

//获取min_element
std::cout << "The smallest element is " << *std::min_element(myints,myints+7) << '\n';

//获取max_element
std::cout << "The largest element is "  << *std::max_element(myints,myints+7) << '\n';

//获取一个range的一个序列
int main () {
  int myints[] = {1,2,3};

  std::sort (myints,myints+3);

  std::cout << "The 3! possible permutations with 3 elements:\n";
  do {
    std::cout << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';
  } while ( std::next_permutation(myints,myints+3) );//对应的就是更大的序列

  std::cout << "After loop: " << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';

  return 0;
}
输出结果
The 3! possible permutations with 3 elements:
1 2 3
1 3 2
2 1 3
2 3 1
3 1 2
3 2 1
After loop: 1 2 3


类似的还有
std::prev_permutation(myints,myints+3) 返回一个bool
true if the function could rearrange the object as a lexicographicaly smaller permutation(字典序更小的子序列).
Otherwise, the function returns false to indicate that the arrangement is not less than the previous, but the largest possible (sorted in descending order).


开考前，可以先用记事本把一些常用的头文件写下来

注意求平均值的时候是否需要四舍五入，如果需要+0.5即可

排名问题，如果两个人分数一样，那么排名也是一样的，比如
第一种情况：
1,1,1,4,5,6

```cpp
stu[0].rank[flag] = 1;
for(int i = 1; i < n; i++) {
	stu[i].rank[flag] = i + 1;
	if(stu[i].score[flag] == stu[i-1].score[flag])
		stu[i].rank[flag] = stu[i-1].rank[flag];
}
```
第二种情况：
1,2,3,4,5
这种比较简单，可以直接使用for循环，或者while循环，临时变量每次自增1即可

通常都是第一种情况，如果是第二种情况的话，很可能会附加排序字段

**fill函数**对于重置exist，flag，visit等类型的数组很方便，也省时间，一般只在fill函数无法满足要求时才考虑使用遍历重置数组

求图的连通分量的个数

```cpp
void dfs(int node) {
	visit[node] = true;
	for(int i = 1; i <= n; i++) {
		if(visit[i] == false && v[node][i] == 1)
			dfs(i);
	}
}

int cnt = 0;
for(int j = 1; j <= n; j++) {
    if(visit[j] == false) {
        dfs(j);
        cnt++;
    }
}
```

还可以用并查集来求连通分量



中序和后序转先序

```cpp
#include <cstdio>
using namespace std;
int post[] = {3, 4, 2, 6, 5, 1};
int in[] = {3, 2, 4, 1, 6, 5};
void pre(int root, int start, int end) {
    if(start > end) return ;
    int i = start;
    while(i < end && in[i] != post[root]) i++;
    printf("%d ", post[root]);
    pre(root - 1 - end + i, start, i - 1);
    pre(root - 1, i + 1, end);
}

int main() {
    pre(5, 0, 5);
    return 0;
}
```

循环输入

```cpp
#include<iostream>
using namespace std;
int main(){
	//cin循环输入测试
	int a;
    //只要输入的整数a不等于9，循环就会一直执行，直到输入9
	while(cin>>a,a!=9){
		printf("%d\n",a);
	}
	return 0;
}
```

如果要在遇到换行符时退出循环，可以使用getchar()函数来判断

```cpp
#include<iostream>
using namespace std;
int main() {
	//cin循环输入测试
	string tkey;
	while(cin >> tkey) {
		cout<<tkey<<endl;
		char c = getchar();
		if(c == '\n') break;
	}
	return 0;
}
```

浮点数统一采用double类型，scanf采用`"%lf"`，printf采用`"%f"`或者`"%lf"`