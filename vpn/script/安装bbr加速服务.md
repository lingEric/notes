
参考链接：https://www.vultrcn.com/5.html
## 一、注意事项

1、安装 Google BBR 需升级系统内核，而安装锐速则需降级系统内核，故两者不能同时安装。

2、安装 Google BBR 需升级系统内核，有可能造成系统不稳定，故不建议将其应用在重要的生产环境中。

3、原版和魔改版 Google BBR 在不同地区的服务器上会有不同效果，具体孰优孰劣请分别安装进行测试。

## 二、原版 Google BBR

按照[《Windows 使用 Xshell 软件连接 Vultr VPS 教程》](https://www.vultrcn.com/3.html)连接服务器，按照下图提示，我们首先复制命令：

```
wget --no-check-certificate https://github.com/teddysun/across/raw/master/bbr.sh && chmod +x bbr.sh && ./bbr.sh
```

然后回到 Xshell 软件，鼠标右击选择粘贴，回车继续。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr01.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr01.png)

回车后系统会自动下载脚本并执行。脚本执行后会显示出当前系统以及系统内核版本，按照下图提示，我们直接回车确认安装即可。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr02.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr02.png)

回车后脚本会继续执行并自动安装最新版系统内核同时开启 Google BBR 拥塞控制算法。当脚本安装完毕后会询问我们是否重启服务器，按照下图提示，我们首先输入“y”，然后回车确认重启即可。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr03.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/bbr03.png)

确认重启后，Xshell 软件会断开连接。等待 3~5 分钟服务器即可重启完毕，我们**重新连接服务器**，然后依次运行下列命令并对比输出值是否一致。

```
sysctl net.ipv4.tcp_available_congestion_control
执行后输出值需为：net.ipv4.tcp_available_congestion_control = reno cubic bbr

sysctl net.ipv4.tcp_congestion_control
执行后输出值需为：net.ipv4.tcp_congestion_control = bbr

sysctl net.core.default_qdisc
执行后输出值需为：net.core.default_qdisc = fq
```

以上三条命令的输出值正确后则说明原版 Google BBR 已经成功安装并开机自启动。

## 三、魔改版 Google BBR

文章开头也说明了魔改版 Google BBR 是在原版 Google BBR 的基础上修改了一些参数，所以两者的性能会有所不同，具体效果则需要大家可以分别进行测试。

PS：魔改版 Google BBR 和 原版 Google BBR 不能够共存的，不要同时安装。

按照[《Windows 使用 Xshell 软件连接 Vultr VPS 教程》](https://www.vultrcn.com/3.html)连接服务器，按照下图提示，我们首先复制命令：

**CentOS 6/7 x64 系统请用这个**

```
wget --no-check-certificate https://raw.githubusercontent.com/nanqinlang-tcp/tcp_nanqinlang/master/General/CentOS/bash/tcp_nanqinlang-1.3.2.sh && bash tcp_nanqinlang-1.3.2.sh
```

**Debian 7/8 x64 系统请用这个**

```
wget --no-check-certificate https://github.com/nanqinlang-tcp/tcp_nanqinlang/releases/download/3.4.2.1/tcp_nanqinlang-fool-1.3.0.sh && bash tcp_nanqinlang-fool-1.3.0.sh
```

然后回到 Xshell 软件，鼠标右击选择粘贴，回车继续。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr01.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr01.png)

回车后系统会自动下载脚本并运行。按照下图提示，我们首先输入“1”（即升级内核），然后回车继续。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr02.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr02.png)

回车后系统会自行执行升级内核命令，当出现下图所示信息时，我们输入“y”，然后回车即可继续安装。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr03.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr03.png)

安装新内核完成后，按照下图提示，我们输入“reboot”，然后回车即可重启服务器以应用新内核。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr04.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr04.png)

确认重启后，Xshell 软件会断开连接。等待 3~5 分钟服务器即可重启完毕，我们**重新连接服务器**，按照下图提示，我们继续复制命令：

**CentOS 6/7 x64 系统请用这个**

```
bash tcp_nanqinlang-1.3.2.sh
```

**Debian 7/8 x64 系统请用这个**

```
bash tcp_nanqinlang-fool-1.3.0.sh
```

然后回到 Xshell 软件，鼠标右击选择粘贴，回车继续。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr05.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr05.png)

回车后系统会自动运行脚本。按照下图提示，我们输入“2”（即开启魔改版 Google BBR 算法），然后回车继续即可。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr06.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr06.png)

回车后系统会自行执行开启算法命令，当出现下图所示信息时，我们输入“y”，然后回车即可继续开启。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr03.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr03.png)

当出现下图所示信息时代表魔改版 Google BBR 已经成功安装并开机自启动。

[![#加速# 原版 & 魔改版 Google BBR 拥塞控制算法一键安装脚本](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr07.png)](https://static.vultrcn.com/wp-content/uploads/2018/03/mod-bbr07.png)

以上就是原版 Google BBR 与魔改版 Google BBR 完整安装过程