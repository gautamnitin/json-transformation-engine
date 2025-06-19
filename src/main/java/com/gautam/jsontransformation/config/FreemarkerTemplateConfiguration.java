package com.gautam.jsontransformation.config;

import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class FreemarkerTemplateConfiguration {

    @Value("${freemarker.template.dir.path}")
    private String templateDir;
    @Value("${template.mode.dev:false}")
    private boolean devMode;

    /**
     * Initializes a Freemarker configuration with the specified template directory and mode.
     *
     * @throws IOException if the template directory is not valid.
     */
    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() throws IOException {
        freemarker.template.Configuration configuration =
                new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        configuration.setDirectoryForTemplateLoading(new File(templateDir));
        configuration.setDefaultEncoding("UTF-8");
        if (devMode) {
            configuration.setTemplateUpdateDelayMilliseconds(0);
        } else {
            configuration.setTemplateUpdateDelayMilliseconds(60000);
        }
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }
}
