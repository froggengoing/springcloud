import com.froggengo.redis.RedisMain;
import com.froggengo.redis.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 使用场景：
 * 1.文章列表、关注列表、粉丝列表、留言评论
 * 2、消息队列
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes= RedisMain.class)
public class Class5_templateList {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     *  lrange:0是第一个元素，1是第二个，-1是最后一个，-2是倒数第二个，以此类推
     *  LRANGE list 0 10返回11个元素，即[0,10],
     *  [start,end]超过list边界不会报异常
     *  不移除元素
     */
    @Test
    public void test() {
        String key = "newList";
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        User user = new User("ttt", "classOne", 18);
        User user1 = new User("ttt1", "classOne1", 181);
        User user2 = new User("ttt2", "classOne2", 182);
        User user3 = new User("ttt3", "classOne3", 183);
        User user4 = new User("ttt4", "classOne4", 184);
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user)));
        //list允许重复
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user1)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user2)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user3)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user4)));
        Long size = redisTemplate.opsForList().size(key);
        System.out.println(size);
        assert size==6;
        List<Object> range = redisTemplate.opsForList().range(key, 0, 2);
        range.forEach(n->System.out.println(serializer.deserialize(((String)n).getBytes())));
    }

    /**
     *
     */
    @Test
    public void testlpop() {
        String key = "newList";
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        String user = (String) redisTemplate.opsForList().leftPop(key);
        System.out.println(serializer.deserialize(user.getBytes()));
    }

    /**
     *
     */
    @Test
    public void testrpop() {
        String key = "newList";
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        String user = (String) redisTemplate.opsForList().rightPop(key);
        System.out.println(serializer.deserialize(user.getBytes()));
    }
    /**
     *取出元素（等待超时返回），插入另一个列表
     *
     */
    @Test
    public void testrpopAndLeftPust() {
        String key = "newList";
        String key2= "newList2";
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        String user = (String) redisTemplate.opsForList().rightPopAndLeftPush(key,key2);
        String user2 = (String) redisTemplate.opsForList().rightPopAndLeftPush(key,key2,10, TimeUnit.SECONDS);
        System.out.println(serializer.deserialize(user.getBytes()));
        System.out.println(serializer.deserialize(user2.getBytes()));
    }

    /**
     * ltrim 保留指定区间的元素，0第一个元，-1最后一个
     * start > size,导致list为空，key删除
     * end   > size,end =size
     * start > end ,导致list为空，key删除
     *
     */
    @Test
    public void testltrim() {
        String key = "newList4";
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        User user = new User("ttt", "classOne", 18);
        User user1 = new User("ttt1", "classOne1", 181);
        User user2 = new User("ttt2", "classOne2", 182);
        User user3 = new User("ttt3", "classOne3", 183);
        User user4 = new User("ttt4", "classOne4", 184);
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user1)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user2)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user3)));
        redisTemplate.opsForList().leftPush(key, new String(serializer.serialize(user4)));

        redisTemplate.opsForList().trim(key, 1, 2);
        Long length = redisTemplate.opsForList().size(key);
        System.out.println(length);
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        range.forEach(n->System.out.println(serializer.deserialize(((String)n).getBytes())));

    }
}
