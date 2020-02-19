# c语言中的引用传递

两种引用传递的定义方式

- 第一种

  ```c
  
  #include<stdio.h> 
  void changeValue(int *a);
  int main(){
  	int a =1;
  	changeValue(&a);
  	printf("%d",a);
  	
  	return 0;
  }
  
  void changeValue(int *a){
  	*a=12;
  }
  ```

  Output

  ```
  12
  ```

  

- 第二种

  ```c
  
  #include<stdio.h>
  void changevalue(int &a);
  int main(){
  	int a =1;
  	changevalue(a);
  	printf("%d",a);
  	return 0;
  } 
  
  void changevalue(int &a){
  	a=12;
  }
  ```

  Output

  ```
  12
  ```

  