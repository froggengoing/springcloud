### 查看EndPoint

#### 请求地址

```java
http://127.0.0.1:18083/actuator/sentinel
```



![image-20200810113023350](https://gitee.com/froggengo/cloudimage/raw/master/img/20210323131400.png)

### 持久化

```yml
###Nacos修改该条规则是可以同步到Sentinel的，但是通过Sentinel控制台修改或新增却不可以同步到Nacos
spring:
  application:
    name: sentinel-example
  cloud:
    sentinel:
      ###使用Nacos作为sentinel配置中心
      datasource:
        ds2:
          nacos:
            server-addr: 'localhost:8848'
            dataId: sentinel-example
            groupId: DEFAULT_GROUP
            data-type: json
            #Property: spring.cloud.sentinel.datasource.ds2.nacos.ruleType不能为空
            #参考com.alibaba.csp.sentinel.slots.block.flow.FlowRule
            rule-type: flow
```

通过上面查看enpoint得到的flowrule，复制nacos中，创建相应名称的配置文件，如下：

```json
//Data ID:sentinel-example
//Group:DEFAULT_GROUP
[
    {
    "resource": "t2",
    "limitApp": "default",
    "grade": 1,
    "count": 2,
    "strategy": 0,
    "refResource": null,
    "controlBehavior": 0,
    "warmUpPeriodSec": 10,
    "maxQueueingTimeMs": 500,
    "clusterMode": false,
    "clusterConfig": null
    }
]
```

> 一条限流规则主要由下面几个因素组成，我们可以组合这些元素来实现不同的限流效果：
>
> - `resource`：资源名，即限流规则的作用对象
> - `count`: 限流阈值
> - `grade`: 限流阈值类型（QPS 或并发线程数）
> - `limitApp`: 流控针对的调用来源，若为 `default` 则不区分调用来源
> - `strategy`: 调用关系限流策略
> - `controlBehavior`: 流量控制效果（直接拒绝、Warm Up、匀速排队）
>
> 1. 流量控制主要有两种统计类型，一种是统计并发线程数，另外一种则是统计 QPS。类型由 `FlowRule` 的 `grade` 字段来定义。其中，0 代表根据并发数量来限流，1 代表根据 QPS 来进行流量控制。其中线程数、QPS 值，都是由 `StatisticSlot` 实时统计获取的。
> 2. 

重启程序，flowrule依然有效

修改规则，sentinel立即收到

如果需要支持，从sentinel推送修改过的配置到nacos，则需要相应的修改，[参考一](https://blog.csdn.net/a1036645146/article/details/107844149)，[参考二](https://github.com/alibaba/Sentinel/wiki/Sentinel-%E6%8E%A7%E5%88%B6%E5%8F%B0%EF%BC%88%E9%9B%86%E7%BE%A4%E6%B5%81%E6%8E%A7%E7%AE%A1%E7%90%86%EF%BC%89#%E8%A7%84%E5%88%99%E9%85%8D%E7%BD%AE) 

### 原理分析

```java
entry = SphU.entry(KEY);
//com.alibaba.csp.sentinel.CtSph#entry
//com.alibaba.csp.sentinel.CtSph#entryWithPriority
//com.alibaba.csp.sentinel.CtSph#lookProcessChain   //组合一个处理链（责任链模式）
//com.alibaba.csp.sentinel.slotchain.SlotChainProvider#newSlotChain
//1、通过spi服务，找到责任链的builder
ServiceLoader<SlotChainBuilder> LOADER = ServiceLoader.load(SlotChainBuilder.class);
//sentinel-parameter-flow-control-1.6.3.jar!\META-INF\services\
//com.alibaba.csp.sentinel.slotchain.SlotChainBuilder
//指定为com.alibaba.csp.sentinel.slots.HotParamSlotChainBuilder
//所以这里使用HotParamSlotChainBuilder构建责任链
//2、具体builder方法如下：
public ProcessorSlotChain build() {
	ProcessorSlotChain chain = new DefaultProcessorSlotChain();
	chain.addLast(new NodeSelectorSlot());
	chain.addLast(new ClusterBuilderSlot());
	chain.addLast(new LogSlot());
	chain.addLast(new StatisticSlot());
	chain.addLast(new ParamFlowSlot());
	chain.addLast(new SystemSlot());
	chain.addLast(new AuthoritySlot());
	chain.addLast(new FlowSlot());
	chain.addLast(new DegradeSlot());
	return chain;
}
//执行责任链中每个ProcessorSlot,即上面build中添加的ProcessorSlot
//DefaultProcessorSlotChain
public void entry(Context context, ResourceWrapper resourceWrapper, Object t, int count, boolean prioritized, Object... args)throws Throwable {
      first.transformEntry(context, resourceWrapper, t, count, prioritized, args);
}

```

> - `NodeSelectorSlot` 负责收集资源的路径，并将这些资源的调用路径，以树状结构存储起来，用于根据调用路径来限流降级；
> - `ClusterBuilderSlot` 则用于存储资源的统计信息以及调用者信息，例如该资源的 RT, QPS, thread count 等等，这些信息将用作为多维度限流，降级的依据；
> - `StatistcSlot` 则用于记录，统计不同纬度的 runtime 信息；
> - `SystemSlot` 则通过系统的状态，例如 load1 等，来控制总的入口流量；
> - `AuthorizationSlot` 则根据黑白名单，来做黑白名单控制；
> - `FlowSlot` 则用于根据预设的限流规则，以及前面 slot 统计的状态，来进行限流；
> - `DegradeSlot` 则通过统计信息，以及预设的规则，来做熔断降级；

#### 与其他框架适配原理

##### webservlet/springboot

Servlet的适配代码看一下，具体的代码是：

```java
public class CommonFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest sRequest = (HttpServletRequest)request;
        Entry entry = null;

        try {
        	// 根据请求生成的资源
            String target = FilterUtil.filterTarget(sRequest);
            target = WebCallbackManager.getUrlCleaner().clean(target);

            // “申请”该资源
            ContextUtil.enter(target);
            entry = SphU.entry(target, EntryType.IN);

            // 如果能成功“申请”到资源，则说明未被限流
            // 则将请求放行
            chain.doFilter(request, response);
        } catch (BlockException e) {
        	// 否则如果捕获了BlockException异常，说明请求被限流了
        	// 则将请求重定向到一个默认的页面
            HttpServletResponse sResponse = (HttpServletResponse)response;
            WebCallbackManager.getUrlBlockHandler().blocked(sRequest, sResponse);
        } catch (IOException e2) {
            // 省略部分代码
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    @Override
    public void destroy() {

    }
}
//通过Servlet的Filter进行扩展，实现一个Filter，然后在doFilter方法中对请求进行限流控制，如果请求被限流则将请求重定向到一个默认页面，否则将请求放行给下一个Filter。
```





