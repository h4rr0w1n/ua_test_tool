
# CTSW009 - Distribute IPM to AMHS users and AMQP consumers
ctsw009_content = """# Test Case: CTSW009 - Distribute an IPM to AMHS users and AMQP consumers
# Description: Test distribution of IPMs addressing both AMHS users and AMQP consumers
# Reference: EUR Doc 047 Appendix A, Page 25, Section 4.4.3.4.4
# Test Criteria: Successful if IUT distributes IPM correctly to both AMHS and AMQP recipients
# Verification: Check MTE and IPM heading at AMHS interface, addressee indicators at AMQP port

# ============================================================================
# MESSAGES ADDRESSING BOTH AMHS USERS AND AMQP CONSUMERS
# Message shall have originator-report-request flag set to non-delivery-report
# ============================================================================

# Subcase 1: Two primary recipients (AMHS + AMQP) + Two copy recipients (AMHS + AMQP)
subcase.1.id=CTSW009.1
subcase.1.name=Primary and Copy Recipients Mixed
subcase.1.description=First IPM with two primary recipients (one AMHS user, one AMQP consumer) 
                      and two copy recipients (one AMHS user, one AMQP consumer)
subcase.1.amhs.primary-recipients=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.copy-recipients=/CN=AMHS_USER_2/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_2/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject=CTSW009.1 - Primary and Copy Recipients
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with mixed AMHS and AMQP primary and copy recipients
subcase.1.amhs.originator-report-request=non-delivery-report
# Expected: MTE contains all AMHS recipient addresses, IPM heading has all recipients
# Expected: AMQP message contains addressee indicators of both AMQP consumers

# Subcase 2: Two primary recipients (AMHS + AMQP) + Two blind copy recipients (AMHS + AMQP)
subcase.2.id=CTSW009.2
subcase.2.name=Primary and Blind Copy Recipients Mixed
subcase.2.description=Second IPM with two primary recipients (one AMHS user, one AMQP consumer)
                      and two blind copy recipients (one AMHS user, one AMQP consumer)
subcase.2.amhs.primary-recipients=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.bcc-recipients=/CN=AMHS_USER_3/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_3/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject=CTSW009.2 - Primary and BCC Recipients
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with mixed AMHS and AMQP primary and blind copy recipients
subcase.2.amhs.originator-report-request=non-delivery-report
# Expected: MTE contains only AMHS BCC recipient address, IPM heading has all recipients
# Expected: AMQP message contains addressee indicator of AMQP BCC consumer only
"""

with open(os.path.join(TESTCASES_DIR, "CTSW009.properties"), "w") as f:
    f.write(ctsw009_content)
print("Generated CTSW009.properties")

# CTSW010 - Reject IPM addressing more AMQP consumers than maximum configured
ctsw010_content = """# Test Case: CTSW010 - Reject an IPM addressing more AMQP consumers than the maximum configured
# Description: Test rejection of messages exceeding Maximum message number of recipients
# Reference: EUR Doc 047 Appendix A, Page 26, Section 4.4.2.7, 4.4.8
# Test Criteria: Successful if IUT rejects IPM exceeding configured max recipients
# Verification: Check NDR contains unable-to-transfer, too-many-recipients

# Note: Assumes Maximum message number of recipients parameter is set to 512
# ============================================================================

# Subcase 1: IPM with 512 recipients (at limit) - Should be accepted
subcase.1.id=CTSW010.1
subcase.1.name=Recipients At Maximum Limit
subcase.1.description=First IPM with 512 recipients (equals Maximum message number of recipients)
subcase.1.amhs.recipient-file=data/512_recipients.txt
subcase.1.amhs.subject=CTSW010.1 - 512 Recipients At Limit
subcase.1.amhs.priority=FF
subcase.1.amhs.body-part-type=ia5-text
subcase.1.amhs.content=ATS message with 512 recipients - should be accepted
subcase.1.amhs.originator-report-request=non-delivery-report
subcase.1.amhs.num-recipients=512
subcase.1.amhs.expected-ndr=false

# Subcase 2: IPM with 513 recipients (exceeds limit) - NDR expected
subcase.2.id=CTSW010.2
subcase.2.name=Recipients Exceeds Maximum Limit
subcase.2.description=Second IPM with 513 recipients (exceeds Maximum message number of recipients)
subcase.2.amhs.recipient-file=data/513_recipients.txt
subcase.2.amhs.subject=CTSW010.2 - 513 Recipients Exceeds Limit
subcase.2.amhs.priority=FF
subcase.2.amhs.body-part-type=ia5-text
subcase.2.amhs.content=ATS message with 513 recipients - should be rejected
subcase.2.amhs.originator-report-request=non-delivery-report
subcase.2.amhs.num-recipients=513
subcase.2.amhs.expected-ndr=true
subcase.2.amhs.ndr-reason=unable-to-transfer
subcase.2.amhs.ndr-diagnostic=too-many-recipients
subcase.2.amhs.ndr-supplementary=unable to convert to AMQP due to number of recipients
"""

with open(os.path.join(TESTCASES_DIR, "CTSW010.properties"), "w") as f:
    f.write(ctsw010_content)
print("Generated CTSW010.properties")

# CTSW011 - Probe Conveyance Test
ctsw011_content = """# Test Case: CTSW011 - Probe Conveyance Test
# Description: Test probe handling for various scenarios
# Reference: EUR Doc 047 Appendix A, Page 27, Section 4.4.6.2, 4.4.6.3, 4.4.6.6
# Test Criteria: Successful if IUT generates DR for valid probes, NDR for invalid
# Verification: Check report type (DR/NDR) returned for each probe scenario

# ============================================================================
# PROBES WITH VARIOUS CONDITIONS
# Check reports received at AMHS Test Tool-AMHS interface
# ============================================================================

# Subcase 1: Probe with content-length < max size, reachable AMQP consumer - DR expected
subcase.1.id=CTSW011.1
subcase.1.name=Probe Valid Content Length Reachable Consumer
subcase.1.description=Probe 1 with content-length lower than Maximum message data size,
                      addressing reachable AMQP consumer
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.content-length=1000
subcase.1.amhs.max-message-data-size=10000
subcase.1.amhs.consumer-reachable=true
subcase.1.amhs.expected-report=DR

# Subcase 2: Probe with content-length < max size, unreachable AMQP consumer - NDR expected
subcase.2.id=CTSW011.2
subcase.2.name=Probe Valid Content Length Unreachable Consumer
subcase.2.description=Probe 2 with content-length lower than Maximum message data size,
                      addressing AMQP consumer that cannot be mapped to valid AMQP address
subcase.2.amhs.recipient=/CN=UNKNOWN_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.priority=FF
subcase.2.amhs.content-length=1000
subcase.2.amhs.max-message-data-size=10000
subcase.2.amhs.consumer-reachable=false
subcase.2.amhs.expected-report=NDR

# Subcase 3: Probe with content-length > max size, reachable AMQP consumer - NDR expected
subcase.3.id=CTSW011.3
subcase.3.name=Probe Exceeds Content Length
subcase.3.description=Probe 3 with content-length higher than Maximum message data size,
                      addressing reachable AMQP consumer
subcase.3.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.3.amhs.priority=FF
subcase.3.amhs.content-length=20000
subcase.3.amhs.max-message-data-size=10000
subcase.3.amhs.consumer-reachable=true
subcase.3.amhs.expected-report=NDR

# Subcase 4: Probe with 512 recipients (at limit) - DR expected
subcase.4.id=CTSW011.4
subcase.4.name=Probe At Recipient Limit
subcase.4.description=Probe 4 with 512 AMQP consumers (equals Maximum message number of recipients)
subcase.4.amhs.recipient-file=data/512_recipients.txt
subcase.4.amhs.priority=FF
subcase.4.amhs.num-recipients=512
subcase.4.amhs.max-recipients=512
subcase.4.amhs.expected-report=DR

# Subcase 5: Probe with 513 recipients (exceeds limit) - NDR expected
subcase.5.id=CTSW011.5
subcase.5.name=Probe Exceeds Recipient Limit
subcase.5.description=Probe 5 with more than 512 AMQP consumers (exceeds Maximum message number of recipients)
subcase.5.amhs.recipient-file=data/513_recipients.txt
subcase.5.amhs.priority=FF
subcase.5.amhs.num-recipients=513
subcase.5.amhs.max-recipients=512
subcase.5.amhs.expected-report=NDR
"""

with open(os.path.join(TESTCASES_DIR, "CTSW011.properties"), "w") as f:
    f.write(ctsw011_content)
print("Generated CTSW011.properties")

# CTSW012 - Reject Probe for unknown recipients
ctsw012_content = """# Test Case: CTSW012 - Reject a Probe for unknown recipients before conversion into AMQP format
# Description: Test probe rejection for unknown recipients
# Reference: EUR Doc 047 Appendix A, Page 28, Section 4.4.6.5, 4.4.6.6
# Test Criteria: Successful if IUT rejects probe for unknown recipients
# Verification: Check NDR for unknown recipient, DR for valid recipient

# ============================================================================
# PROBE WITH MIXED VALID/UNKNOWN RECIPIENTS
# First recipient can be translated, second cannot
# ============================================================================

subcase.1.id=CTSW012.1
subcase.1.name=Probe With Mixed Valid And Unknown Recipients
subcase.1.description=Probe with two primary recipients: first AMQP consumer can be translated,
                      second AMQP consumer is unknown (no match in address look-up table)
subcase.1.amhs.primary-recipients=/CN=VALID_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/,/CN=UNKNOWN_CONSUMER/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.first-recipient-valid=true
subcase.1.amhs.second-recipient-valid=false
# Expected: NDR returned for unknown recipient with unrecognised-OR-name
# Expected: DR generated for valid recipient
subcase.1.amhs.expected-ndr-for-unknown=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=unrecognised-OR-name
"""

with open(os.path.join(TESTCASES_DIR, "CTSW012.properties"), "w") as f:
    f.write(ctsw012_content)
print("Generated CTSW012.properties")

# CTSW013 - Reject Probe with unknown originator address
ctsw013_content = """# Test Case: CTSW013 - Reject a Probe with unknown originator address
# Description: Test probe rejection when originator address cannot be converted
# Reference: EUR Doc 047 Appendix A, Page 29, Section 4.4.6.4
# Test Criteria: Successful if IUT rejects probe with unknown originator
# Verification: Check NDR contains unable-to-transfer, invalid-arguments

# ============================================================================
# PROBE WITH UNKNOWN ORIGINATOR ADDRESS
# Originator cannot be translated by MTCU
# ============================================================================

subcase.1.id=CTSW013.1
subcase.1.name=Probe With Unknown Originator
subcase.1.description=Probe with invalid AMHS address in originator-name element that 
                      cannot be translated by MTCU (no match in address look-up table)
subcase.1.amhs.originator=/CN=UNKNOWN_ORIGINATOR/OU=INVALID/O=BAD/PRMD=NONE/ADMD=ICAO/C=XX/
subcase.1.amhs.primary-recipients=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/,/CN=AMQP_CONSUMER_2/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.priority=FF
subcase.1.amhs.originator-valid=false
# Expected: Probe rejected, NDR returned for all recipients
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=invalid-arguments
subcase.1.amhs.ndr-supplementary=unable to convert to AMQP due to unrecognized originator O/R address
"""

with open(os.path.join(TESTCASES_DIR, "CTSW013.properties"), "w") as f:
    f.write(ctsw013_content)
print("Generated CTSW013.properties")

# CTSW014 - Incoming RN relating to subject message with priority different from SS
ctsw014_content = """# Test Case: CTSW014 - Incoming RN relating to a subject message with priority different from SS
# Description: Test RN rejection when subject message priority is not SS
# Reference: EUR Doc 047 Appendix A, Page 30, Section 4.4.7.1, 4.4.7.2, 4.4.7.3, 4.4.1.2
# Test Criteria: Successful if IUT rejects RN when priority indicator != SS
# Verification: Check logging and reporting to Control Position

# ============================================================================
# RETURN RECEIPTS (RN) WITH DIFFERENT SUBJECT MESSAGE PRIORITIES
# Originator is AMHS user, addressing AMQP consumer
# ============================================================================

# Subcase 1: RN with subject message priority SS - Should be transferred (not rejected)
subcase.1.id=CTSW014.1
subcase.1.name=RN With Subject Priority SS
subcase.1.description=First RN with subject message related to IPM with ATS-message-priority SS
subcase.1.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject-ipm-priority=SS
subcase.1.amhs.message-type=RN
# Expected: RN transferred to ITCU, not rejected, logged and reported to Control Position
subcase.1.amhs.expected-rejection=false
subcase.1.amhs.notify-control-position=true

# Subcase 2: RN with subject message priority DD - Should be rejected
subcase.2.id=CTSW014.2
subcase.2.name=RN With Subject Priority DD
subcase.2.description=Second RN with subject message related to IPM with priority indicator DD
subcase.2.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.2.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.2.amhs.subject-ipm-priority=DD
subcase.2.amhs.message-type=RN
# Expected: Error logged and reported to Control Position, RN stored for appropriate action
subcase.2.amhs.expected-rejection=true
subcase.2.amhs.notify-control-position=true
"""

with open(os.path.join(TESTCASES_DIR, "CTSW014.properties"), "w") as f:
    f.write(ctsw014_content)
print("Generated CTSW014.properties")

# CTSW015 - Incoming RN without related subject message
ctsw015_content = """# Test Case: CTSW015 - Incoming RN without related subject message
# Description: Test RN rejection when subject message did not pass Gateway before
# Reference: EUR Doc 047 Appendix A, Page 31, Section 4.4.7.1
# Test Criteria: Successful if IUT rejects RN with fictitious subject IPM
# Verification: Check NDR contains unable-to-transfer, invalid-arguments

# ============================================================================
# RETURN RECEIPT (RN) WITH FICTITIOUS SUBJECT IPM
# Originator is AMHS user, addressing AMQP consumer
# ============================================================================

subcase.1.id=CTSW015.1
subcase.1.name=RN Without Related Subject Message
subcase.1.description=RN with fictitious subject IPM that did not pass the Gateway before
subcase.1.amhs.originator=/CN=AMHS_USER_1/OU=TEST/O=GATEWAY/PRMD=AMHS/ADMD=ICAO/C=EU/
subcase.1.amhs.recipient=/CN=AMQP_CONSUMER_1/OU=TEST/O=GATEWAY/PRMD=SWIM/ADMD=ICAO/C=EU/
subcase.1.amhs.subject-ipm-id=FICTITIOUS-IPM-ID-NOT-FOUND
subcase.1.amhs.message-type=RN
# Expected: Error logged and reported to Control Position, RN stored
# Expected: NDR generated with misrouted RN information
subcase.1.amhs.expected-ndr=true
subcase.1.amhs.ndr-reason=unable-to-transfer
subcase.1.amhs.ndr-diagnostic=invalid-arguments
subcase.1.amhs.ndr-supplementary=unable to notify RN to SWIM due to misrouted RN
subcase.1.amhs.notify-control-position=true
"""

with open(os.path.join(TESTCASES_DIR, "CTSW015.properties"), "w") as f:
    f.write(ctsw015_content)
print("Generated CTSW015.properties")

print("\\nGenerated CTSW009-CTSW015. Continuing with remaining test cases...")
