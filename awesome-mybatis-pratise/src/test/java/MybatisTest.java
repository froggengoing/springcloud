import com.froggengo.MybaitspractiseMain;
import com.froggengo.entity.Payment;
import com.froggengo.mapper.PaymentMapper;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author froggengo@qq.com
 * @date 2020/12/11 9:07.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={MybaitspractiseMain.class})
public class MybatisTest {
    @Autowired
    PaymentMapper paymentMapper;
    @Test
    public void test (){
        Payment payment = new Payment();
        payment.setSerial("fly");
        List<Payment> list = paymentMapper.getList(payment);
        list.forEach(n-> System.out.println(n.getId()));
    }
}
