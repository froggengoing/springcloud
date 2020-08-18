import com.froggengo.redis.RedisMain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {RedisMain.class})
@RunWith(SpringRunner.class)
public class RedisTest1 {
    //@Autowired
    RedisConnectionFactory connectionFactory;

    @Test
    public void testRedis(){
        RedisConnection connection = connectionFactory.getConnection();
        connection.listCommands().lPush("javaClientList".getBytes(),"helloworld".getBytes());
        connection.listCommands().lPush("javaClientList".getBytes(),"second".getBytes());
        connection.listCommands().lPush("javaClientList".getBytes(),"third".getBytes());
        connection.listCommands().lPush("javaClientList".getBytes(),"first".getBytes());
        System.out.println(connection.listCommands().lLen("javaClientList".getBytes()));
        connection.close();
    }

}
