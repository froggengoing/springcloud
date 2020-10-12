### 流程图

![](servlet.assets/http%E6%B8%85%E6%B1%82servlet%E5%A4%84%E7%90%86%E6%B5%81%E7%A8%8B.png)

源码解析

```java
//tomcat容器实现
final class StandardWrapperValve extends ValveBase {
    ...
    @Override
    public final void invoke(Request request, Response response){
         // 为当前请求创建一个过滤器链
		// Create the filter chain for this request
		ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain(request, wrapper, servlet);
        
    }
    //自己实现
public class MyFilter implements Filter {
    private static Log log= LogFactory.getLog(MyFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("MyFilter#init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("MyFilter#doFilter() before");
        chain.doFilter(request, response);
        log.info("MyFilter#doFilter() after");
    }

    @Override
    public void destroy() {
        log.info("MyFilter#destroy()");
    }
}
```

1. `ApplicationFilterChain`循环调用`chain.doFilter(request, response);`，直到filter执行完，请求的`servlet`作为filterChain末尾执行。所以执行完`servlet`会返回`自定义servlet`执行`dofilter`下面的方法