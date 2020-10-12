最近看到了内网ATA上的一篇[断网故障时Mtop触发tomcat高并发场景下的BUG排查和修复（已被apache采纳）](https://yq.aliyun.com/articles/2889?spm=5176.100239.blogcont39093.8.s7Uavb)，引起了我的好奇，感觉原作者对应底层十分了解，写的很复杂。原来对于tomcat的线程模型不怎么清楚，但是它又是我们日常最常用的服务器，于是我对它的线程模型进行了补习。

#### **一. tomcat支持的请求处理方式**

Tomcat支持三种接收请求的处理方式：BIO、NIO、APR

1. BIO模式：阻塞式I/O操作，表示Tomcat使用的是传统Java I/O操作(即Java.io包及其子包)。Tomcat7以下版本默认情况下是以bio模式运行的，由于每个请求都要创建一个线程来处理，线程开销较大，不能处理高并发的场景，在三种模式中性能也最低。启动tomcat看到如下日志，表示使用的是BIO模式：
   ![这里写图片描述](1%E3%80%81tomcat-%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.assets/20170712084104165.png)
2. NIO模式：是java SE 1.4及后续版本提供的一种新的I/O操作方式(即java.nio包及其子包)。是一个基于缓冲区、并能提供非阻塞I/O操作的Java API，它拥有比传统I/O操作(bio)更好的并发运行性能。在tomcat 8之前要让Tomcat以nio模式来运行比较简单，只需要在Tomcat安装目录/conf/server.xml文件中将如下配置：

```
<Connector port="8080" protocol="HTTP/1.1"connectionTimeout="20000"redirectPort="8443" />1
```

修改成

```
<Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"connectionTimeout="20000"redirectPort="8443" />1
```

Tomcat8以上版本，默认使用的就是NIO模式，不需要额外修改
![这里写图片描述](1%E3%80%81tomcat-%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.assets/20170712084323183.png)

1. apr模式：简单理解，就是从操作系统级别解决异步IO问题，大幅度的提高服务器的处理和响应性能， 也是Tomcat运行高并发应用的首选模式。
   启用这种模式稍微麻烦一些，需要安装一些依赖库，下面以在CentOS7 mini版环境下Tomcat-8.0.35为例，介绍安装步聚：

```
APR 1.2+ development headers (libapr1-dev package)
OpenSSL 0.9.7+ development headers (libssl-dev package)
JNI headers from Java compatible JDK 1.4+
GNU development environment (gcc, make)1234
```

#### **二. tomcat的NioEndpoint**

我们先来简单回顾下目前一般的NIO服务器端的大致实现，借鉴infoq上的一篇文章Netty系列之Netty线程模型中的一张图
![这里写图片描述](1%E3%80%81tomcat-%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.assets/20170712084736110.png)

一个或多个Acceptor线程，每个线程都有自己的Selector，Acceptor只负责accept新的连接，一旦连接建立之后就将连接注册到其他Worker线程中。
多个Worker线程，有时候也叫IO线程，就是专门负责IO读写的。一种实现方式就是像Netty一样，每个Worker线程都有自己的Selector，可以负责多个连接的IO读写事件，每个连接归属于某个线程。另一种方式实现方式就是有专门的线程负责IO事件监听，这些线程有自己的Selector，一旦监听到有IO读写事件，并不是像第一种实现方式那样（自己去执行IO操作），而是将IO操作封装成一个Runnable交给Worker线程池来执行，这种情况每个连接可能会被多个线程同时操作，相比第一种并发性提高了，但是也可能引来多线程问题，在处理上要更加谨慎些。tomcat的NIO模型就是第二种。

这就要详细了解下tomcat的NioEndpoint实现了。先来借鉴看下 [断网故障时Mtop触发tomcat高并发场景下的BUG排查和修复（已被apache采纳）](https://yq.aliyun.com/articles/2889?spm=5176.100239.blogcont39093.8.s7Uavb) 中的一张图
![这里写图片描述](1%E3%80%81tomcat-%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.assets/20170712085042514.png)
这张图勾画出了NioEndpoint的大致执行流程图，worker线程并没有体现出来，它是作为一个线程池不断的执行IO读写事件即SocketProcessor（一个Runnable），即这里的Poller仅仅监听Socket的IO事件，然后封装成一个个的SocketProcessor交给worker线程池来处理。下面我们来详细的介绍下NioEndpoint中的Acceptor、Poller、SocketProcessor。
它们处理客户端连接的主要流程如图所示：
![这里写图片描述](1%E3%80%81tomcat-%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.assets/20170712085200173.png)
图中Acceptor及Worker分别是以线程池形式存在，Poller是一个单线程。注意，与BIO的实现一样，缺省状态下，在server.xml中没有配置<Executor>，则以Worker线程池运行，如果配置了<Executor>，则以基于java concurrent 系列的java.util.concurrent.ThreadPoolExecutor线程池运行。

1. Acceptor
   接收socket线程，这里虽然是基于NIO的connector，但是在接收socket方面还是传统的serverSocket.accept()方式，获得SocketChannel对象，然后封装在一个tomcat的实现类org.apache.tomcat.util.net.NioChannel对象中。然后将NioChannel对象封装在一个PollerEvent对象中，并将PollerEvent对象压入events queue里。这里是个典型的生产者-消费者模式，Acceptor与Poller线程之间通过queue通信，Acceptor是events queue的生产者，Poller是events queue的消费者。
2. Poller
   Poller线程中维护了一个Selector对象，NIO就是基于Selector来完成逻辑的。在connector中并不止一个Selector，在socket的读写数据时，为了控制timeout也有一个Selector，在后面的BlockSelector中介绍。可以先把Poller线程中维护的这个Selector标为主Selector。 Poller是NIO实现的主要线程。首先作为events queue的消费者，从queue中取出PollerEvent对象，然后将此对象中的channel以OP_READ事件注册到主Selector中，然后主Selector执行select操作，遍历出可以读数据的socket，并从Worker线程池中拿到可用的Worker线程，然后将socket传递给Worker。整个过程是典型的NIO实现。
3. Worker
   Worker线程拿到Poller传过来的socket后，将socket封装在SocketProcessor对象中。然后从Http11ConnectionHandler中取出Http11NioProcessor对象，从Http11NioProcessor中调用CoyoteAdapter的逻辑，跟BIO实现一样。在Worker线程中，会完成从socket中读取http request，解析成HttpServletRequest对象，分派到相应的servlet并完成逻辑，然后将response通过socket发回client。在从socket中读数据和往socket中写数据的过程，并没有像典型的非阻塞的NIO的那样，注册OP_READ或OP_WRITE事件到主Selector，而是直接通过socket完成读写，这时是阻塞完成的，但是在timeout控制上，使用了NIO的Selector机制，但是这个Selector并不是Poller线程维护的主Selector，而是BlockPoller线程中维护的Selector，称之为辅Selector。

#### **三. tomcat8的并发参数控制**

本篇的tomcat版本是tomcat8.5。可以到这里看下tomcat8.5的配置参数

1. acceptCount
   文档描述为：
   The maximum queue length for incoming connection requests when all possible request processing threads are in use. Any requests received when the queue is full will be refused. The default value is 100.
   这个参数就立马牵涉出一块大内容：TCP三次握手的详细过程，这个之后再详细探讨。这里可以简单理解为：连接在被ServerSocketChannel accept之前就暂存在这个队列中，acceptCount就是这个队列的最大长度。ServerSocketChannel accept就是从这个队列中不断取出已经建立连接的的请求。所以当ServerSocketChannel accept取出不及时就有可能造成该队列积压，一旦满了连接就被拒绝了
2. acceptorThreadCount
   文档如下描述
   The number of threads to be used to accept connections. Increase this value on a multi CPU machine, although you would never really need more than 2. Also, with a lot of non keep alive connections, you might want to increase this value as well. Default value is 1.
   Acceptor线程只负责从上述队列中取出已经建立连接的请求。在启动的时候使用一个ServerSocketChannel监听一个连接端口如8080，可以有多个Acceptor线程并发不断调用上述ServerSocketChannel的accept方法来获取新的连接。参数acceptorThreadCount其实使用的Acceptor线程的个数。
3. maxConnections
   文档描述如下
   The maximum number of connections that the server will accept and process at any given time. When this number has been reached, the server will accept, but not process, one further connection. This additional connection be blocked until the number of connections being processed falls below maxConnections at which point the server will start accepting and processing new connections again. Note that once the limit has been reached, the operating system may still accept connections based on the acceptCount setting. The default value varies by connector type. For NIO and NIO2 the default is 10000. For APR/native, the default is 8192.
   Note that for APR/native on Windows, the configured value will be reduced to the highest multiple of 1024 that is less than or equal to maxConnections. This is done for performance reasons. If set to a value of -1, the maxConnections feature is disabled and connections are not counted.
   这里就是tomcat对于连接数的一个控制，即最大连接数限制。一旦发现当前连接数已经超过了一定的数量（NIO默认是10000），上述的Acceptor线程就被阻塞了，即不再执行ServerSocketChannel的accept方法从队列中获取已经建立的连接。但是它并不阻止新的连接的建立，新的连接的建立过程不是Acceptor控制的，Acceptor仅仅是从队列中获取新建立的连接。所以当连接数已经超过maxConnections后，仍然是可以建立新的连接的，存放在上述acceptCount大小的队列中，这个队列里面的连接没有被Acceptor获取，就处于连接建立了但是不被处理的状态。当连接数低于maxConnections之后，Acceptor线程就不再阻塞，继续调用ServerSocketChannel的accept方法从acceptCount大小的队列中继续获取新的连接，之后就开始处理这些新的连接的IO事件了。
4. maxThreads
   文档描述如下
   The maximum number of request processing threads to be created by this Connector, which therefore determines the maximum number of simultaneous requests that can be handled. If not specified, this attribute is set to 200. If an executor is associated with this connector, this attribute is ignored as the connector will execute tasks using the executor rather than an internal thread pool.
   这个简单理解就算是上述worker的线程数。他们专门用于处理IO事件，默认是200。