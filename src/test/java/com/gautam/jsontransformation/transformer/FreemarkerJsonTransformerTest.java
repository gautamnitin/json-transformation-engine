package com.gautam.jsontransformation.transformer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class FreemarkerJsonTransformerTest {

    // Temporary directory provided by JUnit to hold our test template.
    @TempDir
    Path tempDir;

    // Mocks for plugin and schema validators.
    @Mock
    private TransformerPlugin plugin;

    @Mock
    private JsonSchemaValidator inputSchemaValidator;

    @Mock
    private JsonSchemaValidator outputSchemaValidator;

    private FreemarkerJsonTransformer transformer;
    private String templateFileName = "output_template.ftl";

    @BeforeEach
    public void setUp() throws Exception {
        // Create a simple Freemarker template file in the temporary directory.
        // In this example the template uses two fields: order.orderNumber and extraInfo.
        File templateFile = tempDir.resolve(templateFileName).toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(templateFile))) {
            writer.write("{\"result\": \"${order.orderNumber}\", \"extra\": \"${extraInfo}\", \"pluginFlag\": \"${pluginAdded?if_exists}\"}");
        }

        // Instantiate the transformer with devMode on (for testing) and our mocks.
        transformer = new FreemarkerJsonTransformer(
                tempDir.toFile().getAbsolutePath(),
                true,
                plugin,
                inputSchemaValidator,
                outputSchemaValidator
        );
    }

    @Test
    public void testSuccessfulTransformation() throws Exception {
        // Prepare a sample input JSON.
        String inputJson = "{\"order\": {\"orderNumber\": \"12345\"}}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "testExtra");

        // Configure mocks: simply pass the data through.
        when(plugin.beforeProcessing(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(plugin.afterProcessing(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(inputSchemaValidator).validate(any());
        doNothing().when(outputSchemaValidator).validate(any());

        // Execute transformation.
        String outputJson = transformer.transform(templateFileName, inputJson, extraParams);

        // Validate that output JSON contains the data from the input and extra params.
        assertTrue(outputJson.contains("12345"), "Output should include order number.");
        assertTrue(outputJson.contains("testExtra"), "Output should include extra parameters.");
    }

    @Test
    public void testPluginInvocation() throws Exception {
        String inputJson = "{\"order\": {\"orderNumber\": \"12345\"}}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "testExtra");

        // Simulate plugin modifying the data model before processing.
        when(plugin.beforeProcessing(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataModel = (Map<String, Object>) invocation.getArgument(0);
            dataModel.put("pluginAdded", "pluginInvoked");
            return dataModel;
        });
        // Simulate simple afterProcessing that returns the string unmodified.
        when(plugin.afterProcessing(any())).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(inputSchemaValidator).validate(any());
        doNothing().when(outputSchemaValidator).validate(any());

        String outputJson = transformer.transform(templateFileName, inputJson, extraParams);

        // Check that the plugin hook values made it into the resulting JSON.
        assertTrue(outputJson.contains("pluginInvoked"), "Plugin value should be present in the output.");
    }

    @Test
    public void testInputSchemaValidationFailure() throws Exception {
        String inputJson = "{\"order\": {\"orderNumber\": \"12345\"}}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "testExtra");

        // Simulate input validator throwing an exception.
        doThrow(new Exception("Input schema validation failed")).when(inputSchemaValidator).validate(any());

        Exception exception = assertThrows(Exception.class, () -> {
            transformer.transform(templateFileName, inputJson, extraParams);
        });
        assertEquals("java.lang.Exception: Input schema validation failed", exception.getMessage());
    }

    @Test
    public void testOutputSchemaValidationFailure() throws Exception {
        String inputJson = "{\"order\": {\"orderNumber\": \"12345\"}}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "testExtra");

        // Let input validation pass.
        doNothing().when(inputSchemaValidator).validate(any());
        // Configure output validator to throw an exception.
        doThrow(new Exception("Output schema validation failed")).when(outputSchemaValidator).validate(any());
        when(plugin.beforeProcessing(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(plugin.afterProcessing(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Exception exception = assertThrows(Exception.class, () -> {
            transformer.transform(templateFileName, inputJson, extraParams);
        });
        assertEquals("java.lang.Exception: Output schema validation failed", exception.getMessage());
    }

    @Test
    public void testTemplateProcessingError() throws Exception {
        // Point to a non-existent template file to simulate a processing error.
        String inputJson = "{\"order\": {\"orderNumber\": \"12345\"}}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "testExtra");

        // Even though validators and plugin are set up to pass,
        // an invalid template file name should result in an IOException.
        assertThrows(IOException.class, () -> {
            transformer.transform("nonexistent_template.ftl", inputJson, extraParams);
        });
    }
}