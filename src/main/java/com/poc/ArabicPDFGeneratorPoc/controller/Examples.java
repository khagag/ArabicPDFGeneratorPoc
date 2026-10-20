package com.poc.ArabicPDFGeneratorPoc.controller;

/** Request bodies shown in Swagger UI's example dropdown. */
final class Examples {

    private Examples() {
    }

    static final String INVOICE_AR = """
            {
              "template": "INVOICE",
              "language": "AR",
              "data": {
                "invoiceNumber": "INV-2026-0042",
                "date": "2026-10-20",
                "dueDate": "2026-11-20",
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
                  { "description": "خدمات استشارية", "quantity": 2, "unitPrice": 1500 },
                  { "description": "ترخيص برنامج (سنوي)", "quantity": 1, "unitPrice": 4800.5 },
                  { "description": "Technical support - دعم فني", "quantity": 3, "unitPrice": 250 }
                ],
                "notes": "شكراً لتعاملكم معنا. يرجى السداد خلال 30 يوماً."
              }
            }
            """;

    static final String INVOICE_EN = """
            {
              "template": "INVOICE",
              "language": "EN",
              "data": {
                "invoiceNumber": "INV-2026-0043",
                "date": "2026-10-20",
                "dueDate": "2026-11-20",
                "currency": "USD",
                "taxRate": 10,
                "company": { "name": "Nile Trading Co.", "address": "12 Tahrir St, Cairo", "taxId": "123-456-789" },
                "customer": { "name": "John Smith", "address": "1 Main St, Springfield", "phone": "+1 555 0100" },
                "items": [
                  { "description": "Consulting services", "quantity": 2, "unitPrice": 150 },
                  { "description": "Software license (annual)", "quantity": 1, "unitPrice": 480.5 }
                ],
                "notes": "Thank you for your business."
              }
            }
            """;

    static final String LETTER_AR = """
            {
              "template": "LETTER",
              "language": "AR",
              "data": {
                "date": "2026-10-20",
                "sender": { "name": "سارة إبراهيم", "title": "مدير الموارد البشرية", "company": "شركة النور للتجارة" },
                "recipient": { "name": "محمود حسن", "address": "الإسكندرية، مصر" },
                "subject": "خطاب تعيين",
                "paragraphs": [
                  "يسعدنا أن نخبركم بقبولكم في وظيفة مهندس برمجيات لدى شركتنا اعتباراً من الأول من نوفمبر 2026.",
                  "يرجى الحضور إلى مقر الشركة في الموعد المحدد ومعكم أصول المستندات المطلوبة، وسيقوم فريقنا بمساعدتكم في إجراءات الاستلام.",
                  "نتطلع إلى انضمامكم لفريقنا ونتمنى لكم التوفيق."
                ]
              }
            }
            """;
}
