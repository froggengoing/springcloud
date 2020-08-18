import com.froggengo.redis.RedisMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * zset的 元素关联一个double的socre，用于排序，从小到大
 * 排行榜
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes= RedisMain.class)
public class Class7_templateZSet {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Test
    public void test (){

    }
}