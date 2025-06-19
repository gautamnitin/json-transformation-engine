package com.gautam.jsontransformation.transformer.template;

import java.util.Map;

public interface TemplateEngine {
    /**
     * Processes the given template with the provided data model.
     *
     * @param templateName the name of the template file.
     * @param dataModel the data model to apply.
     * @return the rendered output as a string.
     * @throws Exception if rendering fails.
     */
    String process(String templateName, Map<String, Object> dataModel) throws Exception;
}
