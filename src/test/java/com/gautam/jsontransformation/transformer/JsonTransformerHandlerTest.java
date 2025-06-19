package com.gautam.jsontransformation.transformer;

import com.gautam.jsontransformation.transformer.template.TemplateEngine;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonTransformerHandlerTest {

    /**
     * Test case for a successful transformation where all the dependencies are provided.
     */
    @Test
    public void testTransformSuccess() throws Exception {
        // Arrange
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        TransformerPlugin plugin = mock(TransformerPlugin.class);
        JsonSchemaValidator inputValidator = mock(JsonSchemaValidator.class);
        JsonSchemaValidator outputValidator = mock(JsonSchemaValidator.class);

        String templateName = "template.ftl";
        String inputJson = "{\"name\":\"Alice\"}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("age", 30);

        // After object mapping, we expect the base data model to be {"name":"Alice", "age":30}.
        // The plugin's beforeProcessing hook might modify this data model.
        Map<String, Object> modifiedDataModel = new HashMap<>();
        modifiedDataModel.put("name", "Alice");
        modifiedDataModel.put("age", 30);
        modifiedDataModel.put("greeting", "Hello");

        // Configure the plugin and template engine mocks:
        when(plugin.beforeProcessing(anyMap())).thenReturn(modifiedDataModel);
        String processedOutput = "{\"greeting\":\"Hello\", \"name\":\"Alice\", \"age\":30}";
        when(templateEngine.process(eq(templateName), eq(modifiedDataModel))).thenReturn(processedOutput);
        when(plugin.afterProcessing(processedOutput)).thenReturn(processedOutput);

        JsonTransformerHandler handler = new JsonTransformerHandler(
                templateEngine,
                plugin,
                inputValidator,
                outputValidator);

        // Act
        String result = handler.transform(templateName, inputJson, extraParams);

        // Assert
        assertEquals(processedOutput, result);
        verify(inputValidator, times(1)).validate(inputJson);
        verify(outputValidator, times(1)).validate(processedOutput);
        verify(plugin, times(1)).beforeProcessing(anyMap());
        verify(plugin, times(1)).afterProcessing(processedOutput);
    }

    /**
     * Test case where the input JSON fails schema validation.
     */
    @Test
    public void testInputSchemaValidationFailure() throws Exception {
        // Arrange
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        // We don't need a plugin or output validator in this scenario.
        TransformerPlugin plugin = null;
        JsonSchemaValidator inputValidator = mock(JsonSchemaValidator.class);
        JsonSchemaValidator outputValidator = null;

        String templateName = "template.ftl";
        String invalidInputJson = "{\"invalid\":true}";
        Map<String, Object> extraParams = Collections.emptyMap();

        // Force the input validator to throw an exception.
        doThrow(new Exception("Input JSON schema error")).when(inputValidator).validate(invalidInputJson);

        JsonTransformerHandler handler = new JsonTransformerHandler(
                templateEngine,
                plugin,
                inputValidator,
                outputValidator);

        // Act & Assert: We expect a RuntimeException wrapping the validation error.
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                handler.transform(templateName, invalidInputJson, extraParams)
        );
        assertTrue(thrown.getMessage().contains("Input JSON schema error"));
    }

    /**
     * Test case where the output JSON fails schema validation.
     */
    @Test
    public void testOutputSchemaValidationFailure() throws Exception {
        // Arrange
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        // We do not use a plugin or input validator in this test.
        TransformerPlugin plugin = null;
        JsonSchemaValidator inputValidator = null;
        JsonSchemaValidator outputValidator = mock(JsonSchemaValidator.class);

        String templateName = "template.ftl";
        String inputJson = "{\"key\":\"value\"}";
        Map<String, Object> extraParams = null;

        // The template engine returns output that fails validation.
        String outputJson = "{\"result\":\"invalid\"}";
        when(templateEngine.process(eq(templateName), anyMap())).thenReturn(outputJson);
        doThrow(new Exception("Output JSON schema error")).when(outputValidator).validate(outputJson);

        JsonTransformerHandler handler = new JsonTransformerHandler(
                templateEngine,
                plugin,
                inputValidator,
                outputValidator);

        // Act & Assert: Expect a RuntimeException because of the output validation failure.
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                handler.transform(templateName, inputJson, extraParams)
        );
        assertTrue(thrown.getMessage().contains("Output JSON schema error"));
    }

    /**
     * Test case for transformation without providing validators or a plugin.
     */
    @Test
    public void testTransformWithoutValidatorsAndPlugin() throws Exception {
        // Arrange
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        TransformerPlugin plugin = null;
        JsonSchemaValidator inputValidator = null;
        JsonSchemaValidator outputValidator = null;

        String templateName = "template.ftl";
        String inputJson = "{\"foo\":\"bar\"}";
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("additional", "param");

        String expectedOutput = "{\"combined\":\"output\"}";
        when(templateEngine.process(eq(templateName), anyMap())).thenReturn(expectedOutput);

        JsonTransformerHandler handler = new JsonTransformerHandler(
                templateEngine,
                plugin,
                inputValidator,
                outputValidator);

        // Act
        String result = handler.transform(templateName, inputJson, extraParams);

        // Assert
        assertEquals(expectedOutput, result);
        verify(templateEngine, times(1)).process(eq(templateName), anyMap());
    }
}
