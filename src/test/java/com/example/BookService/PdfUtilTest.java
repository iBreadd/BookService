package com.example.BookService;

import com.example.BookService.utill.PdfUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PdfUtilTest {
    @Test
    public void testGeneratePdfWithMemoryMeasurement() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent(1000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
}
