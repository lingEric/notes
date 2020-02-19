# Git学习

## git简介

1. 版本控制工具需要提供的功能

   - 协同修改
   - 数据备份
   - 版本管理
   - 权限控制
   - 历史纪录
   - 分支管理

2. 版本控制工具类型

   - 集中式【CVS、SVN、VSS】

     存在单点故障问题，本地机器只保存当前状态，如果服务器宕机，则历史数据全部丢失

   - 分布式【Git、Mercurial...】

     不存在单点故障问题

3. Git优势

   - 不需要联网，大部分操作在本地完成
   - 完整性保证，不存在单点故障
   - 尽可能添加数据而不是删除或修改数据
   - 分支操作非常快捷流畅
   - 与linux命令全面兼容

## Git维护本地仓库命令

```shell
初始化git仓库，可以加上仓库名xx【自动创建新的文件夹xx】，不加仓库名，则默认为当前目录
git init 
设置全局参数，用于后面代码提交时，显示的数据字段，项目可以单独配置，优先级高于全局配置
git config --global user.name
git config --global user.email
查看当前仓库的三个工作区【工作区域，暂存区，本地仓库】的状态
git status
添加指定文件到暂存区
git add 
把指定文件从暂存区移除【对于新增文件，可以随意使用，但是历史追踪文件慎用此命令】
git rm --cached
把暂存区文件提交到本地仓库
git commit -m 'commit message'
从本地仓库拿出文件的某一个版本，后面可以加上版本号，不加默认为HEAD版本，即忽略本地修改
git checkout -- <file>
代码回滚，默认mixed，HEAD则表示<file>回滚到最新提交的版本，忽略本地修改，同上一条命令
git reset HEAD <file>
查看git的版本变更信息，不包括本地的版本变更信息
git log 
显示的效果更美观
git log --pretty=oneline
效果同上一条
git log --oneline
查看git的版本变更信息，包括本地的所有历史版本回滚记录
git reflog

HEAD指针
移动指针3种方式
1.索引值
git reset --hard 索引值

2.^
只能后退,n个^，后退n步
git reset --hard HEAD^

3.~
git reset --hard HEAD~n
只能后退，n表示后退的步数


移动指针的三种参数区别
1.soft
只移动本地仓库，工作区域和暂存区不变更，【最常用】

2.mixed
移动本地仓库和暂存区

3.hard【最常用】
移动本地库和暂存区，工作区

工作区文件和暂存区文件比较
git diff <文件名>
工作区文件和历史版本文件比较
git diff <索引值> <文件名>

创建分支
git branch <分支名>
创建新分支并切换到该分支
git checkout -b <分支名>
查看所有分支
git branch -v
切换分支
git checkout <分支名>
将指定分支合并到当前所在分支
git merge <分支名>

```

## 

