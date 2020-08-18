import org.junit.Test;

public class  test{
    @Test
    public void test (){
        String node="/election/p_0000000001";
        String substring = node.substring(node.lastIndexOf("/") + 1);
        System.out.println(substring);
    }
}