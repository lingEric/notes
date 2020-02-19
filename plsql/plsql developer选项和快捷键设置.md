# plsql developer选项和快捷键设置

1、登录后默认自动选中My Objects 

默认情况下，PLSQL Developer登录后，Brower里会选择All objects，如果你登录的用户是dba，要展开tables目录，正常情况都需要Wait几秒钟，而选择My Objects后响应速率则是以毫秒计算的。

设置方法：
Tools菜单 --> Brower Filters，会打开Brower Folders的定单窗口，把“My Objects”设为默认即可。
Tools菜单－－Brower Folders，中把你经常点的几个目录（比如：Tables Views Seq Functions Procedures）移得靠上一点，并加上颜色区分，这样你的平均寻表时间会大大缩短，试试看。

2、记住密码   

  这是个有争议的功能，因为记住密码会给带来数据安全的问题。 但假如是开发用的库，密码甚至可以和用户名相同，每次输入密码实在没什么意义，可以考虑让PLSQL Developer记住密码。

设置方法：菜单Tools --> Preferences --> Oracle --> Logon History --> Store With Password

3、双击即显示表数据

鼠标双击表或者视图时的默认响应实在让我感到失望，因为我最关心的是表结构和数据，但是双击后这两件事情都没有发生，也许默认响应是高手们需要的，但对我来说查看数据和表结构是最主要的，其他的我不关心。不过好的是这是可以设置的，你可以给鼠标双击和拖放绑定需要的事件，比如：双击编辑数据，拖放显示表结构，Yeah！

设置方法：菜单Tools --> Preferences --> Browser，在右侧，为不同的Object Type绑定双击和拖放操作。

4、SQL语句字符全部大写

自认为这是个好习惯，信息系统的核心是数据库，系统出问题时最先要查的就是SQL语句，怎样在浩瀚的日志中快速找到那条SQL语句是件比较痛苦的事情。 SQL语句全部大写并不能彻底解决这一问题，但在一堆代码中间找一行全部大写的字符相对容易些，你的眼睛会感谢你。 

设置方法：菜单Tools --> Preferences --> Editor --> Keyword Case --> Uppercase

5、特殊Copy   

  在SQL Window里写好的SQL语句通常需要放到Java或者别的语言内，就需要转成字符串并上加上相应的连字符，这一个事不需要再重复做了，在写好的SQL上点右键，使用特殊Copy即OK！

设置方法：鼠标右键 --> Special Copy

6、自定义快捷键 

  PLSQL Developer里预留了很多键让用户自定义，这是件很Hight的事情。不像霸道的Word，基本上所有的键都已预定义了功能，修改起来很是头疼。 通常情况下，打开PLSQL Developer后，最经常干的事就是打开SQL Window和Command Window，就给这两个操作定义了快捷键，ALT+S和ALT+ C，这样拿鼠标点三下的事情只需要按一下键。

设置方法：菜单Tools --> Preferences --> Key Configuration
7、SQL Window中根据光标位置自动选择语句

设置方法：Preferences --> Window Types --> SQL Window，将AutoSelect statement选中即可。注意，每条语句后面要加分号。

 

8、启动PLSQL Developer，window list菜单自动调出

需要两步设置，首先要保存桌面设置，然后勾上Window list选项，具体操作如下：
a、在菜单项的Tools下的Preference选项中的User Interface中选择Option，在右边对于的Autosave desktop中把前面的复选框勾选上。
b、在菜单项的Tools下的Window list选项勾上。

 

9、格式化SQL(format)

当大家拿到一段较长的SQL语句时，想快速查看其中的逻辑，可以放在这个工具里，进行格式化，语句的逻辑也就一目了然了

 

10、数据库自动检测连接情况

因为数据库每过一段时间不操作，就会自动断开，然后需要自己手动连接，因为网络原因，总会卡在那里。工具提供了检测连接的功能，可以自动连接。

具体设置在Tools-Preferences-Check connection,Check connection前面勾选即可。

 

11、常用的快捷键

其实这些在第6项中都提到，下面是我的习惯设置：

ctrl+tab 切换windows窗口（或者alt+left/right）

alt + n 创建新sql window

alt + c 关闭当前窗口

alt + f 格式化sql

alt + end 列出所有结果

shift + esc 停止运行


12.自动替换


快捷输入SQL语句，例如输入s，按下空格，自动替换成SELECT；再例如，输入sf，按下空格，自动替换成SELECT * FROM，非常方便，节省了大量的时间去编写重复的SQL语句。

设置方法：菜单Tools –> Preferences –> Editor –> AutoReplace. –> Edit

下面定义了一些规则作为参考

s=SELECT 
f=FROM 
w=WHERE 
o=ORDER BY 
d=DELETE 
sf=SELECT * FROM 
df=DELETE FROM 
sc=SELECT COUNT(*) FROM