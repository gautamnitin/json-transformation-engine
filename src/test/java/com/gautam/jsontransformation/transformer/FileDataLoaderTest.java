package com.gautam.jsontransformation.transformer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDataLoaderTest {

    @Test
    public void testLoadData(@TempDir Path tempDir) throws Exception {
        // Create a temporary file with known content.
        Path tempFile = tempDir.resolve("testData.json");
        String expectedContent = "{\"key\": \"value\"}";
        Files.write(tempFile, expectedContent.getBytes(StandardCharsets.UTF_8));

        // Instantiate FileDataLoader with the temporary file path.
        FileDataLoader loader = new FileDataLoader(tempFile.toString());
        String loadedData = loader.loadData();

        // Verify that the loaded content matches the expected content.
        assertEquals(expectedContent, loadedData);
    }
}
