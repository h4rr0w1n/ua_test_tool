#!/usr/bin/env python3
"""
Generate recipient address files for test cases.
Creates files with 512 and 513 recipient addresses in X.400 format.
"""

def generate_recipients(count, filename):
    """Generate a file with the specified number of recipients."""
    base_format = "/CN=RECIPIENT{:05d}/OU=TEST/O=TEST/PRMD=TEST/ADMD=ICAO/C=XX/"
    
    recipients = []
    for i in range(1, count + 1):
        recipients.append(base_format.format(i))
    
    with open(filename, 'w') as f:
        f.write(',\n'.join(recipients))
    
    print(f"Generated {filename} with {count} recipients")

# Generate recipient files
generate_recipients(512, 'src/main/resources/testcases/data/recipients-512.txt')
generate_recipients(513, 'src/main/resources/testcases/data/recipients-513.txt')

print("Recipient files generated successfully!")
