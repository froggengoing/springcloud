import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**\
 * https://github.com/lettuce-io/lettuce-core/wiki/Asynchronous-API
 */
public class Class2_redisAsyn{

        /**
         * 使用异步方法获取返回结果
         * @throws ExecutionException
         * @throws InterruptedException
         */
        @Test
        public void test () throws ExecutionException, InterruptedException {
                CompletableFuture<String> future = new CompletableFuture<>();

                System.out.println("Current state: " + future.isDone());

                future.complete("my value");

                System.out.println("Current state: " + future.isDone());
                System.out.println("Got value: " + future.get());
        }

        /**
         * 添加回调方法
         */
        @Test
        public void test2(){
                final CompletableFuture<String> future = new CompletableFuture<>();
                future.thenRun(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        System.out.println("Got value: " + future.get());
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                });
                System.out.println("Current state: " + future.isDone());
                future.complete("my value");
                System.out.println("Current state: " + future.isDone());
        }

        /**
         * 使用jdk8里面的Consumer接口
         */
        @Test
        public void testConsumer (){
                CompletableFuture<String> future = new CompletableFuture<>();

                future.thenAccept(new Consumer<String>() {
                        @Override
                        public void accept(String value) {
                                System.out.println("Got value: " + value);
                        }
                });

                System.out.println("Current state: " + future.isDone());
                future.complete("my value");
                System.out.println("Current state: " + future.isDone());
        }
        @Test
        public void testRedis () throws ExecutionException, InterruptedException {
                RedisClient client = RedisClient.create("redis://localhost");
                RedisAsyncCommands<String, String> commands = client.connect().async();
                RedisFuture<String> future = commands.get("key");
                String value = future.get();
                System.out.println(value);

                try {
                        RedisFuture<String> future2 = commands.get("key");
                        //设置超时
                        String value1 = future2.get(1, TimeUnit.MINUTES);
                        System.out.println(value1);
                } catch (Exception e) {
                        e.printStackTrace();
                }

                //使用回调
                RedisFuture<String> future3 = commands.get("key");
                future3.thenAccept(new Consumer<String>() {
                        @Override
                        public void accept(String value) {
                                System.out.println(value);
                        }
                });
                //使用lambda表达式
                RedisFuture<String> future4 = commands.get("key");

                future4.thenAccept(System.out::println);
        }
 }
