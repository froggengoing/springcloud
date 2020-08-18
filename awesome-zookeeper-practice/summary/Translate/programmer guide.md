# 翻译官网

## zookeeper数据模型

> ZooKeeper has a hierarchal namespace, much like a distributed file system. The only difference is that each node in the namespace can have data associated with it as well as children. It is like having a file system that allows a file to also be a directory. Paths to nodes are always expressed as canonical, absolute, slash-separated paths; there are no relative reference. Any unicode character can be used in a path subject to the following constraints:
>
> - The null character (\u0000) cannot be part of a path name. (This causes problems with the C binding.)
> - The following characters can't be used because they don't display well, or render in confusing ways: \u0001 - \u001F and \u007F
> - \u009F.
> - The following characters are not allowed: \ud800 - uF8FF, \uFFF0 - uFFFF.
> - The "." character can be used as part of another name, but "." and ".." cannot alone be used to indicate a node along a path, because ZooKeeper doesn't use relative paths. The following would be invalid: "/a/b/./c" or "/a/b/../c".
> - The token "zookeeper" is reserved.

ZooKeeper具有层次结构的名称空间，非常类似于分布式文件系统。唯一的区别是，命名空间中的每个节点都可以具有与其关联的数据以及子节点。就像拥有一个文件系统包含文件和目录一样。到节点的路径始终表示为规范的，绝对的，斜杠分隔的路径，没有相对路径/引用。所有的unicode都能使用除了以下限制：

* \u0000即空字符串
* \u0001 - \u001F 和\u007F
* \u009F
* \ud800 - uF8FF, \uFFF0 - uFFFF
* .和..不允许被单独使用
* zookeeper为保留关键字

### ZNodes

> Every node in a ZooKeeper tree is referred to as a *znode*. Znodes maintain a stat structure that includes version numbers for data changes, acl changes. The stat structure also has timestamps. The version number, together with the timestamp, allows ZooKeeper to validate the cache and to coordinate updates. Each time a znode's data changes, the version number increases. For instance, whenever a client retrieves data, it also receives the version of the data. And when a client performs an update or a delete, it must supply the version of the data of the znode it is changing. If the version it supplies doesn't match the actual version of the data, the update will fail. (This behavior can be overridden.
>
> ###### Note
>
> > In distributed application engineering, the word *node* can refer to a generic host machine, a server, a member of an ensemble, a client process, etc. In the ZooKeeper documentation, *znodes* refer to the data nodes. *Servers* refers to machines that make up the ZooKeeper service; *quorum peers* refer to the servers that make up an ensemble; client refers to any host or process which uses a ZooKeeper service.
>
> Znodes are the main entity that a programmer access. They have several characteristics that are worth mentioning here.

ZooKeeper树中的每个节点都称为znode。Znodes维护一个统计信息的结构，其中包括用于数据更改和acl更改的版本号。数据结构也有时间戳。ZooKeeper使用版本号和时间戳验证缓存并协调更新操作。znode的数据每次更改时，版本号都会增加。例如，每当客户端检索数据时，它也会接收数据的版本。并且，当客户端执行更新或删除时，它必须提供其更改的znode数据的版本。如果它提供的版本与数据的实际版本不匹配，则更新将失败。

> 注意：
>
> 在分布式应用程序工程中，节点一词可以指代通用主机，服务器，集合的成员，客户端进程等。在ZooKeeper文档中，znodes指的是数据节点。服务器是指组成ZooKeeper服务的计算机；*quorum peers* 是指组成集群的服务器；客户端是指使用ZooKeeper服务的任何主机或进程。

#### 监听器Watches

> Clients can set watches on znodes. Changes to that znode trigger the watch and then clear the watch. When a watch triggers, ZooKeeper sends the client a notification. More information about watches can be found in the section [ZooKeeper Watches](https://zookeeper.apache.org/doc/r3.6.1/zookeeperProgrammers.html#ch_zkWatches).

客户端可以在znodes上设置监听器。对该znode的更改将触发这些监听器然后清除监听器。监听器被触发时，ZooKeeper向客户端发送通知。

#### 数据访问Data Access

> The data stored at each znode in a namespace is read and written atomically. Reads get all the data bytes associated with a znode and a write replaces all the data. Each node has an Access Control List (ACL) that restricts who can do what.
>
> ZooKeeper was not designed to be a general database or large object store. Instead, it manages coordination data. This data can come in the form of configuration, status information, rendezvous, etc. A common property of the various forms of coordination data is that they are relatively small: measured in kilobytes. The ZooKeeper client and the server implementations have sanity checks to ensure that znodes have less than 1M of data, but the data should be much less than that on average. Operating on relatively large data sizes will cause some operations to take much more time than others and will affect the latencies of some operations because of the extra time needed to move more data over the network and onto storage media. If large data storage is needed, the usual pattern of dealing with such data is to store it on a bulk storage system, such as NFS or HDFS, and store pointers to the storage locations in ZooKeeper.

存储在名称空间中每个znode上的数据的读写操作都是原子性的。读取将获取与znode关联的所有数据字节，而写入将替换所有数据。每个节点都有一个访问控制列表（ACL），用于限制谁可以执行操作。

ZooKeeper并非设计为通用数据库或大型对象存储。相反，它管理协调数据。这些数据的形式可以是配置信息，状态信息，集合点等。各种形式的协调数据的共同特征是它们相对较小，以KB为单位。ZooKeeper客户端和服务器实现具有健全性检查，以确保znode的数据少于1M，但数据平均值应比这还要少得多。对相对大的数据量进行操作将导致某些操作比其他操作花费更多的时间，并且会影响某些操作的延迟，因为需要更多时间才能通过网络将更多数据移动到存储介质上。如果需要大数据存储，则处理此类数据的通常方式是将其存储在大容量存储系统（例如NFS或HDFS）上，并存储指向ZooKeeper中存储位置的指针。

#### Ephemeral Nodes

> ZooKeeper also has the notion of ephemeral nodes. These znodes exists as long as the session that created the znode is active. When the session ends the znode is deleted. Because of this behavior ephemeral znodes are not allowed to have children. The list of ephemerals for the session can be retrieved using **getEphemerals()** api.
>
> ##### getEphemerals()
>
> Retrieves the list of ephemeral nodes created by the session for the given path. If the path is empty, it will list all the ephemeral nodes for the session. **Use Case** - A sample use case might be, if the list of ephemeral nodes for the session needs to be collected for duplicate data entry check and the nodes are created in a sequential manner so you do not know the name for duplicate check. In that case, getEphemerals() api could be used to get the list of nodes for the session. This might be a typical use case for service discovery.

ZooKeeper还具有临时节点的概念。只要创建znode的会话处于活动状态，这些znode就会存在。会话结束时，将删除znode。由于这种行为，临时的znode不允许有子节点。可以使用getEphemerals（）API获取会话的临时节点列表。

获取给定的路径的临时节点（由会话创建的），如果路径为空，它将列出该会话的所有临时节点。使用案例：如果需要收集会话的临时节点列表以进行重复数据输入检查，并且这些节点以顺序方式创建，你不知道重复检查的路径。在这种情况下，可以使用getEphemerals（）api获取会话的节点列表。这可能是服务发现的典型用例。

#### Sequence Nodes -- Unique Naming

> When creating a znode you can also request that ZooKeeper append a monotonically increasing counter to the end of path. This counter is unique to the parent znode. The counter has a format of %010d -- that is 10 digits with 0 (zero) padding (the counter is formatted in this way to simplify sorting), i.e. "0000000001". See [Queue Recipe](https://zookeeper.apache.org/doc/r3.6.1/recipes.html#sc_recipes_Queues) for an example use of this feature. Note: the counter used to store the next sequence number is a signed int (4bytes) maintained by the parent node, the counter will overflow when incremented beyond 2147483647 (resulting in a name "-2147483648").

创建znode时，您还可以要求ZooKeeper在路径末尾附加一个单调递增的计数器。该计数器对于父级znode是唯一的。计数器的格式为％010d-填充为0（零）的10位数字（计数器以这种方式格式化以简化排序），即“ 0000000001”。有关此功能的示例用法，请参见[队列用法](https://zookeeper.apache.org/doc/r3.6.1/recipes.html#sc_recipes_Queues )。注意：父节点维护带符号的int（4字节）用于存储下一个序列号的计数器，当计数器的计数值超过2147483647（结果为“ -2147483648”）时，计数器将溢出。

#### Container Nodes

> ZooKeeper has the notion of container znodes. Container znodes are special purpose znodes useful for recipes such as leader, lock, etc. When the last child of a container is deleted, the container becomes a candidate to be deleted by the server at some point in the future.
>
> Given this property, you should be prepared to get KeeperException.NoNodeException when creating children inside of container znodes. i.e. when creating child znodes inside of container znodes always check for KeeperException.NoNodeException and recreate the container znode when it occurs.

ZooKeeper具有容器znodes的概念。容器znode是特殊用途的znode，可用于诸如leader，lock等。当容器节点的最后一个子容器节点被删除时，该容器节点将成为服务器将来要删除的候选对象。

基于这种特性，您应该准备获取KeeperException。在容器znodes中创建子级时可能会抛出NoNodeException。所以在容器znode内创建子znode时，请始终检查KeeperException.出现NoNodeException时重新创建容器znode。

#### TTL Nodes

> When creating PERSISTENT or PERSISTENT_SEQUENTIAL znodes, you can optionally set a TTL in milliseconds for the znode. If the znode is not modified within the TTL and has no children it will become a candidate to be deleted by the server at some point in the future.
>
> Note: TTL Nodes must be enabled via System property as they are disabled by default. See the [Administrator's Guide](https://zookeeper.apache.org/doc/r3.6.1/zookeeperAdmin.html#sc_configuration) for details. If you attempt to create TTL Nodes without the proper System property set the server will throw KeeperException.UnimplementedException.

创建PERSISTENT或PERSISTENT_SEQUENTIAL znode时，可以选择为znode设置TTL存活时间（以毫秒为单位）。如果znode在TTL内未修改且没有子代，它将成为将来服务器上将被删除的候选者。

注意：TTL节点必须通过“系统”属性启用，因为默认情况下它们是禁用的。有关详细信息，请参见[Administrator's Guide](https://zookeeper.apache.org/doc/r3.6.1/zookeeperAdmin.html#sc_configuration)。如果您尝试在没有正确设置系统属性的情况下创建TTL节点，则服务器将抛出KeeperException.UnimplementedException。

### Time in ZooKeeper

> ZooKeeper tracks time multiple ways:
>
> - **Zxid** Every change to the ZooKeeper state receives a stamp in the form of a *zxid* (ZooKeeper Transaction Id). This exposes the total ordering of all changes to ZooKeeper. Each change will have a unique zxid and if zxid1 is smaller than zxid2 then zxid1 happened before zxid2.
> - **Version numbers** Every change to a node will cause an increase to one of the version numbers of that node. The three version numbers are version (number of changes to the data of a znode), cversion (number of changes to the children of a znode), and aversion (number of changes to the ACL of a znode).
> - **Ticks** When using multi-server ZooKeeper, servers use ticks to define timing of events such as status uploads, session timeouts, connection timeouts between peers, etc. The tick time is only indirectly exposed through the minimum session timeout (2 times the tick time); if a client requests a session timeout less than the minimum session timeout, the server will tell the client that the session timeout is actually the minimum session timeout.
> - **Real time** ZooKeeper doesn't use real time, or clock time, at all except to put timestamps into the stat structure on znode creation and znode modification.

* **Zxid** ：对ZooKeeper状态的每次更改都会接收到以zxid（ZooKeeper事务ID）的形式的标记。ZooKeeper记录所有的修改顺序。每个更改将具有唯一的zxid，并且如果zxid1小于zxid2，则zxid1发生在zxid2之前。

* **Version numbers** ：对节点的每次更改都会导致该节点的版本号增加。这三个版本号分别是version （对znode的数据进行更改的次数），cversion （对znode的子级进行更改的次数）和aversion （对znode的ACL进行更改的次数）。

* **Ticks** ：使用多个ZooKeeper服务器时，服务器使用ticks 来定义事件的时间，例如上传的状态，会话超时，点到点的连接超时等。tick时间通过最短的会话超时时间间接设置，最短的会话超时时间是tick time的两倍。如果客户端请求的会话超时时间小于最小会话超时时间，服务器将告知客户端该会话超时时间实际上是最小会话超时时间。

* **Real time** ：ZooKeeper完全不使用实时时间或时钟时间，只是在znode创建和znode修改时将时间戳放入stat结构中。

  

### ZooKeeper Stat Structure

> The Stat structure for each znode in ZooKeeper is made up of the following fields:
>
> - **czxid** The zxid of the change that caused this znode to be created.
> - **mzxid** The zxid of the change that last modified this znode.
> - **pzxid** The zxid of the change that last modified children of this znode.
> - **ctime** The time in milliseconds from epoch when this znode was created.
> - **mtime** The time in milliseconds from epoch when this znode was last modified.
> - **version** The number of changes to the data of this znode.
> - **cversion** The number of changes to the children of this znode.
> - **aversion** The number of changes to the ACL of this znode.
> - **ephemeralOwner** The session id of the owner of this znode if the znode is an ephemeral node. If it is not an ephemeral node, it will be zero.
> - **dataLength** The length of the data field of this znode.
> - **numChildren** The number of children of this znode.

ZooKeeper中每个znode的Stat结构由以下字段组成：

* **czxid** ：zxid被修改时将创建这个节点
* **mzxid** ：znode最后一次修改的Zxid
* **pzxid** ：子znode最后一次修改的zxid
* **ctime** ：znode创建的时间戳，毫秒为单位。
* **mtime** ：znode最后一次修改的时间戳
* **version** ：znode修改的次数
* **cversion** ：子znode修改的次数
* **aversion** ：znode的ACL修改的次数
* **ephemeralOwner** ：创建该临时节点的会话ID，如果不是临时节点则为0.
* **dataLength** ：数据的长度
* **numChildren** ：子节点的数量

## ZooKeeper Sessions

> A ZooKeeper client establishes a session with the ZooKeeper service by creating a handle to the service using a language binding. Once created, the handle starts off in the CONNECTING state and the client library tries to connect to one of the servers that make up the ZooKeeper service at which point it switches to the CONNECTED state. During normal operation the client handle will be in one of these two states. If an unrecoverable error occurs, such as session expiration or authentication failure, or if the application explicitly closes the handle, the handle will move to the CLOSED state. The following figure shows the possible state transitions of a ZooKeeper client:

ZooKeeper客户端创建句柄通过bind绑定与ZooKeeper服务建立会话。创建句柄后，该句柄将开始处于CONNECTING状态，客户端尝试连接到ZooKeeper集群中的其中一个服务器，连接成功后它将切换为CONNECTED状态。在正常操作期间，客户端句柄将处于这两种状态之一。如果发生不可恢复的错误，例如会话到期或身份验证失败，或者如果应用程序显式关闭了句柄，则该句柄将移至CLOSED状态。下图显示了ZooKeeper客户端可能的状态转换：

![State transitions](programmer%20guide.assets/state_dia.jpg)

> To create a client session the application code must provide a connection string containing a comma separated list of host:port pairs, each corresponding to a ZooKeeper server (e.g. "127.0.0.1:4545" or "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"). The ZooKeeper client library will pick an arbitrary server and try to connect to it. If this connection fails, or if the client becomes disconnected from the server for any reason, the client will automatically try the next server in the list, until a connection is (re-)established.

应用程序代码必须提供一个连接字符串，包含用逗号分隔的host:port，每一个地址都对应一台zookeeper服务器，比如"127.0.0.1:4545" 或 "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"。ZooKeeper客户端库将选择一个任意服务器并尝试连接到该服务器。如果此连接失败，或者客户端由于任何原因与服务器断开连接，则客户端将自动尝试列表中的下一个服务器，直到（重新）建立连接为止。

**3.2.0增加**：可以在连接字符串中追加“chroot”后缀。当执行所有与这个根目录相关的路径时，将执行客户端命令。比如使用（"127.0.0.1:4545/app/a" or "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a"）时，所有的路径都是以/app/a为根目录。 "/foo/bar"将被解析为"/app/a/foo/bar"（从客户端的角度）。这个功能在多用户环境中特别有用，特定ZooKeeper服务器中的每个用户都可以有不同的根目录。当用户的应用程序使用/斜杠作为根目录时，可以被更好的复用，而真实的路径比如/app/a可以在运行时在决定。

> When a client gets a handle to the ZooKeeper service, ZooKeeper creates a ZooKeeper session, represented as a 64-bit number, that it assigns to the client. If the client connects to a different ZooKeeper server, it will send the session id as a part of the connection handshake. As a security measure, the server creates a password for the session id that any ZooKeeper server can validate.The password is sent to the client with the session id when the client establishes the session. The client sends this password with the session id whenever it reestablishes the session with a new server.
>
> One of the parameters to the ZooKeeper client library call to create a ZooKeeper session is the session timeout in milliseconds. The client sends a requested timeout, the server responds with the timeout that it can give the client. The current implementation requires that the timeout be a minimum of 2 times the tickTime (as set in the server configuration) and a maximum of 20 times the tickTime. The ZooKeeper client API allows access to the negotiated timeout.

当客户端获得ZooKeeper服务的句柄时，ZooKeeper会分配给客户端一个64位数字的ZooKeeper会话ID。如果客户端连接到其他ZooKeeper服务器，它将发送会话ID作为连接握手的一部分。为了安全起见，服务器会为会话ID创建一个任何ZooKeeper服务器都可以验证的密码。客户端建立会话时，会将密码和会话ID发送给客户端。每当客户端与新服务器重新建立会话时，客户端都会发送此密码以及会话ID给新的服务器。

Zookeeper客户端用来创建会话的方法的**其中一个参数就是会话超时时间（毫秒为单位）**，客户端请求超时，服务器会返回一个超时响应。当前的实现要求超时至少是tickTime的2倍（在服务器配置中设置），最大是tickTime的20倍。ZooKeeper客户端API允协商的超时的时间。

> When a client (session) becomes partitioned from the ZK serving cluster it will begin searching the list of servers that were specified during session creation. Eventually, when connectivity between the client and at least one of the servers is re-established, the session will either again transition to the "connected" state (if reconnected within the session timeout value) or it will transition to the "expired" state (if reconnected after the session timeout). It is not advisable to create a new session object (a new ZooKeeper.class or zookeeper handle in the c binding) for disconnection. The ZK client library will handle reconnect for you. In particular we have **heuristics** built into the client library to handle things like "herd effect", etc... Only create a new session when you are notified of session expiration (mandatory).
>
> Session expiration is managed by the ZooKeeper cluster itself, not by the client. When the ZK client establishes a session with the cluster it provides a "timeout" value detailed above. This value is used by the cluster to determine when the client's session expires. Expirations happens when the cluster does not hear from the client within the specified session timeout period (i.e. no heartbeat). At session expiration the cluster will delete any/all ephemeral nodes owned by that session and immediately notify any/all connected clients of the change (anyone watching those znodes). At this point the client of the expired session is still disconnected from the cluster, it will not be notified of the session expiration until/unless it is able to re-establish a connection to the cluster. The client will stay in disconnected state until the TCP connection is re-established with the cluster, at which point the watcher of the expired session will receive the "session expired" notification.

当客户端（会话）从ZK服务群集中断开时，它将开始搜索在会话创建过程中指定的服务器列表。最终，当客户机和至少一个服务器之间重新建立连接时，会话将再次过渡到“已连接”状态(如果在会话超时值内重新连接)，或者过渡到“过期”状态(如果在会话超时后重新连接)。不建议在断开连接时创建一个新的会话对象（new ZooKeeper.class或zookeeper句柄）。ZK客户端库自动处理重新连接。特别是，在客户端库中内置了**启发式**方法来处理“群效应”之类的问题，仅在收到会话到期通知时（强制性），才创建一个新会话。

会话过期由ZooKeeper集群本身管理，而不是由客户机管理。当ZK客户端与群集建立会话时，它将提供上面详述的“超时”时间。集群使用此值来确定客户端会话何时过期。当群集在指定的会话超时时间内未从客户端收到消息时，就认为客户端已经断开（没有心跳 Heartbreak）。在会话超期时，集群将删除该会话拥有的任何/所有临时节点Ephemeral Nodes，并立即将该更改通知任何/所有已连接的客户端（任何监听这些znode的客户端）。客户端与集群断开连接后，除非重新建立连接，否则不会再收到超期通知。客户端将保持断开连接状态，直到集群重新建立TCP连接，此时过期会话的监听程序将收到“会话过期”通知。

简单说就是，断开的连接是无法接收到会话超时的通知的。而没有断开的连接则会在会话超时时马上接收到通知。

会话超时的状态转换如下

1. “connected”，客户端与集群建立连接
2. ......客户端从集群中断开（partitioned）
3. “disconnected”：客户端失去与集群的连接
4. .......当超过timeout的时间后，集群终止会话。由于客户端断开连接，所以没有接收到任何通知
5. .......客户端重新获得集群网络水平的连接
6. “expired”，客户端与集群重新建立连接，然后接收到超期通知

> Another parameter to the ZooKeeper session establishment call is the default watcher. Watchers are notified when any state change occurs in the client. For example if the client loses connectivity to the server the client will be notified, or if the client's session expires, etc... This watcher should consider the initial state to be disconnected (i.e. before any state changes events are sent to the watcher by the client lib). In the case of a new connection, the first event sent to the watcher is typically the session connection event.
>
> The session is kept alive by requests sent by the client. If the session is idle for a period of time that would timeout the session, the client will send a PING request to keep the session alive. This PING request not only allows the ZooKeeper server to know that the client is still active, but it also allows the client to verify that its connection to the ZooKeeper server is still active. The timing of the PING is conservative enough to ensure reasonable time to detect a dead connection and reconnect to a new server.
>
> Once a connection to the server is successfully established (connected) there are basically two cases where the client lib generates connectionloss (the result code in c binding, exception in Java -- see the API documentation for binding specific details) when either a synchronous or asynchronous operation is performed and one of the following holds:
>
> 1. The application calls an operation on a session that is no longer alive/valid
> 2. The ZooKeeper client disconnects from a server when there are pending operations to that server, i.e., there is a pending asynchronous call.

ZooKeeper会话建立过程传的另一个参数是watcher监听器。当客户端的任何修改，监听器都能接收到通知。例如，如果客户端失去与服务器的连接，则会收到通知，或者客户端的会话超时。监听器将“disconnected ”作为初始状态。新连接建立后，发送给watcher的第一个事件通常是会话连接（session connection）事件。

客户端发送的请求使会话保持活动状态。如果会话空闲一段时间会使会话超时，则客户端将发送PING请求以使会话保持活动状态。通过PING请求不仅使ZooKeeper服务器知道客户端仍处于活动状态，而且使客户端验证其与ZooKeeper服务器的连接仍处于活动状态。PING的时间足够保守，以确保有足够的时间来检测死连接以及客户端重新连接到新服务器。

与服务器的连接成功建立（连接）后，当执行同步或异步操作并且满足以下两种情况之一时，客户端库会失去连接（binding异常）：

* 应用程序在失效的会话上调用操作
* 当该服务器有待处理的操作时，即存在一个待处理的异步调用，ZooKeeper客户端将从服务器断开连接。

> **Added in 3.2.0 -- SessionMovedException**. There is an internal exception that is generally not seen by clients called the SessionMovedException. This exception occurs because a request was received on a connection for a session which has been reestablished on a different server. The normal cause of this error is a client that sends a request to a server, but the network packet gets delayed, so the client times out and connects to a new server. When the delayed packet arrives at the first server, the old server detects that the session has moved, and closes the client connection. Clients normally do not see this error since they do not read from those old connections. (Old connections are usually closed.) One situation in which this condition can be seen is when two clients try to reestablish the same connection using a saved session id and password. One of the clients will reestablish the connection and the second client will be disconnected (causing the pair to attempt to re-establish its connection/session indefinitely).

**3.2.0新增--SessionMovedException** 客户端通常看不见SessionMovedException的。已经在另一台服务器上重新建立连接的会话接收到一个请求时，就会发生此异常。通常导致这个错误是因为客户端向服务器发送请求，但是网络数据包被延迟，而客户端由于与集群断开而重新连接到新服务器。当延迟的数据包到达原来的服务器时，原来的服务器检测到这个会话已被转移，因此关闭客户端连接。客户端通常不会看到此错误，因为它们不会从那些旧连接中读取数据。(旧的连接通常是关闭的。)另一种情况是，两个客户端尝试使用保存的会话ID和密码来重新建立相同的连接。其中一个客户端将重新建立连接，而第二个客户端将断开连接（导致它会无限期地尝试重新建立连接/会话）

> **Updating the list of servers**. We allow a client to update the connection string by providing a new comma separated list of host:port pairs, each corresponding to a ZooKeeper server. The function invokes a probabilistic load-balancing algorithm which may cause the client to disconnect from its current host with the goal to achieve expected uniform number of connections per server in the new list. In case the current host to which the client is connected is not in the new list this call will always cause the connection to be dropped. Otherwise, the decision is based on whether the number of servers has increased or decreased and by how much.
>
> For example, if the previous connection string contained 3 hosts and now the list contains these 3 hosts and 2 more hosts, 40% of clients connected to each of the 3 hosts will move to one of the new hosts in order to balance the load. The algorithm will cause the client to drop its connection to the current host to which it is connected with probability 0.4 and in this case cause the client to connect to one of the 2 new hosts, chosen at random.
>
> Another example -- suppose we have 5 hosts and now update the list to remove 2 of the hosts, the clients connected to the 3 remaining hosts will stay connected, whereas all clients connected to the 2 removed hosts will need to move to one of the 3 hosts, chosen at random. If the connection is dropped, the client moves to a special mode where he chooses a new server to connect to using the probabilistic algorithm, and not just round robin.
>
> In the first example, each client decides to disconnect with probability 0.4 but once the decision is made, it will try to connect to a random new server and only if it cannot connect to any of the new servers will it try to connect to the old ones. After finding a server, or trying all servers in the new list and failing to connect, the client moves back to the normal mode of operation where it picks an arbitrary server from the connectString and attempts to connect to it. If that fails, it will continue trying different random servers in round robin. (see above the algorithm used to initially choose a server)

更新服务器列表。客户端通过提供一个新的以逗号分隔的地址（host:port）来更新连接字符串，每个地址分别对应于一个ZooKeeper服务器。该函数会调用概率负载均衡算法，该算法可能导致客户端从其当前主机断开连接，以实现在新的服务器列表中均衡每台服务器的连接数。如果客户端连接的当前主机不在新列表中，那么更新地址将导致该客户端连接断开。否则，将基于服务器数量是增加还是减少以及增减的数量来做出决定。

例如，如果以前的连接字符串包含3个服务器，而现在列表包含这3个服务器和另外2个服务器，原来3台服务器中的每台服务器的40％的客户端将移至新两条服务器上，以平衡负载。该算法将导致40%客户端断开与当前服务器的连接，同时也导致这些客户端连接至新的任意一台服务器上。

另一个例子，假设我们有5台服务器，现在新列表删除了其中2台服务器，连接到其余3台服务器的客户端保持连接状态，而连接到2台被删除的服务器上的所有客户端将重新连接至被保留的任意一台服务器上。如果断开连接，客户端将进入一种特殊模式，在该模式下，客户端将使用概率算法连接至一台新的服务器，而不是轮询。

在第一个示例中，每个客户端有40%概率断开连接，但是一旦做出决定，它将尝试连接到随机的新服务器，并且只有当它无法连接到任何新服务器时，它才会尝试连接到旧服务器。当通过概率算法找到一台服务器，或尝试新列表中的所有服务器并且都无法连接后，客户端将返回正常操作模式，在该模式下，客户端从连接字符串中选择随机的服务器并尝试连接至该服务器。如果失败，它将继续循环尝试其他的服务器。

> **Local session**. Added in 3.5.0, mainly implemented by [ZOOKEEPER-1147](https://issues.apache.org/jira/browse/ZOOKEEPER-1147).
>
> - Background: The creation and closing of sessions are costly in ZooKeeper because they need quorum confirmations, they become the bottleneck of a ZooKeeper ensemble when it needs to handle thousands of client connections. So after 3.5.0, we introduce a new type of session: local session which doesn't have a full functionality of a normal(global) session, this feature will be available by turning on *localSessionsEnabled*.
>
> when *localSessionsUpgradingEnabled* is disable:
>
> - Local sessions cannot create ephemeral nodes
> - Once a local session is lost, users cannot re-establish it using the session-id/password, the session and its watches are gone for good. Note: Losing the tcp connection does not necessarily imply that the session is lost. If the connection can be reestablished with the same zk server before the session timeout then the client can continue (it simply cannot move to another server).
> - When a local session connects, the session info is only maintained on the zookeeper server that it is connected to. The leader is not aware of the creation of such a session and there is no state written to disk.
> - The pings, expiration and other session state maintenance are handled by the server which current session is connected to.

**Local session**. 在3.5.0新增，通过 [ZOOKEEPER-1147](https://issues.apache.org/jira/browse/ZOOKEEPER-1147) 实现：

* 背景：在ZooKeeper中，会话的创建和关闭非常昂贵，因为它们需要仲裁(quorum)确认，当需要处理数千个客户端连接时，它们成为ZooKeeper集群性能的瓶颈。所以在3.5.0后，引入新的会话类型：local session本地会话，它不需要实现一个正常会话的完整功能，这个特性可以通过*localSessionsEnabled*设置

当关闭localSessionsUpgradingEnabled*时：

* 本地会话无法创建临时节点
* 一旦本地会话丢失，用户将无法使用会话ID /密码重新建立会话，该会话及其watcher监听器将永久消失。注意：丢失tcp连接并不一定意味着会话已丢失。如果可以在会话超时之前可以同一台zk服务器重新建立连接，则客户端可以继续连接（它根本无法移动到另一台服务器）。
* 当本地会话连接时，会话信息仅在与其连接的Zookeeper服务器上维护。leader不知道此类会话的创建，也没有将状态写入磁盘。ping，到期和其他会话状态维护由当前会话连接的服务器处理。

当开启*localSessionsUpgradingEnabled*时：

* 本地会话可以自动升级到全局会话。
* 创建新会话后，用LocalSessionTracker封装并 保存至本地。随后可以根据需要升级至全局会话（如创建临时节点）。如果需要升级，则将会话从本地集合中删除，同时保留相同的会话ID。
* 当前，只有创建临时节点才需要将会话从本地升级到全局。原因是临时节点的创建在很大程度上依赖于全局会话。如果本地会话可以在不升级到全局会话的情况下创建临时节点，则将导致不同节点之间的数据不一致。leader还需要了解会话的生命周期，以清理关闭/到期的临时节点。这就需要本地会话升级至全局会话，因为本地会话只绑定到其特定的服务器。
* 会话在升级过程中既可以是本地会话，也可以是全局会话，但是不能由两个线程同时调用升级操作。
* ZooKeeperServer（单机版）使用SessionTrackerImpl; LeaderZookeeper使用LeaderSessionTracker，其中包含SessionTrackerImpl（global）和LocalSessionTracker（如果启用），FollowerZooKeeperServer和ObserverZooKeeperServer使用拥有LocalSessionTracker的LearnerSessionTracker。session的UML类图：

+----------------+ +--------------------+ +---------------------+ | | --> | | ----> | LocalSessionTracker | | SessionTracker | | SessionTrackerImpl | +---------------------+ | | | | +-----------------------+ | | | | +-------------------------> | LeaderSessionTracker | +----------------+ +--------------------+ | +-----------------------+ | | | | | | | +---------------------------+ +---------> | | | UpgradeableSessionTracker | | | | | ------------------------+ +---------------------------+ | | | v +-----------------------+ | LearnerSessionTracker | +-----------------------+

> - Q&A
> - What's the reason for having the config option to disable local session upgrade?
>   - In a large deployment which wants to handle a very large number of clients, we know that clients connecting via the observers which is supposed to be local session only. So this is more like a safeguard against someone accidentally creates lots of ephemeral nodes and global sessions.
> - *When is the session created?*
>   - In the current implementation, it will try to create a local session when processing *ConnectRequest* and when *createSession* request reaches *FinalRequestProcessor*.
> - *What happens if the create for session is sent at server A and the client disconnects to some other server B which ends up sending it again and then disconnects and connects back to server A?*
>   - When a client reconnects to B, its sessionId won’t exist in B’s local session tracker. So B will send validation packet. If CreateSession issued by A is committed before validation packet arrive the client will be able to connect. Otherwise, the client will get session expired because the quorum hasn’t know about this session yet. If the client also tries to connect back to A again, the session is already removed from local session tracker. So A will need to send a validation packet to the leader. The outcome should be the same as B depending on the timing of the request.

* 问题（Q/A）
* 什么情况下需要禁用 *local session upgrade*
  * 在想要处理大量客户端的大型部署中，客户端通过（observers）观察者进行连接，而（observers）观察者应该仅是本地会话。因此，这更像是防止有人意外创建大量临时节点和全局会话的防护措施。
* 会话何时创建？
  * 在当前实现中，它将在处理ConnectRequest以及createSession请求到达FinalRequestProcessor时尝试创建本地会话。
* 如果会话的创建是在服务器A上发送的，并且客户端断开与其他服务器B的连接，而服务器B最终又再次发送了该消息，然后断开并重新连接回服务器A，该怎么办？
  * 当客户端重新连接到B时，其sessionId在不存在于B的本地会话跟踪器中。因此，B将发送验证数据包。如果在验证数据包到达之前提交了由A发出的CreateSession，则客户端将可以连接。否则，客户端会话将过期，因为仲裁（quorum）还不知道这个seesion。如果客户端也尝试再次连接回A，则该会话已从本地会话跟踪器中删除。因此，A将需要将验证数据包发送给leader。根据请求的时间，结果应与B相同。

## ZooKeeper Watches

> All of the read operations in ZooKeeper - **getData()**, **getChildren()**, and **exists()** - have the option of setting a watch as a side effect. Here is ZooKeeper's definition of a watch: a watch event is one-time trigger, sent to the client that set the watch, which occurs when the data for which the watch was set changes. There are three key points to consider in this definition of a watch:
>
> - **One-time trigger** One watch event will be sent to the client when the data has changed. For example, if a client does a getData("/znode1", true) and later the data for /znode1 is changed or deleted, the client will get a watch event for /znode1. If /znode1 changes again, no watch event will be sent unless the client has done another read that sets a new watch.
> - **Sent to the client** This implies that an event is on the way to the client, but may not reach the client before the successful return code to the change operation reaches the client that initiated the change. Watches are sent asynchronously to watchers. ZooKeeper provides an ordering guarantee: a client will never see a change for which it has set a watch until it first sees the watch event. Network delays or other factors may cause different clients to see watches and return codes from updates at different times. The key point is that everything seen by the different clients will have a consistent order.
> - **The data for which the watch was set** This refers to the different ways a node can change. It helps to think of ZooKeeper as maintaining two lists of watches: data watches and child watches. getData() and exists() set data watches. getChildren() sets child watches. Alternatively, it may help to think of watches being set according to the kind of data returned. getData() and exists() return information about the data of the node, whereas getChildren() returns a list of children. Thus, setData() will trigger data watches for the znode being set (assuming the set is successful). A successful create() will trigger a data watch for the znode being created and a child watch for the parent znode. A successful delete() will trigger both a data watch and a child watch (since there can be no more children) for a znode being deleted as well as a child watch for the parent znode.
>
> Watches are maintained locally at the ZooKeeper server to which the client is connected. This allows watches to be lightweight to set, maintain, and dispatch. When a client connects to a new server, the watch will be triggered for any session events. Watches will not be received while disconnected from a server. When a client reconnects, any previously registered watches will be reregistered and triggered if needed. In general this all occurs transparently. There is one case where a watch may be missed: a watch for the existence of a znode not yet created will be missed if the znode is created and deleted while disconnected.
>
> **New in 3.6.0:** Clients can also set permanent, recursive watches on a znode that are not removed when triggered and that trigger for changes on the registered znode as well as any children znodes recursively.

ZooKeeper中的所有读取操作，**getData()**, **getChildren()**, 和**exists()** ，并且可以 选择设置一个watcher监听器。以下是watcher的定义，watch event是一个one-time trigger（一次性的触发器），当watch监听的数据改变时，会发送通知给客户端。以下是watch监听器三个核心关键的三个点：

* **one-time-trigger:**当数据改变时，会向客户端发送watch事件。比如，如果一个客户端执行`getData('/znode1',true)`，然后`/znode1`的数据被修改或删除，客户端会接收到一个关于`/znode1`watch事件.如果`/znode1`再次改变，除非客户端重新设置一个新的watch监听器，否则不会再接收到watch事件。
* **sent to client：** 这表明了一个事件正在发送给客户端，但是还没有到达客户端，即在修改操作返回成功代码到达引发修改的客户端之前。watch发送异步的watcher监听器 。zookeeper提供顺序保证，一个客户端在没有第一次接受到watch事件之前，客户端无法感知修改。服务器延迟或者其他因素可能会导致不同的客户端在不同的时间接收到watch事件和返回值。关键点在于所有客户端接收到的信息会保证顺序一致。
* **The data for which the watch was set** 设置了监听器的数据：这指的是一个节点会以不同的方式改变。这对zookeeper维护两种watches列表有帮助：数据监听器和子监听器。`getData() 和exists()` 设置数据监听器。`getChildren()`设置子监听器。或者，可以考虑根据返回的数据类型设置watch监听器。`getData（）和exist（）`返回有关节点数据的信息，而`getChildren（）`返回子级列表。因此，setData（）将触发数据监听是否设置了znode（假设设置成功）。成功的create（）将触发正在创建的znode的数据监视，并触发父znode的子监听。成功的delete（）将同时触发要删除的znode的数据监听和子监听（因为不可能有更多的子监视）以及父znode的子监听。

watch由客户端连接到的ZooKeeper服务器上维护。这使watch可以轻量级地设置，维护和调度。当客户端连接到新服务器时，所有会话事件都会触发watch。当与服务器断开时，不会接受到watch。客户端重新连接时，任何先前注册的watch将被重新注册并在需要时触发。通常，所有这些都是透明发生的。在某些情况下，可能会丢失watch监听：如果在断开连接时创建并删除了znode，则会丢失存在但没有创建的znode的监听。

3.6.0中的新增功能：客户端还可以在znode上设置永久性、递归的监听，这些监听在触发时不会被删除，并且会以递归方式触发注册的znode以及所有子znode的更改。

### Semantics of Watches

> We can set watches with the three calls that read the state of ZooKeeper: exists, getData, and getChildren. The following list details the events that a watch can trigger and the calls that enable them:
>
> - **Created event:** Enabled with a call to exists.
> - **Deleted event:** Enabled with a call to exists, getData, and getChildren.
> - **Changed event:** Enabled with a call to exists and getData.
> - **Child event:** Enabled with a call to exists, getData.

我们可以通过调用ZooKeeper状态时设置手表：exist，getData和getChildren。以下列表详细说明了watch可以触发的事件以及触发事件的方法：

* **Created event:**调用exists
* **Deleted event:**调用exists, getData, and getChildren.
* **Changed event:** 调用exists, getData
* **Child event:** 调用exists, getData

### Persistent, Recursive Watches

> **New in 3.6.0:** There is now a variation on the standard watch described above whereby you can set a watch that does not get removed when triggered. Additionally, these watches trigger the event types *NodeCreated*, *NodeDeleted*, and *NodeDataChanged* and, optionally, recursively for all znodes starting at the znode that the watch is registered for. Note that *NodeChildrenChanged* events are not triggered for persistent recursive watches as it would be redundant.
>
> Persistent watches are set using the method *addWatch()*. The triggering semantics and guarantees (other than one-time triggering) are the same as standard watches. The only exception regarding events is that recursive persistent watchers never trigger child changed events as they are redundant. Persistent watches are removed using *removeWatches()* with watcher type *WatcherType.Any*.

3.6.0中的新增功能：对上述的watch标准有所改动，你可以设置当事件触发时watch不被移除。此外，这些监视会触发事件类型NodeCreated，NodeDeleted和NodeDataChanged，并还可以选择从配置znode监听器 递归触发监听器。请注意，永久的递归监听器不会触发NodeChildrenChanged事件，因为这将是多余的。

使用方法addWatch（）设置永久性监视器。它的触发语义和保证性（一次性触发除外）与标准watch相同。关于事件的唯一例外是，递归永久性监听器永远不会触发子节点的更改事件（changed events ），因为它们是多余的。使用WatcherType.Any类型的监听器可以调用removeWatches（）删除永久性监听器。

### Remove Watches

> We can remove the watches registered on a znode with a call to removeWatches. Also, a ZooKeeper client can remove watches locally even if there is no server connection by setting the local flag to true. The following list details the events which will be triggered after the successful watch removal.
>
> - **Child Remove event:** Watcher which was added with a call to getChildren.
> - **Data Remove event:** Watcher which was added with a call to exists or getData.
> - **Persistent Remove event:** Watcher which was added with a call to add a persistent watch.

可以通过调用removeWatches来删除在znode上注册的watch。另外，即使没有服务器连接，ZooKeeper客户端也可以通过将本地标志设置为true来在本地删除watch。以下列表详细说明了成功删除watch后将触发的事件。

	* **Child Remove event:** 通过执行getChildren添加的watch
	* **Data Remove event:**  通过exists或者getData添加的watch。
	* **Persistent Remove event:** 通过调用add添加persistent watch

### What ZooKeeper Guarantees about Watches

> With regard to watches, ZooKeeper maintains these guarantees:
>
> - Watches are ordered with respect to other events, other watches, and asynchronous replies. The ZooKeeper client libraries ensures that everything is dispatched in order.
> - A client will see a watch event for a znode it is watching before seeing the new data that corresponds to that znode.
> - The order of watch events from ZooKeeper corresponds to the order of the updates as seen by the ZooKeeper service.

关于手表，ZooKeeper保持以下保证：

 * watch是根据其他event，其他watch和异步响应来排序的。zookeeper客户端的类库确保所有数据按顺序分发。
 * 客户端先接收到正在监视的znode的事件，然后才能查看该znode对应的新数据。
 * ZooKeeper中监视事件的顺序与ZooKeeper服务的更新顺序一致。

### Things to Remember about Watches

> - Standard watches are one time triggers; if you get a watch event and you want to get notified of future changes, you must set another watch.
> - Because standard watches are one time triggers and there is latency between getting the event and sending a new request to get a watch you cannot reliably see every change that happens to a node in ZooKeeper. Be prepared to handle the case where the znode changes multiple times between getting the event and setting the watch again. (You may not care, but at least realize it may happen.)
> - A watch object, or function/context pair, will only be triggered once for a given notification. For example, if the same watch object is registered for an exists and a getData call for the same file and that file is then deleted, the watch object would only be invoked once with the deletion notification for the file.
> - When you disconnect from a server (for example, when the server fails), you will not get any watches until the connection is reestablished. For this reason session events are sent to all outstanding watch handlers. Use session events to go into a safe mode: you will not be receiving events while disconnected, so your process should act conservatively in that mode.

* 标准的watch都只会触发一次，如果你接收到一个watch事件后，如果还想监听后续的变化，必须重新设置一个watch
* 因为标准手表是一次触发，并且在获取事件后和重新发送新请求设置watch之间存在延迟，所以您无法可靠地看到ZooKeeper中节点发生的每项更改。



## ZooKeeper access control using ACLs

> ZooKeeper uses ACLs to control access to its znodes (the data nodes of a ZooKeeper data tree). The ACL implementation is quite similar to UNIX file access permissions: it employs permission bits to allow/disallow various operations against a node and the scope to which the bits apply. Unlike standard UNIX permissions, a ZooKeeper node is not limited by the three standard scopes for user (owner of the file), group, and world (other). ZooKeeper does not have a notion of an owner of a znode. Instead, an ACL specifies sets of ids and permissions that are associated with those ids.
>
> Note also that an ACL pertains only to a specific znode. In particular it does not apply to children. For example, if */app* is only readable by ip:172.16.16.1 and */app/status* is world readable, anyone will be able to read */app/status*; ACLs are not recursive.
>
> ZooKeeper supports pluggable authentication schemes. Ids are specified using the form *scheme:expression*, where *scheme* is the authentication scheme that the id corresponds to. The set of valid expressions are defined by the scheme. For example, *ip:172.16.16.1* is an id for a host with the address *172.16.16.1* using the *ip* scheme, whereas *digest:bob:password* is an id for the user with the name of *bob* using the *digest* scheme.
>
> When a client connects to ZooKeeper and authenticates itself, ZooKeeper associates all the ids that correspond to a client with the clients connection. These ids are checked against the ACLs of znodes when a client tries to access a node. ACLs are made up of pairs of *(scheme:expression, perms)*. The format of the *expression* is specific to the scheme. For example, the pair *(ip:19.22.0.0/16, READ)* gives the *READ* permission to any clients with an IP address that starts with 19.22.

ZooKeeper使用ACL来控制对其znode（ZooKeeper数据树的数据节点）的访问。ACL的实现与UNIX系统文件的访问权限非常相似。它使用权限标志bit来允许/禁止针对节点及其所适用范围的各种操作。与标准UNIX权限不同，ZooKeeper节点不受用户（文件所有者），组和全局（其他）这三个标准范围的限制。ZooKeeper没有znode拥有者的概念。而是，ACL指定一组ID和与这些ID相关联的权限。

还请注意，ACL仅与特定的znode有关。特别是它不适用于子节点。例如，如果/ app仅可由ip：172.16.16.1读取，而/ app / status是全局可读的，则任何人都可以读取/ app / status； ACL不是递归的。

ZooKeeper支持插入式的身份验证方案。使用格式scheme：expression指定ID，其中scheme是ID所对应的身份验证方案。有效的expression由scheme定义。例如，ip:172.16.16.1是使用ip scheme并且服务器地址为172.16.16.1的id。而digest：bob：password是使用 digest scheme的名为bob的服务器名称。

当客户端连接到ZooKeeper并对其进行身份验证时，ZooKeeper会将与该客户端相对应的所有ID与该客户端连接相关联。当客户端尝试访问节点时，将根据znodes的ACL检查这些ID。ACL由成对的（scheme：expression，perms）组成。expression的格式特定于该scheme。例如，（ip：19.22.0.0/16，READ）为IP地址以19.22开头的任何客户端提供READ权限。

### ACL Permissions

> ZooKeeper supports the following permissions:
>
> - **CREATE**: you can create a child node
> - **READ**: you can get data from a node and list its children.
> - **WRITE**: you can set data for a node
> - **DELETE**: you can delete a child node
> - **ADMIN**: you can set permissions
>
> The *CREATE* and *DELETE* permissions have been broken out of the *WRITE* permission for finer grained access controls. The cases for *CREATE* and *DELETE* are the following:
>
> You want A to be able to do a set on a ZooKeeper node, but not be able to *CREATE* or *DELETE* children.
>
> *CREATE* without *DELETE*: clients create requests by creating ZooKeeper nodes in a parent directory. You want all clients to be able to add, but only request processor can delete. (This is kind of like the APPEND permission for files.)
>
> Also, the *ADMIN* permission is there since ZooKeeper doesn’t have a notion of file owner. In some sense the *ADMIN* permission designates the entity as the owner. ZooKeeper doesn’t support the LOOKUP permission (execute permission bit on directories to allow you to LOOKUP even though you can't list the directory). Everyone implicitly has LOOKUP permission. This allows you to stat a node, but nothing more. (The problem is, if you want to call zoo_exists() on a node that doesn't exist, there is no permission to check.)
>
> *ADMIN* permission also has a special role in terms of ACLs: in order to retrieve ACLs of a znode user has to have *READ* or *ADMIN* permission, but without *ADMIN* permission, digest hash values will be masked out.

zookeeper支持以下权限：

* create ：创建子节点
* read : 读取节点的数据以及子节点列表
* write ：设置节点的数据
* delete ：删除子节点
* admin ： 设置权限

create 和 delete 权限已经与 write 权限分离，以获得更细粒度的访问控制。使用 create 和 delete 用例如下：

创建但不删除：客户端通过在父目录中创建ZooKeeper节点来创建请求。所有的客户端都可以添加，但只有请求的处理能被删除？？？

另外，由于ZooKeeper没有文件所有者的概念，因此具有ADMIN权限。从某种意义上说，ADMIN权限将实体指定为所有者。ZooKeeper不支持LOOKUP权限（修改目录的权限，即使您无法列出目录也可以使您进行LOOKUP操作）。每个人都隐式具有LOOKUP权限。这使您可以统计节点，但仅此而已。 （问题是，如果要在不存在的节点上调用zoo_exists（），则不会检查权限。）

就ACL而言，ADMIN权限也具有特殊作用：为了检索znode用户的ACL，必须具有READ或ADMIN权限。如果没有ADMIN权限，digest哈希 值将被屏蔽。

> **Builtin ACL Schemes**
>
> ZooKeeeper has the following built in schemes:
>
> - **world** has a single id, *anyone*, that represents anyone.
> - **auth** is a special scheme which ignores any provided expression and instead uses the current user, credentials, and scheme. Any expression (whether *user* like with SASL authentication or *user:password* like with DIGEST authentication) provided is ignored by the ZooKeeper server when persisting the ACL. However, the expression must still be provided in the ACL because the ACL must match the form *scheme:expression:perms*. This scheme is provided as a convenience as it is a common use-case for a user to create a znode and then restrict access to that znode to only that user. If there is no authenticated user, setting an ACL with the auth scheme will fail.
> - **digest** uses a *username:password* string to generate MD5 hash which is then used as an ACL ID identity. Authentication is done by sending the *username:password* in clear text. When used in the ACL the expression will be the *username:base64* encoded *SHA1* password *digest*.
> - **ip** uses the client host IP as an ACL ID identity. The ACL expression is of the form *addr/bits* where the most significant *bits* of *addr* are matched against the most significant *bits* of the client host IP.
> - **x509** uses the client X500 Principal as an ACL ID identity. The ACL expression is the exact X500 Principal name of a client. When using the secure port, clients are automatically authenticated and their auth info for the x509 scheme is set.

ACL（Access Control List）内置方案

zookeeper内置方案如下：

* world ：单一编号，代表任何用户

* auth ： 一个特殊的方案，它忽略任何的权限表达式，而使用当前的用户、证书、scheme方案。当使用ACL时，任何的权限表达式（包括想SASL认证或者使用user:password的DIGEST认证）都会被zookeeper忽略。但依旧需要在ACL中提供权限表达式，因为ACL的格式必须符合*scheme:expression:perms*.。这种方式十分便利，举个例子，用户创建一个znode然后限制只有该用户才能访问这个节点。如果用户没有经过身份验证，则使用auth scheme设置ACL将失败。

* digest：使用 *username:password*字符串生产MD5哈希值，这个哈希值用作ACL 的id身份标识。通过以明文形式发送username：password来完成身份验证。在ACL中使用digest，是以username：base64编码的SHA1密码摘要。

* ip : 用户使用服务器IP作为ACL id身份标识。它的ACL表达式的格式是addr/bits。addr的最高有效位bits与客户端主机的ip的最高有效位匹配。

* X509: 使用客户端X500主体作为ACL id身份标识。其表达式是客户端X500主体名称。当使用安全端口时，将自动对客户端进行身份验证，并设置其x509方案的身份验证信息。

* > * X.500是，构成全球分布式的名录服务系统的协议。
  >
  > - javax.security.auth.x500.X500Principal

> #### ZooKeeper C client API
>
> The following constants are provided by the ZooKeeper C library:
>
> - *const* *int* ZOO_PERM_READ; //can read node’s value and list its children
> - *const* *int* ZOO_PERM_WRITE;// can set the node’s value
> - *const* *int* ZOO_PERM_CREATE; //can create children
> - *const* *int* ZOO_PERM_DELETE;// can delete children
> - *const* *int* ZOO_PERM_ADMIN; //can execute set_acl()
> - *const* *int* ZOO_PERM_ALL;// all of the above flags OR’d together
>
> The following are the standard ACL IDs:
>
> - *struct* Id ZOO_ANYONE_ID_UNSAFE; //(‘world’,’anyone’)
> - *struct* Id ZOO_AUTH_IDS;// (‘auth’,’’)
>
> ZOO_AUTH_IDS empty identity string should be interpreted as “the identity of the creator”.
>
> ZooKeeper client comes with three standard ACLs:
>
> - *struct* ACL_vector ZOO_OPEN_ACL_UNSAFE; //(ZOO_PERM_ALL,ZOO_ANYONE_ID_UNSAFE)
> - *struct* ACL_vector ZOO_READ_ACL_UNSAFE;// (ZOO_PERM_READ, ZOO_ANYONE_ID_UNSAFE)
> - *struct* ACL_vector ZOO_CREATOR_ALL_ACL; //(ZOO_PERM_ALL,ZOO_AUTH_IDS)
>
> The ZOO_OPEN_ACL_UNSAFE is completely open free for all ACL: any application can execute any operation on the node and can create, list and delete its children. The ZOO_READ_ACL_UNSAFE is read-only access for any application. CREATE_ALL_ACL grants all permissions to the creator of the node. The creator must have been authenticated by the server (for example, using “*digest*” scheme) before it can create nodes with this ACL.
>
> The following ZooKeeper operations deal with ACLs:
>
> - *int* *zoo_add_auth* (zhandle_t *zh,*const* *char** scheme,*const* *char** cert, *int* certLen, void_completion_t completion, *const* *void* *data);
>
> The application uses the zoo_add_auth function to authenticate itself to the server. The function can be called multiple times if the application wants to authenticate using different schemes and/or identities.
>
> - *int* *zoo_create* (zhandle_t *zh, *const* *char* *path, *const* *char* *value,*int* valuelen, *const* *struct* ACL_vector *acl, *int* flags,*char* *realpath, *int* max_realpath_len);
>
> zoo_create(...) operation creates a new node. The acl parameter is a list of ACLs associated with the node. The parent node must have the CREATE permission bit set.
>
> - *int* *zoo_get_acl* (zhandle_t *zh, *const* *char* *path,*struct* ACL_vector *acl, *struct* Stat *stat);
>
> This operation returns a node’s ACL info. The node must have READ or ADMIN permission set. Without ADMIN permission, the digest hash values will be masked out.
>
> - *int* *zoo_set_acl* (zhandle_t *zh, *const* *char* *path, *int* version,*const* *struct* ACL_vector *acl);
>
> This function replaces node’s ACL list with a new one. The node must have the ADMIN permission set.
>
> Here is a sample code that makes use of the above APIs to authenticate itself using the “*foo*” scheme and create an ephemeral node “/xyz” with create-only permissions.
>
> ###### Note
>
> > This is a very simple example which is intended to show how to interact with ZooKeeper ACLs specifically. See *.../trunk/zookeeper-client/zookeeper-client-c/src/cli.c* for an example of a C client implementation

C语言库的zookeeper客户端API

以下常量均由zookeeper C语言类库提供：

* ```c
  const int ZOO_PERM_READ; //读取node节点 的值以及子节点列表
  const int ZOO_PERM_WRITE;// 设置node节点的值
  const int ZOO_PERM_CREATE; //创建子节点
  const int ZOO_PERM_DELETE;// 删除子节点
  const int ZOO_PERM_ADMIN; //能执行set_acl()
  const int ZOO_PERM_ALL;// 上述所有权限
  ```

  

以下是标准 的ACL id

* ```c
  struct Id ZOO_ANYONE_ID_UNSAFE; //(‘world’,’anyone’)
  struct Id ZOO_AUTH_IDS;// (‘auth’,’’)
  ```

  

ZOO_AUTH_IDS 如果身份认证字符串为空，则被解析为“创建者的身份”

zookeeper客户端有三个标准的ACL:

* ```c
  struct ACL_vector ZOO_OPEN_ACL_UNSAFE; //(ZOO_PERM_ALL,ZOO_ANYONE_ID_UNSAFE)
  struct ACL_vector ZOO_READ_ACL_UNSAFE;// (ZOO_PERM_READ, ZOO_ANYONE_ID_UNSAFE)
  struct ACL_vector ZOO_CREATOR_ALL_ACL; //(ZOO_PERM_ALL,ZOO_AUTH_IDS)
  ```

  

ZOO_OPEN_ACL_UNSAFE对所有ACL开放，任何的应用程序都可以在节点执行任何操作，create、list、delete它的子节点。ZOO_READ_ACL_UNSAFE任何应用程序都是只读访问。CREATE_ALL_ACL 授予该节点创建者所有的权限。创建者必须在创建该节点前已经通过了服务器的身份验证（比如，“digest”方案）。

以下是处理ACLS的操作：

```c
int zoo_add_auth (zhandle_t *zh,const char *scheme,const char *cert, int certLen, void_completion_t completion, const void *data);
```

zookeeper使用 `zoo_add_auth` 函数向服务器进行认证。如果应用程序要使用不同的方案和/或身份进行身份验证，则可以多次调用该功能。

```c
int zoo_create (zhandle_t *zh, const char *path, const char *value,int valuelen, const struct ACL_vector *acl, int flags,char *realpath, int max_realpath_len);
```

`zoo_create`方法创建一个新的节点。参数acl是一个node节点相关联的acls列表。父节点必须有创建权限。

```
int zoo_get_acl (zhandle_t *zh, const char *path,struct ACL_vector *acl, struct Stat *stat);
```

返回node的acl信息。节点必须有read和admin的权限。如果没有admin权限，它的digest 的hash值将被屏蔽

```
int zoo_set_acl (zhandle_t *zh, const char *path, int version,const struct ACL_vector *acl);
```

该方法将使用新的acls替换节点中旧的acls。节点必须有admin权限。

以下是一个简单的代码示例，利用以上api做认证，使用“/foo” scheme并使用只写权限创建一个临时节点“/xyz”。

注意：

> 这是一个非常简单的代码示例，目的是展示如何与zookeeper ACLS交互。可以在`.../trunk/zookeeper-client/zookeeper-client-c/src/cli.c`查看一个简单的C语言客户端的实现。

```c
#include <string.h>
#include <errno.h>

#include "zookeeper.h"

static zhandle_t *zh;

/**
 * In this example this method gets the cert for your
 *   environment -- you must provide
 */
char *foo_get_cert_once(char* id) { return 0; }

/** Watcher function -- empty for this example, not something you should
 * do in real code */
void watcher(zhandle_t *zzh, int type, int state, const char *path,
         void *watcherCtx) {}

int main(int argc, char argv) {
  char buffer[512];
  char p[2048];
  char *cert=0;
  char appId[64];

  strcpy(appId, "example.foo_test");
  cert = foo_get_cert_once(appId);
  if(cert!=0) {
    fprintf(stderr,
        "Certificate for appid [%s] is [%s]\n",appId,cert);
    strncpy(p,cert, sizeof(p)-1);
    free(cert);
  } else {
    fprintf(stderr, "Certificate for appid [%s] not found\n",appId);
    strcpy(p, "dummy");
  }

  zoo_set_debug_level(ZOO_LOG_LEVEL_DEBUG);

  zh = zookeeper_init("localhost:3181", watcher, 10000, 0, 0, 0);
  if (!zh) {
    return errno;
  }
  if(zoo_add_auth(zh,"foo",p,strlen(p),0,0)!=ZOK)
    return 2;

  struct ACL CREATE_ONLY_ACL[] = {{ZOO_PERM_CREATE, ZOO_AUTH_IDS}};
  struct ACL_vector CREATE_ONLY = {1, CREATE_ONLY_ACL};
  int rc = zoo_create(zh,"/xyz","value", 5, &CREATE_ONLY, ZOO_EPHEMERAL,
                  buffer, sizeof(buffer)-1);

  /** this operation will fail with a ZNOAUTH error */
  int buflen= sizeof(buffer);
  struct Stat stat;
  rc = zoo_get(zh, "/xyz", 0, buffer, &buflen, &stat);
  if (rc) {
    fprintf(stderr, "Error %d for %s\n", rc, __LINE__);
  }

  zookeeper_close(zh);
  return 0;
}
```

## Pluggable ZooKeeper authentication

> ZooKeeper runs in a variety of different environments with various different authentication schemes, so it has a completely pluggable authentication framework. Even the builtin authentication schemes use the pluggable authentication framework.
>
> To understand how the authentication framework works, first you must understand the two main authentication operations. The framework first must authenticate the client. This is usually done as soon as the client connects to a server and consists of validating information sent from or gathered about a client and associating it with the connection. The second operation handled by the framework is finding the entries in an ACL that correspond to client. ACL entries are <*idspec, permissions*> pairs. The *idspec* may be a simple string match against the authentication information associated with the connection or it may be a expression that is evaluated against that information. It is up to the implementation of the authentication plugin to do the match. Here is the interface that an authentication plugin must implement:
>
> ```
> public interface AuthenticationProvider {
>     String getScheme();
>     KeeperException.Code handleAuthentication(ServerCnxn cnxn, byte authData[]);
>     boolean isValid(String id);
>     boolean matches(String id, String aclExpr);
>     boolean isAuthenticated();
> }
> ```

ZooKeeper在具有各种不同身份验证方案的各种不同环境中运行，因此它具有完全可插拔的身份验证框架。甚至内置的身份验证方案也使用可插入身份验证框架。

要了解身份验证框架如何工作，首先必须了解两个主要的身份验证操作。框架首先必须对客户端进行身份验证。通常在客户端连接到服务器后立即执行此操作，其中包括验证从客户端发送或收集的有关客户端的信息，并将这些信息与连接相关联。框架处理的第二个操作是在ACL中查找与客户端相对应的条目。ACL条目是<idspec，permissions>对。 idspec可以是与连接相关联的身份验证信息的简单字符串匹配，也可以是根据该信息相关的表达式。匹配由身份验证插件的实现决定。这是身份验证插件必须实现的接口：

```java
public interface AuthenticationProvider {
    String getScheme();
    KeeperException.Code handleAuthentication(ServerCnxn cnxn, byte authData[]);
    boolean isValid(String id);
    boolean matches(String id, String aclExpr);
    boolean isAuthenticated();
}
```

> The first method *getScheme* returns the string that identifies the plugin. Because we support multiple methods of authentication, an authentication credential or an *idspec* will always be prefixed with *scheme:*. The ZooKeeper server uses the scheme returned by the authentication plugin to determine which ids the scheme applies to.
>
> *handleAuthentication* is called when a client sends authentication information to be associated with a connection. The client specifies the scheme to which the information corresponds. The ZooKeeper server passes the information to the authentication plugin whose *getScheme* matches the scheme passed by the client. The implementor of *handleAuthentication* will usually return an error if it determines that the information is bad, or it will associate information with the connection using *cnxn.getAuthInfo().add(new Id(getScheme(), data))*.
>
> The authentication plugin is involved in both setting and using ACLs. When an ACL is set for a znode, the ZooKeeper server will pass the id part of the entry to the *isValid(String id)* method. It is up to the plugin to verify that the id has a correct form. For example, *ip:172.16.0.0/16* is a valid id, but *ip:host.com* is not. If the new ACL includes an "auth" entry, *isAuthenticated* is used to see if the authentication information for this scheme that is associated with the connection should be added to the ACL. Some schemes should not be included in auth. For example, the IP address of the client is not considered as an id that should be added to the ACL if auth is specified.
>
> ZooKeeper invokes *matches(String id, String aclExpr)* when checking an ACL. It needs to match authentication information of the client against the relevant ACL entries. To find the entries which apply to the client, the ZooKeeper server will find the scheme of each entry and if there is authentication information from that client for that scheme, *matches(String id, String aclExpr)* will be called with *id* set to the authentication information that was previously added to the connection by *handleAuthentication* and *aclExpr* set to the id of the ACL entry. The authentication plugin uses its own logic and matching scheme to determine if *id* is included in *aclExpr*.
>
> There are two built in authentication plugins: *ip* and *digest*. Additional plugins can adding using system properties. At startup the ZooKeeper server will look for system properties that start with "zookeeper.authProvider." and interpret the value of those properties as the class name of an authentication plugin. These properties can be set using the *-Dzookeeeper.authProvider.X=com.f.MyAuth* or adding entries such as the following in the server configuration file:
>
> ```
> authProvider.1=com.f.MyAuth
> authProvider.2=com.f.MyAuth2
> ```
>
> Care should be taking to ensure that the suffix on the property is unique. If there are duplicates such as *-Dzookeeeper.authProvider.X=com.f.MyAuth -Dzookeeper.authProvider.X=com.f.MyAuth2*, only one will be used. Also all servers must have the same plugins defined, otherwise clients using the authentication schemes provided by the plugins will have problems connecting to some servers.

`getScheme()`返回标识认证插件的字符串。因为我们支持多种身份验证方法，所以身份验证凭据或idspec将始终以scheme：作为前缀。ZooKeeper服务器使用身份验证插件返回的方案（scheme）来确定该方案（scheme）适用于哪些ID。

`handleAuthentication()`:当客户端发送该连接相关联的认证信息时，该方法将被调用。客户端指定认证信息所对应的方案（scheme）。ZooKeeper服务器将认证信息传递给身份验证插件，该插件的getScheme与客户端传递的方案（scheme）匹配。如果`handleAuthentication()`的实现确定认证信息不正确，则通常会返回错误，或者使用`cnxn.getAuthInfo().add(new Id(getScheme()，data))`将信息与连接关联。

身份验证插件涉及设置和使用ACL。为znode设置ACL后，ZooKeeper服务器会将条目的id部分传递给`isValid(String id)`方法。由插件来验证ID是否具有正确的格式。例如，ip：172.16.0.0/16是有效的ID，但ip：host.com不是有效的ID。如果新的ACL包含“auth”条目，则使用`isAuthenticated`判断与该连接关联的方案(scheme)的身份验证信息是否应该添加到ACL中。某些方案不应包含在auth中。比如，如果已经指定了auth方案，客户端的ip地址不能作为id添加至ACL中。

ZooKeeper在检查ACL时调用`match(String id，String aclExpr)`。它需要将客户端的身份验证信息与相关的ACL条目进行匹配。为了找到客户端对应的条目，zookeeper会遍历scheme的所有条目，如果客户端有针对该scheme的认证信息，`match(String id，String aclExpr)`将被调用，其中id为先前通过`handleAuthentication`添加的认证信息，而aclExpr是acl条目的id。认证插件使用自身的逻辑和匹配的scheme来判断id是否包含在aclExpr。

有两个内置的认证插件：ip和digest。其他插件可以通过系统属性添加。在zookeeper的启动过程中，会查找系统属性中是否包含`zookeeper.authProvider`并解析这些属性的值作为认证插件的class类名。这些属性可以通过`-Dzookeeeper.authProvider.X=com.f.MyAuth`设置，或者在服务器的配置文件中添加如下条目：

```properties
authProvider.1=com.f.MyAuth
authProvider.2=com.f.MyAuth2
```

必须确保属性的后缀是全局唯一的。如果重复配置，比如`-Dzookeeeper.authProvider.X=com.f.MyAuth -Dzookeeper.authProvider.X=com.f.MyAuth2`，只有第一个会被使用。所有的服务器都必须定义相同的插件，否则客户端使用插件提供认证方案(scheme)连接至服务器时，会引发许多问题。

> **Added in 3.6.0**: An alternate abstraction is available for pluggable authentication. It provides additional arguments.
>
> ```
> public abstract class ServerAuthenticationProvider implements AuthenticationProvider {
>     public abstract KeeperException.Code handleAuthentication(ServerObjs serverObjs, byte authData[]);
>     public abstract boolean matches(ServerObjs serverObjs, MatchValues matchValues);
> }
> ```
>
> Instead of implementing AuthenticationProvider you extend ServerAuthenticationProvider. Your handleAuthentication() and matches() methods will then receive the additional parameters (via ServerObjs and MatchValues).
>
> - **ZooKeeperServer** The ZooKeeperServer instance
> - **ServerCnxn** The current connection
> - **path** The ZNode path being operated on (or null if not used)
> - **perm** The operation value or 0
> - **setAcls** When the setAcl() method is being operated on, the list of ACLs that are being set

3.6.0中新增：提供一个认证插件的抽象类。它提供额外的参数。

```java
public abstract class ServerAuthenticationProvider implements AuthenticationProvider {
    public abstract KeeperException.Code handleAuthentication(ServerObjs serverObjs, byte authData[]);
    public abstract boolean matches(ServerObjs serverObjs, MatchValues matchValues);
}
```

可以继承`ServerAuthenticationProvider`抽闲类，而不是实现AuthenticationProvider接口。`handleAuthentication() and matches() `方法会接收额外的参数（通过`ServerObjs and MatchValues`）。

* **ZooKeeperServer**： zookeeper服务器实例
* **ServerCnxn**： 当前连接
* **path**：正在操作的znode节点路径
* **perm**：操作数或0
* **setAcls**：调用setAcls()时，设置ACLS列表

## Consistency Guarantees

> ZooKeeper is a high performance, scalable service. Both reads and write operations are designed to be fast, though reads are faster than writes. The reason for this is that in the case of reads, ZooKeeper can serve older data, which in turn is due to ZooKeeper's consistency guarantees:
>
> - *Sequential Consistency* : Updates from a client will be applied in the order that they were sent.
> - *Atomicity* : Updates either succeed or fail -- there are no partial results.
> - *Single System Image* : A client will see the same view of the service regardless of the server that it connects to. i.e., a client will never see an older view of the system even if the client fails over to a different server with the same session.
> - *Reliability* : Once an update has been applied, it will persist from that time forward until a client overwrites the update. This guarantee has two corollaries:
>   1. If a client gets a successful return code, the update will have been applied. On some failures (communication errors, timeouts, etc) the client will not know if the update has applied or not. We take steps to minimize the failures, but the guarantee is only present with successful return codes. (This is called the *monotonicity condition* in Paxos.)
>   2. Any updates that are seen by the client, through a read request or successful update, will never be rolled back when recovering from server failures.
> - *Timeliness* : The clients view of the system is guaranteed to be up-to-date within a certain time bound (on the order of tens of seconds). Either system changes will be seen by a client within this bound, or the client will detect a service outage.
>
> Using these consistency guarantees it is easy to build higher level functions such as leader election, barriers, queues, and read/write revocable locks solely at the ZooKeeper client (no additions needed to ZooKeeper). See [Recipes and Solutions](https://zookeeper.apache.org/doc/r3.6.1/recipes.html) for more details.
>
> ###### Note
>
> > Sometimes developers mistakenly assume one other guarantee that ZooKeeper does *not* in fact make. This is: * Simultaneously Consistent Cross-Client Views* : ZooKeeper does not guarantee that at every instance in time, two different clients will have identical views of ZooKeeper data. Due to factors like network delays, one client may perform an update before another client gets notified of the change. Consider the scenario of two clients, A and B. If client A sets the value of a znode /a from 0 to 1, then tells client B to read /a, client B may read the old value of 0, depending on which server it is connected to. If it is important that Client A and Client B read the same value, Client B should call the **sync()** method from the ZooKeeper API method before it performs its read. So, ZooKeeper by itself doesn't guarantee that changes occur synchronously across all servers, but ZooKeeper primitives can be used to construct higher level functions that provide useful client synchronization. (For more information, see the [ZooKeeper Recipes](https://zookeeper.apache.org/doc/r3.6.1/recipes.html).

zookeeper是一个高性能的可扩展的服务。读写操作都能设计的非常快，但是读操作会快于写操作。原因是在读取操作时，ZooKeeper可以提供较旧的数据，这又是由于ZooKeeper的一致性保证：

* *Sequential Consistency*顺序一致性：按发送的顺序进行执行来自客户端的更新
* *Atomicity*原子性：更新要不成功要不失败，没有中间结果。
* *Single System Image*单一系统镜像：不管所连接到的服务器示哪一台，客户端都将看到相同的服务器视图。比如，客户端永远不会看到一个旧的系统视图即使客户端在当前会话中由于故障转移至新的服务器上了。
* *Reliability*可靠性：一旦更新生效，它将持续到客户端再次覆盖此更新为止。这个保证有两个推论：
  * 如果客户获得成功的返回码，说明更新已生效。在某些故障（通信错误，超时等）上，客户端将不知道更新是否已生效。我们会采取措施以最大程度地减少失败，但是只有成功的返回码才能提供保证。（这在Paxos中称为单调性条件。)
  * 从服务器故障中恢复时，客户端通过读取请求或成功更新看到的任何更新都不会回滚。
* *Timeliness*及时性：保证系统的客户视图在特定时间范围内（约数十秒）是最新的。客户端可以在此时间范围内看到系统更改，或者检测到服务中断。

使用这些一致性可以确保仅在ZooKeeper客户端上就可以轻松构建高性能的功能，例如leader选举，障碍，队列和读/写可撤销锁。可以查看[Recipes and Solutions](https://zookeeper.apache.org/doc/r3.6.1/recipes.html) 获取更多细节。

注意：

> 有时，开发人员错误地假设了另一个保证，但ZooKeeper实际上并不能做出这些保证。比如：Simultaneously Consistent Cross-Client Views同时一致的跨客户视图：zookeeper并不能保证任何时间，两个不同的客户端都能获取zookeeper相同的数据视图。由于网络延迟等因素，一个客户端可能会在另一客户端收到更改通知之前执行更新。考虑两个客户，比如A和B的情况。如果客户端A将znode / a的值从0设置为1，然后告诉客户端B读取/ a，则客户端B可能会读取旧值0，具体取决于它连接到的服务器。如果需要客户端A和客户端B必须读取相同的值，则客户端B应该在执行读取操作之前，需要调用ZooKeeper API中的`sync()`方法。因此，ZooKeeper本身并不能保证所有服务器上的更改都会同步发生，但是可用ZooKeeper原语构造更高级别的功能，以提供有用的客户端同步。（更多细节，查看[ZooKeeper Recipes](https://zookeeper.apache.org/doc/r3.6.1/recipes.html)）

## Bindings

ZooKeeper客户端库有两种语言：Java和C。以下各节对此进行了描述。

### Java Binding

> There are two packages that make up the ZooKeeper Java binding: **org.apache.zookeeper** and **org.apache.zookeeper.data**. The rest of the packages that make up ZooKeeper are used internally or are part of the server implementation. The **org.apache.zookeeper.data** package is made up of generated classes that are used simply as containers.
>
> The main class used by a ZooKeeper Java client is the **ZooKeeper** class. Its two constructors differ only by an optional session id and password. ZooKeeper supports session recovery across instances of a process. A Java program may save its session id and password to stable storage, restart, and recover the session that was used by the earlier instance of the program.
>
> When a ZooKeeper object is created, two threads are created as well: an IO thread and an event thread. All IO happens on the IO thread (using Java NIO). All event callbacks happen on the event thread. Session maintenance such as reconnecting to ZooKeeper servers and maintaining heartbeat is done on the IO thread. Responses for synchronous methods are also processed in the IO thread. All responses to asynchronous methods and watch events are processed on the event thread. There are a few things to notice that result from this design:
>
> - All completions for asynchronous calls and watcher callbacks will be made in order, one at a time. The caller can do any processing they wish, but no other callbacks will be processed during that time.
> - Callbacks do not block the processing of the IO thread or the processing of the synchronous calls.
> - Synchronous calls may not return in the correct order. For example, assume a client does the following processing: issues an asynchronous read of node **/a** with *watch* set to true, and then in the completion callback of the read it does a synchronous read of **/a**. (Maybe not good practice, but not illegal either, and it makes for a simple example.) Note that if there is a change to **/a** between the asynchronous read and the synchronous read, the client library will receive the watch event saying **/a** changed before the response for the synchronous read, but because of the completion callback blocking the event queue, the synchronous read will return with the new value of **/a** before the watch event is processed.
>
> Finally, the rules associated with shutdown are straightforward: once a ZooKeeper object is closed or receives a fatal event (SESSION_EXPIRED and AUTH_FAILED), the ZooKeeper object becomes invalid. On a close, the two threads shut down and any further access on zookeeper handle is undefined behavior and should be avoided.

ZooKeeper Java binding有两个包：org.apache.zookeeper和org.apache.zookeeper.data。Zookeeper的其余包都是内部使用或者作为服务器实现的包。**org.apache.zookeeper.data**包由被用作容器的生成类组成。

zookeeper客户端使用的主类是`Zookeeper`。它的两个构造函数的区别仅在于可选的 会话ID和密码。ZooKeeper支持跨进程实例的会话恢复。Java程序可以将其会话ID和密码保存到硬盘中，然后重新启动并恢复该程序的该实例所使用的会话。

创建ZooKeeper对象时，还将创建两个线程：IO线程和事件线程。所有IO都发生在IO线程上（使用Java NIO）。所有事件回调都在事件线程上发生。会话维护（例如重新连接到ZooKeeper服务器和维护心跳）都是在IO线程上完成。同步方法的响应也在IO线程中处理。所有异步方法和监视事件的响应都在事件线程上处理。这种设计模式需要注意的是：

* 异步调用和watcher回调，都是按顺序的逐个完成。调用者可以执行任何他们希望的处理，但在此期间不会再处理任何其他回调。
* 回调不会阻止IO线程的处理或同步调用的处理。
* 同步调用可能不会以正确的顺序返回。例如，假设客户端进行以下处理：发出对节点/ a的异步读取，并将watch设置为true。然后在读取的完成回调中，执行/ a的同步读取。（也许不是很好的做法，但也不是非法的，这作为一个简单的例子。）请注意，如果异步读取和同步读取之间，/ a发生更改，客户端类库在同步读取响应之前，接收到表明/a已被修改的watch事件。但是由于异步读取的回调阻止了事件队列，在处理watch事件之前，同步读取将返回/ a的新值。

zookeeper关闭的逻辑是很简单的。一旦一个zookeeper对象关闭了或接收到一个致命的事件（SESSION_EXPIRED and AUTH_FAILED），zookeeper的对象都会失效。一旦关闭，IO线程与事件都会关闭，任何对zookeeper访问的句柄都是未定义的行为。

> #### Client Configuration Parameters
>
> The following list contains configuration properties for the Java client. You can set any of these properties using Java system properties. For server properties, please check the [Server configuration section of the Admin Guide](https://zookeeper.apache.org/doc/r3.6.1/zookeeperAdmin.html#sc_configuration). The ZooKeeper Wiki also has useful pages about [ZooKeeper SSL support](https://cwiki.apache.org/confluence/display/ZOOKEEPER/ZooKeeper+SSL+User+Guide), and [SASL authentication for ZooKeeper](https://cwiki.apache.org/confluence/display/ZOOKEEPER/ZooKeeper+and+SASL).
>
> - *zookeeper.sasl.client* : Set the value to **false** to disable SASL authentication. Default is **true**.
> - *zookeeper.sasl.clientconfig* : Specifies the context key in the JAAS login file. Default is "Client".
> - *zookeeper.server.principal* : Specifies the server principal to be used by the client for authentication, while connecting to the zookeeper server, when Kerberos authentication is enabled. If this configuration is provided, then the ZooKeeper client will NOT USE any of the following parameters to determine the server principal: zookeeper.sasl.client.username, zookeeper.sasl.client.canonicalize.hostname, zookeeper.server.realm Note: this config parameter is working only for ZooKeeper 3.5.7+, 3.6.0+
> - *zookeeper.sasl.client.username* : Traditionally, a principal is divided into three parts: the primary, the instance, and the realm. The format of a typical Kerberos V5 principal is primary/instance@REALM. zookeeper.sasl.client.username specifies the primary part of the server principal. Default is "zookeeper". Instance part is derived from the server IP. Finally server's principal is username/IP@realm, where username is the value of zookeeper.sasl.client.username, IP is the server IP, and realm is the value of zookeeper.server.realm.
> - *zookeeper.sasl.client.canonicalize.hostname* : Expecting the zookeeper.server.principal parameter is not provided, the ZooKeeper client will try to determine the 'instance' (host) part of the ZooKeeper server principal. First it takes the hostname provided as the ZooKeeper server connection string. Then it tries to 'canonicalize' the address by getting the fully qualified domain name belonging to the address. You can disable this 'canonicalization' by setting: zookeeper.sasl.client.canonicalize.hostname=false
> - *zookeeper.server.realm* : Realm part of the server principal. By default it is the client principal realm.
> - *zookeeper.disableAutoWatchReset* : This switch controls whether automatic watch resetting is enabled. Clients automatically reset watches during session reconnect by default, this option allows the client to turn off this behavior by setting zookeeper.disableAutoWatchReset to **true**.
> - *zookeeper.client.secure* : If you want to connect to the server secure client port, you need to set this property to **true** on the client. This will connect to server using SSL with specified credentials. Note that it requires the Netty client.
> - *zookeeper.clientCnxnSocket* : Specifies which ClientCnxnSocket to be used. Possible values are **org.apache.zookeeper.ClientCnxnSocketNIO** and **org.apache.zookeeper.ClientCnxnSocketNetty** . Default is **org.apache.zookeeper.ClientCnxnSocketNIO** . If you want to connect to server's secure client port, you need to set this property to **org.apache.zookeeper.ClientCnxnSocketNetty** on client.
> - *zookeeper.ssl.keyStore.location and zookeeper.ssl.keyStore.password* : Specifies the file path to a JKS containing the local credentials to be used for SSL connections, and the password to unlock the file.
> - *zookeeper.ssl.trustStore.location and zookeeper.ssl.trustStore.password* : Specifies the file path to a JKS containing the remote credentials to be used for SSL connections, and the password to unlock the file.
> - *jute.maxbuffer* : In the client side, it specifies the maximum size of the incoming data from the server. The default is 0xfffff(1048575) bytes, or just under 1M. This is really a sanity check. The ZooKeeper server is designed to store and send data on the order of kilobytes. If incoming data length is more than this value, an IOException is raised. This value of client side should keep same with the server side(Setting **System.setProperty("jute.maxbuffer", "xxxx")** in the client side will work), otherwise problems will arise.
> - *zookeeper.kinit* : Specifies path to kinit binary. Default is "/usr/bin/kinit".

以下列表包含Java客户端的可配置的属性。您可以使用Java系统属性来设置任何这些属性。对于服务器的属性清查阅[Server configuration section of the Admin Guide](https://zookeeper.apache.org/doc/r3.6.1/zookeeperAdmin.html#sc_configuration)。zookeeper维基百科也非常有帮助[ZooKeeper SSL support](https://cwiki.apache.org/confluence/display/ZOOKEEPER/ZooKeeper+SSL+User+Guide), and [SASL authentication for ZooKeeper](https://cwiki.apache.org/confluence/display/ZOOKEEPER/ZooKeeper+and+SASL)。

* *zookeeper.sasl.client* :false来关闭SASL认证。默认为true
* *zookeeper.sasl.clientconfig*：在JAAS登录文件中指定上下文密钥。默认 是“client"
* *zookeeper.server.principal* : 当启用Kerberos身份验证时，指定在连接到zookeeper服务器时客户端用于身份验证的服务器主体。如果提供了此配置，则ZooKeeper客户端将不会使用以下任何参数来确定服务器主体：`zookeeper.sasl.client.username, zookeeper.sasl.client.canonicalize.hostname, zookeeper.server.realm`。注意：此配置参数仅适用于ZooKeeper 3.5.7 +，3.6.0 +。
* *zookeeper.sasl.client.username* :传统上，主体分为三个部分：primary，instance和realm。Kerberos V5主体的格式为`primary/instance@REALM`。zookeeper.sasl.client.username指定了primary部分的服务器主体。默认是“zookeeper”。Instance部分来自于服务器IP。最终服务器的主体为username/IP@realm，其中`zookeeper.sasl.client.username`指定了username，服务器IP对应IP，zookeeper.server.realm指定realm。
* *zookeeper.sasl.client.canonicalize.hostname* :当没有提供zookeeper.server.principal参数，ZooKeeper客户端将尝试确定ZooKeeper服务器主体的“instance”（host）部分。首先，它使用提供的主机名作为ZooKeeper服务器连接字符串。然后，它尝试通过获取属于该地址的全称来“规范”该地址。您可以通过以下设置禁用此“规范化”：`zookeeper.sasl.client.canonicalize.hostname = false`。
* *zookeeper.server.realm* :服务器主体的Realm部分。默认是客户端的realm主体。
* *zookeeper.disableAutoWatchReset* : 此开关控制是否启用watch自动重置。客户端默认在会话重新连接是自动重置，此选项允许客户端通过设置`zookeeper.disableAutoWatchReset 为 true`来关闭此行为。
* *zookeeper.client.secure* :如果要连接到服务器安全客户端端口，则需要在客户端上将此属性设置为true。这将使用具有指定凭据的SSL连接到服务器。请注意，它需要Netty客户端。
* *zookeeper.clientCnxnSocket* : 指定要使用的ClientCnxnSocket。可能值：**org.apache.zookeeper.ClientCnxnSocketNIO** 和**org.apache.zookeeper.ClientCnxnSocketNetty**。默认是**org.apache.zookeeper.ClientCnxnSocketNIO** 。如果你希望连接至服务器安全端口，则设置为**org.apache.zookeeper.ClientCnxnSocketNetty**。
* *zookeeper.ssl.keyStore.location and zookeeper.ssl.keyStore.password* :指定到JKS的文件路径，该文件包含用于SSL连接的本地凭据，以及用于解锁文件的密码。
* *jute.maxbuffer* : 在客户端，它指定从服务器传入数据的最大大小。默认值为0xfffff（1048575）字节，或略低于1M。这确实是一个健全性的检查。ZooKeeper服务器被设计为存储和发送千字节量级的数据。如果传入数据长度大于此值，则引发IOException。客户端的此值应与服务器端相同（客户端中的设置System.setProperty（“ jute.maxbuffer”，“ xxxx”）将起作用），否则会出现问题。
* *zookeeper.kinit* :指定kinit二进制文件的路径。默认值为“ / usr / bin / kinit”。

## Building Blocks: A Guide to ZooKeeper Operations

> This section surveys all the operations a developer can perform against a ZooKeeper server. It is lower level information than the earlier concepts chapters in this manual, but higher level than the ZooKeeper API Reference. 

本节讨论开发人员可以对ZooKeeper服务器执行的所有操作。它是比本手册前面的概念章节更低级别的信息，但比《 ZooKeeper API参考》更高的级别。

### Handling Errors

> Both the Java and C client bindings may report errors. The Java client binding does so by throwing KeeperException, calling code() on the exception will return the specific error code. The C client binding returns an error code as defined in the enum ZOO_ERRORS. API callbacks indicate result code for both language bindings. See the API documentation (javadoc for Java, doxygen for C) for full details on the possible errors and their meaning.

Java和C客户端的 `binding` 都可能报告错误。Java客户端`binding`通过抛出KeeperException来做到这一点，在异常上调用code()将返回特定的错误代码。C客户端`binding`返回如枚举ZOO_ERRORS中定义的错误代码。这两种语言的API回调的都会给出返回的结果代码。

### Connecting to ZooKeeper

> Before we begin, you will have to set up a running Zookeeper server so that we can start developing the client. For C client bindings, we will be using the multithreaded library(zookeeper_mt) with a simple example written in C. To establish a connection with Zookeeper server, we make use of C API - *zookeeper_init* with the following signature:
>
> ```
> int zookeeper_init(const char *host, watcher_fn fn, int recv_timeout, const clientid_t *clientid, void *context, int flags);
> ```
>
> - **host* : Connection string to zookeeper server in the format of host:port. If there are multiple servers, use comma as separator after specifying the host:port pairs. Eg: "127.0.0.1:2181,127.0.0.1:3001,127.0.0.1:3002"
> - *fn* : Watcher function to process events when a notification is triggered.
> - *recv_timeout* : Session expiration time in milliseconds.
> - **clientid* : We can specify 0 for a new session. If a session has already establish previously, we could provide that client ID and it would reconnect to that previous session.
> - **context* : Context object that can be associated with the zkhandle_t handler. If it is not used, we can set it to 0.
> - *flags* : In an initiation, we can leave it for 0.

在开始之前，必须启动一个的Zookeeper服务器实例，以便我们可以开始开发客户端。对于C客户端`binding`，我们将使用多线程库（zookeeper_mt）以及一个用C编写的简单示例。为了与Zookeeper服务器建立连接，我们使用`zookeeper_init()`，其声明如下：

```
int zookeeper_init(const char *host, watcher_fn fn, int recv_timeout, const clientid_t *clientid, void *context, int flags);
```

- *host* : 格式为“ host:port”连接字符串。如果有多个服务器，则以逗号分隔。"127.0.0.1:2181,127.0.0.1:3001,127.0.0.1:3002"。
- *fn*：当通知被触发时的处理事件的watcher函数
- *recv_timeout* :会话超期时间，以毫秒为单位。
- *clientid*：对一个新的会话指定为0。如果先前已经建立了会话，我们可以提供该客户端ID，它将重新连接到该先前的会话。
- *context*：可以与zkhandle_t处理程序关联的上下文对象。如果不使用，可以将其设置为0。
- *flags*：在初始化中，我们可以将其保留为0。

# 学习笔记

## 分布式架构

分布式的特点：

* 分布性
* 对等性
* 并发性
* 缺乏全局时钟

分布式环境的问题：

* 通信异常
* 网络分区（脑裂）
* 请求响应的特有的三态（成功、失败、超时（特有））
* 节点故障

分布式事务：

cap理论：

* C：consistence，一致性。
* A：Avaliable，可用性。
* P：partition tolerance，分区性容错性

BASE理论：

* Basically Available：基本可用。

  > * 响应时间损失：查询原来只要0.5s，现在需要2s
  > * 功能损失：购物高峰期，保证部分人下单成功，而另一部分因引导值降级页面

* Soft state：软状态。

  > * 数据副本允许数据同步延时

* Eventually consistent：最终一致性。

## 分布式协议

### 2pc：two phase commit

![image-20200721210517117](programmer%20guide.assets/image-20200721210517117.png)

角色：参与者与协调者

* 第一阶段：投票
* 第二阶段：提交或回滚

缺点：

* 同步阻塞
* 单点问题（协调者）
* 数据不一致，协调者发出commit请求，但只有部分接收到请求
* 保守，参与者故障，协调者只能等待超时才能执行事务中断

### 3PC：three phase commit

![image-20200721211301368](programmer%20guide.assets/image-20200721211301368.png)

角色：参与者和协调者

* 第一阶段：cancommit：询问
* 第二阶段：precommit：预提交（写undo、redo日志），事务中断
* 第三阶段：docommit：提交，事务中断。协调者故障（宕机或网络中断），参与者仍会执行提交。

优点：

* 降低参与者阻塞范围，协调者单点故障仍能保证达成一致

缺点：

* precommit后，如果出现网络分区，将导致数据仍然提交，这又导致了数据不一致

### paxos

![image-20200721214338637](programmer%20guide.assets/image-20200721214338637.png)

分布式系统，一般部署在同一个局域网，而不用担心数据在通道中被篡改。

#### paxos推到分析

paxos假设背景：paxos小岛上通过议会形式通过法令，议员之间通过信使传递消息。

角色：Proposer、Acceptor、Learner

- **倡议者（Proposer）**：倡议者可以提出提议（数值或者操作命令）以供投票表决
- **接受者（Acceptor）**：接受者可以对倡议者提出的提议进行投票表决，提议有超半数的接受者投票即被选中
- **学习者（Learner）**：学习者无投票权，只是从接受者那里获知哪个提议被选中

#### 选定提案：

* 情景一：最简单的方法是，Proposer只发送给一个Accetor，单点故障严重

* 情景 二：多个Accetor，足够多个Acceptor通过，Proposer才能被选定

  * p1：一个Accetor必须通过它收到的第一个提案，可能无法满足多数人通过，这个条件。

    解决：提案由：【编号，提案内容组成】=》【M,V】

    ![image-20200721221031415](programmer%20guide.assets/image-20200721221031415.png)

  * p2a：如果【M0,v0】被选定，那么编号M比M0高，且被通过的提案，V必须等于V0

  * p2b：如果【M0,V0】被选定，那么proposer产生的更高的【M,V】，其V必须等于V0

  * P2C：如果【Mn,Vn】被通过，则半数以上的Acceptor集合，满足任意一个：1）批准过编号小于Mn提案的Acceptor，2）批准过小于Mn的Acceptor，其编号最大的提案，V为Vn

#### Acceptor通过提案

acceptor可能接收到Proposer两种请求，分别是Prepare和Accept请求

* P1a：未响应过任何大于编号Mn的prepare请求时，通过Mn的提案。每个acceptor只需要记住通过的最大编号的提案

算法概述

![image-20200721231922266](programmer%20guide.assets/image-20200721231922266.png)

* 

#### 提案获取

![image-20200721232501548](programmer%20guide.assets/image-20200721232501548.png)



### zookeeper

#### 设计目标

* 简单的数据结构，树形结构的数据模型，ZNode数据节点
* 构建集群，集群超半数正常工作，就对外提供服务
* 顺序访问，全局唯一递增编号
* 高性能，100%读场景下，12W的QPS

应用： 数据发布订阅、负载均衡、命名服务、分布式协调/通知、集群管理、master选举、分布式锁、分布式队列等

#### 基本概念

##### 集群角色

典型的集群模型：Master/Slave模式，主备模式

Zookeeper：Leader、Follower、Obsever

leader：读写

follower：读，参与leader选举，写操作的投票

observer：读，不参与选举，和投票

##### 会话

客户端与Zookeeper服务器通过TCP长连接建立连接，通过心跳检测与服务器保持会话，并接受服务器的watch事件通知。客户端由于网络故障等原因与服务器断开时，只要在sessionTimeOut恢复前重新连接，则会话任然有效。

##### 数据节点

Znonde，参考下文翻译部分

##### 版本：

Zookeeper为每个Znode维护一个Stat的数据结构，记录version（当前znode版本）、cversion（当前Znode子节点的版本）、aversion（acl版本）

##### Watcher

事件监听器

##### ACL

访问控制列表：

create、read、write、delete、admin

#### ZAB协议（zookeeper atomic broadcast）

![image-20200721235129953](programmer%20guide.assets/image-20200721235129953.png)

![image-20200721235255981](programmer%20guide.assets/image-20200721235255981.png)



#### zab协议介绍









## zookeeper配置说明

### zookeeper 服务配置参数说明

> zookeeper 默认参考配置文件：zoo_sample.cfg
>
> - tickTime：心跳间隔，这个时间作为 zookeeper 服务器之间或 zookeeper 服务器与客户端服务器维持心跳的时间间隔，即每隔 tickTime 时间就会发送一个心跳。
> - initLimit：这个配置项是用来配置 zookeeper 接受客户端（这里所说的客户端不是用户连接 zookeeper 服务器的客户端，而是 zookeeper 服务器集群中连接到 Leader 的 Follower 服务器）初始化连接时最长能忍受多少个心跳时间间隔数。当已经超过 5个心跳的时间（也就是 tickTime）长度后 zookeeper 服务器还没有收到客户端的返回信息，那么表明这个客户端连接失败。总的时间长度就是 5 x 2000 毫秒 = 10 秒
> - syncLimit：这个配置项表示 Leader 与 Follower 之间发送消息，请求和相应时间长度，最长不能超过多少个 tickTime 的时间长度，总的时间长度就是 2 x 2000 毫秒 = 4 秒
> - dataDir：zookeeper 存储数据的目录，默认情况下，zookeeper 的日志问价也会保存至该目录
> - clientPort：客户端连接zookeeper的端口号
> - server.A = B：C：D：
>   - A 是一个整形数字，表示服务器下标。
>   - B 是这个服务器的 ip 地址。
>   - C 表示的是这个服务器与集群中的 Leader 服务器交换信息的端口。
>   - D 表示的是万一集群中的 Leader 服务器挂了，需要一个端口来重新进行选举，选出一个新的 Leader，而这个端口就是用来执行选举时服务器相互通信的端口。如果是伪集群的配置方式，由于 B 都是一样，所以不同的 zookeeper 实例通信端口号不能一样，所以要给它们分配不同的端口号。



## 节点类型

ephemeral Nodes临时节点

persistent Nodes持久节点

sequence Nodes顺序节点

Container Nodes容器节点

TTL Nodes

## 事件监听





## 持久性







# 开发实战

## 分布式锁



## 思考问题



## 资料

1. [官方文档3.6.1](https://zookeeper.apache.org/doc/r3.6.1/zookeeperOver.html)
2. [zookeeper实操好文](https://woodwhales.cn/2020/04/06/065/)
3. [ZooKeeper基本原理及安装部署](https://zhuanlan.zhihu.com/p/30024403)
4. [10分钟看懂！基于Zookeeper的分布式锁](https://blog.csdn.net/qiangcuo6087/article/details/79067136)
5. [图解 Paxos 一致性协议](https://blog.xiaohansong.com/Paxos.html)
6. [paxos的mutil paxos版实现--raft动画#必须看#](http://thesecretlivesofdata.com/raft/)
7. [Zookeeper ZAB 协议分析](https://blog.xiaohansong.com/zab.html) 

