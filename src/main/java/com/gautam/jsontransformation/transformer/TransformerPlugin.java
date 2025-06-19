package com.gautam.jsontransformation.transformer;

import java.util.Map;

public interface TransformerPlugin {
    /**
     * Hook to modify the input data model before processing.
     *
     * @param dataModel The original data model.
     * @return A potentially modified data model.
     */
    Map<String, Object> beforeProcessing(Map<String, Object> dataModel);

    /**
     * Hook to modify the output JSON after transformation.
     *
     * @param outputJson The output JSON string.
     * @return A potentially modified output JSON string.
     */
    String afterProcessing(String outputJson);
}
