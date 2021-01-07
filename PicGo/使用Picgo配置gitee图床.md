使用Picgo配置gitee图床

之前一直使用微博图床edge或者chrome插件完成图片上传，也一直用着还可以，直到这个插件会修改浏览器主页【这个问题其实也好解决，可以修改host文件，把它改的导航页指定为某个ip，我之前是这么干的】，并且每次上传图片都要登陆新浪微博，每次登陆都要输入手机验证码，极其繁琐，不知道是不是因为我不怎么用新浪微博的原因。

使用PicGo搭配Snipaste和gitee，现在能完美实现图片随时截屏，上传，把markdown格式的图片链接自动复制到剪贴板，可以说是一气呵成了。

首先Snipaste是一个截图贴图工具，是一直都在用的截图软件，很好用。F1截图，F3贴图，可以用来对比，不用来回窗口或者应用程序切换，对于程序员来说是一大利器了。截图之后也可以直接Ctrl+c复制到剪贴板，搭配PicGo的快捷键【Ctrl+shift+p】直接上传到gitee中。

PicGo的安装这里就不细讲了，比较简单，需要注意的是，PicGo依赖Node.Js环境，然后在Gitee中开一个公开仓库，在PicGo中搜索gitee插件，配置对应的仓库地址和token即可。

可以配置PicGo的快捷上传，默认是Ctrl+shift+p

![](https://gitee.com/ericling666/imgbed/raw/master/img/20210107144014.png)