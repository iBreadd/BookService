package com.example.BookService;

import com.example.BookService.utill.PdfUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PdfSymbolTest {
    @Test
    public void testPdfGenerationWithSingleSymbol() {
        String singleSymbolHtmlContent = "<html><body><p>☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️☀️</p></body></html>";

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(singleSymbolHtmlContent);

        assertNotNull(pdfBase64, "PDF generation with a single symbol failed!");
    }
    @Test
    public void testPdfGenerationWithSymbols() {
        String symbolHtmlContent = PdfUtil.generateRandomHtmlContent(1000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(symbolHtmlContent);

        assertNotNull(pdfBase64, "PDF generation with symbols failed!");
    }
}
