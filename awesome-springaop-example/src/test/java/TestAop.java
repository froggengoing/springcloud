import com.froggengo.SpringAopMain;
import com.froggengo.springaoptest.DriveCar;
import com.froggengo.springaoptest.previous.Annimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SpringAopMain.class)
@RunWith(SpringRunner.class)
public class TestAop {
    @Autowired
    Annimal annimal;
    @Autowired
    DriveCar driveCar;

    /**
     * 主要是debug查看aop调用流程
     */
    @Test
    public void test (){
        //driveCar.driver("宾利");
        annimal.sale();

    }
}
