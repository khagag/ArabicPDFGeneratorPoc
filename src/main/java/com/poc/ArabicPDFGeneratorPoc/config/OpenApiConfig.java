package com.poc.ArabicPDFGeneratorPoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("FOP PDF Service")
                .version("1.0.0")
                .description("Generates PDFs from JSON using Apache FOP. "
                        + "The `template` field selects the XSLT stylesheet; "
                        + "set `language` to `AR` for right-to-left Arabic output."));
    }
}
