# printf()函数的打印格式

### 1.常用的formatter

这是一些常用的格式化formatter

| formatter | description                        |
| --------- | ---------------------------------- |
| %c        | character                          |
| %d        | decimal (integer) number (base 10) |
| %e        | exponential floating-point number  |
| %f        | floating-point number              |
| %i        | integer (base 10)                  |
| %o        | octal number (base 8)              |
| %s        | a string of characters             |
| %u        | unsigned decimal (integer) number  |
| %x        | number in hexadecimal (base 16)    |
| %%        | print a percent sign               |
| \%        | print a percent sign               |

### 2.整数填充0

| formatter                   | description |
| --------------------------- | ----------- |
| printf("%03d", 0);          | 000         |
| printf("%03d", 1);          | 001         |
| printf("%03d", 123456789);  | 123456789   |
| printf("%03d", -10);        | -10         |
| printf("%03d", -123456789); | -123456789  |

### 3.浮点数格式化

| Description                                                 | Code                                | Result         |
| :---------------------------------------------------------- | :---------------------------------- | :------------- |
| Print one position after the decimal                        | printf("'%.1f'", 10.3456);          | '10.3'         |
| Two positions after the decimal                             | printf("'%.2f'", 10.3456);          | '10.35'        |
| Eight-wide, two positions after the decimal                 | printf("'%8.2f'", 10.3456);         | '   10.35'     |
| Eight-wide, four positions after the decimal                | printf("'%8.4f'", 10.3456);         | ' 10.3456'     |
| Eight-wide, two positions after the decimal, zero-filled    | printf("'%08.2f'", 10.3456);        | '00010.35'     |
| Eight-wide, two positions after the decimal, left-justified | printf("'%-8.2f'", 10.3456);        | '10.35   '     |
| Printing a much larger number with that same format         | printf("'%-8.2f'", 101234567.3456); | '101234567.35' |

