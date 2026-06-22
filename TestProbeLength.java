import com.attech.amhs.ua.service.TestCaseConfigLoader;
import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import java.util.List;

public class TestProbeLength {
    public static void main(String[] args) {
        List<TestCase> testCases = TestCaseConfigLoader.loadAllTestCases();
        for (TestCase tc : testCases) {
            if ("CTSW011".equals(tc.getId())) {
                System.out.println("Found CTSW011");
                for (TestSubcase sub : tc.getSubcases()) {
                    if ("CTSW011.3".equals(sub.getId())) {
                        String contentLength = sub.getAmhsDefault("content-length");
                        System.out.println("Subcase 3 content-length = '" + contentLength + "'");
                        // Try to parse as int
                        try {
                            int len = Integer.parseInt(contentLength.trim());
                            System.out.println("Parsed length = " + len);
                        } catch (NumberFormatException e) {
                            System.out.println("Not a number");
                        }
                    }
                }
                break;
            }
        }
    }
}