# git分支合并，解决冲突

## 1.手动解决冲突

手动解决冲突，需要使用编辑器，把所有文件中出现的冲突地方，把git文件合并的特殊符号，也就是

```shell
>>>>>>brancha
some code that was editted in brancha
======
some code that was editted in branchb
<<<<<<branchb
```



## 2.使用命令完全采用某个分支的版本

如果不想手动解决冲突，完全采用分支合并时的某一个分支，参考下面

```shell
//当前在test分支，执行分支合并，把T9724分支合并过来
$git merge T9724
//合并出现冲突，使用命令解决冲突，其中xxx表示文件路径，theirs表示对冲突文件完全采用分支T9724的处理，对应的还有ours，表示采用当前分支test的处理
git checkout xxx --theirs
```

![](http://ww1.sinaimg.cn/large/006edVQGly1g5nnvfir79j30un08c0v6.jpg)

