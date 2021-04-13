## 总的讨论

所有I/O多路复用操作都是同步的，涵盖`select/poll`。[[2\]](https://www.zhihu.com/question/19732473/answer/26091478)[[4\]](https://notes.shichao.io/unp/ch6/#synchronous-io-versus-asynchronous-io)
阻塞/非阻塞是相对于同步I/O来说的，与异步I/O无关。
`select/poll/epoll`本身是同步的，可以阻塞也可以不阻塞。

## Select:

### 关于Select是否阻塞：

在使用`int select(int nfds, fd_set *readfds, fd_set *writefds, fd_set *exceptfds, struct timeval *timeout)`函数时，可以设置`timeval`决定该系统调用是否阻塞[[5\]](http://man7.org/linux/man-pages/man2/select.2.html)。

```
If both fields of the
       timeval structure are zero, then select() returns immediately.  (This
       is useful for polling.)  If timeout is NULL (no timeout), select()
       can block indefinitely.
```

![tim 20180415181145](https://user-images.githubusercontent.com/22494815/38777319-966e1c54-40d8-11e8-9ecc-ca75f3254a38.jpg)

## Poll:

### 关于Poll是否阻塞：

在使用`int poll(struct pollfd *fds, nfds_t nfds, int timeout)`函数获取信息时，可以通过指定timeout的值来决定是否阻塞[[6\]](http://man7.org/linux/man-pages/man2/poll.2.html)（当timeout＜0时，会无限期阻塞；当timeout=0时，会立即返回）。

```
Specifying a negative value
       in timeout means an infinite timeout.  Specifying a timeout of zero
       causes poll() to return immediately, even if no file descriptors are
       ready.
```

[《Linux/UNIX系统编程手册》Michael Kerrisk著 P1099]
[![tim 20180415181739](https://user-images.githubusercontent.com/22494815/38777377-6181e862-40d9-11e8-99f8-e283303c3d7f.jpg)](https://user-images.githubusercontent.com/22494815/38777377-6181e862-40d9-11e8-99f8-e283303c3d7f.jpg)

## Epoll:

### 关于Epoll是否阻塞：

在使用`epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout)`函数来获取是否有发生变化/事件的文件描述符时，可以通过指定timeout来指定该调用是否阻塞（当timeout=-1时，会无限期阻塞；当timeout=0时，会立即返回）。[[1\]](http://man7.org/linux/man-pages/man2/epoll_wait.2.html)

```
Specifying a timeout of -1 makes epoll_wait(2) wait indefinitely, 
while specifying a timeout equal to zero makes epoll_wait(2) to 
return immediately even if no events are available (return code equal 
to zero).
```

[《Linux/UNIX系统编程手册》Michael Kerrisk著 P1116]
[![tim 20180415175123](https://user-images.githubusercontent.com/22494815/38777156-e0307fce-40d5-11e8-8e53-f3bf1bc5d011.jpg)](https://user-images.githubusercontent.com/22494815/38777156-e0307fce-40d5-11e8-8e53-f3bf1bc5d011.jpg)

### 关于Epoll是否同步：

当前是否有发生变化/事件的文件描述符需要通过`epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout)`显式地进行查询，因此不是异步；其他资料佐证了这一点[[2\]](https://www.zhihu.com/question/19732473/answer/26091478)。

### 参考资料

[1] http://man7.org/linux/man-pages/man2/epoll_wait.2.html
[2] https://www.zhihu.com/question/19732473/answer/26091478
[4] https://notes.shichao.io/unp/ch6/#synchronous-io-versus-asynchronous-io
[5] http://man7.org/linux/man-pages/man2/select.2.html
[6] http://man7.org/linux/man-pages/man2/poll.2.html
[7] 《Linux/UNIX系统编程手册》Michael Kerrisk著