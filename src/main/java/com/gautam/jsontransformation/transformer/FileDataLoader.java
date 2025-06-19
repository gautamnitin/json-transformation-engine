package com.gautam.jsontransformation.transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDataLoader implements DataLoader {
    private final String filePath;

    public FileDataLoader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String loadData() throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
    }
}
