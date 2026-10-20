package com.poc.ArabicPDFGeneratorPoc.config;

import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Builds the single, thread-safe {@link FopFactory}.
 * <p>
 * Fonts are shipped inside the jar (src/main/resources/fonts). FOP needs real file
 * URLs for embedded fonts, so they are extracted to a temp directory at startup and
 * a FOP configuration pointing at them is generated on the fly.
 * <p>
 * Arabic notes:
 * <ul>
 *   <li>the font MUST contain Arabic glyphs and the OpenType GSUB tables
 *       (needed for letter joining) - Amiri does; {@code advanced="true"} enables them</li>
 *   <li>text direction comes from {@code writing-mode="rl-tb"} in the XSLT</li>
 * </ul>
 */
@Configuration
public class FopConfig {

    /** Add more fonts here. family = the name used in font-family inside the XSLT. */
    private record FontSpec(String file, String family, String weight, String style) {
    }

    private static final List<FontSpec> FONTS = List.of(
            new FontSpec("Amiri-Regular.ttf", "Amiri", "normal", "normal"),
            new FontSpec("Amiri-Bold.ttf", "Amiri", "bold", "normal")
    );

    @Bean
    public FopFactory fopFactory() throws Exception {
        Path fontDir = Files.createTempDirectory("fop-fonts");
        fontDir.toFile().deleteOnExit();

        StringBuilder fontXml = new StringBuilder();
        for (FontSpec font : FONTS) {
            Path target = fontDir.resolve(font.file());
            try (InputStream in = new ClassPathResource("fonts/" + font.file()).getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IllegalStateException("Missing font on classpath: fonts/" + font.file(), e);
            }
            target.toFile().deleteOnExit();

            fontXml.append("<font embed-url=\"").append(target.toUri())
                    .append("\" kerning=\"yes\" advanced=\"yes\" embedding-mode=\"subset\">")
                    .append("<font-triplet name=\"").append(font.family())
                    .append("\" style=\"").append(font.style())
                    .append("\" weight=\"").append(font.weight()).append("\"/>")
                    .append("</font>");
        }

        String config = """
                <?xml version="1.0" encoding="UTF-8"?>
                <fop version="1.0">
                  <use-cache>false</use-cache>
                  <renderers>
                    <renderer mime="application/pdf">
                      <fonts>%s</fonts>
                    </renderer>
                  </renderers>
                </fop>
                """.formatted(fontXml);

        FopConfParser parser = new FopConfParser(
                new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8)),
                fontDir.toUri());
        return parser.getFopFactoryBuilder().build();
    }
}
