package com.froggengo.guava.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 参考：https://www.jianshu.com/p/c8532617773e
 * 回收：
 * 1. 基于容量回收：CacheBuilder.maximumSize(long)
 *      缓存条目超过容量时，将尝试回收最近没有使用或总体上很少使用的缓存项
 * 2. 定时回收（Timed Eviction）
 *      expireAfterAccess(long, TimeUnit)：缓存项在给定时间内没有被读/写访问，则回收。
 *          请注意这种缓存的回收顺序和基于大小回收一样。
 *      expireAfterWrite(long, TimeUnit)：缓存项在给定时间内没有被写访问（创建或覆盖），则回收。
 *          如果认为缓存数据总是在固定时候后变得陈旧不可用，这种回收方式是可取的。
 * 3. 基于引用的回收（Reference-based Eviction）
 *      CacheBuilder.weakKeys():使用过弱引用存储键值。
 *      CacheBuilder.weakValues():使用弱引用存储值。
 *      CacheBuilder.softValues():使用软引用存储值。软引用就是在内存不够是才会按照顺序回收。
 * 4. 显示清除
 *      个别清除：Cache.invalidate(key)
 *      批量清除：Cache.invalidateAll(keys)
 *      清除所有缓存项：Cache.invalidateAll()
 */
public class CacheMain {
    public static void main(String[] args) {
        AtomicInteger count=new AtomicInteger(0);
        LoadingCache<String, User> cache = CacheBuilder.newBuilder()
                //区别于build(),这里再调用get(key)时会创建新的对象返回
                /*.maximumSize(1000)*/.build(new CacheLoader<String, User>() {
                    @Override
                    public User load(String key) throws Exception {
                        int cur = count.getAndIncrement();
                        System.out.println("动态生成");
                        return new User(key,cur);
                    }
                });
        IntStream.range(0,10).forEach(n->{
            User user = null;
            try {
                user = cache.get("key"+n);
                System.out.println("长度"+cache.size());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(user.name+"==>"+user.age);
        });
        try {
            System.out.println(cache.get("key1"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    static class User{
        String name;
        int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
