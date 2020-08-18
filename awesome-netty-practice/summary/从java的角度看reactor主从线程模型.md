从java的角度看reactor主从线程模型
写在前面
本文来源于b站鲁班学院视频
常用的 Reactor 线程模型有三种， Reactor 单线程模型, Reactor 多线程模型, 主从 Reactor 多线程模
型。本文主要介绍主从Reactor多线程模型，并写一个小的demo。

主从Reactor多线程模型
服务端用于接收客户端连接的不再是个 1 个单独的 NIO 线程，而是一个独立的 NIO 线程池。Acceptor 接收到客户端 TCP 连接请求处理完成后（可能包含接入认证等），将新创建的SocketChannel 注册到 IO 线程池（subReactor 线程池）的某个 IO 线程上，由它负责SocketChannel 的读写和编解码工作。 Acceptor 线程池仅仅只用于客户端的登陆、握手和安全认证，一旦链路建立成功，就将链路注册到后端 subReactor 线程池的 IO 线程上，由 IO 线程负责后续的 IO 操作。
Reactor模型中的三个重要角色

Reactor：把IO事件分配给对应的handler处理

Acceptor：处理客户端连接事件

Handler：处理非阻塞的任务

流程图
为了更好的理解主从reactor主从线程模型，我简单实现了一个demo，大体执行流程如下。

![在这里插入图片描述](img/1.png)

关键类：
BossGroup：该类只对连接事件感兴趣，它会监听一个端口 ,如果有请求进来，它会进行连接，后续的读写操作都会由TCPSubReactor来管理
Acceptor：该类的作用是管理多个TCPSubReactor，BossGroup要把读写请求委托给TCPSubReactor处理，必须要通过Acceptor类，因此，Acceptor类是BossGroup中的Selectionkey的一个附加对象，以便在有连接请求过来时直接调用Acceptor的run方法将后续操作直接派发给TcpSubReactor
TCPSubReactor：该类只处理读写事件，它通过执行SelectionKey里的附加对象（也就是TcpHandler的run方法）来处理读写事件。
TcpHandler：他通过执行HandlerState接口的handler方法来处理具体的读写操作。
WorkState，WriteState，ReadState：这三个类都实现了HandlerState接口，每个类对handler方法都有不同的实现方式。

GITHUB地址：
https://github.com/1315402725/HelloReactor

作者: 爱笑的k11
链接: http://1315402725.github.io/posts/f648507a/
来源: 爱笑的k11
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。