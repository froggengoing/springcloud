### 安装

1. 下载mongodb压缩包

   ```
   mongodb-win32-x86_64-2012plus-4.2.7.zip
   ```

   

2. 解压，手动创建一些目录

   ```properties
   #mongodb安装路径
   D:\mongodb\mongodb
   ##手动创建
   D:\mongodb\data\db
   D:\mongodb\data\logs
   
   ```

3. 运行命令

   ```shell
   #mongodb安装路径
   #D:\mongodb\mongodb\bin
   #执行
   mongod -dbpath D:\mongodb\data
   ```

   启动成功后，打印

   ```
   FTDC     [initandlisten] Initializing full-time diagnostic data capture with directory 'D:/mongodb/data/diagnostic.data'
   SHARDING [LogicalSessionCacheRefresh] Marking collection config.system.sessions as collection version: <unsharded>
   CONTROL  [LogicalSessionCacheReap] Sessions collection is not set up; waiting until next sessions reap interval: config.system.sessions do
   NETWORK  [listener] Listening on 127.0.0.1
   STORAGE  [LogicalSessionCacheRefresh] createCollection: config.system.sessions with provided UUID: 848f03e0-a101-4f51-ad4c-fd068ea6bd2b an 3e0-a101-4f51-ad4c-fd068ea6bd2b") }
   NETWORK  [listener] waiting for connections on port 27017
   ```

   

4. 浏览器中输入：http://localhost:27017

   ```
   It looks like you are trying to access MongoDB over HTTP on the native driver port.
   ```

   

5. 创建MongoDB服务，注意已管理员方式启动cmd

   ```shell
   mongod --bind_ip 127.0.0.1 --logpath "D:\mongodb\logs\MongoDB.log" --logappend --dbpath "D:\mongodb\data\db" --port 27017 --serviceName "mongodb" --install
   ```

   

6. 将MongoDB目录的bin添加至环境变量中

   ```properties
   path={原来的path}D:\mongodb\mongodb\bin;
   ```

   

7. 删除服务

   ```java
   mongod --bind_ip 127.0.0.1 --logpath "D:\mongodb\logs\MongoDB.log" --logappend --dbpath "D:\mongodb\data\db" --port 27017 --serviceName "mongodb" --remove
   ```



### 命令

```shell
#################################
1、显示当前数据库服务上的数据库
show dbs;
2、切换到指定的数据库进行操作
use mydb
3、显示当前数据库的所有集合（collections）
show collections;
4、查看数据库服务的状态
db.serverStatus();
5、查询指定数据库的统计信息
use admin
db.stat()
6、查询指定数据库包含的集合名称列表
use test1
db.getCollectionNames()
7、统计集合记录数
db.test1.count()
8、统计指定条件的记录数
db.test1.find({"name":"yunweicai"}).count()
9、查询指定数据库的集合当前可用的存储空间
db.test1.storageSize()
10、查询指定数据库的集合分配的存储空间
db.test1.totalSize()
#################################
1、创建数据库
不需要什么create database的命令，只要使用use命令就可以创建数据库
use test1
2、删除数据库
use test1
db.dropDatabase()
3、创建集合
可以使用命令db.createCollection(name, { size : ..., capped : ..., max : ... } )创建集合
也可以直接插入一个数据库就直接创建了
db.test1.insert({"name":"mongodb","user":"opcai"})
4、删除集合
db.test1.drop()
5、插入记录
db.test1.save({"name":"yunweicai"})
或者
db.test1.insert({"name":"mongodb","user":"opcai"})
6、查询记录
db.test1.find()
find()里面可以指定多个条件进行查询，如果为空，就查询所有的数据
7、删除记录
db.test1.remove({"name":"yunweicai"})
需要指定一个条件，没有条件是不允许删除操作的。
######################
1、创建索引
use testdb
db.test1.ensureIndex({"name":1,"age":-1})
ensureIndex方法参数中，数字1表示升序，-1表示降序。
2、查询索引
db.test1.getIndexes()
3、删除索引
删除所有的索引：
db.test1.dropIndexes()
根据索引名称进行删除
db.test1.dropIndex("name_1")
4、重建索引
db.test1.reIndex()
```



### 使用

#### pom

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
</dependency>
```



#### 示例

```java
public class Person {

  private String id;
  private String name;
  private int age;

  public Person(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public int getAge() {
    return age;
  }

  @Override
  public String toString() {
    return "Person [id=" + id + ", name=" + name + ", age=" + age + "]";
  }
}
```



