package com.poc.ArabicPDFGeneratorPoc.controller;

import com.poc.ArabicPDFGeneratorPoc.model.PdfRequest;
import com.poc.ArabicPDFGeneratorPoc.model.TemplateType;
import com.poc.ArabicPDFGeneratorPoc.service.PdfGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pdf")
@Tag(name = "PDF", description = "Generate PDF documents from JSON")
public class PdfController {

    private final PdfGeneratorService pdfService;

    public PdfController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
    }

    @Operation(
            summary = "Generate a PDF",
            description = "Merges the JSON `data` into the XSLT template chosen by `template` "
                    + "and renders it with Apache FOP. Use `language: AR` for right-to-left Arabic.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PdfRequest.class),
                            examples = {
                                    @ExampleObject(name = "Invoice (Arabic)", value = Examples.INVOICE_AR),
                                    @ExampleObject(name = "Invoice (English)", value = Examples.INVOICE_EN),
                                    @ExampleObject(name = "Letter (Arabic)", value = Examples.LETTER_AR)
                            })))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The generated PDF",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Invalid request (unknown template, missing field..)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Template/FOP processing failed",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generate(@Valid @RequestBody PdfRequest request) {
        byte[] pdf = pdfService.generate(request.template(), request.language(), request.data());

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(request.template().getFileBaseName() + ".pdf", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @Operation(summary = "List available templates")
    @GetMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TemplateType> templates() {
        return Arrays.asList(TemplateType.values());
    }
}
