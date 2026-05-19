#!/usr/bin/env python
import pdfplumber
import sys

try:
    pdf_path = 'Appendix-A-AMHS_SWIM-Gateway-Testing-Plan-v3.0.pdf'
    with pdfplumber.open(pdf_path) as pdf:
        full_text = ""
        for i, page in enumerate(pdf.pages):
            text = page.extract_text()
            full_text += f"\n=== PAGE {i+1} ===\n"
            full_text += text + "\n"
        print(full_text)
except Exception as e:
    print(f'Error: {e}', file=sys.stderr)
    sys.exit(1)
