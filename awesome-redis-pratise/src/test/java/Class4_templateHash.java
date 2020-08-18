import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.froggengo.redis.RedisMain;
import com.froggengo.redis.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * 存储对象各个属性的值。
 * 标签+id，field , value
 * 比如购物车
 * 好处是：可以修改对象单个field的属性，而不必整个对象取出来
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= RedisMain.class)
public class Class4_templateHash {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    /**
     * {"name":"ttt","className":"classOne","age":18}
     * 因此Jackson2JsonRedisSerializer导致，redis数据库多了反斜线
     */
    @Test
    public void testSerial (){
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        User user = new User("ttt", "classOne", 18);
        byte[] serialize = serializer.serialize(user);
        System.out.println(serialize);
        System.out.println(new String(serialize));
    }

    /**
     * @see DefaultHashOperations#put(java.lang.Object, java.lang.Object, java.lang.Object)
     *
     */
    @Test
    public void testHash(){
        User user = new User("fly", "class1", 20);
        redisTemplate.opsForHash().put("user",user.getName(),user);
        // Jackson2JsonRedisSerializer redisSerializer = new Jackson2JsonRedisSerializer(User.class);
        //cast取决于serializer的配置，否则 报ClassCastException错误
        User o = (User)redisTemplate.opsForHash().get("user", user.getName());
        System.out.println(o);
    }

    /**
     *
     */
    @Test
    public void testEntries (){
        String key="user";
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        entries.forEach((k,v)->{
            System.out.println(k);
            System.out.println((User)v);
        });
    }

    /**
     *如果key存在删除，返回true
     * 否则，返回false
     */
    @Test
    public void testdel (){
        String key="user";
        Boolean isdel = redisTemplate.delete(key);
        System.out.println(isdel);
    }

    /**
     * 如果hashkey不存在则设置值
     * @see DefaultHashOperations#putIfAbsent(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Test
    public void testPutIf (){
        String key="user";
        User user = new User("fly", "class1", 20);
        redisTemplate.opsForHash().putIfAbsent("user",user.getName(),user);
        User user2 = new User("fly", "class2", 99);
        Boolean isdel = redisTemplate.opsForHash().putIfAbsent(key,user2.getName(),user2);
        System.out.println(redisTemplate.opsForHash().get(key,user2.getName()));
        System.out.println(isdel);
    }

    @Test
    public void testentries (){
        String key="user";
        User user = new User("fly", "class1", 20);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
       entries.forEach((k,v)->{
           System.out.println(k+"=="+v);
       });
    }

    @Test
    public void test (){
        String key="user";
        User user = new User("fly", "class1", 20);
        redisTemplate.opsForHash().put(key+":"+user.getName(),"name",user.getName());
        redisTemplate.opsForHash().put(key+":"+user.getName(),"className",user.getClassName());
        redisTemplate.opsForHash().put(key+":"+user.getName(),"age",user.getAge());
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key+":"+user.getName());
        entries.forEach((k,v)->{
            System.out.println(k+"=="+v);
        });
    }
}
