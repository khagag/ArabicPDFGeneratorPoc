package com.poc.ArabicPDFGeneratorPoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(name = "PdfRequest", description = "JSON payload that is merged into the selected XSLT template")
public record PdfRequest(

        @NotNull(message = "template is required")
        @Schema(description = "Template (XSLT) used to generate the PDF", example = "INVOICE",
                requiredMode = Schema.RequiredMode.REQUIRED)
        TemplateType template,

        @Schema(description = "Document language / direction. Defaults to EN", example = "AR",
                defaultValue = "EN")
        Language language,

        @NotNull(message = "data is required")
        @Schema(description = "Free-form JSON object. It is converted to XML under a <data> root "
                + "element; arrays become repeated <item> elements.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, Object> data) {

    public PdfRequest {
        if (language == null) {
            language = Language.EN;
        }
    }
}
