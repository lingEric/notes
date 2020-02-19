## 出错场景：
协同开发时，我们从远程服务器上pull下代码的时候，出现以下提示信息：
Auto Merge Failed; Fix Conflicts and Then Commit the Result.

## 原因分析：
利用git status，输出如下：
root@hyk-virt:/etc# git status

```
On branch master
Your branch and 'origin/master' have diverged,
and have 2 and 2 different commits each, respectively.
Unmerged paths:
  (use "git add/rm <file>..." as appropriate to mark resolution)
both modified:      apt/sources.list
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)
modified:   cups/subscriptions.conf
modified:   cups/subscriptions.conf.O
modified:   mtab
modified:   update-manager/release-upgrades
no changes added to commit (use "git add" and/or "git commit -a")
```

从git status的结果可以发现：其中sources.list这个文件存在合并冲突
而进一步分析git pull的原理，实际上git pull是分了两步走的，（1）从远程pull下origin/master分支（2）将远程的origin/master分支与本地master分支进行合并
以上的错误，是出在了第二步骤

## 解决方法

1. 方法一：如果我们确定远程的分支正好是我们需要的，而本地的分支上的修改比较陈旧或者不正确，那么可以直接丢弃本地分支内容，运行如下命令(看需要决定是否需要运行git fetch取得远程分支)：
   $:git reset --hard origin/master
   或者$:git reset --hard ORIG_HEAD
   解释：
    git-reset - Reset current HEAD to the specified state
   --hard
                  Resets the index and working tree. Any changes to tracked files
                  in the working tree since <commit> are discarded.
2. 方法二：我们不能丢弃本地修改，因为其中的某些内容的确是我们需要的，此时需要对unmerged的文件进行手动修改，删掉其中冲突的部分，然后运行如下命令
   $:git add filename
   $:git commit -m "message"
3. 方法三：如果我们觉得合并以后的文件内容比价混乱，想要废弃这次合并，回到合并之前的状态，那么可以运行如下命令：
   $:git reset --hard HEAD