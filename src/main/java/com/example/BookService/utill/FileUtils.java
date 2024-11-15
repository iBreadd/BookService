package com.example.BookService.utill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static void saveHtmlToFile(String htmlContent, String fileName) {
        Path path = Paths.get("output", fileName);
        try {
            Files.createDirectories(path.getParent());

            Files.write(path, htmlContent.getBytes());
            System.out.println("Saved HTML content to file: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save HTML content: " + e.getMessage());
        }
    }
}
