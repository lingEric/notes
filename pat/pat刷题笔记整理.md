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

