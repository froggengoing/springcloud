import com.froggengo.redis.RedisMain;
import com.froggengo.redis.config.Appconfig;
import com.froggengo.redis.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 使用场景：
 * key-value的存储，
 * 1、单个字符串，对象序列化后的json字符串，
 * 2、存储图片
 * 3、计数器，粉丝数、点赞数等
 * 4、bit的操作，可以用于一个月的登录天数等
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {RedisAutoConfiguration.class})
@SpringBootTest(classes=RedisMain.class)
//@SpringBootTest
public class Class3_redistemplate {
    @Autowired
    RedisTemplate<String, Object>  redisTemplate;

    /**
     * string 设置与取值
     */
    @Test
    public void testString(){
        redisTemplate.opsForValue().set("template2","hell");
        String foo = (String) redisTemplate.opsForValue().get("template2");
        System.out.println(foo);

    }

    /**
     * String 设置和超时
     * 设置key 10秒超时，10秒之后在取数据就取不到了
     */
    @Test
    public void testStringExpire() throws InterruptedException {
        redisTemplate.opsForValue().set("stringTest","timeout",10, TimeUnit.SECONDS);
        String foo = (String) redisTemplate.opsForValue().get("stringTest");
        System.out.println(foo);
        TimeUnit.SECONDS.sleep(10);
        String test = (String) redisTemplate.opsForValue().get("stringTest");
        System.out.println(test);
    }

    /**
     *
     * 设置超时，同上
     */
    @Test
    public void testStringDuration() throws InterruptedException {
        String key ="stringTest2";
        redisTemplate.opsForValue().set(key,"timeout", Duration.ofSeconds(5));
        String foo = (String) redisTemplate.opsForValue().get(key);
        System.out.println(foo);
        TimeUnit.SECONDS.sleep(5);
        String test = (String) redisTemplate.opsForValue().get(key);
        System.out.println(test);
    }

    /**
     *
     * 如果没有key，则设置.注意返回结果可能为true，false，null
     * @see ValueOperations#setIfAbsent(java.lang.Object, java.lang.Object)
     * @see ValueOperations#setIfAbsent(java.lang.Object, java.lang.Object, long timeout, TimeUnit unit);
     * @see ValueOperations#setIfAbsent(java.lang.Object, java.lang.Object, java.time.Duration)
     *
     */
    @Test
    public void testSetx() throws InterruptedException {
        String key ="stringTest3";
        redisTemplate.opsForValue().set(key,"timeout");
        String foo = (String) redisTemplate.opsForValue().get(key);
        System.out.println(foo);
        //再次设置
        Boolean timeout = redisTemplate.opsForValue().setIfAbsent(key, "ttt");
        String foo2 = (String) redisTemplate.opsForValue().get(key);
        System.out.println(timeout);
        System.out.println(foo2);
    }

    /**
     * 如果存在则设置
     * @see ValueOperations#setIfPresent(java.lang.Object, java.lang.Object)
     * @see ValueOperations#setIfPresent(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
     * @see ValueOperations#setIfPresent(java.lang.Object, java.lang.Object, java.time.Duration)
     */
    @Test
    public void testSetifPresent() throws InterruptedException {
        String key ="stringTest4";
        redisTemplate.opsForValue().set(key,"timeout");
        String foo = (String) redisTemplate.opsForValue().get(key);
        System.out.println(foo);
        //再次设置,成功
        Boolean timeout = redisTemplate.opsForValue().setIfPresent(key, "ttt");
        String foo2 = (String) redisTemplate.opsForValue().get(key);
        System.out.println(timeout);
        System.out.println(foo2);
        //不存在的key，无法设置
        String key2="kkk";
        Boolean ttt = redisTemplate.opsForValue().setIfPresent(key2, "ttt");
        String key2value = (String) redisTemplate.opsForValue().get(key2);
        System.out.println(ttt);
        System.out.println(key2value);
    }

    /**
     * @see ValueOperations#multiSet(java.util.Map)
     * 注意，同一个map中，如果key重复，则value会被覆盖。只有是执行multiSetIfAbsent前key已经存在才不会设置
     * 如果map中，某个key已经存在，则整个map都无法设置，返回false
     * @see ValueOperations#multiSetIfAbsent(java.util.Map)
     * @throws InterruptedException
     */
    @Test
    public void testMutilSet() throws InterruptedException {
        HashMap<String, String> map = new HashMap<>();
        map.put("map1","2map1value");
        map.put("map2","2map2value");
        map.put("map3","2map3value");
        map.put("map2","2map4value");//观察map2的值
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.opsForValue().multiGet(map.keySet()).forEach(System.out::println);
        System.out.println("============================");
        HashMap<String, String> map1 = new HashMap<>();
        map.put("3map4","2map4value");
        map1.put("3map1","2map1value");
        map1.put("3map2","2map2value");
        map1.put("3map3","2map3value");
        map1.put("3map2","2map4value");//观察map2的值
        Boolean setIfAbsent = redisTemplate.opsForValue().multiSetIfAbsent(map1);
        System.out.println(setIfAbsent);
        redisTemplate.opsForValue().multiGet(map1.keySet()).forEach(System.out::println);
    }

    /**
     * 设置并返回旧值
     * @see ValueOperations#getAndSet(java.lang.Object, java.lang.Object)
     */
    @Test
    public void testGetAndSet (){
        String key ="stringTest4";
        String foo = (String) redisTemplate.opsForValue().getAndSet(key, "timeoutTTTTTT");
        System.out.println(foo);
        foo = (String) redisTemplate.opsForValue().get(key);
        System.out.println(foo);
    }
    /**
     * 增加
     * @see ValueOperations#increment(java.lang.Object)
     * @see ValueOperations#increment(java.lang.Object, long)
     * @see ValueOperations#increment(java.lang.Object, double)
     * @see ValueOperations#decrement(java.lang.Object)
     * @see ValueOperations#decrement(java.lang.Object, long)
     */
    @Test
    public void testIncre (){
        String key ="stringTestIncre";
        Boolean delete = redisTemplate.delete(key);
        System.out.println(delete);
        redisTemplate.opsForValue().set(key,99);
        Long foo = (Long) redisTemplate.opsForValue().increment(key);
        System.out.println("100="+foo);
        foo = (Long) redisTemplate.opsForValue().increment(key,10);
        System.out.println("110="+foo);
        //Double foo2 = (Double) redisTemplate.opsForValue().increment(key,10.1d);//导致value为double型，在执行decremetn会报错
        //System.out.println("120.1="+foo2);
        foo = (Long) redisTemplate.opsForValue().decrement(key);
        System.out.println("119="+foo);
        foo = (Long) redisTemplate.opsForValue().decrement(key,10l);
        System.out.println("109="+foo);
        Double foo2 = (Double) redisTemplate.opsForValue().increment(key,10.1d);
        System.out.println("109.1="+foo2);
    }

    /**
     *
     */
    @Test
    public void testAppend () {
        String key = "testAppend";
        redisTemplate.opsForValue().set(key,"hello=");
        System.out.println(redisTemplate.opsForValue().get(key));
        //追加
        Integer im_append = redisTemplate.opsForValue().append(key, "im append");
        System.out.println(im_append);
        //?????
        //"\"hello=\"im append"
        //实际取出来是hello=
        System.out.println(redisTemplate.opsForValue().get(key));
    }

    /**
     * @see ValueOperations#get(java.lang.Object, long, long)
     * @see ValueOperations#set(java.lang.Object, java.lang.Object, long)
     */
    @Test
    public void testget() {
        String key = "testAppend";
        System.out.println(redisTemplate.opsForValue().get(key,3,12));
        redisTemplate.opsForValue().set(key," insert ",2);
        System.out.println(redisTemplate.opsForValue().get(key));
    }

    @Test
    public void testSize() {
        String key = "testAppend";
        System.out.println(redisTemplate.opsForValue().size(key));
    }

    /**
     * @see ValueOperations#setBit(java.lang.Object, long, boolean)
     */
    @Test
    public void testBit() {
        String key = "testBit";
        redisTemplate.opsForValue().set(key,"adcd");
        System.out.println(redisTemplate.opsForValue().get(key));
        redisTemplate.opsForValue().setBit(key,6,true);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().getBit(key,0));
    }

    /**
     * 1、0或1为第一个点
     * 2、当key不存在时，创建key
     * 3、字符串会自动增长，以适应offset位置
     * 4、offset为0到2的32次方-1之间
     * 5、当设置的offset超过当前字符串位数时，redis需要时间分配内存
     * 以10年MacBook为例，2的32次方-1需要300ms，2的30次方需要80ms，2的26次方，需要8ms
     */
    @Test
    public void testBit1() {
        String key = "testBit";
        redisTemplate.opsForValue().set(key,"a");
        System.out.println(redisTemplate.opsForValue().get(key));
        //redisTemplate.opsForValue().setBit(key,0,true);
        redisTemplate.opsForValue().setBit(key,1,true);
        redisTemplate.opsForValue().setBit(key,2,true);
        redisTemplate.opsForValue().setBit(key,3,true);
        redisTemplate.opsForValue().setBit(key,4,true);
        redisTemplate.opsForValue().setBit(key,5,true);
        redisTemplate.opsForValue().setBit(key,6,true);
        redisTemplate.opsForValue().setBit(key,7,true);
        redisTemplate.opsForValue().setBit(key,8,true);
        redisTemplate.opsForValue().setBit(key,9,true);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().getBit(key,1));
    }
///######################################################################################

}
