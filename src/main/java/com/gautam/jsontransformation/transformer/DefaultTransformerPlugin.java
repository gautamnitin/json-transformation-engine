package com.gautam.jsontransformation.transformer;

import java.util.Map;

public class DefaultTransformerPlugin implements TransformerPlugin {

    @Override
    public Map<String, Object> beforeProcessing(Map<String, Object> dataModel) {
        // Example: Enrich the data model by adding an extra field.
        dataModel.put("pluginAdded", "This field was added in beforeProcessing");
        return dataModel;
    }

    @Override
    public String afterProcessing(String outputJson) {
        // Example: Further modify output JSON if needed.
        return outputJson;
    }
}
