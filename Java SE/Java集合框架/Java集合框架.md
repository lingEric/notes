# Java集合框架

## 集合框架介绍

Java集合是java提供的工具包，包含了常用的数据结构：集合、链表、队列、栈、数组、映射等。Java集合工具包位置是java.util.*
Java集合主要可以划分为4个部分：List列表、Set集合、Map映射、工具类(Iterator迭代器、Enumeration枚举类、Arrays和Collections)、。
Java集合工具包框架图(如下)：

[![img](https://images0.cnblogs.com/blog/497634/201309/08171028-a5e372741b18431591bb577b1e1c95e6.jpg)](https://images0.cnblogs.com/blog/497634/201309/08171028-a5e372741b18431591bb577b1e1c95e6.jpg)

**大致说明：**

看上面的框架图，先抓住它的主干，即Collection和Map。

1 Collection是一个接口，是高度抽象出来的集合，它包含了集合的基本操作和属性。

  Collection包含了List和Set两大分支。
  (01) List是一个有序的队列，每一个元素都有它的索引。第一个元素的索引值是0。
          List的实现类有LinkedList, ArrayList, Vector, Stack。

  (02) Set是一个不允许有重复元素的集合。
          Set的实现类有HastSet和TreeSet。HashSet依赖于HashMap，它实际上是通过HashMap实现的；TreeSet依赖于TreeMap，它实际上是通过TreeMap实现的。

2 Map是一个映射接口，即key-value键值对。Map中的每一个元素包含“一个key”和“key对应的value”。

   AbstractMap是个抽象类，它实现了Map接口中的大部分API。而HashMap，TreeMap，WeakHashMap都是继承于AbstractMap。
   Hashtable虽然继承于Dictionary，但它实现了Map接口。

接下来，再看Iterator。它是遍历集合的工具，即我们通常通过Iterator迭代器来遍历集合。我们说Collection依赖于Iterator，是因为Collection的实现类都要实现iterator()函数，返回一个Iterator对象。
ListIterator是专门为遍历List而存在的。

再看Enumeration，它是JDK 1.0引入的抽象类。作用和Iterator一样，也是遍历集合；但是Enumeration的功能要比Iterator少。在上面的框图中，Enumeration只能在Hashtable, Vector, Stack中使用。

最后，看Arrays和Collections。它们是操作数组、集合的两个工具类。

有了上面的整体框架之后，我们接下来对每个类分别进行分析。





## Collection架构

下面先看看Collection的一些框架类的关系图：

[![img](https://images0.cnblogs.com/blog/497634/201309/08172429-1ecddb7a87e347369ffc7c1c30f18396.jpg)](https://images0.cnblogs.com/blog/497634/201309/08172429-1ecddb7a87e347369ffc7c1c30f18396.jpg)

Collection是一个接口，它主要的两个分支是：**List** 和 **Set**。

List和Set都是接口，它们继承于Collection。**List是有序的队列，List中可以有重复的元素**；而**Set是数学概念中的集合，Set中没有重复元素**！
List和Set都有它们各自的实现类。

  为了方便，我们抽象出了AbstractCollection抽象类，它实现了Collection中的绝大部分函数；这样，在Collection的实现类中，我们就可以通过继承AbstractCollection省去重复编码。AbstractList和AbstractSet都继承于AbstractCollection，具体的List实现类继承于AbstractList，而Set的实现类则继承于AbstractSet。

  另外，Collection中有一个iterator()函数，它的作用是返回一个Iterator接口。通常，我们通过Iterator迭代器来遍历集合。ListIterator是List接口所特有的，在List接口中，通过ListIterator()返回一个ListIterator对象。

  接下来，我们看看各个接口和抽象类的介绍；然后，再对实现类进行详细的了解。 

### **1 Collection简介**

Collection的定义如下：

```java
public interface Collection<E> extends Iterable<E> {}
```

**它是一个接口，是高度抽象出来的集合，它包含了集合的基本操作：添加、删除、清空、遍历(读取)、是否为空、获取大小、是否保护某元素等等。**


Collection接口的所有子类(直接子类和间接子类)都必须实现2种构造函数：不带参数的构造函数 和 参数为Collection的构造函数。带参数的构造函数，可以用来转换Collection的类型。



```java
// Collection的API
abstract boolean         add(E object)
abstract boolean         addAll(Collection<? extends E> collection)
abstract void            clear()
abstract boolean         contains(Object object)
abstract boolean         containsAll(Collection<?> collection)
abstract boolean         equals(Object object)
abstract int             hashCode()
abstract boolean         isEmpty()
abstract Iterator<E>     iterator()
abstract boolean         remove(Object object)
abstract boolean         removeAll(Collection<?> collection)
abstract boolean         retainAll(Collection<?> collection)
abstract int             size()
abstract <T> T[]         toArray(T[] array)
abstract Object[]        toArray()
```



 

### **2 List简介**

List的定义如下：

```java
public interface List<E> extends Collection<E> {}
```

**List是一个继承于Collection的接口，即List是集合中的一种。List是有序的队列，List中的每一个元素都有一个索引；第一个元素的索引值是0，往后的元素的索引值依次+1。和Set不同，List中允许有重复的元素。**
*List的官方介绍如下*：

```
A List is a collection which maintains an ordering for its elements. Every element in the List has an index. Each element can thus be accessed by its index, with the first index being zero. Normally, Lists allow duplicate elements, as compared to Sets, where elements have to be unique.
```

 关于API方面。既然List是继承于Collection接口，它自然就包含了Collection中的全部函数接口；由于List是有序队列，它也额外的有自己的API接口。主要有“添加、删除、获取、修改指定位置的元素”、“获取List中的子队列”等。



```java
// Collection的API
abstract boolean         add(E object)
abstract boolean         addAll(Collection<? extends E> collection)
abstract void            clear()
abstract boolean         contains(Object object)
abstract boolean         containsAll(Collection<?> collection)
abstract boolean         equals(Object object)
abstract int             hashCode()
abstract boolean         isEmpty()
abstract Iterator<E>     iterator()
abstract boolean         remove(Object object)
abstract boolean         removeAll(Collection<?> collection)
abstract boolean         retainAll(Collection<?> collection)
abstract int             size()
abstract <T> T[]         toArray(T[] array)
abstract Object[]        toArray()
// 相比与Collection，List新增的API：
abstract void                add(int location, E object)
abstract boolean             addAll(int location, Collection<? extends E> collection)
abstract E                   get(int location)
abstract int                 indexOf(Object object)
abstract int                 lastIndexOf(Object object)
abstract ListIterator<E>     listIterator(int location)
abstract ListIterator<E>     listIterator()
abstract E                   remove(int location)
abstract E                   set(int location, E object)
abstract List<E>             subList(int start, int end)
```



 

### **3 Set简介**

Set的定义如下：

```java
public interface Set<E> extends Collection<E> {}
```

**Set是一个继承于Collection的接口，即Set也是集合中的一种。Set是没有重复元素的集合。**

关于API方面。Set的API和Collection完全一样。



```java
// Set的API
abstract boolean         add(E object)
abstract boolean         addAll(Collection<? extends E> collection)
abstract void             clear()
abstract boolean         contains(Object object)
abstract boolean         containsAll(Collection<?> collection)
abstract boolean         equals(Object object)
abstract int             hashCode()
abstract boolean         isEmpty()
abstract Iterator<E>     iterator()
abstract boolean         remove(Object object)
abstract boolean         removeAll(Collection<?> collection)
abstract boolean         retainAll(Collection<?> collection)
abstract int             size()
abstract <T> T[]         toArray(T[] array)
abstract Object[]         toArray()
```



 

### **4 AbstractCollection**

AbstractCollection的定义如下：

```java
public abstract class AbstractCollection<E> implements Collection<E> {}
```

AbstractCollection是一个抽象类，它实现了Collection中除iterator()和size()之外的函数。
AbstractCollection的主要作用：它实现了Collection接口中的大部分函数。从而方便其它类实现Collection，比如ArrayList、LinkedList等，它们这些类想要实现Collection接口，通过继承AbstractCollection就已经实现了大部分的接口了。

 

### **5 AbstractList**

AbstractList的定义如下：

```java
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {}
```

AbstractList是一个继承于AbstractCollection，并且实现List接口的抽象类。它实现了List中除size()、get(int location)之外的函数。
AbstractList的主要作用：它实现了List接口中的大部分函数。从而方便其它类继承List。
另外，和AbstractCollection相比，AbstractList抽象类中，实现了iterator()接口。

 

### **6 AbstractSet**

AbstractSet的定义如下： 

```
public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {}
```

AbstractSet是一个继承于AbstractCollection，并且实现Set接口的抽象类。由于Set接口和Collection接口中的API完全一样，Set也就没有自己单独的API。和AbstractCollection一样，它实现了List中除iterator()和size()之外的函数。
AbstractSet的主要作用：它实现了Set接口中的大部分函数。从而方便其它类实现Set接口。

 

### **7 Iterator**

Iterator的定义如下：

```
public interface Iterator<E> {}
```

Iterator是一个接口，它是集合的迭代器。集合可以通过Iterator去遍历集合中的元素。Iterator提供的API接口，包括：是否存在下一个元素、获取下一个元素、删除当前元素。
注意：Iterator遍历Collection时，是fail-fast机制的。即，当某一个线程A通过iterator去遍历某集合的过程中，若该集合的内容被其他线程所改变了；那么线程A访问集合时，就会抛出ConcurrentModificationException异常，产生fail-fast事件。关于fail-fast的详细内容，我们会在后面专门进行说明。TODO

```
// Iterator的API
abstract boolean hasNext()
abstract E next()
abstract void remove()
```

 

### **8 ListIterator**

ListIterator的定义如下：

```
public interface ListIterator<E> extends Iterator<E> {}
```

ListIterator是一个继承于Iterator的接口，它是队列迭代器。专门用于便利List，能提供向前/向后遍历。相比于Iterator，它新增了添加、是否存在上一个元素、获取上一个元素等等API接口。



```
// ListIterator的API
// 继承于Iterator的接口
abstract boolean hasNext()
abstract E next()
abstract void remove()
// 新增API接口
abstract void add(E object)
abstract boolean hasPrevious()
abstract int nextIndex()
abstract E previous()
abstract int previousIndex()
abstract void set(E object)
```

## ArrayList

### ArrayList介绍

**ArrayList简介**

ArrayList 是一个数组队列，相当于动态数组。与Java中的数组相比，它的容量能动态增长。它继承于AbstractList，实现了List, RandomAccess, Cloneable, java.io.Serializable这些接口。
ArrayList 继承了AbstractList，实现了List。它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能。
ArrayList 实现了RandmoAccess接口，即提供了随机访问功能。RandmoAccess是java中用来被List实现，为List提供快速访问功能的。在ArrayList中，我们即可以通过元素的序号快速获取元素对象；这就是快速随机访问。稍后，我们会比较List的“快速随机访问”和“通过Iterator迭代器访问”的效率。
ArrayList 实现了Cloneable接口，即覆盖了函数clone()，能被克隆。
ArrayList 实现java.io.Serializable接口，这意味着ArrayList支持序列化，能通过序列化去传输。
和Vector不同，ArrayList中的操作**不是线程安全的**！所以，建议在单线程中才使用ArrayList，而在多线程中可以选择Vector或者CopyOnWriteArrayList。

 **ArrayList构造函数**

```java
// 默认构造函数
ArrayList()

// capacity是ArrayList的默认容量大小。当由于增加数据导致容量不足时，容量会添加上一次容量大小的一半。
ArrayList(int capacity)

// 创建一个包含collection的ArrayList
ArrayList(Collection<? extends E> collection)
```

**ArrayList的API**

```java
// Collection中定义的API
boolean             add(E object)
boolean             addAll(Collection<? extends E> collection)
void                clear()
boolean             contains(Object object)
boolean             containsAll(Collection<?> collection)
boolean             equals(Object object)
int                 hashCode()
boolean             isEmpty()
Iterator<E>         iterator()
boolean             remove(Object object)
boolean             removeAll(Collection<?> collection)
boolean             retainAll(Collection<?> collection)
int                 size()
<T> T[]             toArray(T[] array)
Object[]            toArray()
// AbstractList中定义的API
void                add(int location, E object)
boolean             addAll(int location, Collection<? extends E> collection)
E                   get(int location)
int                 indexOf(Object object)
int                 lastIndexOf(Object object)
ListIterator<E>     listIterator(int location)
ListIterator<E>     listIterator()
E                   remove(int location)
E                   set(int location, E object)
List<E>             subList(int start, int end)
// ArrayList新增的API
Object               clone()
void                 ensureCapacity(int minimumCapacity)
void                 trimToSize()
void                 removeRange(int fromIndex, int toIndex)
```

### **ArrayList数据结构**

**ArrayList的继承关系**

```java
java.lang.Object
   ↳     java.util.AbstractCollection<E>
         ↳     java.util.AbstractList<E>
               ↳     java.util.ArrayList<E>

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {}
```

**ArrayList与Collection关系如下图**：

[![img](https://images0.cnblogs.com/blog/497634/201401/272343457973489.jpg)](https://images0.cnblogs.com/blog/497634/201401/272343457973489.jpg)

ArrayList包含了两个重要的对象：elementData 和 size。

1. elementData 是"Object[]类型的数组"，它保存了添加到ArrayList中的元素。实际上，elementData是个动态数组，我们能通过构造函数 ArrayList(int initialCapacity)来执行它的初始容量为initialCapacity；如果通过不含参数的构造函数ArrayList()来创建ArrayList，则elementData的容量默认是10。elementData数组的大小会根据ArrayList容量的增长而动态的增长，具体的增长方式，请参考源码分析中的ensureCapacity()函数。

2. size 则是动态数组的实际大小。

### ArrayList源码解析(基于JDK1.6.0_45)

为了更了解ArrayList的原理，下面对ArrayList源码代码作出分析。ArrayList是通过数组实现的，源码比较容易理解。 

```java
package java.util;

public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    // 序列版本号
    private static final long serialVersionUID = 8683452581122892189L;

    // 保存ArrayList中数据的数组
    private transient Object[] elementData;

    // ArrayList中实际数据的数量
    private int size;

    // ArrayList带容量大小的构造函数。
    public ArrayList(int initialCapacity) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        // 新建一个数组
        this.elementData = new Object[initialCapacity];
    }

    // ArrayList构造函数。默认容量是10。
    public ArrayList() {
        this(10);
    }

    // 创建一个包含collection的ArrayList
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        size = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    }


    // 将当前容量值设为 =实际元素个数
    public void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            elementData = Arrays.copyOf(elementData, size);
        }
    }


    // 确定ArrarList的容量。
    // 若ArrayList的容量不足以容纳当前的全部元素，设置 新的容量=“(原始容量x3)/2 + 1”
    public void ensureCapacity(int minCapacity) {
        // 将“修改统计数”+1
        modCount++;
        int oldCapacity = elementData.length;
        // 若当前容量不足以容纳当前的元素个数，设置 新的容量=“(原始容量x3)/2 + 1”
        if (minCapacity > oldCapacity) {
            Object oldData[] = elementData;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            elementData = Arrays.copyOf(elementData, newCapacity);
        }
    }

    // 添加元素e
    public boolean add(E e) {
        // 确定ArrayList的容量大小
        ensureCapacity(size + 1);  // Increments modCount!!
        // 添加e到ArrayList中
        elementData[size++] = e;
        return true;
    }

    // 返回ArrayList的实际大小
    public int size() {
        return size;
    }

    // 返回ArrayList是否包含Object(o)
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    // 返回ArrayList是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    // 正向查找，返回元素的索引值
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
            if (elementData[i]==null)
                return i;
            } else {
                for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
            }
            return -1;
        }

        // 反向查找，返回元素的索引值
        public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = size-1; i >= 0; i--)
            if (o.equals(elementData[i]))
                return i;
        }
        return -1;
    }

    // 反向查找(从数组末尾向开始查找)，返回元素(o)的索引值
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = size-1; i >= 0; i--)
            if (o.equals(elementData[i]))
                return i;
        }
        return -1;
    }
 

    // 返回ArrayList的Object数组
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    // 返回ArrayList的模板数组。所谓模板数组，即可以将T设为任意的数据类型
    public <T> T[] toArray(T[] a) {
        // 若数组a的大小 < ArrayList的元素个数；
        // 则新建一个T[]数组，数组大小是“ArrayList的元素个数”，并将“ArrayList”全部拷贝到新数组中
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());

        // 若数组a的大小 >= ArrayList的元素个数；
        // 则将ArrayList的全部元素都拷贝到数组a中。
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // 获取index位置的元素值
    public E get(int index) {
        RangeCheck(index);

        return (E) elementData[index];
    }

    // 设置index位置的值为element
    public E set(int index, E element) {
        RangeCheck(index);

        E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    // 将e添加到ArrayList中
    public boolean add(E e) {
        ensureCapacity(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    // 将e添加到ArrayList的指定位置
    public void add(int index, E element) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(
            "Index: "+index+", Size: "+size);

        ensureCapacity(size+1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
             size - index);
        elementData[index] = element;
        size++;
    }

    // 删除ArrayList指定位置的元素
    public E remove(int index) {
        RangeCheck(index);

        modCount++;
        E oldValue = (E) elementData[index];

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                 numMoved);
        elementData[--size] = null; // Let gc do its work

        return oldValue;
    }

    // 删除ArrayList的指定元素
    public boolean remove(Object o) {
        if (o == null) {
                for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
        } else {
            for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
        }
        return false;
    }


    // 快速删除第index个元素
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        // 从"index+1"开始，用后面的元素替换前面的元素。
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        // 将最后一个元素设为null
        elementData[--size] = null; // Let gc do its work
    }

    // 删除元素
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
            return true;
            }
        } else {
            // 便利ArrayList，找到“元素o”，则删除，并返回true。
            for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
            return true;
            }
        }
        return false;
    }

    // 清空ArrayList，将全部的元素设为null
    public void clear() {
        modCount++;

        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }

    // 将集合c追加到ArrayList中
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    // 从index位置开始，将集合c添加到ArrayList
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(
            "Index: " + index + ", Size: " + size);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                 numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    // 删除fromIndex到toIndex之间的全部元素。
    protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                         numMoved);

    // Let gc do its work
    int newSize = size - (toIndex-fromIndex);
    while (size != newSize)
        elementData[--size] = null;
    }

    private void RangeCheck(int index) {
    if (index >= size)
        throw new IndexOutOfBoundsException(
        "Index: "+index+", Size: "+size);
    }


    // 克隆函数
    public Object clone() {
        try {
            ArrayList<E> v = (ArrayList<E>) super.clone();
            // 将当前ArrayList的全部元素拷贝到v中
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }


    // java.io.Serializable的写入函数
    // 将ArrayList的“容量，所有的元素值”都写入到输出流中
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
    // Write out element count, and any hidden stuff
    int expectedModCount = modCount;
    s.defaultWriteObject();

        // 写入“数组的容量”
        s.writeInt(elementData.length);

    // 写入“数组的每一个元素”
    for (int i=0; i<size; i++)
            s.writeObject(elementData[i]);

    if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

    }


    // java.io.Serializable的读取函数：根据写入方式读出
    // 先将ArrayList的“容量”读出，然后将“所有的元素值”读出
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // 从输入流中读取ArrayList的“容量”
        int arrayLength = s.readInt();
        Object[] a = elementData = new Object[arrayLength];

        // 从输入流中将“所有的元素值”读出
        for (int i=0; i<size; i++)
            a[i] = s.readObject();
    }
}
```



**总结**：
(01) ArrayList 实际上是**通过一个数组去保存数据的**。当我们构造ArrayList时；若使用默认构造函数，则ArrayList的**默认容量大小是10**。
(02) 当ArrayList容量不足以容纳全部元素时，ArrayList会重新设置容量：**新的容量=“(原始容量x3)/2 + 1”**。
(03) ArrayList的克隆函数，即是将全部元素克隆到一个数组中。
(04) ArrayList实现java.io.Serializable的方式。当写入到输出流时，先写入“容量”，再依次写入“每一个元素”；当读出输入流时，先读取“容量”，再依次读取“每一个元素”。

 

### ArrayList遍历方式

**ArrayList支持3种遍历方式**

(01) 第一种，**通过迭代器遍历**。即通过Iterator去遍历。

```
Integer value = null;
Iterator iter = list.iterator();
while (iter.hasNext()) {
    value = (Integer)iter.next();
}
```

(02) 第二种，**随机访问，通过索引值去遍历。**
由于ArrayList实现了RandomAccess接口，它支持通过索引值去随机访问元素。

```
Integer value = null;
int size = list.size();
for (int i=0; i<size; i++) {
    value = (Integer)list.get(i);        
}
```

(03) 第三种，**for循环遍历**。如下：

```
Integer value = null;
for (Integer integ:list) {
    value = integ;
}
```

 下面通过一个实例，**比较这3种方式的效率**，实例代码(ArrayListRandomAccessTest.java)如下：

```java
import java.util.*;
import java.util.concurrent.*;

/*
 * @desc ArrayList遍历方式和效率的测试程序。
 *
 * @author skywang
 */
public class ArrayListRandomAccessTest {

    public static void main(String[] args) {
        List list = new ArrayList();
        for (int i=0; i<100000; i++)
            list.add(i);
        //isRandomAccessSupported(list);
        iteratorThroughRandomAccess(list) ;
        iteratorThroughIterator(list) ;
        iteratorThroughFor2(list) ;
    
    }

    private static void isRandomAccessSupported(List list) {
        if (list instanceof RandomAccess) {
            System.out.println("RandomAccess implemented!");
        } else {
            System.out.println("RandomAccess not implemented!");
        }

    }

    public static void iteratorThroughRandomAccess(List list) {

        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();
        for (int i=0; i<list.size(); i++) {
            list.get(i);
        }
        endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        System.out.println("iteratorThroughRandomAccess：" + interval+" ms");
    }

    public static void iteratorThroughIterator(List list) {

        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();
        for(Iterator iter = list.iterator(); iter.hasNext(); ) {
            iter.next();
        }
        endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        System.out.println("iteratorThroughIterator：" + interval+" ms");
    }


    public static void iteratorThroughFor2(List list) {

        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();
        for(Object obj:list)
            ;
        endTime = System.currentTimeMillis();
        long interval = endTime - startTime;
        System.out.println("iteratorThroughFor2：" + interval+" ms");
    }
}
```

**运行结果**：

iteratorThroughRandomAccess：3 ms
iteratorThroughIterator：8 ms
iteratorThroughFor2：5 ms

由此可见，**遍历ArrayList时，使用随机访问(即，通过索引序号访问)效率最高，而使用迭代器的效率最低！**



## LinkedList

### LinkedList介绍

**LinkedList简介**

LinkedList 是一个继承于AbstractSequentialList的双向链表。它也可以被当作堆栈、队列或双端队列进行操作。
LinkedList 实现 List 接口，能对它进行队列操作。
LinkedList 实现 Deque 接口，即能将LinkedList当作双端队列使用。
LinkedList 实现了Cloneable接口，即覆盖了函数clone()，能克隆。
LinkedList 实现java.io.Serializable接口，这意味着LinkedList支持序列化，能通过序列化去传输。
LinkedList 是非同步的。

**LinkedList构造函数**

```java
// 默认构造函数
LinkedList()

// 创建一个LinkedList，保护Collection中的全部元素。
LinkedList(Collection<? extends E> collection)
```

**LinkedList的API** 



```java
//LinkedList的API
boolean       add(E object)
void          add(int location, E object)
boolean       addAll(Collection<? extends E> collection)
boolean       addAll(int location, Collection<? extends E> collection)
void          addFirst(E object)
void          addLast(E object)
void          clear()
Object        clone()
boolean       contains(Object object)
Iterator<E>   descendingIterator()
E             element()
E             get(int location)
E             getFirst()
E             getLast()
int           indexOf(Object object)
int           lastIndexOf(Object object)
ListIterator<E>     listIterator(int location)
boolean       offer(E o)
boolean       offerFirst(E e)
boolean       offerLast(E e)
E             peek()
E             peekFirst()
E             peekLast()
E             poll()
E             pollFirst()
E             pollLast()
E             pop()
void          push(E e)
E             remove()
E             remove(int location)
boolean       remove(Object object)
E             removeFirst()
boolean       removeFirstOccurrence(Object o)
E             removeLast()
boolean       removeLastOccurrence(Object o)
E             set(int location, E object)
int           size()
<T> T[]       toArray(T[] contents)
Object[]     toArray()
```



 **AbstractSequentialList简介**

在介绍LinkedList的源码之前，先介绍一下AbstractSequentialList。毕竟，LinkedList是AbstractSequentialList的子类。

AbstractSequentialList 实现了get(int index)、set(int index, E element)、add(int index, E element) 和 remove(int index)这些函数。**这些接口都是随机访问List的**，LinkedList是双向链表；既然它继承于AbstractSequentialList，就相当于已经实现了“get(int index)这些接口”。

此外，我们若需要通过AbstractSequentialList自己实现一个列表，只需要扩展此类，并提供 listIterator() 和 size() 方法的实现即可。若要实现不可修改的列表，则需要实现列表迭代器的 hasNext、next、hasPrevious、previous 和 index 方法即可。

### **LinkedList数据结构**

**LinkedList的继承关系**



```java
java.lang.Object
   ↳     java.util.AbstractCollection<E>
         ↳     java.util.AbstractList<E>
               ↳     java.util.AbstractSequentialList<E>
                     ↳     java.util.LinkedList<E>

public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable {}
```



 

**LinkedList与Collection关系如下图：**

[![img](https://images0.cnblogs.com/blog/497634/201401/272345393446232.jpg)](https://images0.cnblogs.com/blog/497634/201401/272345393446232.jpg)

LinkedList的本质是双向链表。

1. LinkedList继承于AbstractSequentialList，并且实现了Dequeue接口。 
2. LinkedList包含两个重要的成员：header 和 size。
   header是双向链表的表头，它是双向链表节点所对应的类Entry的实例。Entry中包含成员变量： previous, next, element。其中，previous是该节点的上一个节点，next是该节点的下一个节点，element是该节点所包含的值。 
   size是双向链表中节点的个数。

### LinkedList源码解析(基于JDK1.6.0_45)

LinkedList实际上是通过双向链表去实现的。既然是双向链表，那么它的**顺序访问会非常高效，而随机访问效率比较低**。
既然LinkedList是通过双向链表的，但是它也实现了List接口{*也就是说，它实现了get(int location)、remove(int location)等“根据**索引值**来获取、删除节点的函数”*}。LinkedList是如何实现List的这些接口的，如何将“**双向链表和索引值联系起来的**”？
实际原理非常简单，它就是通过一个**计数索引值**来实现的。例如，当我们调用get(int location)时，首先会比较“location”和“双向链表长度的1/2”；若前者大，则从链表头开始往后查找，直到location位置；否则，从链表末尾开始向前查找，直到location位置。
   这就是“双线链表和索引值联系起来”的方法。

好了，接下来开始阅读源码(只要理解双向链表，那么LinkedList的源码很容易理解的)。

```java
package java.util;

public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    // 链表的表头，表头不包含任何数据。Entry是个链表类数据结构。
    private transient Entry<E> header = new Entry<E>(null, null, null);

    // LinkedList中元素个数
    private transient int size = 0;

    // 默认构造函数：创建一个空的链表
    public LinkedList() {
        header.next = header.previous = header;
    }

    // 包含“集合”的构造函数:创建一个包含“集合”的LinkedList
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    // 获取LinkedList的第一个元素
    public E getFirst() {
        if (size==0)
            throw new NoSuchElementException();

        // 链表的表头header中不包含数据。
        // 这里返回header所指下一个节点所包含的数据。
        return header.next.element;
    }

    // 获取LinkedList的最后一个元素
    public E getLast()  {
        if (size==0)
            throw new NoSuchElementException();

        // 由于LinkedList是双向链表；而表头header不包含数据。
        // 因而，这里返回表头header的前一个节点所包含的数据。
        return header.previous.element;
    }

    // 删除LinkedList的第一个元素
    public E removeFirst() {
        return remove(header.next);
    }

    // 删除LinkedList的最后一个元素
    public E removeLast() {
        return remove(header.previous);
    }

    // 将元素添加到LinkedList的起始位置
    public void addFirst(E e) {
        addBefore(e, header.next);
    }

    // 将元素添加到LinkedList的结束位置
    public void addLast(E e) {
        addBefore(e, header);
    }

    // 判断LinkedList是否包含元素(o)
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    // 返回LinkedList的大小
    public int size() {
        return size;
    }

    // 将元素(E)添加到LinkedList中
    public boolean add(E e) {
        // 将节点(节点数据是e)添加到表头(header)之前。
        // 即，将节点添加到双向链表的末端。
        addBefore(e, header);
        return true;
    }

    // 从LinkedList中删除元素(o)
    // 从链表开始查找，如存在元素(o)则删除该元素并返回true；
    // 否则，返回false。
    public boolean remove(Object o) {
        if (o==null) {
            // 若o为null的删除情况
            for (Entry<E> e = header.next; e != header; e = e.next) {
                if (e.element==null) {
                    remove(e);
                    return true;
                }
            }
        } else {
            // 若o不为null的删除情况
            for (Entry<E> e = header.next; e != header; e = e.next) {
                if (o.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    // 将“集合(c)”添加到LinkedList中。
    // 实际上，是从双向链表的末尾开始，将“集合(c)”添加到双向链表中。
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    // 从双向链表的index开始，将“集合(c)”添加到双向链表中。
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                                ", Size: "+size);
        Object[] a = c.toArray();
        // 获取集合的长度
        int numNew = a.length;
        if (numNew==0)
            return false;
        modCount++;

        // 设置“当前要插入节点的后一个节点”
        Entry<E> successor = (index==size ? header : entry(index));
        // 设置“当前要插入节点的前一个节点”
        Entry<E> predecessor = successor.previous;
        // 将集合(c)全部插入双向链表中
        for (int i=0; i<numNew; i++) {
            Entry<E> e = new Entry<E>((E)a[i], successor, predecessor);
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        // 调整LinkedList的实际大小
        size += numNew;
        return true;
    }

    // 清空双向链表
    public void clear() {
        Entry<E> e = header.next;
        // 从表头开始，逐个向后遍历；对遍历到的节点执行一下操作：
        // (01) 设置前一个节点为null 
        // (02) 设置当前节点的内容为null 
        // (03) 设置后一个节点为“新的当前节点”
        while (e != header) {
            Entry<E> next = e.next;
            e.next = e.previous = null;
            e.element = null;
            e = next;
        }
        header.next = header.previous = header;
        // 设置大小为0
        size = 0;
        modCount++;
    }

    // 返回LinkedList指定位置的元素
    public E get(int index) {
        return entry(index).element;
    }

    // 设置index位置对应的节点的值为element
    public E set(int index, E element) {
        Entry<E> e = entry(index);
        E oldVal = e.element;
        e.element = element;
        return oldVal;
    }
 
    // 在index前添加节点，且节点的值为element
    public void add(int index, E element) {
        addBefore(element, (index==size ? header : entry(index)));
    }

    // 删除index位置的节点
    public E remove(int index) {
        return remove(entry(index));
    }

    // 获取双向链表中指定位置的节点
    private Entry<E> entry(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                                ", Size: "+size);
        Entry<E> e = header;
        // 获取index处的节点。
        // 若index < 双向链表长度的1/2,则从前先后查找;
        // 否则，从后向前查找。
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++)
                e = e.next;
        } else {
            for (int i = size; i > index; i--)
                e = e.previous;
        }
        return e;
    }

    // 从前向后查找，返回“值为对象(o)的节点对应的索引”
    // 不存在就返回-1
    public int indexOf(Object o) {
        int index = 0;
        if (o==null) {
            for (Entry e = header.next; e != header; e = e.next) {
                if (e.element==null)
                    return index;
                index++;
            }
        } else {
            for (Entry e = header.next; e != header; e = e.next) {
                if (o.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }

    // 从后向前查找，返回“值为对象(o)的节点对应的索引”
    // 不存在就返回-1
    public int lastIndexOf(Object o) {
        int index = size;
        if (o==null) {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (e.element==null)
                    return index;
            }
        } else {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (o.equals(e.element))
                    return index;
            }
        }
        return -1;
    }

    // 返回第一个节点
    // 若LinkedList的大小为0,则返回null
    public E peek() {
        if (size==0)
            return null;
        return getFirst();
    }

    // 返回第一个节点
    // 若LinkedList的大小为0,则抛出异常
    public E element() {
        return getFirst();
    }

    // 删除并返回第一个节点
    // 若LinkedList的大小为0,则返回null
    public E poll() {
        if (size==0)
            return null;
        return removeFirst();
    }

    // 将e添加双向链表末尾
    public boolean offer(E e) {
        return add(e);
    }

    // 将e添加双向链表开头
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    // 将e添加双向链表末尾
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    // 返回第一个节点
    // 若LinkedList的大小为0,则返回null
    public E peekFirst() {
        if (size==0)
            return null;
        return getFirst();
    }

    // 返回最后一个节点
    // 若LinkedList的大小为0,则返回null
    public E peekLast() {
        if (size==0)
            return null;
        return getLast();
    }

    // 删除并返回第一个节点
    // 若LinkedList的大小为0,则返回null
    public E pollFirst() {
        if (size==0)
            return null;
        return removeFirst();
    }

    // 删除并返回最后一个节点
    // 若LinkedList的大小为0,则返回null
    public E pollLast() {
        if (size==0)
            return null;
        return removeLast();
    }

    // 将e插入到双向链表开头
    public void push(E e) {
        addFirst(e);
    }

    // 删除并返回第一个节点
    public E pop() {
        return removeFirst();
    }

    // 从LinkedList开始向后查找，删除第一个值为元素(o)的节点
    // 从链表开始查找，如存在节点的值为元素(o)的节点，则删除该节点
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    // 从LinkedList末尾向前查找，删除第一个值为元素(o)的节点
    // 从链表开始查找，如存在节点的值为元素(o)的节点，则删除该节点
    public boolean removeLastOccurrence(Object o) {
        if (o==null) {
            for (Entry<E> e = header.previous; e != header; e = e.previous) {
                if (e.element==null) {
                    remove(e);
                    return true;
                }
            }
        } else {
            for (Entry<E> e = header.previous; e != header; e = e.previous) {
                if (o.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    // 返回“index到末尾的全部节点”对应的ListIterator对象(List迭代器)
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    // List迭代器
    private class ListItr implements ListIterator<E> {
        // 上一次返回的节点
        private Entry<E> lastReturned = header;
        // 下一个节点
        private Entry<E> next;
        // 下一个节点对应的索引值
        private int nextIndex;
        // 期望的改变计数。用来实现fail-fast机制。
        private int expectedModCount = modCount;

        // 构造函数。
        // 从index位置开始进行迭代
        ListItr(int index) {
            // index的有效性处理
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: "+index+ ", Size: "+size);
            // 若 “index 小于 ‘双向链表长度的一半’”，则从第一个元素开始往后查找；
            // 否则，从最后一个元素往前查找。
            if (index < (size >> 1)) {
                next = header.next;
                for (nextIndex=0; nextIndex<index; nextIndex++)
                    next = next.next;
            } else {
                next = header;
                for (nextIndex=size; nextIndex>index; nextIndex--)
                    next = next.previous;
            }
        }

        // 是否存在下一个元素
        public boolean hasNext() {
            // 通过元素索引是否等于“双向链表大小”来判断是否达到最后。
            return nextIndex != size;
        }

        // 获取下一个元素
        public E next() {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            // next指向链表的下一个元素
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        // 是否存在上一个元素
        public boolean hasPrevious() {
            // 通过元素索引是否等于0，来判断是否达到开头。
            return nextIndex != 0;
        }

        // 获取上一个元素
        public E previous() {
            if (nextIndex == 0)
            throw new NoSuchElementException();

            // next指向链表的上一个元素
            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        // 获取下一个元素的索引
        public int nextIndex() {
            return nextIndex;
        }

        // 获取上一个元素的索引
        public int previousIndex() {
            return nextIndex-1;
        }

        // 删除当前元素。
        // 删除双向链表中的当前节点
        public void remove() {
            checkForComodification();
            Entry<E> lastNext = lastReturned.next;
            try {
                LinkedList.this.remove(lastReturned);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }
            if (next==lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = header;
            expectedModCount++;
        }

        // 设置当前节点为e
        public void set(E e) {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = e;
        }

        // 将e添加到当前节点的前面
        public void add(E e) {
            checkForComodification();
            lastReturned = header;
            addBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        // 判断 “modCount和expectedModCount是否相等”，依次来实现fail-fast机制。
        final void checkForComodification() {
            if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        }
    }

    // 双向链表的节点所对应的数据结构。
    // 包含3部分：上一节点，下一节点，当前节点值。
    private static class Entry<E> {
        // 当前节点所包含的值
        E element;
        // 下一个节点
        Entry<E> next;
        // 上一个节点
        Entry<E> previous;

        /**
         * 链表节点的构造函数。
         * 参数说明：
         *   element  —— 节点所包含的数据
         *   next      —— 下一个节点
         *   previous —— 上一个节点
         */
        Entry(E element, Entry<E> next, Entry<E> previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

    // 将节点(节点数据是e)添加到entry节点之前。
    private Entry<E> addBefore(E e, Entry<E> entry) {
        // 新建节点newEntry，将newEntry插入到节点e之前；并且设置newEntry的数据是e
        Entry<E> newEntry = new Entry<E>(e, entry, entry.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        // 修改LinkedList大小
        size++;
        // 修改LinkedList的修改统计数：用来实现fail-fast机制。
        modCount++;
        return newEntry;
    }

    // 将节点从链表中删除
    private E remove(Entry<E> e) {
        if (e == header)
            throw new NoSuchElementException();

        E result = e.element;
        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.next = e.previous = null;
        e.element = null;
        size--;
        modCount++;
        return result;
    }

    // 反向迭代器
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    // 反向迭代器实现类。
    private class DescendingIterator implements Iterator {
        final ListItr itr = new ListItr(size());
        // 反向迭代器是否下一个元素。
        // 实际上是判断双向链表的当前节点是否达到开头
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        // 反向迭代器获取下一个元素。
        // 实际上是获取双向链表的前一个节点
        public E next() {
            return itr.previous();
        }
        // 删除当前节点
        public void remove() {
            itr.remove();
        }
    }


    // 返回LinkedList的Object[]数组
    public Object[] toArray() {
    // 新建Object[]数组
    Object[] result = new Object[size];
        int i = 0;
        // 将链表中所有节点的数据都添加到Object[]数组中
        for (Entry<E> e = header.next; e != header; e = e.next)
            result[i++] = e.element;
    return result;
    }

    // 返回LinkedList的模板数组。所谓模板数组，即可以将T设为任意的数据类型
    public <T> T[] toArray(T[] a) {
        // 若数组a的大小 < LinkedList的元素个数(意味着数组a不能容纳LinkedList中全部元素)
        // 则新建一个T[]数组，T[]的大小为LinkedList大小，并将该T[]赋值给a。
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        // 将链表中所有节点的数据都添加到数组a中
        int i = 0;
        Object[] result = a;
        for (Entry<E> e = header.next; e != header; e = e.next)
            result[i++] = e.element;

        if (a.length > size)
            a[size] = null;

        return a;
    }


    // 克隆函数。返回LinkedList的克隆对象。
    public Object clone() {
        LinkedList<E> clone = null;
        // 克隆一个LinkedList克隆对象
        try {
            clone = (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }

        // 新建LinkedList表头节点
        clone.header = new Entry<E>(null, null, null);
        clone.header.next = clone.header.previous = clone.header;
        clone.size = 0;
        clone.modCount = 0;

        // 将链表中所有节点的数据都添加到克隆对象中
        for (Entry<E> e = header.next; e != header; e = e.next)
            clone.add(e.element);

        return clone;
    }

    // java.io.Serializable的写入函数
    // 将LinkedList的“容量，所有的元素值”都写入到输出流中
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // 写入“容量”
        s.writeInt(size);

        // 将链表中所有节点的数据都写入到输出流中
        for (Entry e = header.next; e != header; e = e.next)
            s.writeObject(e.element);
    }

    // java.io.Serializable的读取函数：根据写入方式反向读出
    // 先将LinkedList的“容量”读出，然后将“所有的元素值”读出
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // 从输入流中读取“容量”
        int size = s.readInt();

        // 新建链表表头节点
        header = new Entry<E>(null, null, null);
        header.next = header.previous = header;

        // 从输入流中将“所有的元素值”并逐个添加到链表中
        for (int i=0; i<size; i++)
            addBefore((E)s.readObject(), header);
    }

}
```

**总结**：

1. LinkedList 实际上是 通过双向链表去实现的。它包含一个非常重要的内部类：**Entry**。Entry是**双向链表节点所对应的数据结构**，它包括的属性有：**当前节点所包含的值**，**上一个节点**，**下一个节点**。
2. 从LinkedList的实现方式中可以发现，它不存在LinkedList容量不足的问题。
3. LinkedList的克隆函数，即是将全部元素克隆到一个新的LinkedList对象中。
4. LinkedList实现java.io.Serializable。当写入到输出流时，先写入“容量”，再依次写入“每一个节点保护的值”；当读出输入流时，先读取“容量”，再依次读取“每一个元素”。
5. 由于LinkedList实现了Deque，而Deque接口定义了在双端队列两端访问元素的方法。提供插入、移除和检查元素的方法。每种方法都存在两种形式：一种形式在操作失败时抛出异常，另一种形式返回一个特殊值（null 或 false，具体取决于操作）。

总结起来如下表格：

```
        第一个元素（头部）                 最后一个元素（尾部）
        抛出异常        特殊值            抛出异常        特殊值
插入    addFirst(e)    offerFirst(e)    addLast(e)        offerLast(e)
移除    removeFirst()  pollFirst()      removeLast()    pollLast()
检查    getFirst()     peekFirst()      getLast()        peekLast()
```

6. LinkedList可以作为**FIFO**(先进先出)的队列，作为FIFO的队列时，下表的方法等价：



```
队列方法       等效方法
add(e)        addLast(e)
offer(e)      offerLast(e)
remove()      removeFirst()
poll()        pollFirst()
element()     getFirst()
peek()        peekFirst()
```



7. LinkedList可以作为**LIFO**(后进先出)的栈，作为LIFO的栈时，下表的方法等价：

```
栈方法        等效方法
push(e)      addFirst(e)
pop()        removeFirst()
peek()       peekFirst()
```

 

### 遍历方式

**LinkedList遍历方式**

LinkedList支持多种遍历方式。建议不要采用随机访问的方式去遍历LinkedList，而采用逐个遍历的方式。

1. 第一种，通过**迭代器**遍历。即通过Iterator去遍历。

```
for(Iterator iter = list.iterator(); iter.hasNext();)
    iter.next();
```

2.  通过**快速随机**访问遍历LinkedList

```
int size = list.size();
for (int i=0; i<size; i++) {
    list.get(i);        
}
```

3. 通过**另外一种for循环**来遍历LinkedList

```
for (Integer integ:list) 
    ;
```

4. 通过**pollFirst()**来遍历LinkedList

```
while(list.pollFirst() != null)
    ;
```

5. 通过**pollLast()**来遍历LinkedList

```
while(list.pollLast() != null)
    ;
```

6. 通过**removeFirst()**来遍历LinkedList

```
try {
    while(list.removeFirst() != null)
        ;
} catch (NoSuchElementException e) {
}
```

(07) 通过**removeLast()**来遍历LinkedList

```
try {
    while(list.removeLast() != null)
        ;
} catch (NoSuchElementException e) {
}
```

 **测试这些遍历方式效率的代码如下**：

```java
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/*
 * @desc 测试LinkedList的几种遍历方式和效率
 *
 * @author skywang
 */
public class LinkedListThruTest {
    public static void main(String[] args) {
        // 通过Iterator遍历LinkedList
        iteratorLinkedListThruIterator(getLinkedList()) ;
        
        // 通过快速随机访问遍历LinkedList
        iteratorLinkedListThruForeach(getLinkedList()) ;

        // 通过for循环的变种来访问遍历LinkedList
        iteratorThroughFor2(getLinkedList()) ;

        // 通过PollFirst()遍历LinkedList
        iteratorThroughPollFirst(getLinkedList()) ;

        // 通过PollLast()遍历LinkedList
        iteratorThroughPollLast(getLinkedList()) ;

        // 通过removeFirst()遍历LinkedList
        iteratorThroughRemoveFirst(getLinkedList()) ;

        // 通过removeLast()遍历LinkedList
        iteratorThroughRemoveLast(getLinkedList()) ;
    }
    
    private static LinkedList getLinkedList() {
        LinkedList llist = new LinkedList();
        for (int i=0; i<100000; i++)
            llist.addLast(i);

        return llist;
    }
    /**
     * 通过快迭代器遍历LinkedList
     */
    private static void iteratorLinkedListThruIterator(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        
        for(Iterator iter = list.iterator(); iter.hasNext();)
            iter.next();

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorLinkedListThruIterator：" + interval+" ms");
    }

    /**
     * 通过快速随机访问遍历LinkedList
     */
    private static void iteratorLinkedListThruForeach(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        
        int size = list.size();
        for (int i=0; i<size; i++) {
            list.get(i);        
        }
        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorLinkedListThruForeach：" + interval+" ms");
    }

    /**
     * 通过另外一种for循环来遍历LinkedList
     */
    private static void iteratorThroughFor2(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        
        for (Integer integ:list) 
            ;

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorThroughFor2：" + interval+" ms");
    }

    /**
     * 通过pollFirst()来遍历LinkedList
     */
    private static void iteratorThroughPollFirst(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        while(list.pollFirst() != null)
            ;

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorThroughPollFirst：" + interval+" ms");
    }

    /**
     * 通过pollLast()来遍历LinkedList
     */
    private static void iteratorThroughPollLast(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        while(list.pollLast() != null)
            ;

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorThroughPollLast：" + interval+" ms");
    }

    /**
     * 通过removeFirst()来遍历LinkedList
     */
    private static void iteratorThroughRemoveFirst(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        try {
            while(list.removeFirst() != null)
                ;
        } catch (NoSuchElementException e) {
        }

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorThroughRemoveFirst：" + interval+" ms");
    }

    /**
     * 通过removeLast()来遍历LinkedList
     */
    private static void iteratorThroughRemoveLast(LinkedList<Integer> list) {
        if (list == null)
            return ;

        // 记录开始时间
        long start = System.currentTimeMillis();
        try {
            while(list.removeLast() != null)
                ;
        } catch (NoSuchElementException e) {
        }

        // 记录结束时间
        long end = System.currentTimeMillis();
        long interval = end - start;
        System.out.println("iteratorThroughRemoveLast：" + interval+" ms");
    }

}
```

**执行结果**：



```
iteratorLinkedListThruIterator：8 ms
iteratorLinkedListThruForeach：3724 ms
iteratorThroughFor2：5 ms
iteratorThroughPollFirst：8 ms
iteratorThroughPollLast：6 ms
iteratorThroughRemoveFirst：2 ms
iteratorThroughRemoveLast：2 ms
```



由此可见，遍历LinkedList时，使用removeFist()或removeLast()效率最高。但用它们遍历时，会删除原始数据；若单纯只读取，而不删除，应该使用第3种遍历方式。
**无论如何，千万不要通过随机访问去遍历LinkedList！**

 

### LinkedList示例

下面通过一个示例来学习如何使用LinkedList的常用API 

```java
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/*
 * @desc LinkedList测试程序。
 *
 * @author skywang
 * @email  kuiwu-wang@163.com
 */
public class LinkedListTest {
    public static void main(String[] args) {
        // 测试LinkedList的API
        testLinkedListAPIs() ;

        // 将LinkedList当作 LIFO(后进先出)的堆栈
        useLinkedListAsLIFO();

        // 将LinkedList当作 FIFO(先进先出)的队列
        useLinkedListAsFIFO();
    }
    
    /*
     * 测试LinkedList中部分API
     */
    private static void testLinkedListAPIs() {
        String val = null;
        //LinkedList llist;
        //llist.offer("10");
        // 新建一个LinkedList
        LinkedList llist = new LinkedList();
        //---- 添加操作 ----
        // 依次添加1,2,3
        llist.add("1");
        llist.add("2");
        llist.add("3");

        // 将“4”添加到第一个位置
        llist.add(1, "4");
        

        System.out.println("\nTest \"addFirst(), removeFirst(), getFirst()\"");
        // (01) 将“10”添加到第一个位置。  失败的话，抛出异常！
        llist.addFirst("10");
        System.out.println("llist:"+llist);
        // (02) 将第一个元素删除。        失败的话，抛出异常！
        System.out.println("llist.removeFirst():"+llist.removeFirst());
        System.out.println("llist:"+llist);
        // (03) 获取第一个元素。          失败的话，抛出异常！
        System.out.println("llist.getFirst():"+llist.getFirst());


        System.out.println("\nTest \"offerFirst(), pollFirst(), peekFirst()\"");
        // (01) 将“10”添加到第一个位置。  返回true。
        llist.offerFirst("10");
        System.out.println("llist:"+llist);
        // (02) 将第一个元素删除。        失败的话，返回null。
        System.out.println("llist.pollFirst():"+llist.pollFirst());
        System.out.println("llist:"+llist);
        // (03) 获取第一个元素。          失败的话，返回null。
        System.out.println("llist.peekFirst():"+llist.peekFirst());
    

        System.out.println("\nTest \"addLast(), removeLast(), getLast()\"");
        // (01) 将“20”添加到最后一个位置。  失败的话，抛出异常！
        llist.addLast("20");
        System.out.println("llist:"+llist);
        // (02) 将最后一个元素删除。        失败的话，抛出异常！
        System.out.println("llist.removeLast():"+llist.removeLast());
        System.out.println("llist:"+llist);
        // (03) 获取最后一个元素。          失败的话，抛出异常！
        System.out.println("llist.getLast():"+llist.getLast());


        System.out.println("\nTest \"offerLast(), pollLast(), peekLast()\"");
        // (01) 将“20”添加到第一个位置。  返回true。
        llist.offerLast("20");
        System.out.println("llist:"+llist);
        // (02) 将第一个元素删除。        失败的话，返回null。
        System.out.println("llist.pollLast():"+llist.pollLast());
        System.out.println("llist:"+llist);
        // (03) 获取第一个元素。          失败的话，返回null。
        System.out.println("llist.peekLast():"+llist.peekLast());

         

        // 将第3个元素设置300。不建议在LinkedList中使用此操作，因为效率低！
        llist.set(2, "300");
        // 获取第3个元素。不建议在LinkedList中使用此操作，因为效率低！
        System.out.println("\nget(3):"+llist.get(2));


        // ---- toArray(T[] a) ----
        // 将LinkedList转行为数组
        String[] arr = (String[])llist.toArray(new String[0]);
        for (String str:arr) 
            System.out.println("str:"+str);

        // 输出大小
        System.out.println("size:"+llist.size());
        // 清空LinkedList
        llist.clear();
        // 判断LinkedList是否为空
        System.out.println("isEmpty():"+llist.isEmpty()+"\n");

    }

    /**
     * 将LinkedList当作 LIFO(后进先出)的堆栈
     */
    private static void useLinkedListAsLIFO() {
        System.out.println("\nuseLinkedListAsLIFO");
        // 新建一个LinkedList
        LinkedList stack = new LinkedList();

        // 将1,2,3,4添加到堆栈中
        stack.push("1");
        stack.push("2");
        stack.push("3");
        stack.push("4");
        // 打印“栈”
        System.out.println("stack:"+stack);

        // 删除“栈顶元素”
        System.out.println("stack.pop():"+stack.pop());
        
        // 取出“栈顶元素”
        System.out.println("stack.peek():"+stack.peek());

        // 打印“栈”
        System.out.println("stack:"+stack);
    }

    /**
     * 将LinkedList当作 FIFO(先进先出)的队列
     */
    private static void useLinkedListAsFIFO() {
        System.out.println("\nuseLinkedListAsFIFO");
        // 新建一个LinkedList
        LinkedList queue = new LinkedList();

        // 将10,20,30,40添加到队列。每次都是插入到末尾
        queue.add("10");
        queue.add("20");
        queue.add("30");
        queue.add("40");
        // 打印“队列”
        System.out.println("queue:"+queue);

        // 删除(队列的第一个元素)
        System.out.println("queue.remove():"+queue.remove());
    
        // 读取(队列的第一个元素)
        System.out.println("queue.element():"+queue.element());

        // 打印“队列”
        System.out.println("queue:"+queue);
    }
}
```

**运行结果**：

```
Test "addFirst(), removeFirst(), getFirst()"
llist:[10, 1, 4, 2, 3]
llist.removeFirst():10
llist:[1, 4, 2, 3]
llist.getFirst():1

Test "offerFirst(), pollFirst(), peekFirst()"
llist:[10, 1, 4, 2, 3]
llist.pollFirst():10
llist:[1, 4, 2, 3]
llist.peekFirst():1

Test "addLast(), removeLast(), getLast()"
llist:[1, 4, 2, 3, 20]
llist.removeLast():20
llist:[1, 4, 2, 3]
llist.getLast():3

Test "offerLast(), pollLast(), peekLast()"
llist:[1, 4, 2, 3, 20]
llist.pollLast():20
llist:[1, 4, 2, 3]
llist.peekLast():3

get(3):300
str:1
str:4
str:300
str:3
size:4
isEmpty():true


useLinkedListAsLIFO
stack:[4, 3, 2, 1]
stack.pop():4
stack.peek():3
stack:[3, 2, 1]

useLinkedListAsFIFO
queue:[10, 20, 30, 40]
queue.remove():10
queue.element():20
queue:[20, 30, 40]
```

