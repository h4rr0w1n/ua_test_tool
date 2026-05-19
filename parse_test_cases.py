#!/usr/bin/env python
# Parse PDF content and extract CTSW test case specifications

import re

with open('pdf_content.txt', 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

# Find all test case sections for CTSW001-CTSW020
test_cases = {}

for case_num in range(1, 21):
    case_id = f'CTSW{case_num:03d}'
    
    # Try to find the detailed section for this case
    # Look for pattern: "CTSW001 û CONVERT AN INCOMING IPM TO AMQP FORMAT"
    pattern = f'{case_id}.*?(?=CTSW\\d{{3}}|CTSW\\d{{4}}|$)'
    match = re.search(pattern, content, re.DOTALL | re.IGNORECASE)
    
    if match:
        section = match.group(0)
        # Clean up the section
        lines = section.split('\n')
        # Take first 50 lines or until we hit another major section
        test_cases[case_id] = '\n'.join(lines[:50])

# Print summary
print("EXTRACTED TEST CASES FROM ICAO EUR DOC 047 APPENDIX A")
print("=" * 80)

for case_num in range(1, 21):
    case_id = f'CTSW{case_num:03d}'
    if case_id in test_cases:
        print(f"\n{case_id}:")
        # Get first meaningful line
        lines = [l.strip() for l in test_cases[case_id].split('\n') if l.strip() and 'PAGE' not in l]
        if lines:
            # Skip header if present
            for line in lines[:10]:
                if case_id in line and len(line) > 20:
                    print(f"  {line}")
                    break
    else:
        print(f"\n{case_id}: Not found")

print("\n" + "=" * 80)
print("Detailed specifications available in test_cases_summary.txt")
