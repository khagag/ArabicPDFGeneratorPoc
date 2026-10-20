package com.poc.ArabicPDFGeneratorPoc.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Errors are always returned as application/problem+json, even when the client
 * only accepts application/pdf (which is what Swagger UI sends).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException ife
                && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String field = ife.getPath().isEmpty() ? "value" : ife.getPath().get(ife.getPath().size() - 1).getFieldName();
            String allowed = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString).collect(Collectors.joining(", "));
            return problem(HttpStatus.BAD_REQUEST, "Invalid value",
                    "'" + ife.getValue() + "' is not valid for '" + field + "'. Allowed values: " + allowed);
        }
        return problem(HttpStatus.BAD_REQUEST, "Malformed JSON request", "The request body could not be parsed");
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ProblemDetail> handleGeneration(PdfGenerationException ex) {
        log.error("PDF generation failed", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "PDF generation failed", ex.getMessage());
    }

    private static ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }
}
