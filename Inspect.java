import java.lang.reflect.Field;
import com.isode.x400api.X400_att;
import com.isode.x400api.AMHS_att;

public class Inspect {
    public static void main(String[] args) throws Exception {
        System.out.println("Fields in X400_att (public including inherited):");
        for (Field field : X400_att.class.getFields()) {
            System.out.println("X400: " + field.getName() + " = " + field.get(null));
        }
        System.out.println("Fields in AMHS_att (public including inherited):");
        for (Field field : AMHS_att.class.getFields()) {
            System.out.println("AMHS: " + field.getName() + " = " + field.get(null));
        }
    }
}
