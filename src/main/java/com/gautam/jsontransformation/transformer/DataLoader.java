package com.gautam.jsontransformation.transformer;

import java.io.IOException;

public interface DataLoader {
    /**
     * Loads JSON data from a source.
     *
     * @return The JSON string.
     * @throws IOException if data retrieval fails.
     */
    String loadData() throws IOException;
}
