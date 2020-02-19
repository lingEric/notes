### 1.Git简介

Git的诞生确实是一个有趣的故事，我们知道，当年Linus创建了开源的Linux，从此，Linux系统不断发展，现在已经成为最大的服务器系统软件了。(请不要傻傻分不清Linus和Linux)

但是随着Linux的不断壮大，就需要各种版本控制了，起初Linus带着他的小弟们使用的是BitKeeper(商业版本控制系统),之后呢由于某种原因BitKeeper的公司不让他们使用了，于是Linus自己花了两周时间写出了Git并且开源了(BitKeeper已哭晕在厕所)，阿弥陀佛，幸亏BitKeeper不让Linus他们用了，要不然我们现在也不会有这么好用的Git了，博主更不会在这写这篇博文了。

之后的岁月里，渐渐有了github,coding等一些可以使用git存储的网站，Git的江湖地位变得无可替代了，如果你是个开发者却还不会使用Git那就太out了。

这里先引用一张图解释Git
工作原理：

- Workspace:工作区，执行git add *命令就把改动提交到了暂存区，执行git pull命令将远程仓库的数据拉到当前分支并合并，执行git checkout [branch-name]切换分支
- Index:暂存区，执行git commit -m '说明' 命令就把改动提交到了仓库区（当前分支）
- Repository:仓库区（或本地仓库），执行git push origin master提交到远程仓库，执行git clone 地址将克隆远程仓库到本地
- Remote:远程仓库，就是类似github，coding等网站所提供的仓库

**注**：实际操作命令和上述命令会有所不同，这里这是解释清楚命令和仓库的关系。

#### 1.1 Git 术语

术语 定义 仓库（Repository） 一个仓库包括了所有的版本信息、所有的分支和标记信息。在Git中仓库的每份拷贝都是完整的。仓库让你可以从中取得你的工作副本。 分支（Branches） 一个分支意味着一个独立的、拥有自己历史信息的代码线（code line）。你可以从已有的代码中生成一个新的分支，这个分支与剩余的分支完全独立。默认的分支往往是叫master。用户可以选择一个分支，选择一个分支执行命令git checkout branch. 标记（Tags） 一个标记指的是某个分支某个特定时间点的状态。通过标记，可以很方便的切换到标记时的状态，例如2009年1月25号在testing分支上的代码状态 提交（Commit） 提交代码后，仓库会创建一个新的版本。这个版本可以在后续被重新获得。每次提交都包括作者和提交者，作者和提交者可以是不同的人 修订（Revision） 用来表示代码的一个版本状态。Git通过用SHA1 hash算法表示的id来标识不同的版本。每一个 SHA1 id都是160位长，16进制标识的字符串.。最新的版本可以通过HEAD来获取。之前的版本可以通过”HEAD~1”来获取，以此类推。

#### 1.2 忽略特定的文件

可以配置Git忽略特定的文件或者是文件夹。这些配置都放在.gitignore文件中。这个文件可以存在于不同的文件夹中，可以包含不同的文件匹配模式。
比如.gitignore内容可以如下：

```text
忽略某文件
npm-debug.log
忽略文件夹
dist/
node_modules/
.idea/
```

同时Git也提供了全局的配置，core.excludesfile。

> 忽略之后的文件或是文件夹Git就不去提交里面的内容了。

#### 1.3 使用.gitkeep来追踪空的文件夹

Git会忽略空的文件夹。如果你想版本控制包括空文件夹，根据惯例会在空文件夹下放置.gitkeep文件。其实对文件名没有特定的要求。一旦一个空文件夹下有文件后，这个文件夹就会在版本控制范围内。

#### 1.4 配置

```text
# 显示当前的Git配置
$ git config --list
# 编辑Git配置文件，只是配置用户信息的话直接看下面两行命令即可
$ git config -e [--global]
# 设置提交代码时的用户信息，是否加上全局--global自行决定，一般是直接设置全局的。另外用户邮箱需要注意最好使用gmail,QQ也可以，需要和你远程仓库保持一致不然你的contribution是不会被记录在远程仓库的
$ git config [--global] user.name "[name]"
$ git config [--global] user.email "[email address]"
```

> Git的设置文件为.gitconfig，它可以在用户主目录下（全局配置），也可以在项目目录下（项目配置）。

**个人觉得git是需要认真学的，虽然是个工具但不学习很容易把自己弄糊涂，希望这篇博客可以在某些时候帮到您，让您大概理解git的工作原理并把基本命令串起来。那么下面就说一下Git重要的基本命令吧。**

### 2.Git安装

[Git-for-window](https://git-for-windows.github.io/index.html)

> 下载安装这个不用多说吧….

### 3.创建仓库

```text
# 在当前目录创建一个文件夹
$ mkdir [project-name]
# 在当前目录新建一个Git代码库
$ git init
# 新建一个目录，将其初始化为Git代码库
$ git init [project-name]
# 下载一个项目和它的整个代码历史（各个分支提交记录等）
$ git clone [url]
```

> git init后会出现.git文件夹，里面有配置文件，如果没有git bash里面输入ls -lah就可以看到了

**关于如何关联Git和远程仓库，比如Coding，github等，可以看这两篇文章**：
[Git链接到自己的Github](http://www.cnblogs.com/plinx/archive/2013/04/08/3009159.html)[Coding帮助中心](https://coding.net/help/doc/git/getting-started.html#git-1)

### 4.提交文件

#### 4.1首次推送

```text
# 添加当前目录的所有文件到暂存区
$ git add *
# 提交暂存区到仓库区
$ git commit -m [message]
# 为远程Git更名为origin
$ git remote add origin git@github.com:abcd/tmp.git
# 推送此次修改，这是首次推送需要加上-u,之后推送就可以直接git push  origin master,origin是远程Git名字，这个可以自己定义，不过一般是用origin罢了，master是默认的分支，如果不在master分支提交需要写清楚分支名称
$ git push -u origin master
```

> 首次推送成功后可以看下下面的命令：

```text
# 添加指定文件到暂存区
$ git add [file1] [file2] ...
# 添加指定目录到暂存区，包括子目录
$ git add [dir]
# 添加当前目录的所有文件到暂存区
$ git add *
# 添加每个变化前，都会要求确认
对于同一个文件的多处变化，可以实现分次提交
$ git add -p
# 删除工作区文件，并且将这次删除放入暂存区
$ git rm [file1] [file2] ...
# 停止追踪指定文件，但该文件会保留在工作区
$ git rm --cached [file]
# 改名文件，并且将这个改名放入暂存区
$ git mv [file-original] [file-renamed]
# 提交暂存区到仓库区
$ git commit -m [message]
# 提交暂存区的指定文件到仓库区
$ git commit [file1] [file2] ... -m [message]
# 提交工作区自上次commit之后的变化，直接到仓库区
$ git commit -a
# 提交时显示所有diff信息
$ git commit -v
# 使用一次新的commit，替代上一次提交
如果代码没有任何新变化，则用来改写上一次commit的提交信息
$ git commit --amend -m [message]
# 重做上一次commit，并包括指定文件的新变化
$ git commit --amend [file1] [file2] ...
# 提交更改到远程仓库
$ git push origin master
# 拉取远程更改到本地仓库默认自动合并
$ git pull origin master
```

> 如果我们只是维护自己的小项目的话，上面的命令已经够用了，自己一个人在master分支想咋折腾就咋折腾

#### 5.分支

> 但如果是多人协作的话，git的魅力就开始提现出来了，每个人有自己的一个分支，各自在自己的分支上工作互不干扰。具体的看这：[Git教程-创建合并分支](http://www.liaoxuefeng.com/wiki/0013739516305929606dd18361248578c67b8067c8c017b000/001375840038939c291467cc7c747b1810aab2fb8863508000)

```text
# 列出所有本地分支
$ git branch
# 列出所有远程分支
$ git branch -r
# 列出所有本地分支和远程分支
$ git branch -a
# 新建一个分支，但依然停留在当前分支
$ git branch [branch-name]
# 新建一个分支，并切换到该分支
$ git checkout -b [branch]
# 新建一个分支，指向指定commit
$ git branch [branch] [commit]
# 新建一个分支，与指定的远程分支建立追踪关系
$ git branch --track [branch] [remote-branch]
# 切换到指定分支，并更新工作区
$ git checkout [branch-name]
# 切换到上一个分支
$ git checkout -
# 建立追踪关系，在现有分支与指定的远程分支之间
$ git branch --set-upstream [branch] [remote-branch]
# 合并指定分支到当前分支，如果有冲突需要手动合并冲突（就是手动编辑文件保存咯），然后add,commit再提交
$ git merge [branch]
# 选择一个commit，合并进当前分支
$ git cherry-pick [commit]
# 删除分支
$ git branch -d [branch-name]
# 删除远程分支
$ git push origin --delete [branch-name]
$ git branch -dr [remote/branch]
```

#### 6.标签

> 标签的作用主要是用来做版本回退的，关于版本回退，这也是Git的亮点之一，起到了后悔药的功能·

```text
# 列出所有tag
$ git tag
# 新建一个tag在当前commit
$ git tag [tag]
# 新建一个tag在指定commit
$ git tag [tag] [commit]
# 删除本地tag
$ git tag -d [tag]
# 删除远程tag
$ git push origin :refs/tags/[tagName]
# 查看tag信息
$ git show [tag]
# 提交指定tag
$ git push [remote] [tag]
# 提交所有tag
$ git push [remote] --tags
# 新建一个分支，指向某个tag
$ git checkout -b [branch] [tag]
```

#### 7.后悔药

> 想一下在你写完N个文件代码后，commit到了本地仓库，突然发现整个应用崩溃了！咋整？Git给了我们吃后悔药
> 的机会：

```text
# 恢复暂存区的指定文件到工作区
$ git checkout [file]

# 恢复某个commit的指定文件到暂存区和工作区
$ git checkout [commit] [file]
# 恢复暂存区的所有文件到工作区
$ git checkout .
# 回退到上一个版本，在Git中，用HEAD表示当前版本
$ git reset --hard HEAD^
# 重置暂存区的指定文件，与上一次commit保持一致，但工作区不变
$ git reset [file]
# 重置暂存区与工作区，与上一次commit保持一致
$ git reset --hard
# 重置当前分支的指针为指定commit，同时重置暂存区，但工作区不变
$ git reset [commit]
# 重置当前分支的HEAD为指定commit，同时重置暂存区和工作区，与指定commit一致
$ git reset --hard [commit]
# 重置当前HEAD为指定commit，但保持暂存区和工作区不变
$ git reset --keep [commit]
# 新建一个commit，用来撤销指定commit
# 后者的所有变化都将被前者抵消，并且应用到当前分支
$ git revert [commit]
# 暂时将未提交的变化移除，稍后再移入
$ git stash
$ git stash pop
```

> 这个时候标签的作用就体现出来了，因为commit号太冗长了，记起来太麻烦有了标签我们相当于自定义了commit号

#### 8. 文件信息

```text
# 显示当前分支的版本历史
$ git log
# 显示commit历史，以及每次commit发生变更的文件
$ git log --stat
# 搜索提交历史，根据关键词
$ git log -S [keyword]
# 显示某个commit之后的所有变动，每个commit占据一行
$ git log [tag] HEAD --pretty=format:%s
# 显示某个commit之后的所有变动，其"提交说明"必须符合搜索条件
$ git log [tag] HEAD --grep feature
# 显示某个文件的版本历史，包括文件改名
$ git log --follow [file]
$ git whatchanged [file]
# 显示指定文件相关的每一次diff
$ git log -p [file]
# 显示过去5次提交
$ git log -5 --pretty --oneline
# 显示所有提交过的用户，按提交次数排序
$ git shortlog -sn
# 显示指定文件是什么人在什么时间修改过
$ git blame [file]
# 显示暂存区和工作区的差异
$ git diff
# 显示暂存区和上一个commit的差异
$ git diff --cached [file]
# 显示工作区与当前分支最新commit之间的差异
$ git diff HEAD
# 显示两次提交之间的差异
$ git diff [first-branch]...[second-branch]
# 显示今天你写了多少行代码
$ git diff --shortstat "@{0 day ago}"
# 显示某次提交的元数据和内容变化
$ git show [commit]
# 显示某次提交发生变化的文件
$ git show --name-only [commit]
# 显示某次提交时，某个文件的内容
$ git show [commit]:[filename]
```

### 8.其它命令

命令 说明 git blame filepath git blame清楚的记录某个文件的更改历史和更改人，简直是查看背锅人的利器，filepath是需要查看的文件路径 git status 显示有变更的文件 git reflog 显示当前分支的最近几次提交