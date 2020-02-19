algorithm库的常用函数整理

[reference](http://www.cplusplus.com/reference/algorithm/)

## [is_permutation](http://www.cplusplus.com/reference/algorithm/is_permutation/)

Test whether range is permutation of another

### Parameters

- first1, last1

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the first sequence. The range used is `[first1,last1)`, which contains all the elements between first1 and last1, including the element pointed by first1 but not the element pointed by last1.

- first2

  [Input iterator](http://www.cplusplus.com/InputIterator) to the initial position of the second sequence. The function considers as many elements of this sequence as those in the range `[first1,last1)`. **If this sequence is shorter**, it causes *undefined behavior*. 所以这一段序列应该要长于前一段

- pred

  Binary function that accepts two elements as argument (one of each of the two sequences, in the same order), and returns a value convertible to `bool`. The value returned indicates whether the elements are considered to match in the context of this function. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



InputIterator1 and InputIterator2 shall point to the same type.

### Return value

`true` if all the elements in the range `[first1,last1)` compare equal to those of the range starting at first2 in any order, and `false` otherwise.

### Example 

```cpp
// is_permutation example
#include <iostream>     // std::cout
#include <algorithm>    // std::is_permutation
#include <array>        // std::array

int main () {
  std::array<int,5> foo = {1,2,3,4,5};
  std::array<int,5> bar = {3,1,4,5,2};

  if ( std::is_permutation (foo.begin(), foo.end(), bar.begin()) )
    std::cout << "foo and bar contain the same elements.\n";

  return 0;
}
```

Output:

```
foo and bar contain the same elements.
```



## [search](http://www.cplusplus.com/reference/algorithm/search/)

### Parameters

- first1, last1

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of the searched sequence. The range used is `[first1,last1)`, which contains all the elements between first1 and last1, including the element pointed by first1 but not the element pointed by last1.

- first2, last2

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of the sequence to be searched for. The range used is `[first2,last2)`. For *(1)*, the elements in both ranges shall be of types comparable using `operator==` (with the elements of the first range as left-hand side operands, and those of the second as right-hand side operands). 

- pred

  Binary function that accepts two elements as arguments (one of each of the two sequences, in the same order), and returns a value convertible to `bool`. The returned value indicates whether the elements are considered to match in the context of this function. The function shall not modify any of its arguments. This can either be a function pointer or a function object.

### Return value

An iterator to the first element of the first occurrence of `[first2,last2) in [first1,last1)`

- If the sequence is not found, the function returns last1.
- If `[first2,last2)` is an empty range, the function returns first1.

### Example

```cpp
// search algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::search
#include <vector>       // std::vector

bool mypredicate (int i, int j) {
  return (i==j);
}

int main () {
  std::vector<int> haystack;

  // set some values:        haystack: 10 20 30 40 50 60 70 80 90
  for (int i=1; i<10; i++) haystack.push_back(i*10);

  // using default comparison:
  int needle1[] = {40,50,60,70};
  std::vector<int>::iterator it;
  it = std::search (haystack.begin(), haystack.end(), needle1, needle1+4);

  if (it!=haystack.end())
    std::cout << "needle1 found at position " << (it-haystack.begin()) << '\n';
  else
    std::cout << "needle1 not found\n";

  // using predicate comparison:
  int needle2[] = {20,30,50};
  it = std::search (haystack.begin(), haystack.end(), needle2, needle2+3, mypredicate);

  if (it!=haystack.end())
    std::cout << "needle2 found at position " << (it-haystack.begin()) << '\n';
  else
    std::cout << "needle2 not found\n";

  return 0;
}
```


Output:

```
`needle1 found at position 3 needle2 not found `
```

## [search_n](http://www.cplusplus.com/reference/algorithm/search_n/)

### Parameters

- first, last

  Forward iterators to the initial and final positions of the searched sequence. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- count

  Minimum number of successive elements to match. Size shall be (convertible to) an integral type.

- val

  Individual value to be compared, or to be used as argument for pred (in the second version). for the first version, T shall be a type supporting comparisons with the elements pointed by InputIterator using `operator==` (with the elements as left-hand size operands, and val as right-hand side).

- pred

  Binary function that accepts two arguments (one element from the sequence as first, and val as second), and returns a value convertible to `bool`. The value returned indicates whether the element is considered a match in the context of this function. The function shall not modify any of its arguments. This can either be a function pointer or a function object.

### Return value

An iterator to the first element of the sequence.
If no such sequence is found, the function returns last.

### Example

```cpp
// search_n example
#include <iostream>     // std::cout
#include <algorithm>    // std::search_n
#include <vector>       // std::vector

bool mypredicate (int i, int j) {
  return (i==j);
}

int main () {
  int myints[]={10,20,30,30,20,10,10,20};
  std::vector<int> myvector (myints,myints+8);

  std::vector<int>::iterator it;

  // using default comparison:
  it = std::search_n (myvector.begin(), myvector.end(), 2, 30);

  if (it!=myvector.end())
    std::cout << "two 30s found at position " << (it-myvector.begin()) << '\n';
  else
    std::cout << "match not found\n";

  // using predicate comparison:
  it = std::search_n (myvector.begin(), myvector.end(), 2, 10, mypredicate);

  if (it!=myvector.end())
    std::cout << "two 10s found at position " << int(it-myvector.begin()) << '\n';
  else
    std::cout << "match not found\n";

  return 0;
}
```

output:

```
Two 30s found at position 2
Two 10s found at position 5
```



## [swap](http://www.cplusplus.com/reference/algorithm/swap/)

### Parameters

- a, b

  Two objects, whose contents are swapped.

### Example

```cpp
// swap algorithm example (C++98)
#include <iostream>     // std::cout
#include <algorithm>    // std::swap
#include <vector>       // std::vector

int main () {

  int x=10, y=20;                              // x:10 y:20
  std::swap(x,y);                              // x:20 y:10

  std::vector<int> foo (4,x), bar (6,y);       // foo:4x20 bar:6x10
  std::swap(foo,bar);                          // foo:6x10 bar:4x20

  std::cout << "foo contains:";
  for (std::vector<int>::iterator it=foo.begin(); it!=foo.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

Output:

```
foo contains: 10 10 10 10 10 10
```

## [swap_ranges](http://www.cplusplus.com/reference/algorithm/swap_ranges/)

### Parameters

- first1, last1

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions in one of the sequences to be swapped. The range used is [first1,last1), which contains all the elements between first1 and last1, including the element pointed by first1 but not the element pointed by last1.

- first2

  [Forward iterator](http://www.cplusplus.com/ForwardIterator) to the initial position in the other sequence to be swapped. The range used includes the same number of elements as the range [first1,last1). The two ranges shall not overlap.


The ranges shall not overlap.

[swap](http://www.cplusplus.com/swap) shall be defined to exchange the types pointed by both iterator types symmetrically (in both orders).

### Return value

An iterator to the last element swapped in the second sequence.

### Example

```cpp
// swap_ranges example
#include <iostream>     // std::cout
#include <algorithm>    // std::swap_ranges
#include <vector>       // std::vector

int main () {
  std::vector<int> foo (5,10);        // foo: 10 10 10 10 10
  std::vector<int> bar (5,33);        // bar: 33 33 33 33 33

  std::swap_ranges(foo.begin()+1, foo.end()-1, bar.begin());

  // print out results of swap:
  std::cout << "foo contains:";
  for (std::vector<int>::iterator it=foo.begin(); it!=foo.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  std::cout << "bar contains:";
  for (std::vector<int>::iterator it=bar.begin(); it!=bar.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

Output:

```
foo contains: 10 33 33 33 10
bar contains: 10 10 10 33 33
```



## [iter_swap](http://www.cplusplus.com/reference/algorithm/iter_swap/)

### Parameters

- a, b

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the objects to swap. [swap](http://www.cplusplus.com/swap) shall be defined to exchange values of the type pointed to by the iterators.

### Example

```cpp
// iter_swap example
#include <iostream>     // std::cout
#include <algorithm>    // std::iter_swap
#include <vector>       // std::vector

int main () {

  int myints[]={10,20,30,40,50 };              //   myints:  10  20  30  40  50
  std::vector<int> myvector (4,99);            // myvector:  99  99  99  99

  std::iter_swap(myints,myvector.begin());     //   myints: [99] 20  30  40  50
                                               // myvector: [10] 99  99  99

  std::iter_swap(myints+3,myvector.begin()+2); //   myints:  99  20  30 [99] 50
                                               // myvector:  10  99 [40] 99

  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output:

```
myvector contains: 10 99 40 99
```



## [transform](http://www.cplusplus.com/reference/algorithm/transform/)

### Parameters

- first1, last1

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the first sequence. The range used is `[first1,last1)`, which contains all the elements between first1 and last1, including the element pointed to by first1 but not the element pointed to by last1.

- first2

  [Input iterator](http://www.cplusplus.com/InputIterator) to the initial position of the second range. The range includes as many elements as `[first1,last1)`.

- result

  [Output iterator](http://www.cplusplus.com/OutputIterator) to the initial position of the range where the operation results are stored. The range includes as many elements as `[first1,last1)`.

- op

  Unary function that accepts one element of the type pointed to by InputIterator as argument, and returns some result value convertible to the type pointed to by OutputIterator. This can either be a function pointer or a function object.

- binary_op

  Binary function that accepts two elements as argument (one of each of the two sequences), and returns some result value convertible to the type pointed to by OutputIterator. This can either be a function pointer or a function object.


Neither  nor  should directly modify the elements passed as its arguments: These are indirectly modified by the algorithm (using the return value) if the same range is specified for .

### Return value

An iterator pointing to the element that follows the last element written in the result sequence.

### Example

```cpp
// transform algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::transform
#include <vector>       // std::vector
#include <functional>   // std::plus

int op_increase (int i) { return ++i; }

int main () {
  std::vector<int> foo;
  std::vector<int> bar;

  // set some values:
  for (int i=1; i<6; i++)
    foo.push_back (i*10);                         // foo: 10 20 30 40 50

  bar.resize(foo.size());                         // allocate space

  std::transform (foo.begin(), foo.end(), bar.begin(), op_increase);
                                                  // bar: 11 21 31 41 51

  // std::plus adds together its two arguments:
  std::transform (foo.begin(), foo.end(), bar.begin(), foo.begin(), std::plus<int>());
                                                  // foo: 21 41 61 81 101

  std::cout << "foo contains:";
  for (std::vector<int>::iterator it=foo.begin(); it!=foo.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

Output:

```
foo contains: 21 41 61 81 101
```



## [replace](http://www.cplusplus.com/reference/algorithm/replace/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions in a sequence of elements that support being compared and assigned a value of type T. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- old_value

  Value to be replaced.

- new_value

  Replacement value.

### Example

```cpp
// replace algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::replace
#include <vector>       // std::vector

int main () {
  int myints[] = { 10, 20, 30, 30, 20, 10, 10, 20 };
  std::vector<int> myvector (myints, myints+8);            // 10 20 30 30 20 10 10 20

  std::replace (myvector.begin(), myvector.end(), 20, 99); // 10 99 30 30 99 10 10 99

  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output:

```
myvector contains: 10 99 30 30 99 10 10 99
```



## [replace_if](http://www.cplusplus.com/reference/algorithm/replace_if/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions in a sequence of elements that support being assigned a value of type T. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- pred

  Unary function that accepts an element in the range as argument, and returns a value convertible to `bool`. The value returned indicates whether the element is to be replaced (if `true`, it is replaced). The function shall not modify its argument. This can either be a function pointer or a function object.

- new_value

  Value to assign to replaced elements.

### Example

```cpp
// replace_if example
#include <iostream>     // std::cout
#include <algorithm>    // std::replace_if
#include <vector>       // std::vector

bool IsOdd (int i) { return ((i%2)==1); }

int main () {
  std::vector<int> myvector;

  // set some values:
  for (int i=1; i<10; i++) myvector.push_back(i);               // 1 2 3 4 5 6 7 8 9

  std::replace_if (myvector.begin(), myvector.end(), IsOdd, 0); // 0 2 0 4 0 6 0 8 0

  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output:

```
myvector contains: 0 2 0 4 0 6 0 8 0
```



## [fill](http://www.cplusplus.com/reference/algorithm/fill/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions in a sequence of elements that support being assigned a value of type T. The range filled is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- val

  Value to assign to the elements in the filled range.

### Example

```cpp
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

output:

myvector contains: 5 5 5 8 8 8 0 0

## [reverse](http://www.cplusplus.com/reference/algorithm/reverse/)

### Parameters

- first, last

  [Bidirectional iterators](http://www.cplusplus.com/BidirectionalIterator) to the initial and final positions of the sequence to be reversed. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last. BidirectionalIterator shall point to a type for which [swap](http://www.cplusplus.com/swap) is properly defined.

### Example

```cpp
// reverse algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::reverse
#include <vector>       // std::vector

int main () {
  std::vector<int> myvector;

  // set some values:
  for (int i=1; i<10; ++i) myvector.push_back(i);   // 1 2 3 4 5 6 7 8 9

  std::reverse(myvector.begin(),myvector.end());    // 9 8 7 6 5 4 3 2 1

  // print out content:
  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output:

```
myvector contains: 9 8 7 6 5 4 3 2 1
```

## [random_shuffle](http://www.cplusplus.com/reference/algorithm/random_shuffle/)

### Parameters

- first, last

  [Random-access iterators](http://www.cplusplus.com/RandomAccessIterator) to the initial and final positions of the sequence to be shuffled. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- gen

  Unary function taking one argument and returning a value, both convertible to/from the appropriate [difference type](http://www.cplusplus.com/iterator_traits::difference_type) used by the iterators. The function shall return a non-negative value less than its argument. This can either be a function pointer or a function object.

RandomAccessIterator shall point to a type for which [swap](http://www.cplusplus.com/swap) is defined and swaps the value of its arguments.

### Example

```cpp
// random_shuffle example
#include <iostream>     // std::cout
#include <algorithm>    // std::random_shuffle
#include <vector>       // std::vector
#include <ctime>        // std::time
#include <cstdlib>      // std::rand, std::srand

// random generator function:
int myrandom (int i) { return std::rand()%i;}

int main () {
  std::srand ( unsigned ( std::time(0) ) );
  std::vector<int> myvector;

  // set some values:
  for (int i=1; i<10; ++i) myvector.push_back(i); // 1 2 3 4 5 6 7 8 9

  // using built-in random generator:
  std::random_shuffle ( myvector.begin(), myvector.end() );

  // using myrandom:
  std::random_shuffle ( myvector.begin(), myvector.end(), myrandom);

  // print out content:
  std::cout << "myvector contains:";
  for (std::vector<int>::iterator it=myvector.begin(); it!=myvector.end(); ++it)
    std::cout << ' ' << *it;

  std::cout << '\n';

  return 0;
}
```

output:

```
myvector contains: 3 4 1 6 8 9 2 7 5
```



## [shuffle](http://www.cplusplus.com/reference/algorithm/shuffle/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of the sequence to be shuffled. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last. ForwardIterator shall point to a type for which [swap](http://www.cplusplus.com/swap) is defined and swaps the value of its arguments. 

- g

  A uniform random number generator, used as the source of randomness. URNG shall be a *uniform random number generator*, such as one of the standard generator classes (see [](http://www.cplusplus.com/) for more information).

### Example

```cpp
// shuffle algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::shuffle
#include <array>        // std::array
#include <random>       // std::default_random_engine
#include <chrono>       // std::chrono::system_clock

int main () {
  std::array<int,5> foo {1,2,3,4,5};

  // obtain a time-based seed:
  unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();

  shuffle (foo.begin(), foo.end(), std::default_random_engine(seed));

  std::cout << "shuffled elements:";
  for (int& x: foo) std::cout << ' ' << x;
  std::cout << '\n';

  return 0;
}
```

Possible output:

```
shuffled elements: 3 1 4 2 5
```



## [is_sorted](http://www.cplusplus.com/reference/algorithm/is_sorted/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of the sequence. The range checked is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last. 

- comp

  Binary function that accepts two elements in the range as arguments, and returns a value convertible to `bool`. The value returned indicates whether the element passed as first argument is considered to go before the second in the specific *strict weak ordering* it defines. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 

### Return value

  `true` if the range `[first,last)` is sorted into ascending order, `false` otherwise.

If the range `[first,last)` contains less than two elements, the function always returns `true`.  

### Example

```cpp
// is_sorted example
#include <iostream>     // std::cout
#include <algorithm>    // std::is_sorted, std::prev_permutation
#include <array>        // std::array

int main () {
  std::array<int,4> foo {2,4,1,3};

  do {
    // try a new permutation:
    std::prev_permutation(foo.begin(),foo.end());

    // print range:
    std::cout << "foo:";
    for (int& x:foo) std::cout << ' ' << x;
    std::cout << '\n';

  } while (!std::is_sorted(foo.begin(),foo.end()));

  std::cout << "the range is sorted!\n";

  return 0;
}
 Edit & Run

```

output:

```
foo: 2 3 4 1
foo: 2 3 1 4
foo: 2 1 4 3
foo: 2 1 3 4
foo: 1 4 3 2
foo: 1 4 2 3
foo: 1 3 4 2
foo: 1 3 2 4
foo: 1 2 4 3
foo: 1 2 3 4
the range is sorted!
```



## [is_sorted_until](http://www.cplusplus.com/reference/algorithm/is_sorted_until/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions in a sequence. The range checked is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- comp

  Binary function that accepts two elements in the range as arguments, and returns a value convertible to `bool`. The value returned indicates whether the element passed as first argument is considered to go before the second in the specific *strict weak ordering* it defines. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 

### Return value

An iterator to the first element in the range which does not follow an ascending order, or last if all elements are sorted or if the range contains less than two elements.

### Example

```cpp
// is_sorted_until example
#include <iostream>     // std::cout
#include <algorithm>    // std::is_sorted_until, std::prev_permutation
#include <array>        // std::array

int main () {
  std::array<int,4> foo {2,4,1,3};
  std::array<int,4>::iterator it;

  do {
    // try a new permutation:
    std::prev_permutation(foo.begin(),foo.end());

    // print range:
    std::cout << "foo:";
    for (int& x:foo) std::cout << ' ' << x;
    it=std::is_sorted_until(foo.begin(),foo.end());
    std::cout << " (" << (it-foo.begin()) << " elements sorted)\n";

  } while (it!=foo.end());

  std::cout << "the range is sorted!\n";

  return 0;
}
```

Output:

```
foo: 2 3 4 1 (3 elements sorted)
foo: 2 3 1 4 (2 elements sorted)
foo: 2 1 4 3 (1 elements sorted)
foo: 2 1 3 4 (1 elements sorted)
foo: 1 4 3 2 (2 elements sorted)
foo: 1 4 2 3 (2 elements sorted)
foo: 1 3 4 2 (3 elements sorted)
foo: 1 3 2 4 (2 elements sorted)
foo: 1 2 4 3 (3 elements sorted)
foo: 1 2 3 4 (4 elements sorted)
the range is sorted!
```

## [lower_bound](http://www.cplusplus.com/reference/algorithm/lower_bound/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of a [sorted](http://www.cplusplus.com/is_sorted) (or properly [partitioned](http://www.cplusplus.com/is_partitioned)) sequence. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- val

  Value of the lower bound to search for in the range. For *(1)*, T shall be a type supporting being compared with elements of the range `[first,last)` as the right-hand side operand of `operator<`.

- comp

  Binary function that accepts two arguments (the first of the type pointed by ForwardIterator, and the second, always val), and returns a value convertible to `bool`. The value returned indicates whether the first argument is considered to go before the second. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



### Return value

An iterator to the lower bound of val in the range.
If all the element in the range compare less than val, the function returns last.

### Example

```cpp
// lower_bound/upper_bound example
#include <iostream>     // std::cout
#include <algorithm>    // std::lower_bound, std::upper_bound, std::sort
#include <vector>       // std::vector

int main () {
  int myints[] = {10,20,30,30,20,10,10,20};
  std::vector<int> v(myints,myints+8);           // 10 20 30 30 20 10 10 20

  std::sort (v.begin(), v.end());                // 10 10 10 20 20 20 30 30

  std::vector<int>::iterator low,up;
  low=std::lower_bound (v.begin(), v.end(), 20); //          ^
  up= std::upper_bound (v.begin(), v.end(), 20); //                   ^

  std::cout << "lower_bound at position " << (low- v.begin()) << '\n';
  std::cout << "upper_bound at position " << (up - v.begin()) << '\n';

  return 0;
}
```

output:

```
lower_bound at position 3
upper_bound at position 6
```



## [upper_bound](http://www.cplusplus.com/reference/algorithm/upper_bound/)

### Parameters

- first, last

  [Forward iterators](http://www.cplusplus.com/ForwardIterator) to the initial and final positions of a [sorted](http://www.cplusplus.com/is_sorted) (or properly [partitioned](http://www.cplusplus.com/is_partitioned)) sequence. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- val

  Value of the upper bound to search for in the range. For *(1)*, T shall be a type supporting being compared with elements of the range `[first,last)` as the left-hand side operand of `operator<`.

- comp

  Binary function that accepts two arguments (the first is always val, and the second of the type pointed by ForwardIterator), and returns a value convertible to `bool`. The value returned indicates whether the first argument is considered to go before the second. The function shall not modify any of its arguments. This can either be a function pointer or a function object.

### Return value

An iterator to the upper bound position for val in the range.
If no element in the range compares greater than val, the function returns last.

### Example

```cpp
// lower_bound/upper_bound example
#include <iostream>     // std::cout
#include <algorithm>    // std::lower_bound, std::upper_bound, std::sort
#include <vector>       // std::vector

int main () {
  int myints[] = {10,20,30,30,20,10,10,20};
  std::vector<int> v(myints,myints+8);           // 10 20 30 30 20 10 10 20

  std::sort (v.begin(), v.end());                // 10 10 10 20 20 20 30 30

  std::vector<int>::iterator low,up;
  low=std::lower_bound (v.begin(), v.end(), 20); //          ^
  up= std::upper_bound (v.begin(), v.end(), 20); //                   ^

  std::cout << "lower_bound at position " << (low- v.begin()) << '\n';
  std::cout << "upper_bound at position " << (up - v.begin()) << '\n';

  return 0;
}
```

output:

```
lower_bound at position 3
upper_bound at position 6
```



## [merge](http://www.cplusplus.com/reference/algorithm/merge/)

### Parameters

- first1, last1

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the first sorted sequence. The range used is `[first1,last1)`, which contains all the elements between first1 and last1, including the element pointed by first1 but not the element pointed by last1.

- first2, last2

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the second sorted sequence. The range used is `[first2,last2)`.

- result

  [Output iterator](http://www.cplusplus.com/OutputIterator) to the initial position of the range where the resulting combined range is stored. Its size is equal to the sum of both ranges above.

- comp

  Binary function that accepts two arguments of the types pointed by the iterators, and returns a value convertible to `bool`. The value returned indicates whether the first argument is considered to go before the second in the specific *strict weak ordering* it defines. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 

The ranges shall not overlap.The elements in both input ranges should be [assignable](http://www.cplusplus.com/is_assignable) to the elements in the range pointed by result. They should also be comparable (with `operator<` for *(1)*, and with comp for *(2)*).

### Return value

An iterator pointing to the *past-the-end* element in the resulting sequence.

### Example

```cpp
// merge algorithm example
#include <iostream>     // std::cout
#include <algorithm>    // std::merge, std::sort
#include <vector>       // std::vector

int main () {
  int first[] = {5,10,15,20,25};
  int second[] = {50,40,30,20,10};
  std::vector<int> v(10);

  std::sort (first,first+5);
  std::sort (second,second+5);
  std::merge (first,first+5,second,second+5,v.begin());

  std::cout << "The resulting vector contains:";
  for (std::vector<int>::iterator it=v.begin(); it!=v.end(); ++it)
    std::cout << ' ' << *it;
  std::cout << '\n';

  return 0;
}
```

output:

```
The resulting vector contains: 5 10 10 15 20 20 25 30 40 50
```

## [min](http://www.cplusplus.com/reference/algorithm/min/)

### Parameters

- a, b

  Values to compare.

- comp

  Binary function that accepts two values of type T as arguments, and returns a value convertible to `bool`. The value returned indicates whether the element passed as first argument is considered less than the second. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 

- il

  An [initializer_list](http://www.cplusplus.com/initializer_list) object. These objects are automatically constructed from *initializer list* declarators.

### Return value

The lesser of the values passed as arguments.

### Example

```cpp
// min example
#include <iostream>     // std::cout
#include <algorithm>    // std::min

int main () {
  std::cout << "min(1,2)==" << std::min(1,2) << '\n';
  std::cout << "min(2,1)==" << std::min(2,1) << '\n';
  std::cout << "min('a','z')==" << std::min('a','z') << '\n';
  std::cout << "min(3.14,2.72)==" << std::min(3.14,2.72) << '\n';
  return 0;
}
 Edit & Run

```

output:

```
min(1,2)==1
min(2,1)==1
min('a','z')==a
min(3.14,2.72)==2.72
```

## [max](http://www.cplusplus.com/reference/algorithm/max/)

同min，具体请看文档

### Example

```cpp
// max example
#include <iostream>     // std::cout
#include <algorithm>    // std::max

int main () {
  std::cout << "max(1,2)==" << std::max(1,2) << '\n';
  std::cout << "max(2,1)==" << std::max(2,1) << '\n';
  std::cout << "max('a','z')==" << std::max('a','z') << '\n';
  std::cout << "max(3.14,2.73)==" << std::max(3.14,2.73) << '\n';
  return 0;
}
```


Output:

```
`max(1,2)==2 max(2,1)==2 max('a','z')==z max(3.14,2.73)==3.14 `
```

## [min_element](http://www.cplusplus.com/reference/algorithm/min_element/)

### Parameters

- first, last

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the sequence to compare. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- comp

  Binary function that accepts two elements in the range as arguments, and returns a value convertible to `bool`. The value returned indicates whether the element passed as first argument is considered less than the second. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



### Return value

An iterator to smallest value in the range, or last if the range is empty.

### Example

```cpp
// min_element/max_element example
#include <iostream>     // std::cout
#include <algorithm>    // std::min_element, std::max_element

bool myfn(int i, int j) { return i<j; }

struct myclass {
  bool operator() (int i,int j) { return i<j; }
} myobj;

int main () {
  int myints[] = {3,7,2,5,6,4,9};

  // using default comparison:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7) << '\n';

  // using function myfn as comp:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7,myfn) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7,myfn) << '\n';

  // using object myobj as comp:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7,myobj) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7,myobj) << '\n';

  return 0;
}
```

Output:

```
The smallest element is 2
The largest element is 9
The smallest element is 2
The largest element is 9
The smallest element is 2
The largest element is 9
```



## [max_element](http://www.cplusplus.com/reference/algorithm/max_element/)

### Parameters

- first, last

  [Input iterators](http://www.cplusplus.com/InputIterator) to the initial and final positions of the sequence to compare. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last.

- comp

  Binary function that accepts two elements in the range as arguments, and returns a value convertible to `bool`. The value returned indicates whether the element passed as first argument is considered less than the second. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



### Return value

An iterator to largest value in the range, or last if the range is empty.

### Example

```cpp
// min_element/max_element example
#include <iostream>     // std::cout
#include <algorithm>    // std::min_element, std::max_element

bool myfn(int i, int j) { return i<j; }

struct myclass {
  bool operator() (int i,int j) { return i<j; }
} myobj;

int main () {
  int myints[] = {3,7,2,5,6,4,9};

  // using default comparison:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7) << '\n';

  // using function myfn as comp:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7,myfn) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7,myfn) << '\n';

  // using object myobj as comp:
  std::cout << "The smallest element is " << *std::min_element(myints,myints+7,myobj) << '\n';
  std::cout << "The largest element is "  << *std::max_element(myints,myints+7,myobj) << '\n';

  return 0;
}
```

Output:

```
The smallest element is 2
The largest element is 9
The smallest element is 2
The largest element is 9
The smallest element is 2
The largest element is 9
```



## [next_permutation](http://www.cplusplus.com/reference/algorithm/next_permutation/)

### Parameters

- first, last

  [Bidirectional iterators](http://www.cplusplus.com/BidirectionalIterator) to the initial and final positions of the sequence. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last. BidirectionalIterator shall point to a type for which [swap](http://www.cplusplus.com/swap) is properly defined. 

- comp

  Binary function that accepts two arguments of the type pointed by BidirectionalIterator, and returns a value convertible to `bool`. The value returned indicates whether the first argument is considered to go before the second in the specific *strict weak ordering* it defines. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



### Return value

`true` if the function could rearrange the object as a lexicographicaly greater permutation.
Otherwise, the function returns `false` to indicate that the arrangement is not greater than the previous, but the lowest possible (sorted in ascending order).

### Example

```cpp
// next_permutation example
#include <iostream>     // std::cout
#include <algorithm>    // std::next_permutation, std::sort

int main () {
  int myints[] = {1,2,3};

  std::sort (myints,myints+3);

  std::cout << "The 3! possible permutations with 3 elements:\n";
  do {
    std::cout << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';
  } while ( std::next_permutation(myints,myints+3) );

  std::cout << "After loop: " << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';

  return 0;
}
```

Output:

```
The 3! possible permutations with 3 elements:
1 2 3
1 3 2
2 1 3
2 3 1
3 1 2
3 2 1
After loop: 1 2 3
```



## [prev_permutation](http://www.cplusplus.com/reference/algorithm/prev_permutation/)

### Parameters

- first, last

  [Bidirectional iterators](http://www.cplusplus.com/BidirectionalIterator) to the initial and final positions of the sequence. The range used is `[first,last)`, which contains all the elements between first and last, including the element pointed by first but not the element pointed by last. BidirectionalIterator shall point to a type for which [swap](http://www.cplusplus.com/swap) is properly defined. 

- comp

  Binary function that accepts two arguments of the type pointed by BidirectionalIterator, and returns a value convertible to `bool`. The value returned indicates whether the first argument is considered to go before the second in the specific *strict weak ordering* it defines. The function shall not modify any of its arguments. This can either be a function pointer or a function object. 



### Return value

`true` if the function could rearrange the object as a lexicographicaly smaller permutation.
Otherwise, the function returns `false` to indicate that the arrangement is not less than the previous, but the largest possible (sorted in descending order).

### Example

```cpp
// next_permutation example
#include <iostream>     // std::cout
#include <algorithm>    // std::next_permutation, std::sort, std::reverse

int main () {
  int myints[] = {1,2,3};

  std::sort (myints,myints+3);
  std::reverse (myints,myints+3);

  std::cout << "The 3! possible permutations with 3 elements:\n";
  do {
    std::cout << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';
  } while ( std::prev_permutation(myints,myints+3) );

  std::cout << "After loop: " << myints[0] << ' ' << myints[1] << ' ' << myints[2] << '\n';

  return 0;
}
```

output:

```
3 2 1 
3 1 2 
2 3 1 
2 1 3 
1 3 2 
1 2 3 
After loop: 3 2 1
```