package com.example.BookService;

import com.example.BookService.utill.PdfUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PdfUtilTest2 {
    @Test
    public void testGeneratePdfWithMemoryMeasurement1() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(1000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement5() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(2000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement2() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(3000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement4() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(4000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement3() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(5000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement6() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(6000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement7() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(7000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement8() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(8000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement9() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(9000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }
    @Test
    public void testGeneratePdfWithMemoryMeasurement10() {
        String randomHtmlContent = PdfUtil.generateRandomHtmlContent2(10000);

        String pdfBase64 = PdfUtil.generatePdfWithMemoryMeasurement(randomHtmlContent);

        assertNotNull(pdfBase64, "PDF generation failed!");
    }

}
