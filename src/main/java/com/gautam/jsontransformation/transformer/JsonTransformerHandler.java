package com.gautam.jsontransformation.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gautam.jsontransformation.transformer.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonTransformerHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonTransformerHandler.class);

    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    private final TransformerPlugin plugin;
    private final JsonSchemaValidator inputSchemaValidator;
    private final JsonSchemaValidator outputSchemaValidator;

    /**
     * Constructs the transformer using a generic template engine.
     *
     * @param templateEngine         the template engine (e.g., FreemarkerTemplateEngine).
     * @param plugin                 plugin for pre- and post-processing (can be null).
     * @param inputSchemaValidator   validator for input JSON (can be null).
     * @param outputSchemaValidator  validator for output JSON (can be null).
     */
    public JsonTransformerHandler(TemplateEngine templateEngine,
                                  TransformerPlugin plugin,
                                  JsonSchemaValidator inputSchemaValidator,
                                  JsonSchemaValidator outputSchemaValidator) {
        this.templateEngine = templateEngine;
        this.plugin = plugin;
        this.inputSchemaValidator = inputSchemaValidator;
        this.outputSchemaValidator = outputSchemaValidator;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Transforms input JSON based on the specified template.
     *
     * @param templateName the template file to use.
     * @param inputJson    the input JSON string.
     * @param extraParams  additional parameters to inject into the data model.
     * @return the transformed JSON as a string.
     * @throws Exception if transformation fails.
     */
    public String transform(String templateName, String inputJson, Map<String, Object> extraParams)
            throws Exception {
        long startTime = System.nanoTime();
        logger.info("Starting JSON transformation.");

        // Parse the input JSON into a data model (using a simple Map)
        Map<String, Object> dataModel = objectMapper.readValue(inputJson, Map.class);

        // Validate input JSON, if a validator is provided.
        if (inputSchemaValidator != null) {
            try {
                inputSchemaValidator.validate(inputJson);
                logger.info("Input JSON schema validation passed.");
            } catch (Exception e) {
                logger.error("Input JSON schema validation failed.", e);
                throw new RuntimeException(e);
            }
        }

        // Inject any extra parameters into the data model.
        if (extraParams != null) {
            dataModel.putAll(extraParams);
        }

        // Pre-processing hook: allow plugins to modify the data model.
        if (plugin != null) {
            dataModel = plugin.beforeProcessing(dataModel);
        }

        // Process using the generic template engine.
        String outputJson = templateEngine.process(templateName, dataModel);

        // Post-processing hook: allow additional modifications.
        if (plugin != null) {
            outputJson = plugin.afterProcessing(outputJson);
        }

        // Validate the output JSON, if a validator is provided.
        if (outputSchemaValidator != null) {
            try {
                outputSchemaValidator.validate(outputJson);
                logger.info("Output JSON schema validation passed.");
            } catch (Exception e) {
                logger.error("Output JSON schema validation failed.", e);
                throw new RuntimeException(e);
            }
        }

        long duration = System.nanoTime() - startTime;
        logger.info("Transformation completed in {} ms", duration / 1_000_000);
        // In production, metric data can be sent to monitoring systems like Micrometer.

        return outputJson;
    }
}
