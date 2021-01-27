System has not been booted with systemd as init system (PID 1).

我在WSL里面执行如下命令：

```shell
sudo systemctl start docker
```

报了如下错误

```
System has not been booted with systemd as init system (PID 1). Can't operate.
Failed to connect to bus: Host is down
```

![](https://gitee.com/ericling666/imgbed/raw/master/img/20210111220158.png)

这个问题的关键是，我想用systemctl命令来管理服务启动，但是系统并不支持，因为WSL里面使用init命令来管理，你可以通过以下命令查看：

```
ps -p 1 -o comm=
```

![](https://gitee.com/ericling666/imgbed/raw/master/img/20210111220736.png)

这个命令的其他参数与systemd类似

| Systemd command                | Sysvinit command             |
| ------------------------------ | ---------------------------- |
| systemctl start service_name   | service service_name start   |
| systemctl stop service_name    | service service_name stop    |
| systemctl restart service_name | service service_name restart |
| systemctl status service_name  | service service_name status  |
| systemctl enable service_name  | chkconfig service_name on    |
| systemctl disable service_name | chkconfig service_name off   |















