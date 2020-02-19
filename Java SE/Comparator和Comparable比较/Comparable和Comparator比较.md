Comparable和Comparator比较

## Comparable介绍【内部比较接口】

> java.lang 
> 接口 Comparable<T>
> 类型参数：
> T - 可以与此对象进行比较的那些对象的类型

public interface Comparable<T>此接口强行对实现它的每个类的对象进行整体排序。这种排序被称为类的自然排序，类的 compareTo 方法被称为它的自然比较方法。

实现此接口的对象列表（和数组）可以通过 Collections.sort（和 Arrays.sort）进行自动排序。实现此接口的对象可以用作有序映射中的键或有序集合中的元素，无需指定比较器。

对于类 C 的每一个 e1 和 e2 来说，当且仅当 e1.compareTo(e2) == 0 与 e1.equals(e2) 具有相同的 boolean 值时，类 C 的自然排序才叫做与 equals 一致。注意，null 不是任何类的实例，即使 e.equals(null) 返回 false，e.compareTo(null) 也将抛出 NullPointerException。

方法摘要

```java
//比较此对象与指定对象的顺序。  
int compareTo(T o) ;
```



## Comparator介绍【外部比较器】

> java.util 
> 接口 Comparator<T>
> 类型参数：
> T - 此 Comparator 可以比较的对象类型

public interface Comparator<T>强行对某个对象 collection 进行整体排序 的比较函数。可以将 Comparator 传递给 sort 方法（如 Collections.sort 或 Arrays.sort），从而允许在排序顺序上实现精确控制。还可以使用 Comparator 来控制某些数据结构（如有序 set或有序映射）的顺序，或者为那些没有自然顺序的对象 collection 提供排序。

当且仅当对于一组元素 S 中的每个 e1 和 e2 而言，c.compare(e1, e2)==0 与 e1.equals(e2) 具有相等的布尔值时，Comparator c 强行对 S 进行的排序才叫做与 equals 一致 的排序。

方法摘要【1.8之后的版本增加了一些新的方法】

```java
//比较用来排序的两个参数。 
int compare(T o1, T o2) 
//指示某个其他对象是否“等于”此 Comparator。 
boolean equals(Object obj) 
```

## 使用和区别

参考链接：<https://www.jianshu.com/p/fa1a1089d44d>

* Comparator位于java.util包下，而Comparable位于java.lang包下
* Comparable接口的实现是在类的内部（如 String、Integer已经实现了Comparable接口，自己就可以完成比较大小操作），Comparator接口的实现是在类的外部（可以理解为一个是自已完成比较，一个是外部程序实现比较）
* 实现Comparable接口要重写compareTo方法, 在compareTo方法里面实现比较，实现Comparator要重写compare方法。

### Comparable使用

```java
package a.comparable;

public class Student implements Comparable<Student> {
    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Student o) {
        int i = 0;
        i = name.compareTo(o.name);
        if(i == 0) {
            return age - o.age;
        } else {
            return i;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class Main{
    public static void main(String[] args) {
        Student eric = new Student("eric", 9);
        Student oldEric = new Student("eric", 49);
        System.out.println(eric.compareTo(oldEric));
        //运行结果：
        //-40
    }
}
```



### Comparator使用

```java
package a.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Student {
    String name;
    int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class StudentComparator implements Comparator<Student> {
    @Override
    public int compare(Student one, Student another) {
        int i = 0;
        i = one.name.compareTo(another.name);
        if (i == 0) {
            return one.age - another.age;
        } else {
            return i;
        }
    }
}

class Main {
    public static void main(String[] args) {
        Student eric = new Student("Eric", 9);
        Student oldEric = new Student("Eric", 59);
        ArrayList<Student> list = new ArrayList<>();
        list.add(oldEric);
        list.add(eric);
        System.out.println("排序前"+list);
        list.sort(new StudentComparator());
        System.out.println("排序后"+list);
        //运行结果：
        //排序前[a.comparator.Student@1b6d3586, a.comparator.Student@4554617c]
		//排序后[a.comparator.Student@4554617c, a.comparator.Student@1b6d3586]        
    }
}
```

## 总结

* 比较规则确定，且只用于一个类中，可用内部比较器，即实现Comparable接口
* 比较规则有多个，涉及多个类，则用外部比较器
* 类结构不能改变的情况下，使用外部比较器



