import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Class1_redisConnect {
    @Test
    public void test (){
        RedisClient client = RedisClient.create("redis://82878871@localhost:6379");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisStringCommands sync = connection.sync();
        String value = (String) sync.get("fly");
        System.out.println(value);

        connection.close();
        client.shutdown();
    }

}
