package com.poc.ArabicPDFGeneratorPoc.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Selects which XSLT stylesheet is used to turn the JSON data into XSL-FO.
 * <p>
 * To add a template: drop an .xsl file into src/main/resources/templates
 * and add a constant here. Swagger picks up the new value automatically.
 */
@Schema(description = "Which XSLT template to render the data with")
public enum TemplateType {

    INVOICE("templates/invoice.xsl", "invoice"),
    LETTER("templates/letter.xsl", "letter");

    private final String resourcePath;
    private final String fileBaseName;

    TemplateType(String resourcePath, String fileBaseName) {
        this.resourcePath = resourcePath;
        this.fileBaseName = fileBaseName;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getFileBaseName() {
        return fileBaseName;
    }
}
