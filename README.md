# ArabicPDFGeneratorPoc

This project is a proof-of-concept (POC) Spring Boot service for generating Arabic PDF documents using Apache FOP and XSL-FO templates.

## Purpose

The service demonstrates how to:
- render PDF files from XSLT/XSL-FO templates
- support Arabic text and RTL layout
- configure custom fonts for Arabic glyphs
- expose a simple REST API for PDF generation

## Notes

This is intentionally a POC and is not a production-hardened PDF platform. It is meant to validate the approach, configuration, and rendering behavior for Arabic documents.

## Run locally

```bash
./mvnw spring-boot:run
```

## Generate a Pdf
sample request:
```
curl -X 'POST'   'http://localhost:8080/api/v1/pdf'   -H 'accept: application/pdf'   -H 'Content-Type: application/json'   -d '{
  "template": "INVOICE",
  "language": "AR",
  "data": {
    "invoiceNumber": "INV-2026-0042",
    "date": "2026-12-20",
    "dueDate": "2026-11-22",
    "currency": "ج.م",
    "taxRate": 14,
    "company": {
      "name": "شركة النور للتجارة",
      "address": "12 شارع التحرير، القاهرة",
      "taxId": "123-456-789"
    },
    "customer": {
      "name": "أحمد محمد علي",
      "address": "دمنهور، البحيرة",
      "phone": "+20 100 000 0000"
    },
    "items": [
      {
        "description": "خدمات استشارية",
        "quantity": 2,
        "unitPrice": 1500
      },
      {
        "description": "ترخيص برنامج (سنوي)",
        "quantity": 1,
        "unitPrice": 4800.5
      },
      {
        "description": "Technical support - دعم فني",
        "quantity": 3,
        "unitPrice": 250
      }
    ],
    "notes": "شكراً لتعاملكم معنا. يرجى السداد خلال 30 يوماً."
  }
}' --output resulted.pdf
```

Then call the API endpoints exposed by the controller to generate PDF output.

## Main technology

- Java 21+
- Spring Boot
- Apache FOP
- XSL templates
- Arabic font support via embedded fonts
