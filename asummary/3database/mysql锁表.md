前几天同事在晚上上线的时候执行sql语句造成锁表，想总结一下以避免后续发生。

# (1) 遇到锁表快速解决办法

　　依次执行1-6步，运行第6步生成的语句即可。

　　如果特别着急，运行 1 2 6 步 以及第6步生成的kill语句 即可。

1.　 **第1步　查看表是否在使用。**

> `show open tables where in_use > 0 ;`
> 如果查询结果为空。则证明表没有在使用。结束。
>
> ```
> mysql>  show open tables where in_use > 0 ;
> Empty set (0.00 sec)
> ```

> 如果查询结果不为空，继续后续的步骤。
>
> ```
> mysql>  show open tables where in_use > 0 ;
> +----------+-------+--------+-------------+
> | Database | Table | In_use | Name_locked |
> +----------+-------+--------+-------------+
> | test     | t     |      1 |           0 |
> +----------+-------+--------+-------------+
> 1 row in set (0.00 sec)
> ```

2.　 **第2步　查看数据库当前的进程，看一下有无正在执行的慢SQL记录线程。**

> `show processlist;`
> show processlist 是显示用户正在运行的线程，需要注意的是，除了 root 用户能看到所有正在运行的线程外，其他用户都只能看到自己正在运行的线程，看不到其它用户正在运行的线程。
> SHOW PROCESSLIST shows which threads are running. If you have the PROCESS privilege, you can see all threads. Otherwise, you can see only your own threads (that is, threads associated with the MySQL account that you are using). If you do not use the FULL keyword, only the first 100 characters of each statement are shown in the Info field.

3.　 **第3步　当前运行的所有事务**

> ```
> SELECT * FROM information_schema.INNODB_TRX;
> ```

4.　 **第4步　当前出现的锁**

> ```
> SELECT * FROM information_schema.INNODB_LOCKs;
> ```

5.　 **第5步　锁等待的对应关系**

> `SELECT * FROM information_schema.INNODB_LOCK_waits;`
> 看事务表INNODB_TRX，里面是否有正在锁定的事务线程，看看ID是否在show processlist里面的sleep线程中，如果是，就证明这个sleep的线程事务一直没有commit或者rollback而是卡住了，我们需要手动kill掉。
> 搜索的结果是在事务表发现了很多任务，这时候最好都kill掉。

6.　 **第6步　批量删除事务表中的事务**

> 这里用的方法是：通过information_schema.processlist表中的连接信息生成需要处理掉的MySQL连接的语句临时文件，然后执行临时文件中生成的指令。
>
> ```
> SELECT concat('KILL ',id,';') 
> FROM information_schema.processlist p 
> INNER JOIN  information_schema.INNODB_TRX x 
> ON p.id=x.trx_mysql_thread_id 
> WHERE db='test';
> ```

> 记得修改对应的数据库名。

> 这个语句执行后结果如下：
>
> ```
> mysql>  SELECT concat('KILL ',id,';')  FROM information_schema.processlist p  INNER JOIN  information_schema.INNODB_TRX x  ON p.id=x.trx_mysql_thread_id  WHERE db='test';
> +------------------------+
> | concat('KILL ',id,';') |
> +------------------------+
> | KILL 42;               |
> | KILL 40;               |
> +------------------------+
> 2 rows in set (0.00 sec)
> ```

> 执行结果里的两个kill语句即可解决锁表。



　首先问几个问题：

1. MySQL里有哪些锁？
2. 如何造成锁表？
3. 如何造成死锁？
4. 全局锁加锁方法的执行命令是什么?主要的应用场景是什么?
5. 做整库备份时为什么要加全局锁?
6. MySQL的自带备份工具, 使用什么参数可以确保一致性视图, 在什么场景下不适用?
7. 不建议使用set global readonly = true的方法加全局锁有哪两点原因?
8. 表级锁有哪两种类型? 各自的使用场景是什么?
9. MDL中读写锁之间的互斥关系怎样的?
10. 如何安全的给小表增加字段?

# (2) 复盘

> 自己创建了一个测试的表`t`，插入了两条数据。然后手动构造锁表和死锁模拟。
> 测试的时候用的root用户测试

> show processlist 是显示用户正在运行的线程，需要注意的是，除了 root 用户能看到所有正在运行的线程外，其他用户都只能看到自己正在运行的线程，看不到其它用户正在运行的线程。
> SHOW PROCESSLIST shows which threads are running. If you have the PROCESS privilege, you can see all threads. Otherwise, you can see only your own threads (that is, threads associated with the MySQL account that you are using). If you do not use the FULL keyword, only the first 100 characters of each statement are shown in the Info field.

> 

## (2.1) 创建表t并插入2条数据

```
CREATE TABLE `t` (
  `id` int(11) NOT NULL,
  `c` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
INSERT INTO `t` (id, c)
VALUES 
(1, 1),
(2, 1);
```

## (2.2) 准备多个shell模拟锁表

```
可以看到我打开三个shell，用root创建了三个连接，分别是 15  40  41 
mysql> show processlist ;
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
| Id | User        | Host            | db   | Command | Time | State                    | Info             | Progress |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
|  1 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  2 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  3 | system user |                 | NULL | Daemon  | NULL | InnoDB purge coordinator | NULL             |    0.000 |
|  4 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  5 | system user |                 | NULL | Daemon  | NULL | InnoDB shutdown handler  | NULL             |    0.000 |
| 15 | root        | localhost:49914 | NULL | Query   |    0 | Init                     | show processlist |    0.000 |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
6 rows in set (0.00 sec)

mysql>
mysql> show processlist ;
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
| Id | User        | Host            | db   | Command | Time | State                    | Info             | Progress |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
|  1 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  2 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  3 | system user |                 | NULL | Daemon  | NULL | InnoDB purge coordinator | NULL             |    0.000 |
|  4 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  5 | system user |                 | NULL | Daemon  | NULL | InnoDB shutdown handler  | NULL             |    0.000 |
| 15 | root        | localhost:49914 | NULL | Query   |    0 | Init                     | show processlist |    0.000 |
| 40 | root        | localhost:50872 | test | Sleep   |   41 |                          | NULL             |    0.000 |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
7 rows in set (0.00 sec)

mysql>
mysql> show processlist ;
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
| Id | User        | Host            | db   | Command | Time | State                    | Info             | Progress |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
|  1 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  2 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  3 | system user |                 | NULL | Daemon  | NULL | InnoDB purge coordinator | NULL             |    0.000 |
|  4 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  5 | system user |                 | NULL | Daemon  | NULL | InnoDB shutdown handler  | NULL             |    0.000 |
| 15 | root        | localhost:49914 | NULL | Query   |    0 | Init                     | show processlist |    0.000 |
| 40 | root        | localhost:50872 | test | Sleep   |   64 |                          | NULL             |    0.000 |
| 41 | root        | localhost:50888 | test | Sleep   |    5 |                          | NULL             |    0.000 |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
8 rows in set (0.00 sec)
```

## (2.3) 模拟锁表

在第一个shell里观察

在第二个shell里执行 `start transaction; delete from t where c=1 ;` 故意打开事务，然后执行语句不提交，占用写锁。

在第三个shell里执行 `delete from t where c=1 ;` 执行删除语句，造成锁表。

这个时候 session3在等待session2释放写锁。这个时候已经锁表了。

如果再在 第三个shell里执行 `delete from t where c=2 ;`

在 第二个shell里执行 `delete from t where c=1 ;`

就会相互等待，造成死锁。

## (2.4) 锁表后查看

然后在第一个shell里查看

### (2.4.1)查看是否锁表

可以看到下面的查询语句有结果，确实是锁表了。

```
mysql>  show open tables where in_use > 0 ;
+----------+-------+--------+-------------+
| Database | Table | In_use | Name_locked |
+----------+-------+--------+-------------+
| test     | t     |      1 |           0 |
+----------+-------+--------+-------------+
1 row in set (0.00 sec)
```



### (2.4.2) 查看数据库当前的进程

```
mysql> show processlist ;
+----+-------------+-----------------+------+---------+------+--------------------------+-------------------------+----------+
| Id | User        | Host            | db   | Command | Time | State                    | Info                    | Progress |
+----+-------------+-----------------+------+---------+------+--------------------------+-------------------------+----------+
|  1 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL                    |    0.000 |
|  2 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL                    |    0.000 |
|  3 | system user |                 | NULL | Daemon  | NULL | InnoDB purge coordinator | NULL                    |    0.000 |
|  4 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL                    |    0.000 |
|  5 | system user |                 | NULL | Daemon  | NULL | InnoDB shutdown handler  | NULL                    |    0.000 |
| 15 | root        | localhost:49914 | NULL | Query   |    0 | Init                     | show processlist        |    0.000 |
| 40 | root        | localhost:50872 | test | Sleep   |   15 |                          | NULL                    |    0.000 |
| 41 | root        | localhost:50888 | test | Query   |   11 | Updating                 | delete from t where c=1 |    0.000 |
+----+-------------+-----------------+------+---------+------+--------------------------+-------------------------+----------+
8 rows in set (0.00 sec)
```

### (2.4.3) 当前运行的所有事务

```
mysql>  SELECT * FROM information_schema.INNODB_TRX;
+--------+-----------+---------------------+-----------------------+---------------------+------------+---------------------+-------------------------+---------------------+-------------------+-------------------+------------------+-----------------------+-----------------+-------------------+-------------------------+---------------------+-------------------+------------------------+----------------------------+------------------+----------------------------+
| trx_id | trx_state | trx_started         | trx_requested_lock_id | trx_wait_started    | trx_weight | trx_mysql_thread_id | trx_query               | trx_operation_state | trx_tables_in_use | trx_tables_locked | trx_lock_structs | trx_lock_memory_bytes | trx_rows_locked | trx_rows_modified | trx_concurrency_tickets | trx_isolation_level | trx_unique_checks | trx_foreign_key_checks | trx_last_foreign_key_error | trx_is_read_only | trx_autocommit_non_locking |
+--------+-----------+---------------------+-----------------------+---------------------+------------+---------------------+-------------------------+---------------------+-------------------+-------------------+------------------+-----------------------+-----------------+-------------------+-------------------------+---------------------+-------------------+------------------------+----------------------------+------------------+----------------------------+
| 23312  | LOCK WAIT | 2019-09-05 23:16:18 | 23312:78:3:2          | 2019-09-05 23:16:18 |          2 |                  41 | delete from t where c=1 | starting index read |                 1 |                 1 |                2 |                  1136 |               1 |                 0 |                       0 | REPEATABLE READ
     |                 1 |                      1 | NULL                       |                0 |                          0 |
| 23311  | RUNNING   | 2019-09-05 23:16:13 | NULL                  | NULL                |          3 |                  40 | NULL                    | NULL
    |                 0 |                 1 |                2 |                  1136 |               3 |                 1 |                       0 | REPEATABLE READ
     |                 1 |                      1 | NULL                       |                0 |                          0 |
+--------+-----------+---------------------+-----------------------+---------------------+------------+---------------------+-------------------------+---------------------+-------------------+-------------------+------------------+-----------------------+-----------------+-------------------+-------------------------+---------------------+-------------------+------------------------+----------------------------+------------------+----------------------------+
2 rows in set (0.00 sec)
```

### (2.4.4) 当前出现的锁

```
mysql>  SELECT * FROM information_schema.INNODB_LOCKs;
+--------------+-------------+-----------+-----------+------------+------------+------------+-----------+----------+-----------+
| lock_id      | lock_trx_id | lock_mode | lock_type | lock_table | lock_index | lock_space | lock_page | lock_rec | lock_data |
+--------------+-------------+-----------+-----------+------------+------------+------------+-----------+----------+-----------+
| 23312:78:3:2 | 23312       | X         | RECORD    | `test`.`t` | PRIMARY    |         78 |         3 |        2 | 1         |
| 23311:78:3:2 | 23311       | X         | RECORD    | `test`.`t` | PRIMARY    |         78 |         3 |        2 | 1         |
+--------------+-------------+-----------+-----------+------------+------------+------------+-----------+----------+-----------+
2 rows in set (0.00 sec)
```

### (2.4.5) 锁等待的对应关系

```
mysql>  SELECT * FROM information_schema.INNODB_LOCK_waits;
+-------------------+-------------------+-----------------+------------------+
| requesting_trx_id | requested_lock_id | blocking_trx_id | blocking_lock_id |
+-------------------+-------------------+-----------------+------------------+
| 23312             | 23312:78:3:2      | 23311           | 23311:78:3:2     |
+-------------------+-------------------+-----------------+------------------+
1 row in set (0.00 sec)
```

### (2.4.6) 删除事务表中的事务

```
mysql>   SELECT   p.id,   p.time,         i.trx_id,       i.trx_state,    p.info FROM     INFORMATION_SCHEMA.PROCESSLIST p,       INFORMATION_SCHEMA.INNODB_TRX  i WHERE p.id = i.trx_mysql_thread_id    AND i.trx_state = 'LOCK WAIT';
+----+------+--------+-----------+-------------------------+
| id | time | trx_id | trx_state | info                    |
+----+------+--------+-----------+-------------------------+
| 41 |   27 | 23312  | LOCK WAIT | delete from t where c=1 |
+----+------+--------+-----------+-------------------------+
1 row in set (0.01 sec)
```

### (2.4.7) kill掉锁表的语句

这儿有两种观点，一种是只kill掉后面等待的那个语句。还有一种是把两个语句都kill掉。这个根据实际情况处理。

```
mysql> kill 41 ;
Query OK, 0 rows affected (0.00 sec)

mysql>   SELECT   p.id,   p.time,         i.trx_id,       i.trx_state,    p.info FROM     INFORMATION_SCHEMA.PROCESSLIST p,       INFORMATION_SCHEMA.INNODB_TRX  i WHERE p.id = i.trx_mysql_thread_id    AND i.trx_state = 'LOCK WAIT';
Empty set (0.01 sec)
```

杀掉41

```
mysql> show processlist ;
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
| Id | User        | Host            | db   | Command | Time | State                    | Info             | Progress |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
|  1 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  2 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  3 | system user |                 | NULL | Daemon  | NULL | InnoDB purge coordinator | NULL             |    0.000 |
|  4 | system user |                 | NULL | Daemon  | NULL | InnoDB purge worker      | NULL             |    0.000 |
|  5 | system user |                 | NULL | Daemon  | NULL | InnoDB shutdown handler  | NULL             |    0.000 |
| 15 | root        | localhost:49914 | NULL | Query   |    0 | Init                     | show processlist |    0.000 |
| 40 | root        | localhost:50872 | test | Sleep   |   56 |                          | NULL             |    0.000 |
+----+-------------+-----------------+------+---------+------+--------------------------+------------------+----------+
7 rows in set (0.00 sec)
```

然后到第3个shell窗口查看，可以看到

```
mysql> delete from t where c=1 ;
ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction
```



因为第3个shell里执行的语句被kill掉了。

到这儿可以看到死锁解决了。

但其实有个问题。第3个shell里的语句被kill掉了。但第2个shell里的语句还在执行。如果第二个shell里的事务不提交或者kill，在第3个shell里执行删除语句还会造成锁表。

第二种观点的办法

```
SELECT
	p.id,
	p.time,
	x.trx_id,
	x.trx_state,
	p.info 
FROM
	INFORMATION_SCHEMA.PROCESSLIST p,
	INFORMATION_SCHEMA.INNODB_TRX  x 
WHERE
	p.id = x.trx_mysql_thread_id  ;
```



```
mysql> SELECT   p.id,   p.time,         x.trx_id,       x.trx_state,    p.info FROM     INFORMATION_SCHEMA.PROCESSLIST p,       INFORMATION_SCHEMA.INNODB_TRX  x WHERE
p.id = x.trx_mysql_thread_id  ;
+----+------+--------+-----------+-------------------------+
| id | time | trx_id | trx_state | info                    |
+----+------+--------+-----------+-------------------------+
| 42 |    3 | 23317  | LOCK WAIT | delete from t where c=1 |
| 40 | 1792 | 23311  | RUNNING   | NULL                    |
+----+------+--------+-----------+-------------------------+
2 rows in set (0.01 sec)
```

然后同时杀掉 40 42 就可以。

# (3) MySQL中的锁

> 数据库锁设计的初衷是处理并发问题。作为多用户共享的资源，当出现并发访问的时候，数据库需要合理地控制资源的访问规则。而锁就是用来实现这些访问规则的重要数据结构。
> 根据加锁的范围，MySQL 里面的锁大致可以分成全局锁、表级锁和行锁三类。

## (3.1) 全局锁

> 全局锁就是对整个数据库实例加锁。MySQL 提供了一个加全局读锁的方法，命令是 Flush tables with read lock (FTWRL)。当你需要让整个库处于只读状态的时候，可以使用这个命令，之后其他线程的以下语句会被阻塞：数据更新语句（数据的增删改）、数据定义语句（包括建表、修改表结构等）和更新类事务的提交语句。

> **全局锁的典型使用场景是，做全库逻辑备份。** 也就是把整库每个表都 select 出来存成文本。

风险：
1.如果在主库备份，在备份期间不能更新，业务停摆
2.如果在从库备份，备份期间不能执行主库同步的binlog，导致主从延迟

> 官方自带的逻辑备份工具是 mysqldump。当 mysqldump 使用参数–single-transaction 的时候，导数据之前就会启动一个事务，来确保拿到一致性视图。而由于 MVCC 的支持，这个过程中数据是可以正常更新的。

> 全局锁主要用在逻辑备份过程中。对于全部是 InnoDB 引擎的库，建议你选择使用 `–single-transaction` 参数，对应用会更友好。

## (3.2) 表级锁

> MySQL 里面表级别的锁有两种：一种是表锁，一种是元数据锁（meta data lock，MDL)。

### (3.2.1) 表锁

> 表锁是在Server层实现的。ALTER TABLE之类的语句会使用表锁，忽略存储引擎的锁机制。

> **表锁的语法是 lock tables … read/write。** 与 FTWRL 类似，可以用 unlock tables 主动释放锁，也可以在客户端断开的时候自动释放。需要注意，lock tables 语法除了会限制别的线程的读写外，也限定了本线程接下来的操作对象。
> 举个例子, 如果在某个线程 A 中执行 lock tables t1 read, t2 write; 这个语句，则其他线程写 t1、读写 t2 的语句都会被阻塞。同时，线程 A 在执行 unlock tables 之前，也只能执行读 t1、读写 t2 的操作。连写 t1 都不允许，自然也不能访问其他表。
> 在还没有出现更细粒度的锁的时候，表锁是最常用的处理并发的方式。而对于 InnoDB 这种支持行锁的引擎，一般不使用 lock tables 命令来控制并发，毕竟锁住整个表的影响面还是太大。

### (3.2.1) metadata lock

**另一类表级的锁是 MDL（metadata lock)。**

> MDL 不需要显式使用，在访问一个表的时候会被自动加上。
> MDL 的作用是并发情况下维护数据的一致性，保证读写的正确性。(避免加字段删字段导致查询结果异常)
> 因此，在 MySQL 5.5 版本中引入了 MDL，当对一个表做增删改查操作的时候，加 MDL 读锁；
> 当要对表做结构变更操作的时候，加 MDL 写锁。

> 读锁之间不互斥，因此你可以有多个线程同时对一张表增删改查。
> 读写锁之间、写锁之间是互斥的，用来保证变更表结构操作的安全性。
> 因此，如果有两个线程要同时给一个表加字段，其中一个要等另一个执行完才能开始执行。

> 事务中的 MDL 锁，在语句执行开始时申请，但是语句结束后并不会马上释放，而会等到整个事务提交后再释放。

给一个表加字段，或者修改字段，或者加索引，需要扫描全表的数据。
而实际上，即使是小表，操作不慎也会出问题。在修改表的时候会持有MDL写锁，如果这个表上的查询语句频繁，而且客户端有重试机制，也就是说超时后会再起一个新 session 再请求的话，这个库的线程很快就会爆满。

MDL 是并发情况下维护数据的一致性，在表上有事务的时候,不可以对元数据经行写入操作,并且这个是在server层面实现的

## (3.3) 行锁

> MySQL 的行锁是在引擎层由各个引擎自己实现的。
> 但并不是所有的引擎都支持行锁，比如 MyISAM 引擎就不支持行锁。
> InnoDB 是支持行锁的，这也是 MyISAM 被 InnoDB 替代的重要原因之一。
> InnoDB行锁包括 Record Lock 、 Gap Lock、 Next-Key Lock

> 在 InnoDB 事务中，行锁是在需要的时候才加上的，但并不是不需要了就立刻释放，而是要等到事务结束时才释放。这个就是两阶段锁协议。

知道了这个设定，对我们使用事务有什么帮助呢？那就是，如果你的事务中需要锁多个行，要把最可能造成锁冲突、最可能影响并发度的锁尽量往后放。

假设你负责实现一个电影票在线交易业务，顾客 A 要在影院 B 购买电影票。我们简化一点，这个业务需要涉及到以下操作：

1. 从顾客 A 账户余额中扣除电影票价；

2. 给影院 B 的账户余额增加这张电影票价；

3. 记录一条交易日志。

   试想如果同时有另外一个顾客 C 要在影院 B 买票，那么这两个事务冲突的部分就是语句 2 了。因为它们要更新同一个影院账户的余额，需要修改同一行数据。
   根据两阶段锁协议，不论你怎样安排语句顺序，所有的操作需要的行锁都是在事务提交的时候才释放的。所以，如果你把语句 2 安排在最后，比如按照 3、1、2 这样的顺序，那么影院账户余额这一行的锁时间就最少。这就最大程度地减少了事务之间的锁等待，提升了并发度。

### (3.3.1) MySQL/InnoDB的加锁过程

Read Committed (RC)
Repeatable Read (RR)

> InnoDB的加锁分析前提条件
> 前提一： 查询列是不是主键？
> 前提二： 当前系统的隔离级别是什么？
> 前提三： 查询列上有索引吗？
> 前提四： 查询列是唯一索引吗？
> 前提五： 两个SQL的执行计划是什么？索引扫描？全表扫描？

```
update t1 set update_time = now() where k = 10 ;
```

> 组合一： k列是主键，RC隔离级别
> 组合二： k列是二级唯一索引，RC隔离级别
> 组合三： k列是二级非唯一索引，RC隔离级别
> 组合四： k列上没有索引，RC隔离级别
> 组合五： k列是主键，RR隔离级别
> 组合六： k列是二级唯一索引，RR隔离级别
> 组合七： k列是二级非唯一索引，RR隔离级别
> 组合八： k列上没有索引，RR隔离级别
> 组合九： Serializable隔离级别

> 组合一: Read Committed 隔离级别，k列是主键，给定SQL：update t1 set update_time = now() where k = 10; 只需要将主键上 k = 10的记录加上X锁即可

> 组合二: Read Committed 隔离级别，k列有unique索引，unique索引上的k=10一条记录加上X锁，同时，会根据读取到的列，回主键索引(聚簇索引)，然后将聚簇索引上对应的主键索引项加X锁。

> 组合三: Read Committed 隔离级别，k列上有索引，那么对应的所有满足SQL查询条件的记录，都会被加锁。同时，这些记录在主键索引上的记录，然后将聚簇索引上对应的主键索引项加X锁。

> 组合四: Read Committed 隔离级别，若k列上没有索引，SQL会走聚簇索引的全扫描进行过滤，由于过滤是由MySQL Server层面进行的。因此每条记录，无论是否满足条件，都会被加上X锁。但是，为了效率考量，MySQL做了优化，对于不满足条件的记录，会在判断后放锁，最终持有的，是满足条件的记录上的锁，但是不满足条件的记录上的加锁/放锁动作不会省略。同时，优化也违背了2PL的约束。

> 组合五: Repeatable Read 隔离级别，k列是主键列，给定SQL update t1 set update_time = now() where k = 10; 只需要将主键上 k = 10的记录加上X锁即可。

> 组合六: Repeatable Read 隔离级别，k列有unique索引，unique索引上的k=10一条记录加上X锁，同时，会根据读取到的列，回主键索引(聚簇索引)，然后将聚簇索引上对应的主键索引项加X锁。

> 组合七：Repeatable Read 隔离级别，k列有索引， 通过索引定位到第一条满足查询条件的记录，加记录上的X锁，加GAP上的GAP锁，然后加主键聚簇索引上的记录X锁，然后返回；然后读取下一条，重复进行。直至进行到第一条不满足条件的记录，此时，不需要加记录X锁，但是仍旧需要加GAP锁，最后返回结束。
>
> 考虑到B+树索引的有序性，满足条件的项一定是连续存放的。如果要插入一条记录，肯定会插入在相同位置，为了保证两次查询查到的值一致，MySQL选择了用GAP锁，将 查询值范围前、查询值范围、查询值范围后 三个GAP给锁起来。
>
> GAP锁的目的，是为了防止同一事务的两次当前读，出现幻读的情况。
>
> ```
>  k           | 7  | 8  | 10 | 10 | 40 | 50 |
> primary id   | 1  | 2  | 3  | 4  | 5  | 6  |
> ```

> 为了保证[8,2]与[10,3]间，[10,3]与[10,4]间，[10,4]与[40,5]不会插入新的满足条件的记录，MySQL选择了用GAP锁，将这三个GAP给锁起来。

> 组合八： 在Repeatable Read隔离级别下，如果进行全表扫描的当前读，那么会锁上表中的所有记录，同时会锁上聚簇索引内的所有GAP，杜绝所有的并发 更新/删除/插入 操作
> 　　聚簇索引上的所有记录，都被加上了X锁。其次，聚簇索引每条记录间的间隙(GAP)，也同时被加上了GAP锁。

> 组合九：Serializable隔离级别下直接用加锁的方式来避免并行访问。
>
> 在RC，RR隔离级别下，都是快照读，不加锁。
> Serializable隔离级别，读不加锁就不再成立，所有的读操作，都是当前读。

## (3.4) 死锁

> 当并发系统中不同线程出现循环资源依赖，涉及的线程都在等待别的线程释放资源时，就会导致这几个线程都进入无限等待的状态，称为死锁。

> 当出现死锁以后，有两种策略：
>
> 1. 一种策略是，直接进入等待，直到超时。这个超时时间可以通过参数 `innodb_lock_wait_timeout` 来设置。
> 2. 另一种策略是，发起死锁检测，发现死锁后，主动回滚死锁链条中的某一个事务，让其他事务得以继续执行。将参数 `innodb_deadlock_detect` 设置为 on，表示开启这个逻辑。

> 在 InnoDB 中，innodb_lock_wait_timeout 的默认值是 50s，意味着如果采用第一个策略，当出现死锁以后，第一个被锁住的线程要过 50s 才会超时退出，然后其他线程才有可能继续执行。对于在线服务来说，这个等待时间往往是无法接受的。
> 可以考虑通过将一行改成逻辑上的多行来减少锁冲突。还是以影院账户为例，可以考虑放在多条记录上，比如 10 个记录，影院的账户总额等于这 10 个记录的值的总和。这样每次要给影院账户加金额的时候，随机选其中一条记录来加。这样每次冲突概率变成原来的 1/10，可以减少锁等待个数，也就减少了死锁检测的 CPU 消耗。

> 如果你的事务中需要锁多个行，要把最可能造成锁冲突、最可能影响并发度的锁的申请时机尽量往后放。

# (4) 可能遇到的问题

## (4.1) 备份一般都会在备库上执行，你在用–single-transaction 方法做逻辑备份的过程中，如果主库上的一个小表做了一个 DDL，比如给一个表上加了一列。这时候，从备库上会看到什么现象呢？

备份一般都会在备库上执行，你在用–single-transaction 方法做逻辑备份的过程中，如果主库上的一个小表做了一个 DDL，比如给一个表上加了一列。这时候，从备库上会看到什么现象呢？

假设这个 DDL 是针对表 t1 的， 这里我把备份过程中几个关键的语句列出来：

```
Q1:SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
Q2:START TRANSACTION  WITH CONSISTENT SNAPSHOT；
/* other tables */
Q3:SAVEPOINT sp;
/* 时刻 1 */
Q4:show create table `t1`;
/* 时刻 2 */
Q5:SELECT * FROM `t1`;
/* 时刻 3 */
Q6:ROLLBACK TO SAVEPOINT sp;
/* 时刻 4 */
/* other tables */
```



在备份开始的时候，为了确保 RR（可重复读）隔离级别，再设置一次 RR 隔离级别 (Q1);
启动事务，这里用 WITH CONSISTENT SNAPSHOT 确保这个语句执行完就可以得到一个一致性视图（Q2)；
设置一个保存点，这个很重要（Q3）；
show create 是为了拿到表结构 (Q4)，然后正式导数据 （Q5），回滚到 SAVEPOINT sp，在这里的作用是释放 t1 的 MDL 锁 （Q6）。当然这部分属于“超纲”，上文正文里面都没提到。
DDL 从主库传过来的时间按照效果不同，我打了四个时刻。题目设定为小表，我们假定到达后，如果开始执行，则很快能够执行完成。

参考答案如下：

1. 如果在 Q4 语句执行之前到达，现象：没有影响，备份拿到的是 DDL 后的表结构。

   

2. 如果在“时刻 2”到达，则表结构被改过，Q5 执行的时候，报 Table definition has changed, please retry transaction，现象：mysqldump 终止；

   

3. 如果在“时刻 2”和“时刻 3”之间到达，mysqldump 占着 t1 的 MDL 读锁，binlog 被阻塞，现象：主从延迟，直到 Q6 执行完成。

   

4. 从“时刻 4”开始，mysqldump 释放了 MDL 读锁，现象：没有影响，备份拿到的是 DDL 前的表结构。

   

## (4.2) 删数据问题

如果你要删除一个表里面的前 10000 行数据，有以下三种方法可以做到：
第一种，直接执行 delete from T limit 10000;
第二种，在一个连接中循环执行 20 次 delete from T limit 500;
第三种，在 20 个连接中同时执行 delete from T limit 500。

你会选择哪一种方法呢？为什么呢？

方案一，事务相对较长，则占用锁的时间较长，会导致其他客户端等待资源时间较长。
方案二，串行化执行，将相对长的事务分成多次相对短的事务，则每次事务占用锁的时间相对较短，其他客户端在等待相应资源的时间也较短。这样的操作，同时也意味着将资源分片使用（每次执行使用不同片段的资源），可以提高并发性。
方案三，人为自己制造锁竞争，加剧并发量。

## (4.3) 问题3

1.如何在死锁发生时,就把发生的sql语句抓出来？
2.在使用连接池的情况下,连接会复用.比如一个业务使用连接set sql_select_limit=1,释放掉以后.其他业务复用该连接时,这个参数也生效.请问怎么避免这种情况,或者怎么禁止业务set session？
3.很好奇双11的成交额,是通过redis累加的嘛？
4.不会改源码能成为专家嘛？

1. show engine innodb status 里面有信息，不过不是很全…
2. 5.7的reset_connection接口可以考虑一下
3. 用redis的话，为了避免超卖需要增加了很多机制来保证。修改都在数据库里执行就方便点。前提是要解决热点问题
4. 我认识几位处理问题和分析问题经验非常丰富的专家，不用懂源码，但是原理还是要很清楚的

## (4.4) 转义导致死锁问题

前天在开发中，还遇到过一次死锁，是在一个批处理中，要删除1000条数据，5个线程，200条数据commit一次，
sol：delete from 表A where id =15426169754750004759008 STORAGEDB
(id是主键)
我同事解决了，说原因是id 是char 类型，但是没有加单引号，所以没有进入id索引中，然后锁表了，所以导致死锁。

这个问题的出现，应该是人为只要并发导致锁冲突吧？但是为什么不加单引号会死锁，加了单引号就能正常跑呢？

# References

[1] [Mysql 锁、事务强制手动kill/释放](https://blog.csdn.net/dujianxiong/article/details/90770301)
[2] [19 | 为什么我只查一行的语句，也执行这么慢？MySQL实战45讲](https://time.geekbang.org/column/article/74687)
[3] [06 | 全局锁和表锁 ：给表加个字段怎么有这么多阻碍？MySQL实战45讲](https://time.geekbang.org/column/article/69862)
[4] [07 | 行锁功过：怎么减少行锁对性能的影响？MySQL实战45讲](https://time.geekbang.org/column/article/70215)
[5] [mysql 5.7 lock-tables](https://dev.mysql.com/doc/refman/5.7/en/lock-tables.html)
[6] 《高性能MySQL》 O’REILLY
[7] [mysql-show-open-tables](https://dev.mysql.com/doc/refman/5.7/en/show-open-tables.html)
[8] [mysql-show-processlist](https://dev.mysql.com/doc/refman/5.7/en/show-processlist.html)
[9] [innodb-locking](https://dev.mysql.com/doc/refman/5.7/en/innodb-locking.html)
[10] [innodb-index-types](https://dev.mysql.com/doc/refman/5.7/en/innodb-index-types.html)
[11] [面试官：同学，分析一下MySQL/InnoDB的加锁过程吧](https://mp.weixin.qq.com/s/uoUS2NCG0Z7cmC2WkDh6MA)