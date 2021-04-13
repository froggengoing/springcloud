# Thread线程池

## 简述

jdk的并发相关类，在java.util.concurrent中，主要包括：

* 线程安全的集合，ConcurrentHashMap
* 阻塞队列，如LinkedBlockingDeque
* 线程池，ThreadPoolExcutor
* AQS锁，如ReentrantLock
* 线程安全的原子类，如AtomicInteger

## 线程池

![image-20210208003342503](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322093402.jpg)

### 线程与任务

```java
public static void main(String[] args) {
    new Thread(()->{
        System.out.println("hello world!");
    }).start();
}
```

如上代码，线程与任务绑定，并且线程只能使用一次，导致频繁创建和销毁和销毁线程，消耗不必要的系统资源

所有就有了线程池，将线程和任务解耦。线程池负责维护执行任务的线程。

#### 核心接口：Executor

```java
public interface Executor {
    //Executor的实现类负责执行任务，客户端不在直接与Thread交流
    void execute(Runnable command);
}
```

##### 简单的复用线程实现，[查考](https://blog.csdn.net/hollis_chuang/article/details/106798600)

```java
public class SerialExecutor2 implements Executor {

    private final Worker worker;
    private final Thread thread;
    boolean started = false;

    public SerialExecutor2() {
        this.worker = new Worker();
        this.thread = new Thread(worker);
    }
    @Override
    public synchronized void execute(Runnable r) {
        if (started) {
            this.worker.addTask(r);
        } else {
            this.started = true;
            this.worker.addTask(r);
            this.thread.start();
        }

    }
}
public class Worker implements Runnable {
    final Queue<Runnable> tasks = new ArrayDeque<>();
    
    public void addTask(Runnable runnable) {
        tasks.add(runnable);
    }
    @Override
    public void run() {
        Runnable active;
        while (true){
            if((active = tasks.poll())!=null){
                active.run();
            }
        }
    }
}
//测试代码
public static void main(String[] args) {
    SerialExecutor2 serialExecutor = new SerialExecutor2();
    serialExecutor.execute(()->{
        System.out.println(1);
    });
    serialExecutor.execute(()->{
        System.out.println(2);
    });
    serialExecutor.execute(()->{
        System.out.println(3);
    });
}
```

> 1. Worker实现Runnable接口，该接口维护一个Runnable队列，并且其run方法为死循环，不断的从队列中获取任务并执行
> 2. Executor的实现类，负责创建一个线程，execute负责启动线程执行Worker，以及向Worker不断添加任务
>
> 这样就实现了复用线程

第二版，与上面类似，但更贴近ThreadPoolExecutor源码实现

```java
public class SerialExecutor2 implements Executor {

    private final Worker worker;
    private final Thread thread;
    final Queue<Runnable> tasks = new ArrayDeque<>();
    boolean started = false;

    public SerialExecutor2() {
        this.worker = new Worker();
        this.thread = new Thread(worker);
    }
    @Override
    public synchronized void execute(Runnable r) {
        if (started) {
            this.worker.addTask(r);
        } else {
            this.started = true;
            this.worker.addTask(r);
            this.thread.start();
        }

    }
    class Worker implements Runnable {
        void addTask(Runnable runnable) {
            tasks.add(runnable);
        }
        @Override
        public void run() {
            Runnable active;
            while (true){
                if((active = tasks.poll())!=null){
                    active.run();
                }
            }
        }
    }
}
```

上面实现的缺点：

1. 线程数量固定
2. 没有办法回收线程
3. 任务队列线程不安全
4. 没有拒绝策略

其实上面的缺点也就是线程池必备的参数条件

### ThreadPoolExecutor



#### 接口：ExecutorService

```java
public interface ExecutorService extends Executor {
	//######################关闭线程池#######################
    //执行已经接受的task，但不在接受新的task
    void shutdown();
    /**
     * 尝试停止正在执行的任务，挂起正在执行的任务，并返回。
     * 并不会等待正在执行的任务完成
     * 并不保证能正确的停止正在执行的任务，比如如果该方法的实现是调用Thread#interrupt，执行的Task并不一定会响应interrupt方法，因此也就不会终止
     */
    List<Runnable> shutdownNow();
    //在调用shutdown()后阻塞直到所有任务执行完成,或出现超时,超时的线程会被interrupted
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;
    //###################线程池状态的判断#####################################
    //返回executor是否关闭状态
    boolean isShutdown();
    // 在调用shutdown()和shutdownNow()后所有的任务都执行完成，则isTerminated()为true
    boolean isTerminated();

    //#################提交Callable或Runnable接口############################
    // 提交Callable接口,通过Future.get()阻塞获取执行结果
    <T> Future<T> submit(Callable<T> task);
    //执行返回的结果类型
    <T> Future<T> submit(Runnable task, T result);
    //提交Callable接口实现,Future.get()返回null
    Future<?> submit(Runnable task);

    //######################提交Callable或Runnable集合#######################
    //提交Callable接口的集合,当所有任务complete返回Futures列表
    //complete可以是正常执行完,也可以是throwing an exception
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;
    //执行给定的task集合,返回future列表
    //Future#isDone为true,任务complete或者timeout
    //超时时间是针对的所有tasks
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;
    //执行给定的 task 集合,返回执行成功的结果
    //只要其中一个task执行完了,就会立即返回,其他task就会退出.比如多线程查找某个存在的值
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;
    //执行给定的task集合,指定超时时间返回执行成功的结果
    //超时时间是针对的所有tasks
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

> * 关闭线程池
> * 提交Callable或Runnable接口
> * 提交Callable或Runnable集合
> * 线程池状态的判断

#### 线程状态和线程数量

```java
//ctl具备两个作用，
//workerCount:低29位作为工作线程的计数，
//runState:而高3位记录线程池状态
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
//29
private static final int COUNT_BITS = Integer.SIZE - 3;
//00011111 11111111 11111111 11111111
//低29位全部转换为1，用于异或运算
private static final int COUNT_MASK = (1 << COUNT_BITS) - 1;

// runState 存储高3位中
//并且RUNNING小于0,即SHUTDOWN
//RUNNING:		11100000 00000000 00000000 00000000
//SHUTDOWN:		00000000 00000000 00000000 00000000
//STOP:			00100000 00000000 00000000 00000000
//TIDYING:		01000000 00000000 00000000 00000000
//TERMINATED:	01100000 00000000 00000000 00000000
/**
 *   RUNNING:  Accept new tasks and process queued tasks
 *   SHUTDOWN: Don't accept new tasks, but process queued tasks
 *   STOP:     Don't accept new tasks, don't process queued tasks,
 *             and interrupt in-progress tasks
 *   TIDYING:  All tasks have terminated, workerCount is zero,
 *             the thread transitioning to state TIDYING
 *             will run the terminated() hook method
 *   TERMINATED: terminated() has completed
*/
private static final int RUNNING    = -1 << COUNT_BITS;
private static final int SHUTDOWN   =  0 << COUNT_BITS;
private static final int STOP       =  1 << COUNT_BITS;
private static final int TIDYING    =  2 << COUNT_BITS;
private static final int TERMINATED =  3 << COUNT_BITS;

//取高3位的,实际该方法未使用过
private static int runStateOf(int c)     { return c & ~COUNT_MASK; }
//取低29位
private static int workerCountOf(int c)  { return c & COUNT_MASK; }
private static int ctlOf(int rs, int wc) { return rs | wc; }

private static boolean runStateLessThan(int c, int s) {
    return c < s;
}

private static boolean runStateAtLeast(int c, int s) {
    return c >= s;
}

private static boolean isRunning(int c) {
    return c < SHUTDOWN;
}
```

状态转换图

![图片描述](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322093403.png)

#### 构造方法关键参数

```java
/**
 * 阻塞队列,保存等待被执行的task
 * workQueue.isEmpty()判断是否为空
 * workQueue.poll(long) 指定等待时间内获取task,超时返回null
 * workQueue.take()阻塞直到返回task
 */
private final BlockingQueue<Runnable> workQueue;

/**
 * 对HashSet<Worker> workers的操作都必须持有mainLock
 */
private final ReentrantLock mainLock = new ReentrantLock();

/**
 * Set containing all worker threads in pool. Accessed only when
 * holding mainLock.
 */
private final HashSet<Worker> workers = new HashSet<>();

/**
 * Wait condition to support awaitTermination.
 */
private final Condition termination = mainLock.newCondition();

/**
 * Tracks largest attained pool size. Accessed only under
 * mainLock.
 */
private int largestPoolSize;

/**
 * Counter for completed tasks. Updated only on termination of
 * worker threads. Accessed only under mainLock.
 */
private long completedTaskCount;

//所有用户控制的参数均应使用volatiles声明，因此正在进行的操作可以使用最新值而不需要lock锁

// 线程工厂，所有线程都是通过addWorker()方法调用ThreadFactory创建的
private volatile ThreadFactory threadFactory;

//拒绝策略
private volatile RejectedExecutionHandler handler;

//单位:nanoseconds,空闲线程存时间
//大于核心线程部分会使用该事件
//或者allowCoreThreadTimeOut设置为true时,也会使用该时间
private volatile long keepAliveTime;

// 核心线程数是否使用keepAliveTime作为空闲的存活时间
private volatile boolean allowCoreThreadTimeOut;

//核心线程数
private volatile int corePoolSize;

//最大线程数
private volatile int maximumPoolSize;

//默认拒绝策略
private static final RejectedExecutionHandler defaultHandler =
	new AbortPolicy();
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    //###省略参数校验###
    //核心线程数量
    this.corePoolSize = corePoolSize;
    //最大线程数量
    this.maximumPoolSize = maximumPoolSize;
    //阻塞队列,等待被执行的task
    this.workQueue = workQueue;
    //线程所允许的空闲时间
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    //线程工厂,默认Executors.defaultThreadFactory()
    this.threadFactory = threadFactory;
    //拒绝策略
    this.handler = handler;
    //AbortPolicy：直接抛出异常，这是默认策略；
    //CallerRunsPolicy：用调用者所在的线程来执行任务；
    //DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
    //DiscardPolicy：直接丢弃任务；
}
```



#### task添加与执行

##### execute

```java
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
  
    // 前面说的那个表示 “线程池状态” 和 “线程数” 的整数
    int c = ctl.get();
  
    // 如果当前线程数少于核心线程数，那么直接添加一个 worker 来执行任务，
    // 创建一个新的线程，并把当前任务 command 作为这个线程的第一个任务(firstTask)
    if (workerCountOf(c) < corePoolSize) {
        // 添加任务成功，那么就结束了。提交任务嘛，线程池已经接受了这个任务，这个方法也就可以返回了
        // 至于执行的结果，到时候会包装到 FutureTask 中。
        // 返回 false 代表线程池不允许提交任务
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    // 到这里说明，要么当前线程数大于等于核心线程数，要么刚刚 addWorker 失败了
  
    // 如果线程池处于 RUNNING 状态，把这个任务添加到任务队列 workQueue 中
    if (isRunning(c) && workQueue.offer(command)) {
        /* 这里面说的是，如果任务进入了 workQueue，我们是否需要开启新的线程
         * 因为线程数在 [0, corePoolSize) 是无条件开启新的线程
         * 如果线程数已经大于等于 corePoolSize，那么将任务添加到队列中，然后进到这里
         */
        int recheck = ctl.get();
        // 如果线程池已不处于 RUNNING 状态，那么移除已经入队的这个任务，并且执行拒绝策略
        if (! isRunning(recheck) && remove(command))
            reject(command);
        // 如果线程池还是 RUNNING 的，并且线程数为 0，那么开启新的线程
        // 到这里，我们知道了，这块代码的真正意图是：担心任务提交到队列中了，但是线程都关闭了
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    // 如果 workQueue 队列满了，那么进入到这个分支
    // 以 maximumPoolSize 为界创建新的 worker，
    // 如果失败，说明当前线程数已经达到 maximumPoolSize，执行拒绝策略
    else if (!addWorker(command, false))
        reject(command);
}
```



##### runWorker()

```JAVA
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    // new Worker()是state==-1，此处是调用Worker类的tryRelease()方法，将state置为0， 而interruptIfStarted()中只有state>=0才允许调用中断
    w.unlock(); // allow interrupts
    ////是否“突然完成”，如果是由于异常导致的进入finally，那么completedAbruptly==true就是突然完成的
    boolean completedAbruptly = true;
    try {
        //getTask()方法控制线程的存活时间
        while (task != null || (task = getTask()) != null) {
            //上锁，不是为了防止并发执行任务，为了在shutdown()时不终止正在运行的worker
            w.lock();
/**
* clearInterruptsForTaskRun操作
* 确保只有在线程stoping时，才会被设置中断标示，否则清除中断标示
* 1、如果线程池状态>=stop，且当前线程没有设置中断状态，wt.interrupt()
* 2、如果一开始判断线程池状态<stop，但Thread.interrupted()为true，即线程已经被中断，又清除了中断标示，再次判断线程池状态是否>=stop
*   是，再次设置中断标示，wt.interrupt()
*   否，不做操作，清除中断标示后进行后续步骤
*/
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                //进入这里说明线程池已经为stop状态了
                wt.interrupt();
            try {
                beforeExecute(wt, task);
                try {
                    task.run();
                    afterExecute(task, null);
                } catch (Throwable ex) {
                    afterExecute(task, ex);
                    throw ex;
                }
            } finally {
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        processWorkerExit(w, completedAbruptly);
    }
}
```

> 这里很重要的一点是，task = getTask()不为空，说明已经将task从队列中取出来，那么即使线程为stop，或者线程为interrupt状态，该任务仍然被执行。考虑shutdownNow()会返回没有执行的task列表，这么做也很合理。但是在下一个循环getTask()就为空了。

##### getTask()

```java
/**
 * Performs blocking or timed wait for a task, depending on
 * current configuration settings, or returns null if this worker
 * must exit because of any of:  以下情况会返回null
 * 1. There are more than maximumPoolSize workers (due to
 *    a call to setMaximumPoolSize).
 *    超过了maximumPoolSize设置的线程数量（因为调用了setMaximumPoolSize()）
 * 2. The pool is stopped.
 *    线程池被stop
 * 3. The pool is shutdown and the queue is empty.
 *    线程池被shutdown，并且workQueue空了
 * 4. This worker timed out waiting for a task, and timed-out
 *    workers are subject to termination (that is,
 *    {@code allowCoreThreadTimeOut || workerCount > corePoolSize})
 *    both before and after the timed wait.
 *    线程等待任务超时
 *
 * @return task, or null if the worker must exit, in which case
 *         workerCount is decremented
 *         返回null表示这个worker要结束了，这种情况下workerCount-1
 */
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?

    for (;;) {
        int c = ctl.get();

        // Check if queue empty only if necessary.
/**
* 对线程池状态的判断，两种情况会workerCount-1，并且返回null
* 线程池状态为shutdown，且workQueue为空（反映了shutdown状态的线程池还是要执行workQueue中剩余的任务的）
* 线程池状态为stop（shutdownNow()会导致变成STOP）（此时不用考虑workQueue的情况）
*/
        if (runStateAtLeast(c, SHUTDOWN)
            && (runStateAtLeast(c, STOP) || workQueue.isEmpty())) {
            decrementWorkerCount();//循环的CAS减少worker数量，直到成功
            return null;
        }

        int wc = workerCountOf(c);

        // Are workers subject to culling?
        //是否判断超时
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
        //1.大于最大连接数且当前可用线程数>1,则返回,并减少worker数量
        //2.大于最大核心数且超时（在循环下一个周期，向阻塞队列取task时会等待一定时间），可用线程>1
        //3.大于最大连接数,且阻塞队列为空
        //4.大于核心数且超时,且阻塞队列为空
        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            //这里满足上面的条件,将减少线程,比如超时
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }
        //1.阻塞从队列中获取task
        //2.或者阻塞一定时间获取task
        try {
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
            workQueue.take();
            if (r != null)
                return r;
            timedOut = true;//取不到task会在下一个循环才退出
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
}
```



#### 添加task流程图

![图4 任务调度流程](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322093404.png)

#### 线程中断

```java
public class Thread{
    //中断线程，设置中断标记
    public void interrupt() {
        if (this != Thread.currentThread()) {
            checkAccess();

            // thread may be blocked in an I/O operation
            synchronized (blockerLock) {
                Interruptible b = blocker;
                if (b != null) {
                    interrupt0();  // set interrupt status
                    b.interrupt(this);
                    return;
                }
            }
        }
        interrupt0();
    }
    //返回当前中断状态，并清除当前状态
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }
    //返回当前中断状态，不清除当前状态。如果当前中断状态为true，则再次调用也是true
    public boolean isInterrupted() {
        return isInterrupted(false);
    }
    //ClearInterrupted表示是否清除原来的状态
    //如果为true，则下次调用返回为false，除非调用之前再次中断
    private native boolean isInterrupted(boolean ClearInterrupted);
}
```



> 线程中断，并不是真的停止/中断线程，只是设置中断标识，让线程自己在合适的时机中断自己。
>
> 阻塞方法区分为：
>
> * 不可中断的阻塞
>
>   * java.io包中的同步Socket I/O
>   * java.io包中的同步I/O
>   * Selector的异步I/O
>   * 获取某个锁
>
> * 可中断的阻塞，会返回InterruptedException
>
>   * Thread.sleep()
>
>   * Object.wait()
>
>   * BlockingQueue.put()/take()
>
>   * Thread.join()
>
>   * 基于`java.nio.channels.InterruptibleChannel`接口的Io
>
>     ```java
>     protected final void begin() {
>         if (this.interruptor == null) {
>             this.interruptor = new Interruptible() {
>                 public void interrupt(Thread target) {
>                     synchronized(AbstractInterruptibleChannel.this.closeLock) {
>                         if (!AbstractInterruptibleChannel.this.closed) {
>                             AbstractInterruptibleChannel.this.closed = true;
>                             AbstractInterruptibleChannel.this.interrupted = target;
>     
>                             try {
>                                                            AbstractInterruptibleChannel.this.implCloseChannel();
>                             } catch (IOException var5) {
>                             }
>     
>                         }
>                     }
>                 }
>             };
>         }
>     
>         blockedOn(this.interruptor);
>         Thread me = Thread.currentThread();
>         if (me.isInterrupted()) {
>             this.interruptor.interrupt(me);
>         }
>     
>     }
>     
>     ```
>
>     >  
>     >
>     > 会实现 `sun.nio.ch.Interruptible`接口，判断`isInterrupted()`中断状态，继而中断线程
>
>     
>
>   * 基于`java.nio.channels.Selector`接口的Io
>
>     ```java
>     protected final void begin() {
>         if (this.interruptor == null) {
>             this.interruptor = new Interruptible() {
>                 public void interrupt(Thread ignore) {
>                     AbstractSelector.this.wakeup();
>                 }
>             };
>         }
>     
>         AbstractInterruptibleChannel.blockedOn(this.interruptor);
>         Thread me = Thread.currentThread();
>         if (me.isInterrupted()) {
>             this.interruptor.interrupt(me);
>         }
>     
>     }
>     ```
>
>   > 同样实现了 `sun.nio.ch.Interruptible`接口，这里会直接调用`wakeup()`，直接唤醒`selector`
>
> 可中断阻塞即在方法实现中会判断线程中断状态，而不可中断阻塞则不会判断阻塞状态。
>
> 比如InputStream.read方法. 在检测到输入数据可用, 到达流末尾或者抛出异常前, 该方法一直阻塞. 而且阻塞的时候不会检查中断标记, 所以中断线程无法使read从阻塞状态返回. 但是关闭流可以使得read方法抛出异常, 从而从阻塞状态返回. [Java Concurrency In Practice 7.1的归纳和总结](https://www.iteye.com/blog/coolxing-1476289) 

##### 示例1

```java
public static void main(String[] args) throws InterruptedException {
    Thread t1= new Thread(() -> {
        int count=0;
        while (true) {
            System.out.println(count);
        }
    });
    t1.start();
    Thread.sleep(1000);
    t1.interrupt();
    System.out.println("完成");
}
```

> 没有阻塞操作,也没有判断线程状态。此时线程会一直执行

##### 示例2

```java
public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        int count = 0;
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(count);
        }
    });
    t1.start();
    Thread.sleep(1000);
    t1.interrupt();
    System.out.println("完成");
}
```



> 没有阻塞操作,但此执行任务都判断线程是否中断。此时1秒后，线程中断后退出。

##### 示例3

```java
public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        int count = 0;
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(count);
            try {
                synchronized (ThreadMain.class) {
                    ThreadMain.class.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("被中断");
            }
        }
    });
    t1.start();
    Thread.sleep(1000);
    t1.interrupt();
    System.out.println("完成");
}
//特别注意这里打印了两个0，所以中断后状态isInterrupted()仍为false
0
完成
java.lang.InterruptedException
	at java.base/java.lang.Object.wait(Native Method)
	at java.base/java.lang.Object.wait(Object.java:328)
	at com.awesomeJdk.myNetty.exec.v3.ThreadMain.lambda$main$0(ThreadMain.java:17)
	at java.base/java.lang.Thread.run(Thread.java:834)
被中断
0
```



##### [参考另一个示例](https://blog.csdn.net/earthhour/article/details/78913911)

```java
public static void main(String[] args) throws InterruptedException {
    Runnable runnable=()-> {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000); // 延迟1秒
                System.out.println(" 中断状态："+Thread.currentThread().isInterrupted());
            } catch (InterruptedException e) {
                System.out.println(" 被中断："+e.getMessage());
                System.out.println(" 异常后状态："+Thread.currentThread().isInterrupted());
                //interrupt();
                //System.out.println(getName()+" 再次中断，状态："+isInterrupted());
            }
        }
        System.out.println("任务退出！");
    };
    Thread thread = new Thread(runnable);
    thread.start();
    Thread.sleep(50);
    thread.interrupt();
    System.out.println("完成");
}
完成
 被中断：sleep interrupted
 异常后，中断状态：false
 中断状态：false
 中断状态：false
```

> **如果线程正在执行wait,sleep,join方法，你调用interrupt()方法，会抛出InterruptedException异常。而一个抛出了异常的线程的状态马上就会被置为非中断状态，如果catch语句没有处理异常，则下一次循环中isInterrupted()为false，线程会继续执行，可能你N次抛出异常，也无法让线程停止。**
>
> 所以如上面代码注释，必须在catch中再次中断

##### 示例4

```java
//阻塞io不响应interrupt()方法
public static void main(String[] args) throws InterruptedException {
    AtomicBoolean isStop=new AtomicBoolean(false);
    Runnable runnable=()->{
        String path = "D:\\hello.txt";
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            int count=0;
            while(true){
                outputStream.write(count++);
                if(isStop.get()){
                    break;
                }
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    Thread t1 = new Thread(runnable);
    t1.start();
    Thread.sleep(1000);
    t1.interrupt();
    Thread.sleep(1000);
    System.out.println("完成");
}
//这里程序一直没有结束运行判断线程t1还在执行
```

### 

#### 回收线程

##### processWorkerExit() 

```java
private void processWorkerExit(Worker w, boolean completedAbruptly) {
    /**
     * 1、worker数量-1
     * 如果是突然终止，说明是task执行时异常情况导致，即run()方法执行时发生了异常，那么正在工作的worker线程数量需要-1
     * 如果不是突然终止，说明是worker线程没有task可执行了，不用-1，因为已经在getTask()方法中-1了
     */
    if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted 代码和注释相反
        decrementWorkerCount();
    /**
     * 2、从Workers Set中移除worker
     */
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        //把worker的完成任务数加到线程池的完成任务数
        completedTaskCount += w.completedTasks;
        //从HashSet<Worker>中移除
        workers.remove(w);
    } finally {
        mainLock.unlock();
    }
    /**
     * 3、在对线程池有负效益的操作时，都需要“尝试终止”线程池
     * 主要是判断线程池是否满足终止的状态
     * 如果状态满足，但还有线程池还有线程，尝试对其发出中断响应，使其能进入退出流程
     * 没有线程了，更新状态为tidying->terminated
     */
    tryTerminate();
    /**
     * 4、是否需要增加worker线程
     * 线程池状态是running 或 shutdown
     * 如果当前线程是突然终止的，addWorker()
     * 如果当前线程不是突然终止的，但当前线程数量 < 要维护的线程数量，addWorker()
     * 故如果调用线程池shutdown()，直到workQueue为空前，线程池都会维持corePoolSize个线程，然后再逐渐销毁这corePoolSize个线程
     */
    int c = ctl.get();
    //如果状态是running、shutdown，即tryTerminate()没有成功终止线程池，尝试再添加一个worker
    if (runStateLessThan(c, STOP)) {
        //不是突然完成的，即没有task任务可以获取而完成的，计算min，并根据当前worker数量判断是否需要addWorker()
        if (!completedAbruptly) {
            //allowCoreThreadTimeOut默认为false，即min默认为corePoolSize
            int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
            //如果min为0，即不需要维持核心线程数量，且workQueue不为空，至少保持一个线程
            if (min == 0 && ! workQueue.isEmpty())
                min = 1;
            //如果线程数量大于最少数量，直接返回，否则下面至少要addWorker一个
            if (workerCountOf(c) >= min)
                return; // replacement not needed
        }
        //添加一个没有firstTask的worker
        //只要worker是completedAbruptly突然终止的，或者线程数量小于要维护的数量，就新添一个worker线程，即使是shutdown状态
        addWorker(null, false);
    }
}
```

> 将线程引用移出线程池就已经结束了线程销毁的部分。但由于引起线程销毁的可能性有很多，线程池还要判断是什么引发了这次销毁，是否要改变线程池的现阶段状态，是否要根据新状态，重新分配线程。
>



#### 阻塞队列

![img](https://gitee.com/froggengo/cloudimage/raw/master/img/20210322093405.png)

为什么使用ReentrantLock

Synchronized和volatile使用场景

worker实现了AbstractQueuedSynchronizer接口

## 参考:

1. [Java线程池实现原理及其在美团业务中的实践](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html) 
2. [Java线程池ThreadPoolExecutor使用和分析(二) - execute()原理](https://www.cnblogs.com/trust-freedom/p/6681948.html#label_3_4)  