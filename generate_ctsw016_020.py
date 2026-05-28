#!/usr/bin/env python3
import os

TESTCASES_DIR = "/workspace/src/main/resources/testcases"

# CTSW016 - Generate DR for successfully delivered message to AMHS user
ctsw016_content = """# Test Case: CTSW016 - Generate a DR for a successfully delivered message to an AMHS user
# Description: Test DR generation when message is successfully delivered to AMHS user
# Reference: EUR Doc 047 Appendix A, Page 32, Section 4.4.8
# Test Criteria: Successful if IUT generates DR for successfully delivered IPM
# Verification: Check DR received at AMHS interface per ICAO Doc 9880 Part II 4.5.6

# ============================================================================
# MESSAGES SUCCESSFULLY DELIVERED TO AMHS USERS
# Originator requests delivery report via originator-report-request or originating-MTA-report-request
# ============================================================================

subcase.1.id=CTSW016.1
subcase.1.name=DR For Successfully Delivered Message To AMHS User
subcase.1.description=IPM successfully delivered to AMHS user with report requested
subcase.1.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW016.1 - DR Expected For AMHS User Delivery
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message successfully delivered to AMHS user - DR expected
subcase.1.amhs.originator-report-request=report
subcase.1.amhs.expected-dr=true
"""

with open(os.path.join(TESTCASES_DIR, "CTSW016.properties"), "w") as f:
    f.write(ctsw016_content)
print("Generated CTSW016.properties")

# CTSW017 - Generate NDR for undeliverable message
ctsw017_content = """# Test Case: CTSW017 - Generate an NDR for an undeliverable message
# Description: Test NDR generation when message cannot be delivered
# Reference: EUR Doc 047 Appendix A, Page 33, Section 4.4.8
# Test Criteria: Successful if IUT generates NDR for undeliverable IPM
# Verification: Check NDR contains appropriate reason and diagnostic codes

# ============================================================================
# UNDELIVERABLE MESSAGES
# Messages that cannot be delivered to recipients
# ============================================================================

subcase.1.id=CTSW017.1
subcase.1.name=NDR For Undeliverable Message
subcase.1.description=IPM addressing unknown recipient - NDR expected
subcase.1.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=UNKNOWN_USER/OU=INVALID/O=BAD/PRMD=NONE/ADMD=ICAO/C=XX/
subcase.1.amhs.subject=CTSW017.1 - NDR Expected For Unknown Recipient
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message to unknown recipient - NDR expected
subcase.1.amhs.originator-report-request=non-delivery-report
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=unrecognised-OR-name
"""

with open(os.path.join(TESTCASES_DIR, "CTSW017.properties"), "w") as f:
    f.write(ctsw017_content)
print("Generated CTSW017.properties")

# CTSW018 - Reject IPM with unsupported body part type
ctsw018_content = """# Test Case: CTSW018 - Reject an IPM with unsupported body part type
# Description: Test rejection of messages with unsupported body part types
# Reference: EUR Doc 047 Appendix A, Page 34, Section 4.4.2.2, 4.4.8
# Test Criteria: Successful if IUT rejects IPM with unsupported body part type
# Verification: Check NDR contains unable-to-transfer, content-syntax-error

# ============================================================================
# MESSAGES WITH UNSUPPORTED BODY PART TYPES
# Only ia5-text, general-text-body-part, and file-transfer-body-part are supported
# ============================================================================

subcase.1.id=CTSW018.1
subcase.1.name=Unsupported Body Part Type
subcase.1.description=IPM with unsupported body part type (e.g., audio, video)
subcase.1.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW018.1 - Unsupported Body Part Type
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=audio
subcase.1.amhs.content=ATS message with unsupported audio body part - should be rejected
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=content-syntax-error
subcase.1.amhs.ndr-supplementary=unable to convert to AMQP due to unsupported body part type
"""

with open(os.path.join(TESTCASES_DIR, "CTSW018.properties"), "w") as f:
    f.write(ctsw018_content)
print("Generated CTSW018.properties")

# CTSW019 - Handle IPM with security labels
ctsw019_content = """# Test Case: CTSW019 - Handle IPM with security labels
# Description: Test handling of messages with security classification
# Reference: EUR Doc 047 Appendix A, Page 35, Section 4.4.4
# Test Criteria: Successful if IUT correctly processes security-labeled messages
# Verification: Check security attributes preserved in AMQP message

# ============================================================================
# MESSAGES WITH SECURITY CLASSIFICATION
# Security labels shall be mapped to AMQP message attributes
# ============================================================================

subcase.1.id=CTSW019.1
subcase.1.name=IPM With Security Classification Unclassified
subcase.1.description=IPM with unclassified security label
subcase.1.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW019.1 - Unclassified Message
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with unclassified security label
subcase.1.amhs.security-classification=unclassified
subcase.1.amhs.expected-ndr=false

subcase.2.id=CTSW019.2
subcase.2.name=IPM With Security Classification Restricted
subcase.2.description=IPM with restricted security label
subcase.2.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject=CTSW019.2 - Restricted Message
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with restricted security label
subcase.2.amhs.security-classification=restricted
subcase.2.amhs.expected-ndr=false
"""

with open(os.path.join(TESTCASES_DIR, "CTSW019.properties"), "w") as f:
    f.write(ctsw019_content)
print("Generated CTSW019.properties")

# CTSW020 - Handle IPM with expiration date
ctsw020_content = """# Test Case: CTSW020 - Handle IPM with expiration date
# Description: Test handling of messages with expiration-time attribute
# Reference: EUR Doc 047 Appendix A, Page 36, Section 4.4.5
# Test Criteria: Successful if IUT correctly processes expiration-time
# Verification: Check message handling based on expiration time

# ============================================================================
# MESSAGES WITH EXPIRATION-TIME
# Messages shall be processed based on expiration-time attribute in MTE
# ============================================================================

subcase.1.id=CTSW020.1
subcase.1.name=IPM With Future Expiration Time
subcase.1.description=IPM with expiration-time in the future - should be delivered
subcase.1.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW020.1 - Valid Expiration Time
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with future expiration time - should be converted to AMQP
subcase.1.amhs.expiration-time=20300101120000
subcase.1.amhs.expected-ndr=false

subcase.2.id=CTSW020.2
subcase.2.name=IPM With Expired Time
subcase.2.description=IPM with expiration-time in the past - should be rejected
subcase.2.amhs.originator=/CN=AMQP_ORIGINATOR/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject=CTSW020.2 - Expired Message
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with expired expiration time - should generate NDR
subcase.2.amhs.expiration-time=20200101120000
subcase.2.amhs.expected-ndr=true
subcase.2.amhs.ndr-reason=unable-to-transfer
subcase.2.amhs.ndr-diagnostic=maximum-time-expired
"""

with open(os.path.join(TESTCASES_DIR, "CTSW020.properties"), "w") as f:
    f.write(ctsw020_content)
print("Generated CTSW020.properties")

print("\\nGenerated CTSW016-CTSW020")
