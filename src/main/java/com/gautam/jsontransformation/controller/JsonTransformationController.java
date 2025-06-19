package com.gautam.jsontransformation.controller;

import com.gautam.jsontransformation.transformer.*;
import com.gautam.jsontransformation.transformer.template.TemplateEngine;
import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "JSON Transformation API", description = "API for transforming JSON using Freemarker templates")
public class JsonTransformationController {

    private final JsonTransformerHandler transformer;

    public JsonTransformationController(TemplateEngine engine) {
        TransformerPlugin plugin = new DefaultTransformerPlugin();
        JsonSchemaValidator inputValidator = new DummyJsonSchemaValidator();
        JsonSchemaValidator outputValidator = new DummyJsonSchemaValidator();

        transformer = new JsonTransformerHandler(engine, plugin, inputValidator, outputValidator);
    }

    @Operation(
            summary = "Transforms input JSON",
            description = "Transforms input JSON data into a new JSON format based on a Freemarker template."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transformation successful"),
            @ApiResponse(responseCode = "500", description = "Internal error during transformation")
    })
    @PostMapping("/transform")
    public String transformJson(@RequestBody String inputJson) {
        // Extra parameters can be injected (e.g., based on user session or specific API calls).
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extraInfo", "Value from REST endpoint");

        try {
            // "output_template.ftl" should exist in the configured template directory.
            return transformer.transform("output_template.ftl", inputJson, extraParams);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "{\"error\": \"Transformation failed due to an internal error.\"}";
        } catch (Exception e) {
            return "{\"error\": \"Transformation failed due to an internal error.\"}";
        }
    }
}
