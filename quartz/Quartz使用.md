# 1. Quartz 体系结构

Quartz 设计有三个核心类，分别是 Scheduler（调度器）Job（任务）和 Trigger （触发器），它们是我们使用 Quartz 的关键。

1）**Job**：定义需要执行的任务。该类是一个接口，只定义一个方法 `execute(JobExecutionContext context)`，在实现类的 `execute` 方法中编写所需要定时执行的 Job（任务）， `JobExecutionContext` 类提供了调度应用的一些信息。Job 运行时的信息保存在 JobDataMap 实例中。

2）**Trigger**：负责设置调度策略。该类是一个接口，描述触发 job 执行的时间触发规则。主要有 SimpleTrigger 和 CronTrigger 这两个子类。当且仅当需调度一次或者以固定时间间隔周期执行调度，SimpleTrigger 是最适合的选择；而 CronTrigger 则可以通过 Cron 表达式定义出各种复杂时间规则的调度方案：如工作日周一到周五的 15：00~16：00 执行调度等。

3）**Scheduler**：调度器就相当于一个容器，装载着任务和触发器。该类是一个接口，代表一个 Quartz 的独立运行容器， Trigger 和 JobDetail 可以注册到 Scheduler 中， 两者在 Scheduler 中拥有各自的组及名称， 组及名称是 Scheduler 查找定位容器中某一对象的依据， Trigger 的组及名称必须唯一， JobDetail 的组和名称也必须唯一（但可以和 Trigger 的组和名称相同，因为它们是不同类型的）。Scheduler 定义了多个接口方法， 允许外部通过组及名称访问和控制容器中 Trigger 和 JobDetail。

Scheduler 可以将 Trigger 绑定到某一 JobDetail 中， 这样当 Trigger 触发时， 对应的 Job 就被执行。*一个 Job 可以对应多个 Trigger， 但一个 Trigger 只能对应一个 Job。*可以通过 SchedulerFactory 创建一个 SchedulerFactory 实例。Scheduler 拥有一个 SchedulerContext，它类似于 SchedulerContext，保存着 Scheduler 上下文信息，Job 和 Trigger 都可以访问 SchedulerContext 内的信息。SchedulerContext 内部通过一个 Map，以键值对的方式维护这些上下文数据，SchedulerContext 为保存和获取数据提供了多个 `put()` 和 `getXxx()` 的方法。可以通过 `Scheduler#getContext()` 获取对应的 SchedulerContext 实例。

4）**JobDetail**：描述 Job 的实现类及其它相关的静态信息，如：Job 名字、描述、关联监听器等信息。Quartz 每次调度 Job 时， 都重新创建一个 Job 实例， 所以它不直接接受一个 Job 的实例，相反它接收一个 Job 实现类，以便运行时通过 `newInstance()` 的反射机制实例化 Job。

5）**ThreadPool**：Scheduler 使用一个线程池作为任务运行的基础设施，任务通过共享线程池中的线程提高运行效率。

Job 有一个 ~~StatefulJob~~ 子接口（Quartz 2 后用 `@PersistJobDataAfterExecution` 注解代替），代表有状态的任务，该接口是一个没有方法的标签接口，其目的是让 Quartz 知道任务的类型，以便采用不同的执行方案。

- 无状态任务在执行时拥有自己的 JobDataMap 拷贝，对 JobDataMap 的更改不会影响下次的执行。
- 有状态任务共享同一个 JobDataMap 实例，每次任务执行对 JobDataMap 所做的更改会保存下来，后面的执行可以看到这个更改，也即每次执行任务后都会对后面的执行发生影响。

正因为这个原因，无状态的 Job **能**并发执行，而有状态的 StatefulJob **不能**并发执行。这意味着如果前次的 StatefulJob 还没有执行完毕，下一次的任务将阻塞等待，直到前次任务执行完毕。有状态任务比无状态任务需要考虑更多的因素，程序往往拥有更高的复杂度，因此除非必要，应该尽量使用**无状态**的 Job。

6）**Listener**：Quartz 拥有完善的事件和监听体系，大部分组件都拥有事件，如：JobListener 监听任务执行前事件、任务执行后事件；TriggerListener 监听触发器触发前事件、触发后事件；TriggerListener 监听调度器开始事件、关闭事件等等，可以注册相应的监听器处理感兴趣的事件。

# 2. 调度示例

使用 Quartz 进行任务调度：

```
package org.quartz.examples;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuartzTest implements Job {

    /**
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     */
    public QuartzTest() {
    }

    /**
     * 该方法实现需要执行的任务
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 从 context 中获取 instName, groupName 以及 dataMap
        String instName = context.getJobDetail().getKey().getName();
        String groupName = context.getJobDetail().getKey().getGroup();
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        // 从 dataMap 中获取 myDescription, myValue 以及 myArray
        String myDescription = dataMap.getString("myDescription");
        int myValue = dataMap.getInt("myValue");
        List<String> myArray = (List<String>) dataMap.get("myArray");
        System.out.println("---> Instance = " + instName + ", group = " + groupName
                + ", description = " + myDescription + ", value =" + myValue
                + ", array item[0] = " + myArray.get(0));
        System.out.println("Runtime: " + new Date().toString() + " <---");
    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 通过 schedulerFactory 获取一个调度器
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        // 创建 jobDetail 实例，绑定 Job 实现类
        // 指明 job 的名称，所在组的名称，以及绑定 job 类
        JobDetail job = JobBuilder.newJob(QuartzTest.class).withIdentity("job1", "group1").build();

        // 定义调度触发规则

        // SimpleTrigger，从当前时间的下 1 秒开始，每隔 1 秒执行 1 次，重复执行 2 次
        /*Trigger trigger = TriggerBuilder.newTrigger()
                // 指明 trigger 的 name 和 group
                .withIdentity("trigger1", "group1")
                // 从当前时间的下 1 秒开始执行，默认为立即开始执行（.startNow()）
                .startAt(DateBuilder.evenSecondDate(new Date()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1) // 每隔 1 秒执行 1 次
                        .withRepeatCount(2)) // 重复执行 2 次，一共执行 3 次
                .build();*/


        // corn 表达式，先立即执行一次，然后每隔 5 秒执行 1 次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                .build();

        // 初始化参数传递到 job
        job.getJobDataMap().put("myDescription", "Hello Quartz");
        job.getJobDataMap().put("myValue", 1990);
        List<String> list = new ArrayList<>();
        list.add("firstItem");
        job.getJobDataMap().put("myArray", list);

        // 把作业和触发器注册到任务调度中
        sched.scheduleJob(job, trigger);

        // 启动计划程序（实际上直到调度器已经启动才会开始运行）
        sched.start();

        // 等待 10 秒，使我们的 job 有机会执行
        Thread.sleep(10000);

        // 等待作业执行完成时才关闭调度器
        sched.shutdown(true);
    }
}
```

运行结果（设置了 sleep 10 秒，故在 0 秒调度一次，5 秒调度一次， 10 秒调度最后一次）：

```
---> Instance = job1, group = group1, description = Hello Quartz, value =1990, array item[0] = firstItem
Runtime: Wed Apr 19 11:24:15 CST 2017 <---
---> Instance = job1, group = group1, description = Hello Quartz, value =1990, array item[0] = firstItem
Runtime: Wed Apr 19 11:24:20 CST 2017 <---
---> Instance = job1, group = group1, description = Hello Quartz, value =1990, array item[0] = firstItem
Runtime: Wed Apr 19 11:24:25 CST 2017 <---
```

# 3. cronExpression 表达式

格式：`[秒] [分] [时] [每月的第几日] [月] [每周的第几日] [年]`

| 字段名       | 必须的 | 允许值           | 允许的特殊字符              |
| :----------- | :----- | :--------------- | :-------------------------- |
| Seconds      | YES    | 0-59             | `,` `-` `*` `/`             |
| Minutes      | YES    | 0-59             | `,` `-` `*` `/`             |
| Hours        | YES    | 0-23             | `,` `-` `*` `/`             |
| Day of month | YES    | 1-31             | `,` `-` `*` `?` `/` `L` `W` |
| Month        | YES    | 1-12 or JAN-DEC  | `,` `-` `*` `/`             |
| Day of week  | YES    | 1-7 or SUN-SAT   | `,` `-` `*` `?` `/` `L` `#` |
| Year         | NO     | empty, 1970-2099 | `,` `-` `*` `/`             |

特殊字符说明：

| 字符      | 含义                                                         |
| :-------- | :----------------------------------------------------------- |
| `*`       | 用于 `指定字段中的所有值`。比如：`*` 在分钟中表示 `每一分钟`。 |
| `?`       | 用于 `指定日期中的某一天`，或是 `星期中的某一个星期`。       |
| `-`       | 用于 `指定范围`。比如：`10-12` 在小时中表示 `10 点，11 点，12 点`。 |
| `,`       | 用于 `指定额外的值`。比如：`MON,WED,FRI` 在日期中表示 `星期一, 星期三, 星期五`。 |
| `/`       | 用于 `指定增量`。比如：`0/15` 在秒中表示 `0 秒, 15 秒, 30 秒, 45 秒`。`5/15` 在秒中表示 `5 秒，20 秒，35 秒，50 秒`。 |
| `L`       | 在两个字段中拥有不同的含义。比如：`L` 在日期（Day of month）表示 `某月的最后一天`。在星期（Day of week）只表示 `7` 或 `SAT`。但是，`值L` 在星期(Day of week)中表示 `某月的最后一个星期几`。 比如：`6L` 表示 `某月的最后一个星期五`。也可以在日期(Day of month)中指定一个偏移量(从该月的最后一天开始).比如：`L-3` 表示 `某月的倒数第三天`。 |
| `W`       | 用于指定工作日（星期一到星期五）比如：`15W` 在日期中表示 `到 15 号的最近一个工作日`。如果第十五号是周六, 那么触发器的触发在 `第十四号星期五`。如果第十五号是星期日，触发器的触发在 `第十六号周一`。如果第十五是星期二，那么它就会工作开始在 `第十五号周二`。然而，如果指定 `1W` 并且第一号是星期六，那么触发器的触发在第三号周一，因为它不会 "jump" 过一个月的日子的边界。 |
| `L`和 `W` | 可以在日期（day-of-month）合使用，表示 `月份的最后一个工作日`。 |
| `#`       | 用于 `指定月份中的第几天`。比如：`6#3` 表示 `月份的第三个星期五`（day 6 = Friday and "#3" = the 3rd one in the month）。其它的有，`2#1` 表示 `月份第一个星期一`。`4#5` 表示 `月份第五个星期三`。注意: 如果只是指定 `#5`，则触发器在月份中**不会**触发。 |

注意：字符不区分大小写，`MON` 和 `mon` 相同。

## 3.1 cronExpression 示例

| 表达式                     | 含义                                                         |
| :------------------------- | :----------------------------------------------------------- |
| `0 0 12 * * ?`             | 每天中午 12 点                                               |
| `0 15 10 ? * *`            | 每天上午 10 点 15 分                                         |
| `0 15 10 * * ?`            | 每天上午 10 点 15 分                                         |
| `0 15 10 * * ? *`          | 每天上午 10 点 15 分                                         |
| `0 15 10 * * ? 2005`       | 在 2005 年里的每天上午 10 点 15 分                           |
| `0 * 14 * * ?`             | 每天下午 2 点到下午 2 点 59 分的每一分钟                     |
| `0 0/5 14 * * ?`           | 每天下午 2 点到 2 点 55 分每隔 5 分钟                        |
| `0 0/5 14,18 * * ?`        | 每天下午 2 点到 2 点 55 分, 下午 6 点到 6 点 55 分, 每隔 5 分钟 |
| `0 0-5 14 * * ?`           | 每天下午 2 点到 2 点 5 分的每一分钟                          |
| `0 10,44 14 ? 3 WED`       | 3 月每周三的下午 2 点 10 分和下午 2 点 44 分                 |
| `0 15 10 ? * MON-FRI`      | 每周一到周五的上午 10 点 15 分                               |
| `0 15 10 15 * ?`           | 每月 15 号的上午 10 点 15 分                                 |
| `0 15 10 L * ?`            | 每月最后一天的上午 10 点 15 分                               |
| `0 15 10 L-2 * ?`          | 每月最后两天的上午10点15分                                   |
| `0 15 10 ? * 6L`           | 每月的最后一个星期五的上午 10 点 15 分                       |
| `0 15 10 ? * 6L 2002-2005` | 2002 年到 2005 年每个月的最后一个星期五的上午 10 点 15 分    |
| `0 15 10 ? * 6#3`          | 每月的第三个星期五的上午 10 点 15 分                         |
| `0 0 12 1/5 * ?`           | 每月的 1 号开始每隔 5 天的中午 12 点                         |
| `0 11 11 11 11 ?`          | 每年 11 月 11 号上午 11 点 11 分                             |

# 4. Listener 示例

监听器在运行时将其注册到调度程序中，并且必须给出一个名称（或者，他们必须通过他们的 `getName()` 来宣传自己的名称）。

## 4.1 TriggerListener 和 JobListener 示例

侦听器与调度程序的 ListenerManager 一起注册，并且描述了监听器想要接收事件的作业/触发器的 Matcher。

1）注册对特定作业的 JobListener：

```
sched.getListenerManager().addJobListener(new MyJobListener(), KeyMatcher.keyEquals(new JobKey("job1", "group1")));
```

2）注册对特定组的所有作业的 JobListener：

```
sched.getListenerManager().addJobListener(new MyJobListener(), GroupMatcher.jobGroupEquals("group1"));
```

3）注册对两个特定组的所有作业的 JobListener：

```
sched.getListenerManager().addJobListener(new MyJobListener(), OrMatcher.or(GroupMatcher.jobGroupEquals("group1"), GroupMatcher.jobGroupEquals("group2")));
```

4）注册一个对所有作业的 JobListener：

```
sched.getListenerManager().addJobListener(new MyJobListener(), EverythingMatcher.allJobs());
```

JobListener 实现类：

```
package org.quartz.examples;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

public class MyJobListener implements JobListener {

    @Override
    public String getName() {
        return "MyJobListener"; // 一定要设置名称
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            try {
                // 立即关闭调度器
                context.getScheduler().shutdown();
                System.out.println("Error occurs when executing jobs, shut down the scheduler.");
                // 给管理员发送邮件...
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
}
```

Job 实现类：

```
package org.quartz.examples;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuartzTest implements Job {

    /**
     * Quartz requires a public empty constructor so that the
     * scheduler can instantiate the class whenever it needs.
     */
    public QuartzTest() {
    }

    /**
     * 该方法实现需要执行的任务
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 故意运行异常，观察监听器是否正常工作
        int i = 1/0;
        
        // 从 context 中获取 instName, groupName 以及 dataMap
        String instName = context.getJobDetail().getKey().getName();
        String groupName = context.getJobDetail().getKey().getGroup();
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        // 从 dataMap 中获取 myDescription, myValue 以及 myArray
        String myDescription = dataMap.getString("myDescription");
        int myValue = dataMap.getInt("myValue");
        List<String> myArray = (List<String>) dataMap.get("myArray");
        System.out.println("---> Instance = " + instName + ", group = " + groupName
                + ", description = " + myDescription + ", value =" + myValue
                + ", array item[0] = " + myArray.get(0));
        System.out.println("Runtime: " + new Date().toString() + " <---");
    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 通过 schedulerFactory 获取一个调度器
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        // 创建 jobDetail 实例，绑定 Job 实现类
        // 指明 job 的名称，所在组的名称，以及绑定 job 类
        JobDetail job = JobBuilder.newJob(QuartzTest.class).withIdentity("job1", "group1").build();

        // 定义调度触发规则

        // SimpleTrigger，从当前时间的下 1 秒开始，每隔 1 秒执行 1 次，重复执行 2 次
        /*Trigger trigger = TriggerBuilder.newTrigger()
                // 指明 trigger 的 name 和 group
                .withIdentity("trigger1", "group1")
                // 从当前时间的下 1 秒开始执行，默认为立即开始执行（.startNow()）
                .startAt(DateBuilder.evenSecondDate(new Date()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1) // 每隔 1 秒执行 1 次
                        .withRepeatCount(2)) // 重复执行 2 次，一共执行 3 次
                .build();*/


        // corn 表达式，先立即执行 1 次，然后每隔 5 秒执行 1 次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                .build();

        // 初始化参数传递到 job
        job.getJobDataMap().put("myDescription", "Hello Quartz");
        job.getJobDataMap().put("myValue", 1990);
        List<String> list = new ArrayList<>();
        list.add("firstItem");
        job.getJobDataMap().put("myArray", list);

        // 注册对特定作业的监听器
        sched.getListenerManager().addJobListener(new MyJobListener(), KeyMatcher.keyEquals(new JobKey("job1", "group1")));

        // 把作业和触发器注册到任务调度中
        sched.scheduleJob(job, trigger);

        // 启动计划程序（实际上直到调度器已经启动才会开始运行）
        sched.start();

        // 等待 10 秒，使我们的 job 有机会执行
        Thread.sleep(10000);

        // 等待作业执行完成时才关闭调度器
        sched.shutdown(true);
    }
}
```

运行结果：

```
[ERROR] 19 四月 11:54:35.361 上午 DefaultQuartzScheduler_Worker-1 [org.quartz.core.JobRunShell]
Job group1.job1 threw an unhandled Exception: 

java.lang.ArithmeticException: / by zero
    at org.quartz.examples.QuartzTest.execute(QuartzTest.java:27)
    at org.quartz.core.JobRunShell.run(JobRunShell.java:202)
    at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)
[ERROR] 19 四月 11:54:35.361 上午 DefaultQuartzScheduler_Worker-1 [org.quartz.core.ErrorLogger]
Job (group1.job1 threw an exception.

org.quartz.SchedulerException: Job threw an unhandled exception. [See nested exception: java.lang.ArithmeticException: / by zero]
    at org.quartz.core.JobRunShell.run(JobRunShell.java:213)
    at org.quartz.simpl.SimpleThreadPool$WorkerThread.run(SimpleThreadPool.java:573)
Caused by: java.lang.ArithmeticException: / by zero
    at org.quartz.examples.QuartzTest.execute(QuartzTest.java:27)
    at org.quartz.core.JobRunShell.run(JobRunShell.java:202)
    ... 1 more

Error occurs when executing jobs, shut down the scheduler.
```

...注册 TriggerListener 的工作原理相同。

## 4.2 SchedulerListener 示例

SchedulerListener 在调度程序的 SchedulerListener 中注册。SchedulerListener 几乎可以实现任何实现 `org.quartz.SchedulerListener` 接口的对象。

注册对添加调度器时的 SchedulerListener：

```
scheduler.getListenerManager().addSchedulerListener(mySchedListener);
```

注册对删除调度器时的 `SchedulerListener`：

```
scheduler.getListenerManager().removeSchedulerListener(mySchedListener);
```

# 5. 参考

- [Quartz Quick Start Guide](http://www.quartz-scheduler.org/documentation/quartz-2.2.x/quick-start.html)
- [Quartz 入门详解](http://www.importnew.com/22890.html)
- [几种任务调度的 Java 实现方法与比较](https://www.ibm.com/developerworks/cn/java/j-lo-taskschedule/)