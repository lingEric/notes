

reference url:http://www.cplusplus.com/reference/algorithm

reference url:https://blog.csdn.net/Swust_Zeng_zhuo_K/article/details/80113384



## `<algorithm>`

Standard Template Library: Algorithms

The header `<algorithm>`
A range is any sequence of objects that can be accessed through iterators or pointers, such as an array or an instance of some of the . Notice though, that algorithms operate through iterators directly on the values, not affecting in any way the structure of any possible container (it never affects the size or storage allocation of the container).

### Functions

#### **Non-modifying sequence operations**

- [**any_of** ](http://www.cplusplus.com/reference/algorithm/any_of/)

  Test if any element in range fulfills condition (function template )

- [**none_of** ](http://www.cplusplus.com/reference/algorithm/none_of/)

  Test if no elements fulfill condition (function template )

- [**for_each**](http://www.cplusplus.com/reference/algorithm/for_each/)

  Apply function to range (function template )

- [**find**](http://www.cplusplus.com/reference/algorithm/find/)

  Find value in range (function template )

- [**find_if**](http://www.cplusplus.com/reference/algorithm/find_if/)

  Find element in range (function template )

- [**find_if_not** ](http://www.cplusplus.com/reference/algorithm/find_if_not/)

  Find element in range (negative condition) (function template )

- [**find_end**](http://www.cplusplus.com/reference/algorithm/find_end/)

  Find last subsequence in range (function template )

- [**find_first_of**](http://www.cplusplus.com/reference/algorithm/find_first_of/)

  Find element from set in range (function template )

- [**adjacent_find**](http://www.cplusplus.com/reference/algorithm/adjacent_find/)

  Find equal adjacent elements in range (function template )

- [**count**](http://www.cplusplus.com/reference/algorithm/count/)

  Count appearances of value in range (function template )

- [**count_if**](http://www.cplusplus.com/reference/algorithm/count_if/)

  Return number of elements in range satisfying condition (function template )

- [**mismatch**](http://www.cplusplus.com/reference/algorithm/mismatch/)

  Return first position where two ranges differ (function template )

- [**equal**](http://www.cplusplus.com/reference/algorithm/equal/)

  Test whether the elements in two ranges are equal (function template )

- [**is_permutation** ](http://www.cplusplus.com/reference/algorithm/is_permutation/)

  Test whether range is permutation of another (function template )

- [**search**](http://www.cplusplus.com/reference/algorithm/search/)

  Search range for subsequence (function template )

- [**search_n**](http://www.cplusplus.com/reference/algorithm/search_n/)

  Search range for elements (function template )

#### **Modifying sequence operations**

- [**copy_n** ](http://www.cplusplus.com/reference/algorithm/copy_n/)

  Copy elements (function template )

- [**copy_if** ](http://www.cplusplus.com/reference/algorithm/copy_if/)

  Copy certain elements of range (function template )

- [**copy_backward**](http://www.cplusplus.com/reference/algorithm/copy_backward/)

  Copy range of elements backward (function template )

- [**move** ](http://www.cplusplus.com/reference/algorithm/move/)

  Move range of elements (function template )

- [**move_backward** ](http://www.cplusplus.com/reference/algorithm/move_backward/)

  Move range of elements backward (function template )

- [**swap**](http://www.cplusplus.com/reference/algorithm/swap/)

  Exchange values of two objects (function template )

- [**swap_ranges**](http://www.cplusplus.com/reference/algorithm/swap_ranges/)

  Exchange values of two ranges (function template )

- [**iter_swap**](http://www.cplusplus.com/reference/algorithm/iter_swap/)

  Exchange values of objects pointed to by two iterators (function template )

- [**transform**](http://www.cplusplus.com/reference/algorithm/transform/)

  Transform range (function template )

- [**replace**](http://www.cplusplus.com/reference/algorithm/replace/)

  Replace value in range (function template )

- [**replace_if**](http://www.cplusplus.com/reference/algorithm/replace_if/)

  Replace values in range (function template )

- [**replace_copy**](http://www.cplusplus.com/reference/algorithm/replace_copy/)

  Copy range replacing value (function template )

- [**replace_copy_if**](http://www.cplusplus.com/reference/algorithm/replace_copy_if/)

  Copy range replacing value (function template )

- [**fill**](http://www.cplusplus.com/reference/algorithm/fill/)

  Fill range with value (function template )

- [**fill_n**](http://www.cplusplus.com/reference/algorithm/fill_n/)

  Fill sequence with value (function template )

- [**generate**](http://www.cplusplus.com/reference/algorithm/generate/)

  Generate values for range with function (function template )

- [**generate_n**](http://www.cplusplus.com/reference/algorithm/generate_n/)

  Generate values for sequence with function (function template )

- [**remove**](http://www.cplusplus.com/reference/algorithm/remove/)

  Remove value from range (function template )

- [**remove_if**](http://www.cplusplus.com/reference/algorithm/remove_if/)

  Remove elements from range (function template )

- [**remove_copy**](http://www.cplusplus.com/reference/algorithm/remove_copy/)

  Copy range removing value (function template )

- [**remove_copy_if**](http://www.cplusplus.com/reference/algorithm/remove_copy_if/)

  Copy range removing values (function template )

- [**unique**](http://www.cplusplus.com/reference/algorithm/unique/)

  Remove consecutive duplicates in range (function template )

- [**unique_copy**](http://www.cplusplus.com/reference/algorithm/unique_copy/)

  Copy range removing duplicates (function template )

- [**reverse**](http://www.cplusplus.com/reference/algorithm/reverse/)

  Reverse range (function template )

- [**reverse_copy**](http://www.cplusplus.com/reference/algorithm/reverse_copy/)

  Copy range reversed (function template )

- [**rotate**](http://www.cplusplus.com/reference/algorithm/rotate/)

  Rotate left the elements in range (function template )

- [**rotate_copy**](http://www.cplusplus.com/reference/algorithm/rotate_copy/)

  Copy range rotated left (function template )

- [**random_shuffle**](http://www.cplusplus.com/reference/algorithm/random_shuffle/)

  Randomly rearrange elements in range (function template )

- [**shuffle** ](http://www.cplusplus.com/reference/algorithm/shuffle/)

  Randomly rearrange elements in range using generator (function template )

#### **Partitions**

- [**partition**](http://www.cplusplus.com/reference/algorithm/partition/)

  Partition range in two (function template )

- [**stable_partition**](http://www.cplusplus.com/reference/algorithm/stable_partition/)

  Partition range in two - stable ordering (function template )

- [**partition_copy** ](http://www.cplusplus.com/reference/algorithm/partition_copy/)

  Partition range into two (function template )

- [**partition_point** ](http://www.cplusplus.com/reference/algorithm/partition_point/)

  Get partition point (function template )

#### **Sorting**

- [**stable_sort**](http://www.cplusplus.com/reference/algorithm/stable_sort/)

  Sort elements preserving order of equivalents (function template )

- [**partial_sort**](http://www.cplusplus.com/reference/algorithm/partial_sort/)

  Partially sort elements in range (function template )

- [**partial_sort_copy**](http://www.cplusplus.com/reference/algorithm/partial_sort_copy/)

  Copy and partially sort range (function template )

- [**is_sorted** ](http://www.cplusplus.com/reference/algorithm/is_sorted/)

  Check whether range is sorted (function template )

- [**is_sorted_until** ](http://www.cplusplus.com/reference/algorithm/is_sorted_until/)

  Find first unsorted element in range (function template )

- [**nth_element**](http://www.cplusplus.com/reference/algorithm/nth_element/)

  Sort element in range (function template )

#### **Binary search**

- [**upper_bound**](http://www.cplusplus.com/reference/algorithm/upper_bound/)

  Return iterator to upper bound (function template )

- [**equal_range**](http://www.cplusplus.com/reference/algorithm/equal_range/)

  Get subrange of equal elements (function template )

- [**binary_search**](http://www.cplusplus.com/reference/algorithm/binary_search/)

  Test if value exists in sorted sequence (function template )

#### **Merge**

- [**inplace_merge**](http://www.cplusplus.com/reference/algorithm/inplace_merge/)

  Merge consecutive sorted ranges (function template )

- [**includes**](http://www.cplusplus.com/reference/algorithm/includes/)

  Test whether sorted range includes another sorted range (function template )

- [**set_union**](http://www.cplusplus.com/reference/algorithm/set_union/)

  Union of two sorted ranges (function template )

- [**set_intersection**](http://www.cplusplus.com/reference/algorithm/set_intersection/)

  Intersection of two sorted ranges (function template )

- [**set_difference**](http://www.cplusplus.com/reference/algorithm/set_difference/)

  Difference of two sorted ranges (function template )

- [**set_symmetric_difference**](http://www.cplusplus.com/reference/algorithm/set_symmetric_difference/)

  Symmetric difference of two sorted ranges (function template )

#### **Heap**

- [**pop_heap**](http://www.cplusplus.com/reference/algorithm/pop_heap/)

  Pop element from heap range (function template )

- [**make_heap**](http://www.cplusplus.com/reference/algorithm/make_heap/)

  Make heap from range (function template )

- [**sort_heap**](http://www.cplusplus.com/reference/algorithm/sort_heap/)

  Sort elements of heap (function template )

- [**is_heap** ](http://www.cplusplus.com/reference/algorithm/is_heap/)

  Test if range is heap (function template )

- [**is_heap_until** ](http://www.cplusplus.com/reference/algorithm/is_heap_until/)

  Find first element not in heap order (function template )

#### **Min/max**

- [**max**](http://www.cplusplus.com/reference/algorithm/max/)

  Return the largest (function template )

- [**minmax** ](http://www.cplusplus.com/reference/algorithm/minmax/)

  Return smallest and largest elements (function template )

- [**min_element**](http://www.cplusplus.com/reference/algorithm/min_element/)

  Return smallest element in range (function template )

- [**max_element**](http://www.cplusplus.com/reference/algorithm/max_element/)

  Return largest element in range (function template )

- [**minmax_element** ](http://www.cplusplus.com/reference/algorithm/minmax_element/)

  Return smallest and largest elements in range (function template )

#### **Other**

- [**next_permutation**](http://www.cplusplus.com/reference/algorithm/next_permutation/)

  Transform range to next permutation (function template )

- [**prev_permutation**](http://www.cplusplus.com/reference/algorithm/prev_permutation/)

  Transform range to previous permutation (function template )





## `<numeric>`

Generalized numeric operations

This header describes a set of algorithms to perform certain operations on sequences of numeric values.

Due to their flexibility, they can also be adapted for other kinds of sequences.



### Functions

- [**accumulate**](http://www.cplusplus.com/reference/numeric/accumulate/)

  Accumulate values in range (function template )

- [**adjacent_difference**](http://www.cplusplus.com/reference/numeric/adjacent_difference/)

  Compute adjacent difference of range (function template )

- [**inner_product**](http://www.cplusplus.com/reference/numeric/inner_product/)

  Compute cumulative inner product of range (function template )

- [**partial_sum**](http://www.cplusplus.com/reference/numeric/partial_sum/)

  Compute partial sums of range (function template )

- [**iota** ](http://www.cplusplus.com/reference/numeric/iota/)

  Store increasing sequence (function template )







## `<functional>`

Function objects

*Function objects*   

They are typically used as arguments to functions, such as *predicates* or *comparison functions* passed to standard [algorithms](http://www.cplusplus.com/algorithm)

### Functions

These functions create objects of *wrapper classes* based on its arguments:

- [**cref** ](http://www.cplusplus.com/reference/functional/cref/)

  Construct reference_wrapper to const (function template )

- [**mem_fn** ](http://www.cplusplus.com/reference/functional/mem_fn/)

  Convert member function to function object (function template )

- [**not1**](http://www.cplusplus.com/reference/functional/not1/)

  Return negation of unary function object (function template )

- [**not2**](http://www.cplusplus.com/reference/functional/not2/)

  Return negation of binary function object (function template )

- [**ref** ](http://www.cplusplus.com/reference/functional/ref/)

  Construct reference_wrapper (function template )



### Classes



#### Wrapper classes

Wrapper classes 

- [**function** ](http://www.cplusplus.com/reference/functional/function/)

  Function wrapper (class template )

- [**reference_wrapper** ](http://www.cplusplus.com/reference/functional/reference_wrapper/)

  Reference wrapper (class template )

- [**unary_negate**](http://www.cplusplus.com/reference/functional/unary_negate/)

  Negate unary function object class (class template )



#### Operator classes

Operator classes 

- [**bit_or** ](http://www.cplusplus.com/reference/functional/bit_or/)

  Bitwise OR function object class (class template )

- [**bit_xor** ](http://www.cplusplus.com/reference/functional/bit_xor/)

  Bitwise XOR function object class (class template )

- [**divides**](http://www.cplusplus.com/reference/functional/divides/)

  Division function object class (class template )

- [**equal_to**](http://www.cplusplus.com/reference/functional/equal_to/)

  Function object class for equality comparison (class template )

- [**greater**](http://www.cplusplus.com/reference/functional/greater/)

  Function object class for greater-than inequality comparison (class template )

- [**greater_equal**](http://www.cplusplus.com/reference/functional/greater_equal/)

  Function object class for greater-than-or-equal-to comparison (class template )

- [**less**](http://www.cplusplus.com/reference/functional/less/)

  Function object class for less-than inequality comparison (class template )

- [**less_equal**](http://www.cplusplus.com/reference/functional/less_equal/)

  Function object class for less-than-or-equal-to comparison (class template )

- [**logical_and**](http://www.cplusplus.com/reference/functional/logical_and/)

  Logical AND function object class (class template )

- [**logical_not**](http://www.cplusplus.com/reference/functional/logical_not/)

  Logical NOT function object class (class template )

- [**logical_or**](http://www.cplusplus.com/reference/functional/logical_or/)

  Logical OR function object class (class template )

- [**minus**](http://www.cplusplus.com/reference/functional/minus/)

  Subtraction function object class (class template )

- [**modulus**](http://www.cplusplus.com/reference/functional/modulus/)

  Modulus function object class (class template )

- [**multiplies**](http://www.cplusplus.com/reference/functional/multiplies/)

  Multiplication function object class (class template )

- [**negate**](http://www.cplusplus.com/reference/functional/negate/)

  Negative function object class (class template )

- [**not_equal_to**](http://www.cplusplus.com/reference/functional/not_equal_to/)

  Function object class for non-equality comparison (class template )

- [**plus**](http://www.cplusplus.com/reference/functional/plus/)

  Addition function object class (class template )



#### Other classes

- [**bad_function_call** ](http://www.cplusplus.com/reference/functional/bad_function_call/)

  Exception thrown on bad call (class )

- [**hash** ](http://www.cplusplus.com/reference/functional/hash/)

  Default hash function object class (class template )

- [**is_bind_expression** ](http://www.cplusplus.com/reference/functional/is_bind_expression/)

  Is bind expression (class template )

- [**is_placeholder** ](http://www.cplusplus.com/reference/functional/is_placeholder/)

  Is placeholder (class template )



### Namespaces

- [**placeholders** ](http://www.cplusplus.com/reference/functional/placeholders/)

  Bind argument placeholders (namespace )