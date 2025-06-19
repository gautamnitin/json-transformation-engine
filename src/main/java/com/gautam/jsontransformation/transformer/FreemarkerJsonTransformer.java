package com.gautam.jsontransformation.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerJsonTransformer {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerJsonTransformer.class);

    private final Configuration freemarkerConfig;
    private final ObjectMapper objectMapper;
    private boolean devMode;
    // Plugin for pre/post processing
    private TransformerPlugin plugin;
    // Schema validators for input and output (can be null if not used)
    private final JsonSchemaValidator inputSchemaValidator;
    private final JsonSchemaValidator outputSchemaValidator;

    /**
     * @param templateDirPath         Directory where Freemarker templates reside.
     * @param devMode                 If true, templates will reload dynamically.
     * @param plugin                  Plugin for pre/post processing (can be null).
     * @param inputSchemaValidator    Validator for input JSON (can be null).
     * @param outputSchemaValidator   Validator for output JSON (can be null).
     * @throws IOException if template directory is invalid.
     */
    public FreemarkerJsonTransformer(
            String templateDirPath,
            boolean devMode,
            TransformerPlugin plugin,
            JsonSchemaValidator inputSchemaValidator,
            JsonSchemaValidator outputSchemaValidator) throws IOException {
        this.devMode = devMode;
        this.plugin = plugin;
        this.inputSchemaValidator = inputSchemaValidator;
        this.outputSchemaValidator = outputSchemaValidator;

        // Set up Freemarker configuration with caching and dynamic reloading based on devMode
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfig.setDirectoryForTemplateLoading(new File(templateDirPath));
        freemarkerConfig.setDefaultEncoding("UTF-8");
        if (devMode) {
            freemarkerConfig.setTemplateUpdateDelayMilliseconds(0);
        } else {
            freemarkerConfig.setTemplateUpdateDelayMilliseconds(60000); // reload every 60 seconds in production
        }
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);
        freemarkerConfig.setWrapUncheckedExceptions(true);

        objectMapper = new ObjectMapper();
    }

    /**
     * Transforms input JSON using the specified Freemarker template.
     *
     * @param templateFileName name of the template file (e.g., "output_template.ftl")
     * @param inputJson        The input JSON string.
     * @param extraParams      Extra parameters to inject into the data model.
     * @return Transformed JSON as a string.
     * @throws IOException       if reading input or template fails.
     * @throws TemplateException if template processing fails.
     */
    public String transform(String templateFileName, String inputJson, Map<String, Object> extraParams)
            throws IOException, TemplateException {
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

        // Load and process the template.
        Template template = freemarkerConfig.getTemplate(templateFileName);
        StringWriter writer = new StringWriter();
        try {
            template.process(dataModel, writer);
        } catch (TemplateException e) {
            logger.error("Error during template processing.", e);
            throw e;
        }
        String outputJson = writer.toString();

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
