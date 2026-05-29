import java.lang.reflect.Method;
public class Inspect {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.isode.x400.highlevel.X400Msg");
        for (Method m : clazz.getMethods()) {
            if (m.getName().toLowerCase().contains("probe") || m.getName().toLowerCase().contains("receipt") || m.getName().toLowerCase().contains("rn")) {
                System.out.println(m);
            }
        }
    }
}
