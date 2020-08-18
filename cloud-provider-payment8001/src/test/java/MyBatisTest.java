import com.froggengo.cloud.Dao.PaymentDao;
import com.froggengo.cloud.Payment8001Main;
import com.froggengo.cloud.entities.Payment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Payment8001Main.class)
@RunWith(SpringRunner.class)
public class MyBatisTest {
    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Test
    public void test (){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PaymentDao mapper = sqlSession.getMapper(PaymentDao.class);
        Payment paymentBy999 = mapper.getPaymentBy999(999);
        System.out.println(paymentBy999);
        //new SqlSessionFactoryBuilder().build();
    }
}
