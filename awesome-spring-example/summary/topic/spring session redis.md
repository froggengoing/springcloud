### pom依赖

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--    redis连接池依赖common-pool-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.7.0</version>
</dependency>
```

### yml配置

```yml
spring:
  ###配置redis
  redis:
    host: localhost
    port:  6379
    password: 82878871
    #database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  ################# 使用 Redis 存储 Session 设置 #################
  # Redis|JDBC|Hazelcast|none
  session:
    store-type: Redis
```

### controller

```java
@RestController
@RequestMapping("/session")
public class MySession {

    @GetMapping("/redisSession/{name}")
    public Object setSession(@PathVariable("name") String name, HttpSession session){
        return session.getAttribute("name");
    }
    @GetMapping("/getRedisSession")
    public Object getSession(HttpSession session){
        //System.out.println(session.getId());
        SessionInfo info = new SessionInfo();
        info.id=session.getId();
        info.creationTime=session.getCreationTime();
        info.lastAccessedTime =session.getLastAccessedTime();
        info.maxInactiveInterval=session.getMaxInactiveInterval();
        info.isNew=session.isNew();
        return info;
    }
    class SessionInfo {
        String id = "";
        long creationTime = 0L;
        long lastAccessedTime = 0L;
        int maxInactiveInterval = 0;
        boolean isNew = false;
        //省略getter和setter
    }

}
```

### 测试结果

修改idea的配置，同一个程序不同的端口启动多次，然后同一个浏览器访问不同端口的程序

```json
{
"id": "4bb82ea1-8187-4a85-8a63-349e3a2ca284",
"creationTime": 1597846210251,
"lastAccessedTime": 1597846210251,
"maxInactiveInterval": 1800,
"new": true
}
//同一个浏览器，每次都返回同样的session
//换一个浏览器，则返回不同session
```

通过redis命令查看session缓存内容

```shell
#所有缓存的key
keys *
#查看 某个hash键 所有key
hkeys 键
##查看 某个hash键 的所有值
hvals 键
```

