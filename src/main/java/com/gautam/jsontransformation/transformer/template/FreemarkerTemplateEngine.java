package com.gautam.jsontransformation.transformer.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Component
public class FreemarkerTemplateEngine implements TemplateEngine {
    private final Configuration configuration;

    public FreemarkerTemplateEngine(Configuration configuration) {
        this.configuration = configuration;
    }


    /*public FreemarkerTemplateEngine(String templateDir, boolean devMode) throws IOException {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
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
    }*/

    @Override
    public String process(String templateName, Map<String, Object> dataModel)
            throws IOException, TemplateException {
        Template template = configuration.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}
