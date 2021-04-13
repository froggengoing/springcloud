## 安装与命令

1. erlang[下载地址](https://www.erlang-solutions.com/resources/download.html) ，并完成安装

2. 下载[RabbitMq](https://www.rabbitmq.com/install-windows.html#installer) 并完成安装。

3. 进入`rabbitmq_server-3.8.4\sbin`，目录下

   ```shell
   ##查看rabbitmqctl提供的命令
   .\rabbitmqctl.bat help
   ##查看rabbitmq-diagnostics提供的命令
   .\rabbitmq-diagnostics.bat help
   
   ```

   

4. 启动插件

   ```shell
   .\rabbitmq-plugins.bat enable rabbitmq_management
   Enabling plugins on node rabbit@mh-fly:
   rabbitmq_management
   The following plugins have been configured:
     rabbitmq_management
     rabbitmq_management_agent
     rabbitmq_web_dispatch
   Applying plugin configuration to rabbit@mh-fly...
   The following plugins have been enabled:
     rabbitmq_management
     rabbitmq_management_agent
     rabbitmq_web_dispatch
   
   set 3 plugins.
   Offline change; changes will take effect at broker restart.
   ```

   

5. 停止与启动服务服务

   因为意见rabbitmq配置为服务，开机启动了

   ```shell
   ##必须管理员模式启动
   PS C:\Windows\system32> net stop RabbitMQ
   RabbitMQ 服务正在停止........
   RabbitMQ 服务已成功停止。
   PS C:\Windows\system32> net start RabbitMQ
   RabbitMQ 服务正在启动 .
   RabbitMQ 服务已经启动成功。
   ```

   

6. 添加用户

   ```shell
   .\rabbitmqctl.bat list_users
   Listing users ...
   user    tags
   guest   [administrator]
   .\rabbitmqctl.bat add_user admin admin
   Adding user "admin" ...
   .\rabbitmqctl.bat list_users
   Listing users ...
   user    tags
   admin   []
   guest   [administrator]
   ```

   

7. 添加超级管理员权限

   > rabbitmq用户角色可分为五类：超级管理员, 监控者, 策略制定者, 普通管理者以及其他。
   >
   > (1) 超级管理员(administrator)
   >
   > 可登陆管理控制台(启用management plugin的情况下)，可查看所有的信息，并且可以对用户，策略(policy)进行操作。
   >
   > (2) 监控者(monitoring)
   >
   > 可登陆管理控制台(启用management plugin的情况下)，同时可以查看rabbitmq节点的相关信息(进程数，内存使用情况，磁盘使用情况等) 
   >
   > (3) 策略制定者(policymaker)
   >
   > 可登陆管理控制台(启用management plugin的情况下), 同时可以对policy进行管理。
   >
   > (4) 普通管理者(management)
   >
   > 仅可登陆管理控制台(启用management plugin的情况下)，无法看到节点信息，也无法对策略进行管理。
   >
   > (5) 其他的
   >
   > 无法登陆管理控制台，通常就是普通的生产者和消费者。

   ```shell
   .\rabbitmqctl.bat set_user_tags admin administrator
   Setting tags for user "admin" to [administrator] ...
   .\rabbitmqctl.bat list_users
   Listing users ...
   user    tags
   admin   [administrator]
   guest   [administrator]
   ```

   

8. 其他 命令

   ```shell
   #添加多角色
   .\rabbitmqctl.bat  set_user_tags  username tag1 tag2 
   #修改密码
   .\rabbitmqctl.bat change_password userName newPassword
   #删除用户
   .\rabbitmqctl.bat delete_user username
   #查看状态
   .\rabbitmqctl.bat status
   #启动
    .\rabbitmqctl.bat start_app
    #停止
     .\rabbitmqctl.bat stop
   ```

   

9. 登陆管理页面

   ```shell
   ##必须先安装插件
   .\rabbitmq-plugins.bat enable rabbitmq_management
   <a>http://localhost:15672/#/</a>
   ```

   

10. 设置权限

    > ```shell
    > 权限相关命令为：
    > 
    > (1) 设置用户权限
    > 
    > .\rabbitmqctl.bat   set_permissions  -p  VHostPath  User  ConfP  WriteP  ReadP
    > 
    > (2) 查看(指定hostpath)所有用户的权限信息
    > 
    > .\rabbitmqctl.bat   list_permissions  [-p  VHostPath]
    > 
    > (3) 查看指定用户的权限信息
    > 
    > .\rabbitmqctl.bat   list_user_permissions  User
    > 
    > (4)  清除用户的权限信息
    > 
    > .\rabbitmqctl.bat   clear_permissions  [-p VHostPath]  User
    > ```
    >
    > 

* [官方指令网址](https://www.rabbitmq.com/rabbitmqctl.8.html) 

11. 消息轨迹插件

    ```shell
    
    ```

    

12. 

### 集群 

```shell
##查看状态
rabbitmqctl.bat status
##关闭rabbitmq
rabbitmqctl.bat stop
##指定端口启动
rabbitmqctl.bat start_app
```



## 官方文档

### 发布与订阅

先前的实例中，创建了一个`workqueue`。`woke queue`的设定是每一个task都只会发送给queue中的其中一个worker。在本文中，我们将会把一个消息发送给多个消费者，即“发布/订阅”。

本文将创建一个日志系统，由两个程序组成，第一个是发送日志信息，，第二个是接收和打印信息。

在日志系统中，运行多个相同的接收程序都会接收到相同的信息。这样，我们可以让其中一个接收者将日志存储到硬盘中，同时其他接收者打印日志到前端。

本质上，发布日志信息会广播通知所有接收者。

#### Exchanges

先前的实例，我们通过Queue接收和发送信息。下面将描述完整的Rabbit消息模型。

先前的模型：

> producer : 发送消息的用户程序
>
> queue： 存储信息的缓存池
>
> consumer： 接收消息的用户程序

RabbitMq消息模型的核心是producer生产者不会直接发送一条消息给一个Queue。通常生产者不知道消息将会发送给你一个Queue。

生产者只是将消息发送给一个exchange。一方面，exchange接收来自生产者的消息另一方面push（推送）至queue中。exchange必须知道它究竟要怎么处理它接收到的消息。是追加至特定的队列？还是追加至多个队列？还是应该将消息丢弃?这些规则通过exchange type定义：

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125443.png)

一些可用的**exchange type：direct, topic, headers 和fanout**。我们将关注最后一个--fanout。下面创建一个fanout的exchange 类型：

```java
channel.exchangeDeclare("logs", "fanout");
```

fanout类型时非常简单的。从名字上你可以猜到它的含义，它会广播所有接收都的信息给所有它知道的队列。这也正是我们日志系统所需要的。

> 查看exchange
>
> ```shell
> sudo rabbitmqctl list_exchanges
> ```
>
> 这个列表中有`amq.*`和默认（未命名）的exchange。这些都是默认创建的，现在我们暂时不需要关注。
>
> 匿名exchange（nameless）
>
> 先前的文章中，我们并不知道exchange。但是仍然可以发送消息给队列。这是因为我们使用一个默认的exchange。我们通过`“”`空字符串定义：
>
> ```java
> channel.basicPublish("", "hello", null, message.getBytes());
> ```
>
> 第一个参数就是exchange的名字。空字符串表示默认或匿名的exchange：消息通过指定的`routingKey`路由至指定的队列。

现在我们将使用命名的exchange：

```java
channel.basicPublish( "logs", "", null, message.getBytes());
```



#### 临时队列

前面我们所使用的队列有特定的名字（“hello”和“task_queue”）。能够给队列命名是非常重要的，因为我们需要将worker指向同一个队列。生产者与消费者通过指定队列名称来共享队列。

但日志系统并不是这种情况。我们需要接受到所有消息，而不是其中一部分。我们也只对当前正在发送的消息感兴趣，而对旧消息不感兴趣。为了解决这个问题，我们需要两件事。

首先，无论何时连接到Rabbit，我们都需要一个全新的空队列。为此，我们可以创建一个具有随机名称的队列，或者更好的选择是-让服务器为我们选择一个随机队列名称。

其次，一旦我们断开了使用者的连接，队列将被自动删除。

在Java客户端中，当我们不向queueDeclare（）提供任何参数时，我们将创建一个非持久的，排他的，自动删除以及自动命名的队列：

```java
String queueName = channel.queueDeclare().getQueue();
```



更多exclusive标志的信息以及其他queue属性，请查阅[guide on queues](https://www.rabbitmq.com/queues.html)。

此时，queueName包含一个随机队列名称。例如，它可能看起来像`amq.gen-JzTY20BRgKO-HjmUJj0wLg`。

#### Bindings

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125444.png)

我们已经创建了一个exchange和queue。我们现在需要让exchange发送消息到我们的 队列中。交换和队列之间的关系称为绑定 ，代码如下：

```
channel.queueBind(queueName, "logs", "");
```

现在名为logs的exchange将会追加消息到queue中。

列出bingdings

```shell
rabbitmqctl list_bindings
```

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125445.png)

生产者的程序，与前面的例子并没有太大的区别。最重要的一点是，现在会将消息发送值logs echange中而不是匿名的exchange。发送消息时需要提供一个routingKey，但这个值会被fanout exchange忽略。完整代码如下：

```java
public class EmitLog {

  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message = argv.length < 1 ? "info: Hello World!" :
                            String.join(" ", argv);

        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
    }
  }
}
```

上述代码中，在建立连接后会声明exchange，这一步是必须的，因为禁止发布消息到不存在的exchange上。

如果没有queue绑定至exchange，将丢失消息。如果没有消费者监听队列，可以安全的丢弃消息。

接收者代码：

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogs {
  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    String queueName = channel.queueDeclare().getQueue();
    channel.queueBind(queueName, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
    };
    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
  }
}
```

### Routing

前面是实例，构建了一个简单的日志系统。我们可以广播日志消费给多个接收者。

本节，我们将添加一个新的特性：只订阅消息的其中一个子集。比如，我们可以存储错误的消息到硬盘中，但是另一个正常则正常打印所有的信息到前端。

#### Bindings

前面的例子中，我们已经创建了bindings。就像调用下面这行代码

```java
channel.queueBind(queueName,EXCHANGE_NAME,"")
```

bindings表示exchange和queue之间的关系。可以简单的解读为：queue对exchange感兴趣的消息类型 

bindings可以使用`routingKey`参数。为了避免与basic_publish参数混淆，我们将其称为`binding key`。下面的代码将bindings 一个key。

```java
channel.queueBind(queueName, EXCHANGE_NAME, "black");
```

这意味着bindings key 依赖于exchange类型。前面所使用的`fanout`类型会忽略这个值。

#### Direct exchange

上一教程中的日志系统将所有消息广播给所有消费者。我们想要扩展它以允许根据消息的严重性过滤消息。例如，我们可能希望仅将严重错误日志消息写入磁盘，而不浪费硬盘空间在warning和info的日志消息上。

`fanout`类型并不能提供这种灵活性，它只能简单的广播。

本节，我们将使用`direct exchange`类型。`direct`的路由算法也很简单，一个消息只有当queue的binding key与routing key相匹配，才会推送该消息至queue中。

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125446.png)

在上图中中，我们可以看到direct exchange类型的X有两个queue队列绑定到它上面。第一个queue的binding key为`orange`，第二个队列的binding  key是`black`和`green`。

这意味着发布到exchange的消息，如果routing key为organge将被路由至Q1，而routing key为black和green的将被路由至Q2.所有其他消息将被丢弃。

#### Multiple bindings

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125447.png)

用相同的bingings key绑定到多个队列是完全合法的。在上图中，我们将x和Q1、Q2都是使用black作为binding key。这种情况，就像fanout类型一样，会广播所有的消息到匹配的队列中。一个routing key为black的消息将被分发至Q1和Q2中。

#### Emitting logs

使用direct exchange来代替fanout exchange，我们可以使用日志等级作为routing key。这样消费者可以选择接受他们所期望的日志等级的消息。

创建direct exchange：

```java
channel.exchangeDeclare(EXCHANGE_NAME, "direct");
```

发布消息，注意routing key为日志等级

```java
channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
```

#### Subscribing

接受者代码与前面的例子大致相同，唯一的区别是为每个感兴趣的日志等级创建一个新的binding

```java
String queueName = channel.queueDeclare().getQueue();

for(String severity : argv){
  channel.queueBind(queueName, EXCHANGE_NAME, severity);
}
```

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125448.png)



#### 生产者代码：

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {

  private static final String EXCHANGE_NAME = "direct_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String severity = getSeverity(argv);
        String message = getMessage(argv);

        channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
    }
  }
  //..
}

```



#### 接受者代码

```java
import com.rabbitmq.client.*;

public class ReceiveLogsDirect {

  private static final String EXCHANGE_NAME = "direct_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
    String queueName = channel.queueDeclare().getQueue();

    if (argv.length < 1) {
        System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
        System.exit(1);
    }

    for (String severity : argv) {
        channel.queueBind(queueName, EXCHANGE_NAME, severity);
    }
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" +
            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };
    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
  }
}
```

### Topics

前面实例中我们加强了日志系统。没有使用fanout exchange，而是使用direct exchange，这使我们可以选择性的接收日志。

虽然使用direct exchange加强了我们的系统，但是仍有一些限制：不能基于多个标准来路由。

在我们的日志系统中，我们可能希望订阅不仅仅是基于严重程度的日志，还希望基于发出日志的来源。比如unix系统工具syslog，就是基于严重等级（info/warn/crit）和设备（auth/cron/kern）路由的

这将带来很多的灵活性。我们可能只需要监听来自“cron”的错误消息，以及来自“kern”的所有日志消息。

实现这些需求，我们需要了解关于“Topic”更多信息。

#### Topic exchange

发布到topic exchange的消息不能有任意的routing key，它必须是以点为分隔符的单词组合。可以是任意的单词，但通常是与消息特性相关的单词。比如`"stock.usd.nyse", "nyse.vmw", "quick.orange.rabbit".` 单词上线为255个字节。

binding key必须是同样的格式。topic exchange的处理逻辑与direct exchange相似，发送至特定的routing key的消息会被转发值所有与bindings key 匹配的队列queue中。需要注意的两点是：

* `*`星号能匹配一个单词
* `#`井号能匹配0个或多个单词

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125449.png)

在此示例中，我们将发送所有描述动物的消息。将使用包含三个词（两个点）的routing key发送消息。`routing key`中的第一个单词将描述一个速度，第二个是颜色，第三个是物种。`"<celerity>.<colour>.<species>".`

我们创建了三个binding：Q1与`“ * .orange.*”`绑定，Q2与`“ *.*.rabbit”`和`“ lazy.＃”`绑定。可以 理解为

* Q1对所有orange类型的动物感兴趣。
* Q2对rabbit以及所有lazy的动物感兴趣。

routing key为` "quick.orange.rabbit" `的消息将被推送给Q1和Q2。 `"lazy.orange.elephant"`同样也会推送给Q1和Q2。而`"quick.orange.fox"` 只会推送给Q1。 `"lazy.brown.fox"和"lazy.pink.rabbit"` 只会推送给Q2。 `"quick.brown.fox"`将被丢弃。

如果routing key为1个单词或者4个单词，比如 `"orange" or "quick.orange.male.rabbit"`，由于不匹配任意的bingdings所以也会被丢弃。

但是`"lazy.orange.male.rabbit"`由于匹配 `"lazy.#".`这种规则将会被推送给Q2.

#### 生产者

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String routingKey = getRouting(argv);
        String message = getMessage(argv);

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
    }
  }
  //..
}
```



#### 消费者

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogsTopic {

  private static final String EXCHANGE_NAME = "topic_logs";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");
    String queueName = channel.queueDeclare().getQueue();

    if (argv.length < 1) {
        System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
        System.exit(1);
    }

    for (String bindingKey : argv) {
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
    }

    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" +
            delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    };
    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
  }
}
```







### Remote procedure call (RPC)

前面的实例中，我们使用workqueue将耗时的任务分配给多个worker。

但如果我们需要在远程的电脑上执行一个方法并等待返回结果呢？这种模式通过称为远程过程调用（RPC）

在本节实例中，我们准备使用RabbitMq来构建RPC系统：一个客户端和一个可扩展的服务器。由于我们没有值得分配的耗时任务，因此我们将创建一个虚拟RPC服务，该服务返回斐波那契数。

### Client interface

为了说明RPC服务是如果工作的，本节将创建一个简单的客户端类。它会暴露一个call方法，该方法会发送一个rpc请求并阻塞直到返回结果。

```java
FibonacciRpcClient fibonacciRpc = new FibonacciRpcClient();
String result = fibonacciRpc.call("4");
System.out.println( "fib(4) is " + result);
```

> 注：
>
> 尽管RPC是计算中非常普遍的模式，但它经常受到批评。当程序员不知道函数调用是本地的还是缓慢的RPC时，就会出现问题。这样的混乱会导致系统难以预测，并给调试增加了不必要的复杂性。滥用RPC服务会导致代码杂乱且难以维护，而不是简化代码。
>
> 牢记这一点，请考虑以下建议：
>
> * 确保能 明细看出哪个函数的调用是本地的，哪个是远程的。
> * 记录您的系统。明确组件之间的依赖关系。
> * 处理异常情况。 RPC服务器长时间关闭后，客户端应如何反应？
>
> 如有疑问，请避免使用RPC。如果你可以的话，您应该使用异步管道-而不是类似RPC的阻塞，结果被异步推送到下一个计算阶段。

#### Callback queue

一般来说，使用RabbitMq来执行RPC是非常简单的。客户端发送一个请求消息然后服务器回应一个响应消息。为了能接收响应消息，我们需要在请求中添加一个`callback queue`。我们可以使用默认队列（在Java客户端独有的）

```java
callbackQueueName = channel.queueDeclare().getQueue();

BasicProperties props = new BasicProperties
                            .Builder()
                            .replyTo(callbackQueueName)
                            .build();

channel.basicPublish("", "rpc_queue", props, message.getBytes());

// ... then code to read a response message from the callback_queue ...
```

> #### Message properties
>
> AMQP 0-9-1协议预定义了消息附带的14个属性集。除以下属性外，大多数属性很少使用：
>
> * deliveryMode: 将消息标记为持久性（值为2）或临时消息（任何其他值）。
> * contentType:用于描述编码的mime类型。例如，对于常用的JSON编码，将此属性设置为：`application/json`
> * replyTo: 通常用于命名回调队列。
> * correlationId:有助于将RPC响应与请求相关联。

#### Correlation Id

在上面介绍的方法中，我们建议为每个RPC请求创建一个回调队列。那是相当低效的，但是幸运的是有更好的方法-我们可以为每个客户端创建一个回调队列。

这引起了一个新问题，在该队列中收到响应后，尚不清楚响应属于哪个请求。这时就应该使用correlationId属性。我们将为每个请求设置一个唯一值。然后，当我们在回调队列中收到消息时，我们将查看该属性，并基于此属性将响应与请求进行匹配。如果我们看到一个未知的correlationId值，我们可以放心地丢弃该消息-它不属于我们的请求。

您可能会问，为什么我们应该忽略回调队列中的未知消息，而不是因错误而报错？这是由于服务器端可能出现竞争状况。尽管可能性不大，但RPC服务器可能会在向我们发送结果之后但在发送请求的确认消息之前宕机。如果发生这种情况，重新启动的RPC服务器将再次处理该请求。这就是为什么在客户端上我们必须妥善处理重复的响应，并且理想情况下RPC应该是幂等的。

#### Summary

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125450.png)

RPC工作流程：

* 对于RPC请求，客户端发送一条消息，该消息具有两个属性：replyTo（为该请求创建的匿名排他队列）和correlationId（为每个请求设置的唯一值）。
* 该请求被发送到rpc_queue队列。
* RPC worker（又名：服务器）正在等待该队列上的请求。出现请求时，它会使用replyTo字段中的队列来完成工作并将带有结果的消息发送回客户端。
* 客户端等待答复队列中的数据。出现消息时，它会检查correlationId属性。如果它与请求中的值匹配，则将响应返回给应用程序。

#### Putting it all together

斐波那契：

```java
private static int fib(int n) {
    if (n == 0) return 0;
    if (n == 1) return 1;
    return fib(n-1) + fib(n-2);
}
```

我们声明我们的斐波那契函数。假定传参均为正整数。（不要指望这种方法适用于大的整数，它可能是最慢的递归实现）。

#### server

```java
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);

                    System.out.println(" [.] fib(" + message + ")");
                    response += fib(n);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
```

服务器代码非常简单：

* 像往常一样，我们首先建立连接，通道并声明队列。
* 我们可能要运行多个服务器进程。为了将负载平均分配到多个服务器，我们需要在channel.basicQos中设置prefetchCount。
* 我们使用basicConsume访问队列，在队列中我们以对象（DeliverCallback）的形式提供回调，该回调将执行方法并将响应发送回去。

```java
public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) {
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = fibonacciRpc.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
```



客户端代码稍微复杂一些：

* 建立连接和channel
* call方法发出RPC请求
* 首先生成唯一的correlationId并保存。消费者回调将使用此值来匹配相应的响应。
* 创建一个专业的排他的队列来接收响应，并订阅该队列
* 我们发布具有两个属性的请求消息：replyTo和correlationId。
* 此时，可以等待到正确的响应到达。
* 由于消费者发送的处理请求是在单独的线程中进行的，因此在响应到达之前，我们将需要挂起主线程。使用BlockingQueue 是其中一种解决方法。在这里，我们使用容量为1的ArrayBlockingQueue，因为我们只需要等待一个响应即可。
* 消费者的工作很简单，对于每一个返回的响应消息，它都会检查correlationId是否为我们要寻找的消息。如果是这样，它将响应消息放入BlockingQueue。
* 同时，主线程正在等待响应，以将其从BlockingQueue中获取。
* 最后，我们将响应返回给用户。



个人理解：

* 服务器监听rabbitmq中的一个队列，此时作为消费者。
* 客户端发送一个消息（请求）至队列中，此时作为生产者。在发送请求的同时，创建一个临时队列用于接收响应消息。
* 服务器接收到请求并完成处理，将响应消息发送至客户端创建的临时队列中。此时服务器作为生产者。
* 客户端监听自己创建的临时队列，接收到服务器的响应



### [Publisher Confirms](https://www.rabbitmq.com/tutorials/tutorial-seven-java.html)





## 整合Springboot示例



### [rabbitmq basicReject / basicNack / basicRecover区别](https://blog.csdn.net/fly_leopard/article/details/102821776)

- ```html
  channel.basicReject(deliveryTag, true);
  ```

​    basic.reject方法拒绝deliveryTag对应的消息，第二个参数是否requeue，true则重新入队列，否则丢弃或者进入死信队列。

该方法reject后，该消费者还是会消费到该条被reject的消息。

- ```html
  channel.basicNack(deliveryTag, false, true);
  ```

​    basic.nack方法为不确认deliveryTag对应的消息，第二个参数是否应用于多消息，第三个参数是否requeue，与basic.reject区别就是同时支持多个消息，可以nack该消费者先前接收未ack的所有消息。nack后的消息也会被自己消费到。

- ```html
  channel.basicRecover(true);
  ```

​    basic.recover是否恢复消息到队列，参数是是否requeue，true则重新入队列，并且尽可能的将之前recover的消息投递给其他消费者消费，而不是自己再次消费。false则消息会重新被投递给自己。

> [官网](https://www.rabbitmq.com/confirms.html#consumer-nacks-requeue) ：It is possible to reject or requeue multiple messages at once using the basic.nack method. This is what differentiates it from basic.reject. It accepts an additional parameter, multiple.

### [RabbitMQ实现重试次数方法一-SpringRetry](https://www.jianshu.com/p/4904c609632f) 



### properties

```properties
# Comma-separated list of addresses to which the client should connect.
spring.rabbitmq.addresses= 
# Virtual host to use when connecting to the broker.
spring.rabbitmq.virtual-host= 
# Connection timeout. Set it to zero to wait forever.
spring.rabbitmq.connection-timeout= 
# Whether to create an AmqpAdmin bean.
spring.rabbitmq.dynamic=true 
# RabbitMQ host.
spring.rabbitmq.host=localhost 
# RabbitMQ port.
spring.rabbitmq.port=5672 
# Login to authenticate against the broker.
spring.rabbitmq.password=guest 
# Login user to authenticate to the broker.
spring.rabbitmq.username=guest 
# Whether to enable publisher confirms.
spring.rabbitmq.publisher-confirms=false 
# Whether to enable publisher returns.
spring.rabbitmq.publisher-returns=false 
# Requested heartbeat timeout; zero for none. If a duration suffix is not specified, seconds will be used.
spring.rabbitmq.requested-heartbeat= 
#########################################################################

# Duration to wait to obtain a channel if the cache size has been reached.
spring.rabbitmq.cache.channel.checkout-timeout= 
# Number of channels to retain in the cache.
spring.rabbitmq.cache.channel.size= 
# Connection factory cache mode.
spring.rabbitmq.cache.connection.mode=channel 
# Number of connections to cache.
spring.rabbitmq.cache.connection.size= 
#########################################################################

# Acknowledge mode of container.
spring.rabbitmq.listener.direct.acknowledge-mode= 
# Whether to start the container automatically on startup.
spring.rabbitmq.listener.direct.auto-startup=true 
# Number of consumers per queue.
spring.rabbitmq.listener.direct.consumers-per-queue= 
# Whether rejected deliveries are re-queued by default.
spring.rabbitmq.listener.direct.default-requeue-rejected= 
# How often idle container events should be published.
spring.rabbitmq.listener.direct.idle-event-interval= 
# Whether to fail if the queues declared by the container are not available on the broker.
spring.rabbitmq.listener.direct.missing-queues-fatal=false 
# Maximum number of unacknowledged messages that can be outstanding at each consumer.
spring.rabbitmq.listener.direct.prefetch= 
# Whether publishing retries are enabled.
spring.rabbitmq.listener.direct.retry.enabled=false 
# Duration between the first and second attempt to deliver a message.
spring.rabbitmq.listener.direct.retry.initial-interval=1000ms 
# Maximum number of attempts to deliver a message.
spring.rabbitmq.listener.direct.retry.max-attempts=3 
# Maximum duration between attempts.
spring.rabbitmq.listener.direct.retry.max-interval=10000ms 
# Multiplier to apply to the previous retry interval.
spring.rabbitmq.listener.direct.retry.multiplier=1 
# Whether retries are stateless or stateful.
spring.rabbitmq.listener.direct.retry.stateless=true 
# Acknowledge mode of container.
spring.rabbitmq.listener.simple.acknowledge-mode= 
# Whether to start the container automatically on startup.
spring.rabbitmq.listener.simple.auto-startup=true 
# Minimum number of listener invoker threads.
spring.rabbitmq.listener.simple.concurrency= 
# Whether rejected deliveries are re-queued by default.
spring.rabbitmq.listener.simple.default-requeue-rejected= 
# How often idle container events should be published.
spring.rabbitmq.listener.simple.idle-event-interval= 
# Maximum number of listener invoker threads.
spring.rabbitmq.listener.simple.max-concurrency= 
# Whether to fail if the queues declared by the container are not available on the broker and/or whether to stop the container if one or more queues are deleted at runtime.
spring.rabbitmq.listener.simple.missing-queues-fatal=true 
# Maximum number of unacknowledged messages that can be outstanding at each consumer.
spring.rabbitmq.listener.simple.prefetch= 
# Whether publishing retries are enabled.
spring.rabbitmq.listener.simple.retry.enabled=false 
# Duration between the first and second attempt to deliver a message.
spring.rabbitmq.listener.simple.retry.initial-interval=1000ms 
# Maximum number of attempts to deliver a message.
spring.rabbitmq.listener.simple.retry.max-attempts=3 
# Maximum duration between attempts.
spring.rabbitmq.listener.simple.retry.max-interval=10000ms 
# Multiplier to apply to the previous retry interval.
spring.rabbitmq.listener.simple.retry.multiplier=1 
# Whether retries are stateless or stateful.
spring.rabbitmq.listener.simple.retry.stateless=true 
# Number of messages to be processed between acks when the acknowledge mode is AUTO. If larger than prefetch, prefetch will be increased to this value.
spring.rabbitmq.listener.simple.transaction-size= 
# Listener container type.
spring.rabbitmq.listener.type=simple 
#########################################################################

# SSL algorithm to use. By default, configured by the Rabbit client library.
spring.rabbitmq.ssl.algorithm= 
# Whether to enable SSL support.
spring.rabbitmq.ssl.enabled=false 
# Path to the key store that holds the SSL certificate.
spring.rabbitmq.ssl.key-store= 
# Password used to access the key store.
spring.rabbitmq.ssl.key-store-password= 
# Key store type.
spring.rabbitmq.ssl.key-store-type=PKCS12 
# Trust store that holds SSL certificates.
spring.rabbitmq.ssl.trust-store= 
# Password used to access the trust store.
spring.rabbitmq.ssl.trust-store-password= 
# Trust store type.
spring.rabbitmq.ssl.trust-store-type=JKS 
# Whether to enable server side certificate validation.
spring.rabbitmq.ssl.validate-server-certificate=true 
# Whether to enable hostname verification.
spring.rabbitmq.ssl.verify-hostname=true 
# Name of the default queue to receive messages from when none is specified explicitly.
spring.rabbitmq.template.default-receive-queue= 
# Name of the default exchange to use for send operations.
spring.rabbitmq.template.exchange= 
# Whether to enable mandatory messages.
spring.rabbitmq.template.mandatory= 
# Timeout for `receive()` operations.
spring.rabbitmq.template.receive-timeout= 
# Timeout for `sendAndReceive()` operations.
spring.rabbitmq.template.reply-timeout= 
# Whether publishing retries are enabled.
spring.rabbitmq.template.retry.enabled=false 
# Duration between the first and second attempt to deliver a message.
spring.rabbitmq.template.retry.initial-interval=1000ms 
# Maximum number of attempts to deliver a message.
spring.rabbitmq.template.retry.max-attempts=3 
# Maximum duration between attempts.
spring.rabbitmq.template.retry.max-interval=10000ms 
# Multiplier to apply to the previous retry interval.
spring.rabbitmq.template.retry.multiplier=1 
# Value of a default routing key to use for send operations.
spring.rabbitmq.template.routing-key= 





spring.rabbitmq.listener.type=simple: 容器类型.simple或direct
 
spring.rabbitmq.listener.simple.auto-startup=true: 是否启动时自动启动容器
spring.rabbitmq.listener.simple.acknowledge-mode: 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
spring.rabbitmq.listener.simple.concurrency: 最小的消费者数量
spring.rabbitmq.listener.simple.max-concurrency: 最大的消费者数量
spring.rabbitmq.listener.simple.prefetch: 一个消费者最多可处理的nack消息数量，如果有事务的话，必须大于等于transaction数量.
spring.rabbitmq.listener.simple.transaction-size: 当ack模式为auto时，一个事务（ack间）处理的消息数量，最好是小于等于prefetch的数量.若大于prefetch， 则prefetch将增加到这个值
spring.rabbitmq.listener.simple.default-requeue-rejected: 决定被拒绝的消息是否重新入队；默认是true（与参数acknowledge-mode有关系）
spring.rabbitmq.listener.simple.missing-queues-fatal=true 若容器声明的队列在代理上不可用，是否失败； 或者运行时一个多多个队列被删除，是否停止容器
spring.rabbitmq.listener.simple.idle-event-interval: 发布空闲容器的时间间隔，单位毫秒
spring.rabbitmq.listener.simple.retry.enabled=false: 监听重试是否可用
spring.rabbitmq.listener.simple.retry.max-attempts=3: 最大重试次数
spring.rabbitmq.listener.simple.retry.max-interval=10000ms: 最大重试时间间隔
spring.rabbitmq.listener.simple.retry.initial-interval=1000ms:第一次和第二次尝试传递消息的时间间隔
spring.rabbitmq.listener.simple.retry.multiplier=1: 应用于上一重试间隔的乘数
spring.rabbitmq.listener.simple.retry.stateless=true: 重试时有状态or无状态
 
spring.rabbitmq.listener.direct.acknowledge-mode= ack模式
spring.rabbitmq.listener.direct.auto-startup=true 是否在启动时自动启动容器
spring.rabbitmq.listener.direct.consumers-per-queue= 每个队列消费者数量.
spring.rabbitmq.listener.direct.default-requeue-rejected= 默认是否将拒绝传送的消息重新入队.
spring.rabbitmq.listener.direct.idle-event-interval= 空闲容器事件发布时间间隔.
spring.rabbitmq.listener.direct.missing-queues-fatal=false若容器声明的队列在代理上不可用，是否失败.
spring.rabbitmq.listener.direct.prefetch= 每个消费者可最大处理的nack消息数量.
spring.rabbitmq.listener.direct.retry.enabled=false  是否启用发布重试机制.
spring.rabbitmq.listener.direct.retry.initial-interval=1000ms # Duration between the first and second attempt to deliver a message.
spring.rabbitmq.listener.direct.retry.max-attempts=3 # Maximum number of attempts to deliver a message.
spring.rabbitmq.listener.direct.retry.max-interval=10000ms # Maximum duration between attempts.
spring.rabbitmq.listener.direct.retry.multiplier=1 # Multiplier to apply to the previous retry interval.
spring.rabbitmq.listener.direct.retry.stateless=true # Whether retries are stateless or stateful.
```



### 资料

1. [RabbitMQ(三) RabbitMQ高级整合应用 ](https://www.cnblogs.com/niugang0920/p/13043708.html) 
2. [RabbitMQ笔记十三：使用@RabbitListener注解消费消息](https://www.jianshu.com/p/382d6f609697)
3. [@RabbitListener起作用的原理](https://blog.csdn.net/zidongxiangxi/article/details/100623548) 
4. [Springboot整合RabbitMQ](https://www.cnblogs.com/your-Name/p/10394620.html)
5. [AMQP协议详解与RABBITMQ，MQ消息队列的应用场景，如何避免消息丢失等消息队列常见问题](https://www.cnblogs.com/theRhyme/p/9578675.html) 
6. [Spring Boot系列——死信队列](https://www.cnblogs.com/bigdataZJ/p/springboot-deadletter-queue.html) 
7. [RabbitMQ整合Spring Booot【死信队列】](https://www.cnblogs.com/toov5/p/10288260.html)



## 笔记

### 持久性

RabbitMQ 支持**消息的持久化**，也就是数据写在磁盘上，为了数据安全考虑，我想大多数用户都会选择持久化。消息队列持久化包括3个部分：
（1）exchange 持久化，在声明时指定 durable => 1
（2）queue 持久化，在声明时指定 durable => 1
（3）消息持久化，在投递时指定 delivery_mode => 2（1 是非持久化）

如果 exchange 和 queue 都是持久化的，那么它们之间的 binding 也是持久化的。如果exchange 和 queue 两者之间有一个持久化，一个非持久化，就不允许建立绑定（这句话说的有问题，实际情况中经常出现非持久化的 queue 绑定到持久化 exchange 上的情况。个人觉得其意思应该是：当两者并非都是持久化时，其对应的 binding 就无法得到恢复）。

即使设置了持久化，也不能百分百保证消息不会丢失。有很小的概率在 RabbitMQ 接受到消息后，还没来得及写到磁盘，就发生重启了。另外，RabbitMQ 也不会对每一个消息执行 fsync(2)，消息可能仅仅写入到缓存，还没来得及 flush 到硬件存储。因此 RabbitMQ 的持久性设置并非足够安全，对于普通的工作队列也许够用了。如果需要加强的安全保证，可以把发布消息的代码封装在事务里。

### [消息的存储](https://my.oschina.net/hncscwc/blog/182083)

#### 大概原理：

所有队列中的消息都以append的方式写到一个文件中，当这个文件的大小超过指定的限制大小后，关闭这个文件再创建一个新的文件供消息的写入。文件名（*.rdq）从0开始然后依次累加。当某个消息被删除时，并不立即从文件中删除相关信息，而是做一些记录，当垃圾数据达到一定比例时，启动垃圾回收处理，将逻辑相邻的文件中的数据合并到一个文件中。存储在`RABBITMQ_BASE`指定的地址中

#### 消息的读写及删除：

rabbitmq在启动时会创建msg_store_persistent,msg_store_transient两个进程，一个用于持久消息的存储，一个用于内存不够时，将存储在内存中的非持久化数据转存到磁盘中。所有队列的消息的写入和删除最终都由这两个进程负责处理，而消息的读取则可能是队列本身直接打开文件进行读取，也可能是发送请求由msg_store_persisteng/msg_store_transient进程进行处理。

在进行消息的存储时，rabbitmq会在ets表中记录消息在文件中的映射，以及文件的相关信息。消息读取时，根据消息ID找到该消息所存储的文件，在文件中的偏移量，然后打开文件进行读取。消息的删除只是从ets表删除指定消息的相关信息，同时更新消息对应存储的文件的相关信息（更新文件有效数据大小）。

### [事务使用](https://www.cnblogs.com/vipstone/p/9350075.html) 

事务的实现主要是对信道（Channel）的设置，主要的方法有三个：

1. channel.txSelect()声明启动事务模式；
2. channel.txComment()提交事务；
3. channel.txRollback()回滚事务；

从上面的可以看出事务都是以tx开头的，tx应该是transaction extend（事务扩展模块）的缩写，如果有准确的解释欢迎在博客下留言。

我们来看具体的代码实现：

```java
// 创建连接
ConnectionFactory factory = new ConnectionFactory();
factory.setUsername(config.UserName);
factory.setPassword(config.Password);
factory.setVirtualHost(config.VHost);
factory.setHost(config.Host);
factory.setPort(config.Port);	
Connection conn = factory.newConnection();
// 创建信道
Channel channel = conn.createChannel();
// 声明队列
channel.queueDeclare(_queueName, true, false, false, null);
String message = String.format("时间 => %s", new Date().getTime());
try {
	channel.txSelect(); // 声明事务
	// 发送消息
	channel.basicPublish("", _queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
	channel.txCommit(); // 提交事务
} catch (Exception e) {
	channel.txRollback();
} finally {
	channel.close();
	conn.close();
}
```

注意：用户需把config.xx配置成自己Rabbit的信息。

从上面的代码我们可以看出，在发送消息之前的代码和之前介绍的都是一样的，只是在发送消息之前，需要声明channel为事务模式，提交或者回滚事务即可。

了解了事务的实现之后，那么事务究竟是怎么执行的，让我们来使用wireshark抓个包看看，如图所示：

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125451.png)

输入ip.addr==rabbitip && amqp查看客户端和rabbit之间的通讯，可以看到交互流程：

- 客户端发送给服务器Tx.Select(开启事务模式)
- 服务器端返回Tx.Select-Ok（开启事务模式ok）
- 推送消息
- 客户端发送给事务提交Tx.Commit
- 服务器端返回Tx.Commit-Ok

以上就完成了事务的交互流程，如果其中任意一个环节出现问题，就会抛出IoException移除，这样用户就可以拦截异常进行事务回滚，或决定要不要重复消息。

那么，既然已经有事务了，没什么还要使用发送方确认模式呢，原因是因为事务的性能是非常差的。**事务性能测试**：

事务模式，结果如下：

- 事务模式，发送1w条数据，执行花费时间：14197s
- 事务模式，发送1w条数据，执行花费时间：13597s
- 事务模式，发送1w条数据，执行花费时间：14216s

非事务模式，结果如下：

- 非事务模式，发送1w条数据，执行花费时间：101s
- 非事务模式，发送1w条数据，执行花费时间：77s
- 非事务模式，发送1w条数据，执行花费时间：106s

从上面可以看出，非事务模式的性能是事务模式的性能高149倍，我的电脑测试是这样的结果，不同的电脑配置略有差异，但结论是一样的，事务模式的性能要差很多，那有没有既能保证消息的可靠性又能兼顾性能的解决方案呢？那就是接下来要讲的Confirm发送方确认模式。

#### 方式二：批量Confirm模式

```java
// 创建连接
ConnectionFactory factory = new ConnectionFactory();
factory.setUsername(config.UserName);
factory.setPassword(config.Password);
factory.setVirtualHost(config.VHost);
factory.setHost(config.Host);
factory.setPort(config.Port);
Connection conn = factory.newConnection();
// 创建信道
Channel channel = conn.createChannel();
// 声明队列
channel.queueDeclare(config.QueueName, false, false, false, null);
// 开启发送方确认模式
channel.confirmSelect();
for (int i = 0; i < 10; i++) {
	String message = String.format("时间 => %s", new Date().getTime());
	channel.basicPublish("", config.QueueName, null, message.getBytes("UTF-8"));
}
channel.waitForConfirmsOrDie(); //直到所有信息都发布，只要有一个未确认就会IOException
System.out.println("全部执行完成");
```

以上代码可以看出来channel.waitForConfirmsOrDie()，使用同步方式等所有的消息发送之后才会执行后面代码，只要有一个消息未被确认就会抛出IOException异常。

#### 方式三：异步Confirm模式

```java
// 创建连接
ConnectionFactory factory = new ConnectionFactory();
factory.setUsername(config.UserName);
factory.setPassword(config.Password);
factory.setVirtualHost(config.VHost);
factory.setHost(config.Host);
factory.setPort(config.Port);
Connection conn = factory.newConnection();
// 创建信道
Channel channel = conn.createChannel();
// 声明队列
channel.queueDeclare(config.QueueName, false, false, false, null);
// 开启发送方确认模式
channel.confirmSelect();
for (int i = 0; i < 10; i++) {
	String message = String.format("时间 => %s", new Date().getTime());
	channel.basicPublish("", config.QueueName, null, message.getBytes("UTF-8"));
}
//异步监听确认和未确认的消息
channel.addConfirmListener(new ConfirmListener() {
	@Override
	public void handleNack(long deliveryTag, boolean multiple) throws IOException {
		System.out.println("未确认消息，标识：" + deliveryTag);
	}
	@Override
	public void handleAck(long deliveryTag, boolean multiple) throws IOException {
		System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
	}
});
```

异步模式的优点，就是执行效率高，不需要等待消息执行完，只需要监听消息即可，以上异步返回的信息如下：

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125452.png)

可以看出，代码是异步执行的，消息确认有可能是批量确认的，是否批量确认在于返回的multiple的参数，此参数为bool值，如果true表示批量执行了deliveryTag这个值以前的所有消息，如果为false的话表示单条确认。

**Confirm性能测试**

测试前提：与事务一样，我们发送1w条消息。

方式一：Confirm普通模式

- 执行花费时间：2253s
- 执行花费时间：2018s
- 执行花费时间：2043s

方式二：Confirm批量模式

- 执行花费时间：1576s
- 执行花费时间：1400s
- 执行花费时间：1374s

方式三：Confirm异步监听方式

- 执行花费时间：1498s
- 执行花费时间：1368s
- 执行花费时间：1363s

#### 总结

综合总体测试情况来看：Confirm批量确定和Confirm异步模式性能相差不大，Confirm模式要比事务快10倍左右。



### [博客：RabbitMQ文件和目录位置](https://blog.csdn.net/u011973222/article/details/86614312) 

#### 指定位置

您可以设置以下环境变量来指定RabbitMQ应该在何处定位某些内容。在大多数环境中，默认值应该可以正常工作。

| Name                          | Description                                                  |
| :---------------------------- | :----------------------------------------------------------- |
| RABBITMQ_BASE                 | This base directory contains sub-directories for the RabbitMQ server's database and log files. Alternatively, set **RABBITMQ_MNESIA_BASE** and **RABBITMQ_LOG_BASE** individually.这个基本目录包含RabbitMQ服务器的数据库和日志文件的子目录。或者，单独设置RABBITMQ_MNESIA_BASE和RABBITMQ_LOG_BASE。 |
| RABBITMQ_CONFIG_FILE          | The path to the configuration file, without the .config extension. If the [configuration file](http://previous.rabbitmq.com/v3_6_x/configure.html#configuration-file) is present it is used by the server to configure RabbitMQ components. See [Configuration guide](http://previous.rabbitmq.com/configure.html) for more information.配置文件的路径，没有.config扩展名。如果配置文件存在，服务器将使用它来配置RabbitMQ组件。默认值是${SYS_PREFIX}/etc/rabbitmq/rabbitmq |
| RABBITMQ_MNESIA_BASE          | This base directory contains sub-directories for the RabbitMQ server's node database, message store and cluster state files, one for each node, unless **RABBITMQ_MNESIA_DIR** is set explicitly. It is important that effective RabbitMQ user has sufficient permissions to read, write and create files and subdirectories in this directory at any time.这个基本目录包含RabbitMQ服务器节点数据库、消息存储和集群状态文件的子目录，每个节点一个，除非显式设置RABBITMQ_MNESIA_DIR。重要的是，有效的RabbitMQ用户有足够的权限随时读写和创建该目录中的文件和子目录。默认值是${SYS_PREFIX}/var/lib/rabbitmq/mnesia |
| RABBITMQ_MNESIA_DIR           | The directory where this RabbitMQ node's Mnesia database files are placed. (In addition to Mnesia files this location also contains message storage and index files as well as schema and cluster details.) |
| RABBITMQ_LOG_BASE             | This base directory contains the RabbitMQ server's log files, unless **RABBITMQ_LOGS** or **RABBITMQ_SASL_LOGS** are set explicitly.默认值是${SYS_PREFIX}/var/log/rabbitmq |
| RABBITMQ_LOGS                 | The path of the RabbitMQ server's Erlang log file. This variable cannot be overridden on Windows. |
| RABBITMQ_SASL_LOGS            | The path of the RabbitMQ server's Erlang SASL (System Application Support Libraries) log file. This variable cannot be overridden on Windows. |
| RABBITMQ_PLUGINS_DIR          | The list of directories where [plugins](http://previous.rabbitmq.com/v3_6_x/plugins.html) are found and loaded from. This is PATH-like variable, where different paths are separated by an OS-specific separator (: for Unix, ; for Windows). Plugins can be [installed](http://previous.rabbitmq.com/v3_6_x/installing-plugins.html) to any of the directories listed here.默认值是 "${RABBITMQ_HOME}/plugins" |
| RABBITMQ_PLUGINS_EXPAND_DIR   | Working directory used to expand enabled plugins when starting the server. It is important that effective RabbitMQ user has sufficient permissions to read and create files and subdirectories in this directory.用于在启动服务器时展开启用插件的工作目录。重要的是，有效的RabbitMQ用户有足够的权限来读取和创建这个目录中的文件和子目录。 |
| RABBITMQ_ENABLED_PLUGINS_FILE | This file records explicitly enabled plugins. When a plugin is enabled or disabled, this file will be recreated. It is important that effective RabbitMQ user has sufficient permissions to read, write and create this file at any time.这个文件显式地记录了启用的插件。当插件被启用或禁用时，将重新创建该文件。重要的是，有效的RabbitMQ用户有足够的权限随时读取、写入和创建此文件。 |
| RABBITMQ_PID_FILE             | File in which the process id is placed for use by rabbitmqctl wait.将进程id放置在其中以供rabbitmqctl等待使用的文件。 |

#### Unix系统默认位置

下表中${install_prefix}默认是/usr/local。Deb / RPM package installations use an empty ${install_prefix}.

| Name                          | Location                                                     |
| :---------------------------- | :----------------------------------------------------------- |
| RABBITMQ_BASE                 | (Not used)                                                   |
| RABBITMQ_CONFIG_FILE          | ${install_prefix}/etc/rabbitmq/rabbitmq                      |
| RABBITMQ_MNESIA_BASE          | ${install_prefix}/var/lib/rabbitmq/mnesia                    |
| RABBITMQ_MNESIA_DIR           | $RABBITMQ_MNESIA_BASE/$RABBITMQ_NODENAME                     |
| RABBITMQ_LOG_BASE             | ${install_prefix}/var/log/rabbitmq                           |
| RABBITMQ_LOGS                 | $RABBITMQ_LOG_BASE/$RABBITMQ_NODENAME.log                    |
| RABBITMQ_SASL_LOGS            | $RABBITMQ_LOG_BASE/$RABBITMQ_NODENAME-sasl.log               |
| RABBITMQ_PLUGINS_DIR          | /usr/lib/rabbitmq/plugins:$RABBITMQ_HOME/pluginsNote that /usr/lib/rabbitmq/plugins is used only when RabbitMQ is [installed](http://previous.rabbitmq.com/v3_6_x/installing-plugins.html) into the standard (default) location. |
| RABBITMQ_PLUGINS_EXPAND_DIR   | $RABBITMQ_MNESIA_BASE/$RABBITMQ_NODENAME-plugins-expand      |
| RABBITMQ_ENABLED_PLUGINS_FILE | ${install_prefix}/etc/rabbitmq/enabled_plugins               |
| RABBITMQ_PID_FILE             | $RABBITMQ_MNESIA_DIR.pid                                     |



#### Windows系统默认位置

| Name                          | Location                                                     |
| :---------------------------- | :----------------------------------------------------------- |
| RABBITMQ_BASE                 | %APPDATA%\RabbitMQ<br />**比如：C:\Users\mh\AppData\Roaming\RabbitMQ** |
| RABBITMQ_CONFIG_FILE          | %RABBITMQ_BASE%\rabbitmq                                     |
| RABBITMQ_MNESIA_BASE          | %RABBITMQ_BASE%\db                                           |
| RABBITMQ_MNESIA_DIR           | %RABBITMQ_MNESIA_BASE%\%RABBITMQ_NODENAME%                   |
| RABBITMQ_LOG_BASE             | %RABBITMQ_BASE%\log                                          |
| RABBITMQ_LOGS                 | %RABBITMQ_LOG_BASE%\%RABBITMQ_NODENAME%.log                  |
| RABBITMQ_SASL_LOGS            | %RABBITMQ_LOG_BASE%\%RABBITMQ_NODENAME%-sasl.log             |
| RABBITMQ_PLUGINS_DIR          | *Installation-directory*/plugins                             |
| RABBITMQ_PLUGINS_EXPAND_DIR   | %RABBITMQ_MNESIA_BASE%\%RABBITMQ_NODENAME%-plugins-expand    |
| RABBITMQ_ENABLED_PLUGINS_FILE | %RABBITMQ_BASE%\enabled_plugins                              |
| RABBITMQ_PID_FILE             | (Not currently supported)                                    |

#### Generic Unix默认位置

这些是在解包Generic Unix tar文件并在不进行修改的情况下运行服务器时获得的默认值。在这个表中，$RABBITMQ_HOME引用解压下载文件时生成的目录rabbitmq_server-3.6.14。

| Name                          | Location                                                |
| :---------------------------- | :------------------------------------------------------ |
| RABBITMQ_BASE                 | (Not used)                                              |
| RABBITMQ_CONFIG_FILE          | $RABBITMQ_HOME/etc/rabbitmq/rabbitmq                    |
| RABBITMQ_MNESIA_BASE          | $RABBITMQ_HOME/var/lib/rabbitmq/mnesia                  |
| RABBITMQ_MNESIA_DIR           | $RABBITMQ_MNESIA_BASE/$RABBITMQ_NODENAME                |
| RABBITMQ_LOG_BASE             | $RABBITMQ_HOME/var/log/rabbitmq                         |
| RABBITMQ_LOGS                 | $RABBITMQ_LOG_BASE/$RABBITMQ_NODENAME.log               |
| RABBITMQ_SASL_LOGS            | $RABBITMQ_LOG_BASE/$RABBITMQ_NODENAME-sasl.log          |
| RABBITMQ_PLUGINS_DIR          | $RABBITMQ_HOME/plugins                                  |
| RABBITMQ_PLUGINS_EXPAND_DIR   | $RABBITMQ_MNESIA_BASE/$RABBITMQ_NODENAME-plugins-expand |
| RABBITMQ_ENABLED_PLUGINS_FILE | $RABBITMQ_HOME/etc/rabbitmq/enabled_plugins             |
| RABBITMQ_PID_FILE             | $RABBITMQ_MNESIA_DIR.pid                                |



## [spring-rabbit消费过程解析及AcknowledgeMode选择](https://blog.csdn.net/weixin_38380858/article/details/84963944)

说明：本文内容来源于对`amqp-client`和`spring-rabbit`包源码的解读及`debug`，尽可能保证内容的准确性。*

`rabbitmq`消费过程示意如下：
![在这里插入图片描述](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125453.png)
图中首字母大写的看上去像类名的，如`ConsumerWorkService`，`MainLoop`，`WorkPoolRunnable`等，没错就是类名，可自行根据类名去查看相关源码。

下面解释上图的含义。

### 1. 启动流程

- 通过`BeanPostProcessor`扫描所有的`bean`中存在的`@RabbitListener`注解及相应的`Method`；
- 由`RabbitListenerContainerFactory`根据配置为每一个`@RabbitListener`注解创建一个`MessageListenerContainer`，持有`@RabbitListener`注解及`Method`信息；
- 初始化`MessageListenerContainer`，主要是循环依次创建`consumer`（`AsyncMessageProcessingConsumer`类），启动`consumer`；
- 创建`consumer`，过程包括：创建`AMQConnection`（仅第一次创建），创建`AMQChannel`（每个`consumer`都会创建），发送消费`queue`的请求（`basic.consume`），接收并处理消息；
- `AMQConnection`持有连接到`rabbitmq server`的`Socket`，创建完成后启动`MainLoop`循环从`Socket`流中读取`Frame`，此时流中没有消息，因为`channel`还没创建完成；
- 创建`AMQChannel`（一个`AMQConnection`中持有多个`AMQChannel`），并将创建完成的`channel`注册到`AMQConnection`持有的`ConsumerWorkService`，实际就是添加到`WorkPool`类的`Map`里面去，此时`Socket`流中也没有消息，因为`channel`还没有与`queue`绑定；
- 创建完成的`AMQChannel`的代理返回给`consumer`，`consumer`通过`channel`发送消费`queue`的请求到`rabbitmq server`（绑定成功），此时还没开始处理消息，但`Socket`流中已经有消息，并且已经被`connection`读取到内存（即`BlockingQueue`）中，并且已经开始向`BlockingQueue`分发；
- `consumer`启动循环，从`BlockingQueue`中取消息，利用`MessageListenerContainer`中持有的`Method`反射调用`@RabbitListener`注解方法处理消息。

### 2. 消费流程

- `rabbitmq server`往`Socket`流中写入字节。
- `AMQConnection`启动一个`main loop thread`来跑`MainLoop`，不断从`Socket`流中读取字节转换成`Frame`对象，这是每个`connection`唯一的数据来源。

Frame对象结构如下：
![在这里插入图片描述](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323125454.png)
`type`：指定当前`Frame`的类型，如`method(1)`、`message header(2)`、`message body(3)`、`heartbeat(8)`等；

`channel`：`channel`的编号，从`0~n`排列，指定当前`Frame`需要交给哪个`channel`处理。`channel-0`为一类，`channel-n`为一类。`channel-0`是一个匿名类，用来处理特殊`Frame`，如`connection.start`。`channel-n`都是`ChannelN`类，由`ChannelManager`类统一管理。

`payload`：当前`Frame`的具体内容。

`consumer`启动后，`connection`读取到的`Frame`如上图所示（一个`consumer`的情况，多个`consumer`的`Frame`可能会交替）。从`basic.deliver`开始是消息的内容，每条消息分成三个`Frame`：第一个是`method`，`basic.deliver`代表这是一个消息，后面一定会再跟着两个`Frame`；第二个是`message header`；第三个是`message body`，`body`读取之后将三个`Frame`整合到一起转换成一条完整的`deliver`命令。

- `AMQConnection`根据读取到的`Frame`中的`type`决定要怎么处理这个`Frame`：`heartbeat(8) do nothing`；其它的根据`channel`编号交给相应的`AMQChannel`去处理，（编号为`0`的是特殊的`channel`，消息相关的用的都是编号非`0`的`channel`），消息都会拿着这个编号到`ChannelManager`找对应的`ChannelN`处理。
- `ChannelN`经过一系列中间过程由`Frame`（消息是三个`Frame`）得到了`Runnable`，将`(ChannelN, Runnable) put`到`ConsumerWorkService`持有的`WorkPool`里面的一个`Map>`里面去。这样这个`Runnable`就进入了与`ChannelN`对应的`BlockingQueue`（写死的`size=1000`）里面了。
- `execute`一个`WorkPoolRunnable`，执行的任务是：从`WorkPool`中找出一个`ready`状态的`ChannelN`，把这个`ChannelN`设为`inProgress`状态，从对应的`BlockingQueue`中取最多`16`（写死的）个`Runnable`在`WorkPoolRunnable`的线程里依次执行（注意：此处不再另开线程，所以可能会堵塞当前线程，导致这个`ChannelN`长时间处于`inProgress`状态），执行完后将当前`ChannelN`状态改为`ready`，并在当前线程`execute`另一个`WorkPoolRunnable`。
- `BlockingQueue`里面的`Runnable`执行的逻辑是：构造一个`Delivery put`到与`ChannelN`对应的`AsyncMessageProcessingConsumer`持有的`BlockingQueue`（`size=prefetchCount`可配置）里面去（如果消息处理速度太慢，`BlockingQueue`已满，此处会堵塞）。
- 每个`AsyncMessageProcessingConsumer`都有一个独立的线程在循环从`BlockingQueue`一次读取一个`Delivery`转换成`Message`反射调用`@RabbitListener`注解方法来处理。

### 3. 无ack消费模式与有ack消费模式对比

根据以上对消费过程的分析，将无`ack`模式与`ack`模式进行对比。

#### 无`ack`模式（`AcknowledgeMode.NONE`）

##### `server`端行为

- `rabbitmq server`默认推送的所有消息都已经消费成功，会不断地向消费端推送消息。
- 因为`rabbitmq server`认为推送的消息已被成功消费，所以推送出去的消息不会暂存在`server`端。

##### 消息丢失的风险

当`BlockingQueue`堆满时（`BlockingQueue`一定会先满），`server`端推送消息会失败，然后断开`connection`。消费端从`Socket`读取`Frame`将会抛出`SocketException`，触发异常处理，`shutdown`掉`connection`和所有的`channel`，`channel shutdown`后`WorkPool`中的`channel`信息（包括`channel inProgress`,`channel ready`以及`Map`）全部清空，所以`BlockingQueue`中的数据会全部丢失。

此外，服务重启时也需对内存中未处理完的消息做必要的处理，以免丢失。

而在`rabbitmq server`，`connection`断掉后就没有消费者去消费这个`queue`，因此在`server`端会看到消息堆积的现象。

#### 有`ack`模式（`AcknowledgeMode.AUTO`，`AcknowledgeMode.MANUAL`）

`AcknowledgeMode.MANUAL`模式需要人为地获取到`channel`之后调用方法向`server`发送`ack`（或消费失败时的`nack`）信息。

`AcknowledgeMode.AUTO`模式下，由`spring-rabbit`依据消息处理逻辑是否抛出异常自动发送`ack`（无异常）或`nack`（异常）到`server`端。

##### `server`端行为

- `rabbitmq server`推送给每个`channel`的消息数量有限制，会保证每个`channel`没有收到`ack`的消息数量不会超过`prefetchCount`。
- `server`端会暂存没有收到`ack`的消息，等消费端`ack`后才会丢掉；如果收到消费端的`nack`（消费失败的标识）或`connection`断开没收到反馈，会将消息放回到原队列头部。

这种模式不会丢消息，但效率较低，因为`server`端需要等收到消费端的答复之后才会继续推送消息，当然，推送消息和等待答复是异步的，可适当增大`prefetchCount`提高效率。

注意，有`ack`的模式下，需要考虑`setDefaultRequeueRejected(false)`，否则当消费消息抛出异常没有`catch`住时，这条消息会被`rabbitmq`放回到`queue`头部，再被推送过来，然后再抛异常再放回…死循环了。设置`false`的作用是抛异常时不放回，而是直接丢弃，所以可能需要对这条消息做处理，以免丢失。更详细的配置参考[这里](https://blog.csdn.net/weixin_38380858/article/details/84258507)。

#### 对比

- 无`ack`模式：效率高，存在丢失大量消息的风险。
- 有`ack`模式：效率低，不会丢消息。