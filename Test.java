import com.isode.x400api.X400_att;
import java.lang.reflect.Field;
public class Test {
    public static void main(String[] args) {
        for (Field f : X400_att.class.getDeclaredFields()) {
            if (f.getName().contains("ORIGINATOR")) {
                System.out.println(f.getName() + " = " + f.getType().getName());
            }
        }
    }
}
