package com.poc.ArabicPDFGeneratorPoc.service;

import com.poc.ArabicPDFGeneratorPoc.exception.PdfGenerationException;
import com.poc.ArabicPDFGeneratorPoc.model.Language;
import com.poc.ArabicPDFGeneratorPoc.model.TemplateType;
import com.poc.ArabicPDFGeneratorPoc.util.JsonToXmlConverter;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

@Service
public class PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorService.class);

    /** The JDK's built-in XSLT 1.0 engine - avoids surprises if another engine is on the classpath. */
    private static final String JDK_XSLT_FACTORY =
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";

    private final FopFactory fopFactory;
    /** Compiled stylesheets. {@link Templates} is thread-safe; Transformers are created per request. */
    private final Map<TemplateType, Templates> compiled = new EnumMap<>(TemplateType.class);

    public PdfGeneratorService(FopFactory fopFactory) throws Exception {
        this.fopFactory = fopFactory;

        TransformerFactory factory = TransformerFactory.newInstance(JDK_XSLT_FACTORY, null);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // Compile everything at startup so a broken template fails fast.
        for (TemplateType type : TemplateType.values()) {
            ClassPathResource resource = new ClassPathResource(type.getResourcePath());
            try (InputStream in = resource.getInputStream()) {
                compiled.put(type, factory.newTemplates(
                        new StreamSource(in, resource.getURL().toExternalForm())));
            }
            log.info("Compiled XSLT template {} ({})", type, type.getResourcePath());
        }
    }

    public byte[] generate(TemplateType template, Language language, Map<String, Object> data) {
        Document xml = JsonToXmlConverter.convert(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream(64 * 1024);
        try {
            FOUserAgent userAgent = fopFactory.newFOUserAgent();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

            Transformer transformer = compiled.get(template).newTransformer();
            transformer.setParameter("lang", language.code());

            // XML + XSLT -> XSL-FO events streamed straight into FOP -> PDF bytes
            transformer.transform(new DOMSource(xml), new SAXResult(fop.getDefaultHandler()));
        } catch (Exception e) {
            throw new PdfGenerationException(
                    "Failed to generate PDF from template " + template + ": " + e.getMessage(), e);
        }
        return out.toByteArray();
    }
}
