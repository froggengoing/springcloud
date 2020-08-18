import com.froggengo.redis.RedisMain;
import com.froggengo.redis.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * 底层是hashtable，所以插入和取出不同顺序
 * 集合并集、交集，差集运算
 * 1、比如共同好友，共同关注等
 * 2、访问的ip（唯一性）等，如果不要求指导ip的地址，应使用hyperloglog
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes= RedisMain.class)
public class Class6_templateSet {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Test
    public void testSet (){
        String key="newSet";
        if(redisTemplate.hasKey(key)) redisTemplate.delete(key);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        User user = new User("ttt", "classOne", 18);
        User user1 = new User("ttt1", "classOne1", 181);
        User user2 = new User("ttt2", "classOne2", 182);
        User user3 = new User("ttt3", "classOne3", 183);
        User user4 = new User("ttt4", "classOne4", 184);
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user)));
        //重复值不在插入
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user1)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user2)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user3)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user4)));
        assert redisTemplate.opsForSet().size(key) == 5;
        System.out.println(redisTemplate.opsForSet().size(key));
    }
    @Test
    public void  testMember(){
        String key="newSet";
        Set<Object> members = redisTemplate.opsForSet().members(key);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        members.forEach(n->System.out.println(serializer.deserialize(((String)n).getBytes())));
    }

    /**
     * 不取出数据，随机返回一个集合中的值
     * 或返回指定数量的值，
     * count > size ,返回整个set
     * count 为负数，当大于size时，则可能返回重复值
     * @see org.springframework.data.redis.core.SetOperations#randomMembers(java.lang.Object, long)
     *
     */
    @Test
    public void testpop (){
        String key="newSet";
        Object members = redisTemplate.opsForSet().randomMember(key);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        System.out.println(serializer.deserialize(((String)members).getBytes()));
        System.out.println(redisTemplate.opsForSet().size(key));
    }

    /**
     * 先 执行testSet()
     */
    @Test
    public void testunion (){
        String key="newSet1";
        String key2="newSet";
        String key3="newSet3";
        String key4="newSet4";
        String key5="newSet5";
        if(redisTemplate.hasKey(key)) redisTemplate.delete(key);
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        User user = new User("ttt", "1classOne", 18);
        User user1 = new User("ttt1", "2classOne1", 181);
        User user2 = new User("ttt2", "3classOne2", 182);
        User user3 = new User("ttt3", "4classOne3", 183);
        User user4 = new User("ttt4", "5classOne4", 184);
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user1)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user2)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user3)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user4)));

        User user5 = new User("ttt", "classOne", 18);
        User user6 = new User("ttt1", "classOne1", 181);
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user5)));
        redisTemplate.opsForSet().add(key, new String(serializer.serialize(user6)));

        //交集为2，
        Set<Object> intersect = redisTemplate.opsForSet().intersect(key, key2);
        //差集为左集合-右集合，5
        Set<Object> difference = redisTemplate.opsForSet().difference(key, key2);
        //并集为10,
        Set<Object> union = redisTemplate.opsForSet().union(key, key2);
        System.out.println(intersect.size());
        System.out.println(difference.size());
        System.out.println(union.size());
        HashSet<String> set1 = new HashSet<>();
        set1.add(key);
        set1.add(key2);
        //把并集、差集、交集存放于另一个key中
        redisTemplate.opsForSet().intersectAndStore(set1, key3);
        //差集为左集合-右集合，5
        redisTemplate.opsForSet().differenceAndStore(set1, key4);
        //并集为10,
        redisTemplate.opsForSet().unionAndStore(set1, key5);
        System.out.println(redisTemplate.opsForSet().size(key3));
        System.out.println(redisTemplate.opsForSet().size(key4));
        System.out.println(redisTemplate.opsForSet().size(key5));
    }
}
