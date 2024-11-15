package com.example.BookService;

import com.example.BookService.utill.PdfUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfUtilTest3 {
    private static final String OUTPUT_DIR = "F:\\LudogorieSoft - стаж\\Project - Valyo\\HtmlFileToPDF";

    @Test
    public void testSavePdfToFile() {
        // Създаване на директория, ако не съществува
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(1000);
        String filePath = OUTPUT_DIR + "test_pdf_1000.pdf";

        PdfUtil.savePdfToFile(randomHtmlContent, filePath);

        // Проверка дали файлът е създаден
        File file = new File(filePath);
        assertTrue(file.exists(), "PDF файлът не е създаден!");
    }

    @Test
    public void testSaveMultiplePdfs() {
        for (int i = 1; i <= 5; i++) {
            String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(i * 2000);
            String filePath = OUTPUT_DIR + "test_pdf_" + (i * 2000) + ".pdf";
            PdfUtil.savePdfToFile(randomHtmlContent, filePath);

            File file = new File(filePath);
            assertTrue(file.exists(), "PDF файлът " + filePath + " не е създаден!");
        }
    }
}
