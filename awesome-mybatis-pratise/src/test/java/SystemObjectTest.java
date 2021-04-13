import com.froggengo.entity.Payment;
import java.io.IOException;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.reflection.*;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.core.io.ClassPathResource;

public class SystemObjectTest {
    @Test
    public void testOne (){
/*        Configuration configuration = new Configuration();
        DefaultReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        DefaultObjectFactory objectFactory = new DefaultObjectFactory();
        DefaultObjectWrapperFactory wrapperFactory = new DefaultObjectWrapperFactory();
        SystemMetaObject.forObject()*/
    }
    @Test
    public void testBean (){
        //SystemMetaObject
        Payment animal = new Payment();
        MetaObject metaObject = MetaObject.forObject(animal, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        metaObject.setValue("id","bean");
        System.out.println(animal.getId());
        System.out.println(metaObject.getValue("id"));
    }
    @Test
    public void testCollection (){
        Payment payment = new Payment();
        payment.setId("bean");
        List<Payment> list = new ArrayList<>();
        MetaObject metaObject = MetaObject.forObject(list, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        metaObject.add(payment);
        System.out.println(list.get(0).getId());
    }
    @Test
    public void testMap (){
        Payment payment = new Payment();
        payment.setId("bean");
        HashMap<String, Payment> map = new HashMap<>();
        MetaObject metaObject = MetaObject.forObject(map, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        metaObject.setValue("pp",payment);
        System.out.println(map.get("pp").getId());
    }
    @Test
    public void testTokenizer (){
        Payment payment = new Payment();
        Payment detailPay = new Payment();
        payment.setId("bean");
        detailPay.setId("detailPayBean");
        payment.setDetail(detailPay);
        MetaObject metaObject = MetaObject.forObject(payment, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        System.out.println(metaObject.getValue("detail.id"));
        metaObject.setValue("detail.serial","detailSerial");
        System.out.println(metaObject.getValue("detail.serial"));

    }
    @Test
    public void testBeanWrapper (){
        Payment payment = new Payment();
        payment.setId("bean");
        MetaObject metaObject = MetaObject.forObject(payment, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        BeanWrapper beanWrapper = new BeanWrapper(metaObject,payment);
        Class<?> id = beanWrapper.getSetterType("id");
        Object id1 = beanWrapper.get(new PropertyTokenizer("id"));
        System.out.println(id1);
    }
    @Test
    public void testMeteClass () throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Payment payment=new Payment();
        MetaClass metaClass = MetaClass.forClass(Payment.class, new DefaultReflectorFactory());
        Invoker id = metaClass.getSetInvoker("id");
        Object[] objects = new Object[1];
        objects[0]="beanName";
        id.invoke(payment,objects);
        System.out.println(payment.getId());
    }

    @Test
    public void testReflector () throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Reflector reflector = new Reflector(Payment.class);
        Payment payment = (Payment) reflector.getDefaultConstructor().newInstance();
    }
    @Test
    public void testReflectorFactory (){
        DefaultReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    }
    @Test
    public void testResource () throws IOException {
        ClassPathResource resource = new ClassPathResource("mybatis/mybatis-config.xml");
        XMLConfigBuilder parser = new XMLConfigBuilder(resource.getInputStream(), null, null);
        org.apache.ibatis.session.Configuration configuration=parser.parse();
    }

}
