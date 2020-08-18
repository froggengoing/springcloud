import com.froggengo.redis.RedisMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes= RedisMain.class)
public class Class8_templatePublishSubcribe {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Test
    public void test (){

    }
}
