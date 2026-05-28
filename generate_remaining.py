#!/usr/bin/env python3
import os

TESTCASES_DIR = "/workspace/src/main/resources/testcases"

# CTSW009-CTSW015 generation
files_to_create = {
    "CTSW009.properties": """# Test Case: CTSW009 - Distribute an IPM to AMHS users and AMQP consumers
subcase.1.id=CTSW009.1
subcase.1.name=Primary and Copy Recipients Mixed
subcase.1.description=IPM with two primary (AMHS+AMQP) and two copy recipients (AMHS+AMQP)
subcase.1.amhs.primary-recipients=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.copy-recipients=/CN=AMHS_USER_2/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_2/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW009.1 - Primary and Copy Recipients
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with mixed AMHS and AMQP recipients
subcase.1.amhs.originator-report-request=non-delivery-report
subcase.2.id=CTSW009.2
subcase.2.name=Primary and BCC Recipients Mixed
subcase.2.description=IPM with two primary (AMHS+AMQP) and two BCC recipients (AMHS+AMQP)
subcase.2.amhs.primary-recipients=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.bcc-recipients=/CN=AMHS_USER_3/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_3/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject=CTSW009.2 - Primary and BCC Recipients
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with mixed AMHS and AMQP BCC recipients
subcase.2.amhs.originator-report-request=non-delivery-report
""",
    "CTSW010.properties": """# Test Case: CTSW010 - Reject IPM exceeding maximum configured recipients
subcase.1.id=CTSW010.1
subcase.1.name=Recipients At Maximum Limit
subcase.1.description=IPM with 512 recipients (at limit) - should be accepted
subcase.1.amhs.recipient-file=data/512_recipients.txt
subcase.1.amhs.subject=CTSW010.1 - 512 Recipients At Limit
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with 512 recipients
subcase.1.amhs.num-recipients=512
subcase.1.amhs.expected-ndr=false
subcase.2.id=CTSW010.2
subcase.2.name=Recipients Exceeds Maximum Limit
subcase.2.description=IPM with 513 recipients (exceeds limit) - NDR expected
subcase.2.amhs.recipient-file=data/513_recipients.txt
subcase.2.amhs.subject=CTSW010.2 - 513 Recipients Exceeds Limit
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with 513 recipients - should be rejected
subcase.2.amhs.num-recipients=513
subcase.2.amhs.expected-ndr=true
subcase.2.amhs.ndr-reason=unable-to-transfer
subcase.2.amhs.ndr-diagnostic=too-many-recipients
""",
    "CTSW011.properties": """# Test Case: CTSW011 - Probe Conveyance Test
subcase.1.id=CTSW011.1
subcase.1.name=Probe Valid Content Length Reachable Consumer
subcase.1.description=Probe with content-length < max size, reachable consumer - DR expected
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.content-length=1000
subcase.1.amhs.consumer-reachable=true
subcase.1.amhs.expected-report=DR
subcase.2.id=CTSW011.2
subcase.2.name=Probe Unreachable Consumer
subcase.2.description=Probe addressing unreachable consumer - NDR expected
subcase.2.amhs.recipient=/CN=UNKNOWN_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.priority=FF
subcase.2.amhs.consumer-reachable=false
subcase.2.amhs.expected-report=NDR
subcase.3.id=CTSW011.3
subcase.3.name=Probe Exceeds Content Length
subcase.3.description=Probe with content-length > max size - NDR expected
subcase.3.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.3.amhs.priority=FF
subcase.3.amhs.content-length=20000
subcase.3.amhs.max-message-data-size=10000
subcase.3.amhs.expected-report=NDR
""",
    "CTSW012.properties": """# Test Case: CTSW012 - Reject Probe for unknown recipients
subcase.1.id=CTSW012.1
subcase.1.name=Probe With Mixed Valid And Unknown Recipients
subcase.1.description=Probe with two recipients: first valid, second unknown
subcase.1.amhs.primary-recipients=/CN=VALID_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/,/CN=UNKNOWN_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.first-recipient-valid=true
subcase.1.amhs.second-recipient-valid=false
subcase.1.amhs.expected-ndr-for-unknown=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=unrecognised-OR-name
""",
    "CTSW013.properties": """# Test Case: CTSW013 - Reject Probe with unknown originator address
subcase.1.id=CTSW013.1
subcase.1.name=Probe With Unknown Originator
subcase.1.description=Probe with invalid originator that cannot be translated
subcase.1.amhs.originator=/CN=UNKNOWN_ORIGINATOR/OU=INVALID/O=BAD/PRMD=NONE/ADMD=ICAO/C=XX/
subcase.1.amhs.primary-recipients=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_2/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.originator-valid=false
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=invalid-arguments
""",
    "CTSW014.properties": """# Test Case: CTSW014 - Incoming RN relating to subject message with priority different from SS
subcase.1.id=CTSW014.1
subcase.1.name=RN With Subject Priority SS
subcase.1.description=RN with subject message priority SS - should be transferred
subcase.1.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject-ipm-priority=SS
subcase.1.amhs.message-type=RN
subcase.1.amhs.expected-rejection=false
subcase.1.amhs.notify-control-position=true
subcase.2.id=CTSW014.2
subcase.2.name=RN With Subject Priority DD
subcase.2.description=RN with subject message priority DD - should be rejected
subcase.2.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.2.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject-ipm-priority=DD
subcase.2.amhs.message-type=RN
subcase.2.amhs.expected-rejection=true
subcase.2.amhs.notify-control-position=true
""",
    "CTSW015.properties": """# Test Case: CTSW015 - Incoming RN without related subject message
subcase.1.id=CTSW015.1
subcase.1.name=RN Without Related Subject Message
subcase.1.description=RN with fictitious subject IPM that did not pass Gateway before
subcase.1.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject-ipm-id=FICTITIOUS-IPM-ID-NOT-FOUND
subcase.1.amhs.message-type=RN
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=invalid-arguments
subcase.1.amhs.ndr-supplementary=unable to notify RN to SWIM due to misrouted RN
subcase.1.amhs.notify-control-position=true
"""
}

for filename, content in files_to_create.items():
    filepath = os.path.join(TESTCASES_DIR, filename)
    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Generated {filename}")

print("\\nGenerated CTSW009-CTSW015")
