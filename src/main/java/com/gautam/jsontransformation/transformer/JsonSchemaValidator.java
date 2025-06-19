package com.gautam.jsontransformation.transformer;

public interface JsonSchemaValidator {
    /**
     * Validates a JSON string against a schema.
     *
     * @param jsonString The JSON to validate.
     * @throws Exception if validation fails.
     */
    void validate(String jsonString) throws Exception;
}

