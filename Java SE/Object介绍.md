# Object介绍

所有类的超类

核心方法

```java
//指示其他某个对象是否与此对象“相等”。 
boolean equals(Object obj)

//返回此 Object 的运行时类。 	  
Class<?> getClass() 
    
//返回该对象的哈希码值。 
int hashCode() 
    
//返回该对象的字符串表示。 	  
String toString() 
```



## 关系操作符==

* 若操作数的类型是**基本数据类型**，则该关系操作符判断的是左右两边操作数的**值是否相等**
* 若操作数的类型是**引用数据类型**，则该关系操作符判断的是左右两边操作数的**内存地址是否相同**。也就是说，若此时返回true,则该操作符作用的一定是同一个对象。

## equals方法

**初衷**：判断两个对象的 content 是否相同
为了更直观地理解equals方法的作用，我们先看Object类中equals方法的实现。

```java
public boolean equals(Object obj) {
	return (this == obj);
}
```

很显然，在Object类中，equals方法是用来比较两个对象的引用是否相等，即是否指向同一个对象。也就是说，在不重写equals方法的时候，自定义的类型判断的就是内存地址是否相等。

接下来，看一下String类型的重写

```java
public boolean equals(Object anObject) {   // 方法签名与 Object类 中的一致
    if (this == anObject) {     // 先判断引用是否相同(是否为同一对象),
        return true;
    }
    if (anObject instanceof String) {   // 再判断类型是否一致,
        // 最后判断内容是否一致.
        String anotherString = (String)anObject;
        int n = count;
        if (n == anotherString.count) {
        char v1[] = value;
        char v2[] = anotherString.value;
        int i = offset;
        int j = anotherString.offset;
        while (n-- != 0) {
            if (v1[i++] != v2[j++])
            return false;
        }
        return true;
        }
    }
    return false;
}
```

即对于诸如“字符串比较时用的什么方法,内部实现如何？”之类问题的回答即为：

使用equals方法，内部实现分为三个步骤：

1. 比较引用是否相同(是否为同一对象)

2. 判断类型是否一致(是否为同一类型）

3. 比较内容是否一致

Java 中所有内置的类的 equals 方法的实现步骤均是如此，特别是诸如 Integer，Double 等包装器类。

### 重写原则

equals 重写原则

对象内容的比较才是设计equals()的真正目的，Java语言对equals()的要求如下，这些要求是重写该方法时必须遵循的：

* 对称性： 如果x.equals(y)返回是“true”，那么y.equals(x)也应该返回是“true” ；

* 自反性： x.equals(x)必须返回是“true” ；

* 类推性： 如果x.equals(y)返回是“true”，而且y.equals(z)返回是“true”，那么z.equals(x)也应该返回是“true”；

* 一致性： 如果x.equals(y)返回是“true”，只要x和y内容一直不变，不管你重复x.equals(y)多少次，返回都是“true” ；

* 任何情况下，x.equals(null)【应使用关系比较符 ==】，永远返回是“false”；x.equals(和x不同类型的对象)永远返回是“false”

## hashCode 

HashCode 只是在需要用到哈希算法的数据结构中才有用，比如 HashSet, HashMap 和 Hashtable

要想进一步了解 hashCode 的作用，我们必须先要了解Java中的容器，因为 HashCode 只是在需要用到哈希算法的数据结构中才有用，比如 HashSet, HashMap 和 Hashtable。

　　Java中的集合（Collection）有三类，一类是List，一类是Queue，再有一类就是Set。 前两个集合内的元素是有序的，元素可以重复；最后一个集合内的元素无序，但元素不可重复。

　　那么, 这里就有一个比较严重的问题：要想保证元素不重复，可两个元素是否重复应该依据什么来判断呢？ 这就是 Object.equals 方法了。但是，如果每增加一个元素就检查一次，那么当元素很多时，后添加到集合中的元素比较的次数就非常多了。 也就是说，如果集合中现在已经有1000个元素，那么第1001个元素加入集合时，它就要调用1000次equals方法。这显然会大大降低效率。于是，Java采用了哈希表的原理。 这样，我们对每个要存入集合的元素使用哈希算法算出一个值，然后根据该值计算出元素应该在数组的位置。所以，当集合要添加新的元素时，可分为两个步骤： 

1. 先调用这个元素的 hashCode 方法，然后根据所得到的值计算出元素应该在数组的位置。如果这个位置上没有元素，那么直接将它存储在这个位置上；

2. 如果这个位置上已经有元素了，那么调用它的equals方法与新元素进行比较：相同的话就不存了，否则，将其存在这个位置对应的链表中（Java 中 HashSet, HashMap 和 Hashtable的实现总将元素放到链表的表头）。

## equals 与 hashCode

谈到hashCode就不得不说equals方法，二者均是Object类里的方法。由于Object类是所有类的基类，所以一切类里都可以重写这两个方法。

原则 1 ： 如果 x.equals(y) 返回 “true”，那么 x 和 y 的 hashCode() 必须相等 ；
原则 2 ： 如果 x.equals(y) 返回 “false”，那么 x 和 y 的 hashCode() 有可能相等，也有可能不等 ；
原则 3 ： 如果 x 和 y 的 hashCode() 不相等，那么 x.equals(y) 一定返回 “false” ；
原则 4 ： 一般来讲，equals 这个方法是给用户调用的，而 hashcode 方法一般用户不会去调用 ；
原则 5 ： 当一个对象类型作为集合对象的元素时，那么这个对象应该拥有自己的equals()和hashCode()设计，而且要遵守前面所说的几个原则。

* hashcode是系统用来快速检索对象而使用
* equals方法本意是用来判断引用的对象是否一致
* 重写equals方法和hashcode方法时，equals方法中用到的成员变量也必定会在hashcode方法中用到,只不过前者作为比较项，后者作为生成摘要的信息项，本质上所用到的数据是一样的，从而保证二者的一致性

详细内容参考链接地址：<https://www.cnblogs.com/skywang12345/p/3324958.html>

<https://juejin.im/post/5a4379d4f265da432003874c>