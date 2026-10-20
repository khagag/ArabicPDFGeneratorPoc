package com.poc.ArabicPDFGeneratorPoc.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collection;
import java.util.Map;

public final class JsonToXmlConverter {

    public static final String ROOT = "data";
    public static final String ARRAY_ITEM = "item";

    private JsonToXmlConverter() {
    }

    public static Document convert(Map<String, Object> data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = factory.newDocumentBuilder().newDocument();
            Element root = doc.createElement(ROOT);
            doc.appendChild(root);
            append(doc, root, data);
            return doc;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot create XML document", e);
        }
    }

    private static void append(Document doc, Element parent, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Element child = doc.createElement(safeName(String.valueOf(entry.getKey())));
                parent.appendChild(child);
                append(doc, child, entry.getValue());
            }
        } else if (value instanceof Collection<?> list) {
            for (Object element : list) {
                Element child = doc.createElement(ARRAY_ITEM);
                parent.appendChild(child);
                append(doc, child, element);
            }
        } else {
            parent.setTextContent(stripInvalidXmlChars(String.valueOf(value)));
        }
    }

    static String safeName(String key) {
        if (key == null || key.isEmpty()) {
            return "_";
        }
        StringBuilder sb = new StringBuilder(key.length());
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            boolean ok = Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.';
            sb.append(ok ? c : '_');
        }
        char first = sb.charAt(0);
        if (!(Character.isLetter(first) || first == '_')) {
            sb.insert(0, '_');
        }
        return sb.toString();
    }

    /** Removes control characters that are illegal in XML 1.0. */
    private static String stripInvalidXmlChars(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        s.codePoints().forEach(cp -> {
            boolean valid = cp == 0x9 || cp == 0xA || cp == 0xD
                    || (cp >= 0x20 && cp <= 0xD7FF)
                    || (cp >= 0xE000 && cp <= 0xFFFD)
                    || (cp >= 0x10000 && cp <= 0x10FFFF);
            if (valid) {
                sb.appendCodePoint(cp);
            }
        });
        return sb.toString();
    }
}
