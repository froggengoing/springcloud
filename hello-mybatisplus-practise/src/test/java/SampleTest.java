import com.froggengo.mybatisplus.MybatisPlusMain;
import com.froggengo.mybatisplus.User;
import com.froggengo.mybatisplus.UserMapper;
import com.froggengo.mybatisplus.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlusMain.class)
public class SampleTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
        System.out.println("-------------");
        userService.list().forEach(System.out::println);
    }


}