package com.gautam.jsontransformation.transformer;

public class DummyJsonSchemaValidator implements JsonSchemaValidator {
    @Override
    public void validate(String jsonString) throws Exception {
        // Replace with a real JSON schema check (e.g., using everit-org/json-schema).
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new Exception("Invalid JSON: empty content");
        }
    }
}
