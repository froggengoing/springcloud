import com.froggengo.redis.RedisMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 事务原理，脑补原理：
 * redis单线程，而multi开启事务后，只是把command添加至队列中。
 * 只有exec时，才执行队列的任务，由于是单线程那么意味着，其他命令必须在队列之后。
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes= RedisMain.class)
public class Class9_templateTradition {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     *
     */
    @Test
    public void test (){
        String key="tradition";
        String key2="tradition1";
        redisTemplate.multi();
        redisTemplate.opsForValue().set(key,"testTradition");
        redisTemplate.opsForValue().set(key2,"testTradition2");
        redisTemplate.exec();
    }

    /**
     * 在watch与exec之间，watch的值被改变了，那么事务取消。从单线程角度也很好理解。
     */
    @Test
    public void testWatch (){
        String key="tradition";
        String key2="tradition1";
        redisTemplate.watch(key);
        redisTemplate.multi();
        redisTemplate.opsForValue().set(key,"testTradition");
        redisTemplate.opsForValue().set(key2,"testTradition2");
        redisTemplate.exec();
    }
}
