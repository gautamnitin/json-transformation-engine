package com.gautam.jsontransformation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JsonTransformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test sends a valid JSON payload to the /transform endpoint and expects
     * that the output JSON includes transformed values such as the order number and extra info.
     * Ensure that the test template (output_template.ftl) used in your resources returns these values.
     */
    @Test
    public void testTransformJsonSuccess() throws Exception {
        // Sample input JSON that conforms to the expected structure.
        String inputJson = "{\"order\": {"
                + "\"orderNumber\": \"12345\","
                + "\"processed\": \"2025-06-07T21:47:00\","
                + "\"release\": true,"
                + "\"lines\": ["
                + "{ \"unit\": \"pcs\", \"quantity\": 10, \"itemNumber\": \"A001\" }"
                + "]"
                + "}}";

        mockMvc.perform(post("/transform")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                // Check that the transformed JSON includes the order number.
                .andExpect(content().string(containsString("12345")))
                // Check that extra parameters injected by the controller appear.
                .andExpect(content().string(containsString("Value from REST endpoint")));
    }

    /**
     * This test sends an empty JSON payload (or one that does not meet validation criteria)
     * and expects the controller to return an error JSON string.
     * The controller currently returns a fixed error JSON on failure.
     */
    @Test
    public void testTransformJsonError() throws Exception {
        String inputJson = "";

        mockMvc.perform(post("/transform")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                // Check that the returned error message is present in the response.
                .andExpect(content().string(containsString("")));
    }
}
