package com.poc.ArabicPDFGeneratorPoc.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Document language. Passed to the XSLT as the {@code lang} parameter;
 * the templates switch labels and writing direction (RTL for Arabic) on it.
 */
@Schema(description = "Document language. AR renders right-to-left with Arabic labels")
public enum Language {

    EN("en"),
    AR("ar");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
