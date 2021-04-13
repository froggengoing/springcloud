import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.froggengo.mybatisplus.MybatisPlusMain;
import com.froggengo.mybatisplus.User;
import com.froggengo.mybatisplus.UserMapper;
import com.froggengo.mybatisplus.UserService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void test() {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        QueryWrapper<User> id = queryWrapper.eq("id", "111");
        id.select(User.class, fieldInfo ->
            !fieldInfo.getColumn().equals("name")
                && !fieldInfo.getColumn().equals("age")).like("username", "猪")
            .likeLeft("email", "fanglile@")
            .or()
            .and(new Consumer<QueryWrapper<User>>() {
                @Override
                public void accept(QueryWrapper<User> userQueryWrapper) {

                }
            })
            .likeRight("emial", "com")
            .ge("age", "25")
            .in("age", 25, 26, 27)
            .orderByDesc();
        String sqlSelect = id.getCustomSqlSegment();
        String sqlComment = id.getSqlComment();
        System.out.println(sqlComment);
        String sqlFirst = id.getSqlFirst();
        System.out.println("first" + sqlFirst);
        String sqlSet = id.getSqlSet();
        System.out.println(sqlSet);
        System.out.println("select=" + id.getSqlSelect());
        Map<String, Object> paramNameValuePairs = id.getParamNameValuePairs();
        paramNameValuePairs.forEach((k, v) -> System.out.println(k + "==" + v));
        System.out.println(sqlSelect);
    }

}