package com.attech.amhs.ua.service;

import com.attech.amhs.ua.model.TestCase;
import com.attech.amhs.ua.model.TestSubcase;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader for initializing test cases from ICAO EUR Doc 047 Appendix A
 * CTSW001 - CTSW020 test cases
 */
public class TestCaseLoader {

    /**
     * Load default test cases template (CTSW001-CTSW020)
     * Users can customize defaults in the returned list
     * 
     * @return List of test cases with empty defaults (to be populated by user)
     */
    public static List<TestCase> loadDefaultTestCases() {
        List<TestCase> testCases = new ArrayList<>();

        addCase(testCases, "CTSW001", "Convert an incoming IPM to AMQP format", "Conversion of IPM into AMQP format", new String[][]{
            {"Basic IPM - Priority KK", "ATS message with priority KK, basic ia5-text body part"},
            {"Basic IPM - Priority GG", "ATS message with priority GG, basic ia5-text body part"},
            {"Basic IPM - Priority FF", "ATS message with priority FF, basic ia5-text body part"},
            {"Basic IPM - Priority DD", "ATS message with priority DD, basic ia5-text body part"},
            {"Basic IPM - Priority SS", "ATS message with priority SS, basic ia5-text body part"},
            {"Extended IPM - Precedence 14", "Extended general-text-body-part with precedence 14"},
            {"Extended IPM - Precedence 28", "Extended general-text-body-part with precedence 28"},
            {"Extended IPM - Precedence 57", "Extended general-text-body-part with precedence 57"},
            {"Extended IPM - Precedence 71", "Extended general-text-body-part with precedence 71"},
            {"Extended IPM - Precedence 107", "Extended general-text-body-part with precedence 107"},
            {"Extended IPM - Multiple Precedence", "Extended general-text-body-part with precedence 107 and 14"}
        });

        addCase(testCases, "CTSW002", "Convert an incoming IPM containing optional-heading-information", "Conversion of IPM with OHI", new String[][]{
            {"Priority FF with OHI", "ATS message with FF priority and OHI text"},
            {"Priority SS with OHI", "ATS message with SS priority and OHI text"},
            {"Precedence 57 with Originators-Ref", "Precedence 57 and contain originators-reference element"},
            {"Precedence 107 with Originators-Ref", "Precedence 107 and contain originators-reference element"}
        });

        addCase(testCases, "CTSW003", "Generate a DR for a successfully translated IPM", "DR generation for translated IPM", new String[][]{
            {"No-Report/Non-Delivery-Report", "Originator: no-report(0), Originating-MTA: non-delivery-report(1)"},
            {"No-Report/Report", "Originator: no-report(0), Originating-MTA: report(2)"},
            {"No-Report/Audited-Report", "Originator: no-report(0), Originating-MTA: audited-report(3)"},
            {"Non-Delivery-Report/Non-Delivery-Report", "Originator: non-delivery-report(1), Originating-MTA: non-delivery-report(1)"},
            {"Non-Delivery-Report/Report", "Originator: non-delivery-report(1), Originating-MTA: report(2)"},
            {"Non-Delivery-Report/Audited-Report", "Originator: non-delivery-report(1), Originating-MTA: audited-report(3)"}
        });

        addCase(testCases, "CTSW004", "Generate an NDR if ATS-message-header has syntax error", "NDR generation for header syntax errors", new String[][]{
            {"Empty Priority", "IPM with empty ATS-message-priority"},
            {"Invalid Priority", "IPM with invalid ATS-message-priority"},
            {"Empty Filing-Time", "IPM with empty ATS-message-filing-time"},
            {"Invalid Filing-Time", "IPM with invalid ATS-message-filing-time"},
            {"Empty Header & No IHE", "IPM with empty ATS-message-header and no IHE"}
        });

        addCase(testCases, "CTSW005", "Generate an NDR if current time exceeds latest delivery time", "NDR for expired delivery time", new String[][]{
            {"Latest Delivery Time in Past", "Latest-delivery-time set to a date in the past"},
            {"Latest Delivery Time in Future", "Latest-delivery-time later than current time"}
        });

        addCase(testCases, "CTSW006", "Reject IPM if payload size exceeds maximum", "Payload size validation", new String[][]{
            {"Payload within Limit", "IA5-text body part size <= Maximum message data size"},
            {"Payload exceeds Limit (IA5)", "IA5-text body part size > Maximum message data size"},
            {"Payload exceeds Limit (FTBP)", "File-transfer-body-part size > Maximum message data size"}
        });

        addCase(testCases, "CTSW007", "Reject IPM with multiple body parts", "Multiple body part validation", new String[][]{
            {"Text + FTBP (Valid)", "One basic ia5-text and one file-transfer-body-part"},
            {"General-Text + FTBP (Valid)", "One general-text-body-part and one file-transfer-body-part"},
            {"Two IA5-Text (Invalid)", "Two ia5-text body parts"},
            {"Three Body Parts (Invalid)", "One basic ia5-text, one general-text-body-part, and one file-transfer-body-part"}
        });

        addCase(testCases, "CTSW008", "Reject IPM with unsupported content-type", "Content-type validation", new String[][]{
            {"Interpersonal-Messaging-1988(22)", "Content-type: interpersonal-messaging-1988(22)"},
            {"Interpersonal-Messaging-1984(2)", "Content-type: interpersonal-messaging-1984(2)"},
            {"EDI-Messaging(35)", "Content-type: edi-messaging(35)"},
            {"Unidentified(0)", "Content-type: unidentified(0)"}
        });

        addCase(testCases, "CTSW009", "Distribute IPM to AMHS users and AMQP consumers", "Multi-protocol distribution", new String[][]{
            {"Primary AMHS+AMQP & Copy AMHS+AMQP", "Primary: 1 AMHS, 1 AMQP; Copy: 1 AMHS, 1 AMQP"},
            {"Primary AMHS+AMQP & BCC AMHS+AMQP", "Primary: 1 AMHS, 1 AMQP; BCC: 1 AMHS, 1 AMQP"}
        });

        addCase(testCases, "CTSW010", "Reject IPM addressing more AMQP consumers than maximum", "Recipient count validation", new String[][]{
            {"Recipients within Limit", "512 recipients (limit=512)"},
            {"Recipients exceed Limit", "513 recipients (limit=512)"}
        });

        addCase(testCases, "CTSW011", "Probe Conveyance Test", "AMHS Probe testing", new String[][]{
            {"Probe 1: Valid", "Content-length < max size, reachable AMQP consumer"},
            {"Probe 2: Unknown Recipient", "Content-length < max size, unreachable/unmappable AMQP consumer"},
            {"Probe 3: Over Limit", "Content-length > max size, reachable AMQP consumer"},
            {"Probe 4: Max Recipients", "512 AMQP consumers (limit=512)"},
            {"Probe 5: Over Max Recipients", "> 512 AMQP consumers (limit=512)"}
        });

        addCase(testCases, "CTSW012", "Reject Probe for unknown recipients", "Probe recipient validation", new String[][]{
            {"Mixed Valid/Unknown Recipients", "Two recipients: one mappable, one unknown"}
        });

        addCase(testCases, "CTSW013", "Reject Probe with unknown originator address", "Probe originator validation", new String[][]{
            {"Invalid Originator Address", "Probe with invalid AMHS address in originator-name"}
        });

        addCase(testCases, "CTSW014", "Incoming RN relating to subject message with priority != SS", "RN priority validation", new String[][]{
            {"Subject Priority SS", "RN with subject IPM priority set to SS"},
            {"Subject Priority DD", "RN with subject IPM priority set to DD"}
        });

        addCase(testCases, "CTSW015", "Incoming RN without related subject message", "RN subject validation", new String[][]{
            {"Fictitious Subject IPM", "RN with a fictitious subject IPM"}
        });

        addCase(testCases, "CTSW016", "Processing of the current encoded-information-types (EIT)", "EIT validation", new String[][]{
            {"EIT: ia5-text(2)", "Built-in-encoded-information-types: ia5-text(2)"},
            {"EIT: unknown(0)", "Built-in-encoded-information-types: unknown(0)"},
            {"EIT: OID 2.6.3.4.2", "Extended-encoded-information-types: OID 2.6.3.4.2"},
            {"EIT: OID 2.6.3.4.0", "Extended-encoded-information-types: OID 2.6.3.4.0"},
            {"EIT: OID {id-cs-eit-authority 1}", "Extended-encoded-information-types: OID {id-cs-eit-authority 1}"},
            {"EIT: OID {id-cs-eit-authority 2}", "Extended-encoded-information-types: OID {id-cs-eit-authority 2}"},
            {"EIT: OIDs {1, 6}", "Extended-encoded-information-types: OID {id-cs-eit-authority 1} and OID {id-cs-eit-authority 6}"},
            {"EIT: OIDs {1, 6, 100}", "Extended-encoded-information-types: OID {id-cs-eit-authority 1, 6, 100}"},
            {"EIT: Invalid OID {3}", "Extended-encoded-information-types: OID {id-cs-eit-authority 3}"},
            {"EIT: OIDs {1, 6} + Invalid {7}", "Extended-encoded-information-types: OID {1, 6} and invalid OID {7}"},
            {"EIT: Built-in(2) + Extended {2.6.3.4.2, 1, 6}", "Mixed built-in ia5-text(2) and extended OIDs"},
            {"EIT: OID {id-eit-file-transfer 0}", "Extended-encoded-information-types: OID {id-eit-file-transfer 0}"}
        });

        addCase(testCases, "CTSW017", "Incoming IPM with an ia5-text-body-part", "IA5-text body part validation", new String[][]{
            {"Extended EIT 2.6.3.4.2", "ia5-text-body-part with extended EIT 2.6.3.4.2"},
            {"Built-in EIT ia5-text(2)", "ia5-text-body-part with built-in value ia5-text(2)"},
            {"Repertoire ita2(2)", "ia5-text-body-part with repertoire ita2(2)"}
        });

        addCase(testCases, "CTSW018", "Incoming IPM with general-text-body-part and ISO 646", "ISO 646 general text validation", new String[][]{
            {"ISO 646 only", "General-text-body-part with ISO 646 (US-ASCII) characters only"},
            {"US-ASCII + non-listed", "General-text-body-part with US-ASCII and non-listed US-ASCII characters"}
        });

        addCase(testCases, "CTSW019", "Incoming IPM with general-text-body-part and non-ISO 646", "Non-ISO 646 general text validation", new String[][]{
            {"ISO 8859-1", "General-text-body-part with ISO 8859-1 repertoire"},
            {"Other (Cyrillic, etc)", "General-text-body-part with Cyrillic, Arabic, Greek or Hebrew repertoire"},
            {"Other (CJK)", "General-text-body-part with Chinese, Japanese or Korean repertoire"}
        });

        addCase(testCases, "CTSW020", "Notify SS to Control Position", "Notification to Control Position", new String[][]{
            {"2 AMQP (Prec 107 & 28)", "Two AMQP consumers (responsible), precedence 107 and 28"},
            {"2 AMQP (Priority SS)", "Two AMQP consumers (responsible), priority SS"},
            {"1 AMQP (Prec 107) + 1 AMHS (Non-Resp)", "1 AMQP (responsible, prec 107), 1 AMHS (non-responsible, prec 107)"},
            {"2 AMQP (Prec 14)", "Two AMQP consumers (responsible), precedence 14"},
            {"2 AMQP (Priority DD)", "Two AMQP consumers (responsible), priority DD"}
        });

        return testCases;
    }

    private static void addCase(List<TestCase> cases, String id, String name, String description, String[][] subcases) {
        TestCase testCase = new TestCase();
        testCase.setId(id);
        testCase.setName(name);
        testCase.setDescription(description);
        
        for (int i = 0; i < subcases.length; i++) {
            TestSubcase subcase = new TestSubcase();
            subcase.setId(id + "." + (i + 1));
            subcase.setName(subcases[i][0]);
            subcase.setDescription(subcases[i][1]);
            testCase.addSubcase(subcase);
        }
        cases.add(testCase);
    }

    /**
     * Load test cases with SAMPLE defaults (for testing/demo)
     * Replace with actual defaults from ICAO EUR Doc 047 when available
     * 
     * @return List of test cases with sample AMHS defaults
     */
    public static List<TestCase> loadTestCasesWithSampleDefaults() {
        List<TestCase> testCases = loadDefaultTestCases();
        
        // Set sample defaults for demonstration
        // In production, these would come from ICAO EUR Doc 047 Appendix A specifications
        for (TestCase testCase : testCases) {
            for (TestSubcase subcase : testCase.getSubcases()) {
                // Sample X.400 defaults (customize per actual test case requirements)
                subcase.setAmhsDefault("recipient", "/CN=TestRecipient/OU=Test/O=TestOrg/PRMD=TestPRMD/ADMD=/C=US/");
                subcase.setAmhsDefault("subject", "Test Message: " + subcase.getId());
                subcase.setAmhsDefault("priority", "NORMAL");
                subcase.setAmhsDefault("content", "This is a test message for " + subcase.getId() + 
                                                 ". Replace with actual test content from ICAO EUR Doc 047.");
            }
        }
        
        return testCases;
    }

    /**
     * Create a single test case programmatically
     * 
     * @param caseId Test case ID (e.g., "CTSW001")
     * @param name Test case name
     * @param subcaseCount Number of subcases to create
     * @return TestCase object
     */
    public static TestCase createTestCase(String caseId, String name, int subcaseCount) {
        TestCase testCase = new TestCase();
        testCase.setId(caseId);
        testCase.setName(name);
        testCase.setDescription("Test case " + caseId);
        
        for (int i = 1; i <= subcaseCount; i++) {
            TestSubcase subcase = new TestSubcase();
            subcase.setId(caseId + "." + i);
            subcase.setName(caseId + " - Subcase " + i);
            subcase.setDescription("Subcase " + i + " for " + caseId);
            testCase.addSubcase(subcase);
        }
        
        return testCase;
    }

    /**
     * Create a test case with predefined AMHS defaults
     * 
     * @param caseId Test case ID
     * @param name Test case name
     * @param recipient Default recipient
     * @param subject Default subject
     * @param priority Default priority
     * @param content Default content
     * @return TestCase with subcase having AMHS defaults
     */
    public static TestCase createTestCaseWithDefaults(String caseId, String name,
                                                       String recipient, String subject,
                                                       String priority, String content) {
        TestCase testCase = createTestCase(caseId, name, 1);
        TestSubcase subcase = testCase.getSubcases().get(0);
        
        subcase.setAmhsDefault("recipient", recipient);
        subcase.setAmhsDefault("subject", subject);
        subcase.setAmhsDefault("priority", priority);
        subcase.setAmhsDefault("content", content);
        
        return testCase;
    }
}
