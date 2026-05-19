#!/usr/bin/env python
"""
Extract detailed CTSW001-CTSW020 specifications from PDF content
"""

import re

with open('pdf_content.txt', 'r', encoding='utf-8', errors='ignore') as f:
    lines = f.readlines()

# Find line numbers for each CTSW case
case_lines = {}
for i, line in enumerate(lines):
    for case_num in range(1, 21):
        case_id = f'CTSW{case_num:03d}'
        if case_id in line and 'CONVERT' in line.upper() or 'GENERATE' in line.upper() or 'REJECT' in line.upper() or 'DISTRIBUTE' in line.upper() or 'PROBE' in line.upper() or 'INCOMING' in line.upper() or 'NOTIFY' in line.upper():
            if case_id not in case_lines:
                case_lines[case_id] = i

# Extract detailed sections
summaries = {}
for case_num in range(1, 21):
    case_id = f'CTSW{case_num:03d}'
    if case_id in case_lines:
        start_idx = case_lines[case_id]
        # Find end (next CTSW or CTSW1xx)
        end_idx = len(lines)
        for j in range(start_idx + 1, min(start_idx + 100, len(lines))):
            if 'CTSW' in lines[j] and lines[j].strip().startswith('CTSW'):
                next_case = lines[j].strip()[:8]
                if next_case != case_id:
                    end_idx = j
                    break
        
        # Extract lines
        section = ''.join(lines[start_idx:end_idx])
        summaries[case_id] = section.strip()

# Save summary to file
with open('test_cases_summary.txt', 'w', encoding='utf-8') as f:
    f.write("=" * 100 + "\n")
    f.write("ICAO EUR DOC 047 APPENDIX A - AMHS TEST CASES (CTSW001-CTSW020)\n")
    f.write("=" * 100 + "\n\n")
    
    for case_num in range(1, 21):
        case_id = f'CTSW{case_num:03d}'
        f.write(f"\n{'='*100}\n")
        f.write(f"{case_id}\n")
        f.write(f"{'='*100}\n")
        
        if case_id in summaries:
            f.write(summaries[case_id])
        else:
            f.write(f"Specification not found in PDF\n")
        
        f.write(f"\n\n")

print("Summary extracted to test_cases_summary.txt")
print(f"Found {len(summaries)} test cases")
