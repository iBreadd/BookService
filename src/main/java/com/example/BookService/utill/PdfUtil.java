package com.example.BookService.utill;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@Slf4j
public final class PdfUtil {
    private static final String TEMPLATE_PATH_CUSTOMER_INFORMATION = "templates/GDPR";
    private static final String TEMPLATE_PATH_DATA_TAKEOUT = "templates/GDPR_takeout";
    private static final int MAX_RETRIES = 3;
    private static final Random RANDOM = new Random();
    private static final String[] HTML_TAGS = {"p", "h1", "h2", "div", "span", "blockquote"};

    private PdfUtil() {
    }

    public static String generatePdf(String htmlContent, String customerUUID) {
        log.info("START generating PDF for customer with UUID:{}", customerUUID);
        Context context = new Context();
        context.setVariable("htmlContent", htmlContent);

        String html = renderHTMLTemplate(context, TEMPLATE_PATH_CUSTOMER_INFORMATION);
        return generate(HtmlCleaner.cleanedHtml(html));
    }

    public static String generatePdfForDataTakeout(String htmlContent, String customerUUID) {
        log.info("START generating PDF with takeout data for customer with UUID:{}", customerUUID);
        Context context = new Context();
        context.setVariable("htmlContent", htmlContent);

        return renderHTMLTemplate(context, TEMPLATE_PATH_DATA_TAKEOUT);
    }

    private static String renderHTMLTemplate(Context context, String templatePath) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");

        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process(templatePath, context);
    }

    public static String generate(String html) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
                renderer.finishPDF();

                byte[] pdfBytes = outputStream.toByteArray();
                return Base64.getEncoder().encodeToString(pdfBytes);
            } catch (IOException e) {
                retryCount++;
                if (retryCount == MAX_RETRIES) {
                    log.error("Cannot generate PDF after " + MAX_RETRIES + " retries. Reason: {}", e.getMessage());
                } else {
                    log.warn("Error generating PDF. Retrying (attempt {} of " + MAX_RETRIES + "). Reason: {}", retryCount, e.getMessage());
                }
            }
        }
        return null;
    }


    public static String generatePdfWithMemoryMeasurement(String htmlContent) {
        System.gc();

        long memoryBefore = getUsedMemory();
        log.info("Memory before PDF generation: {} MB", memoryBefore / (1024 * 1024));

        String pdfBase64 = generate(htmlContent);

        long memoryAfter = getUsedMemory();
        log.info("Memory after PDF generation: {} MB", memoryAfter / (1024 * 1024));

        long memoryUsed = (memoryAfter - memoryBefore) / (1024 * 1024);
        log.info("Memory used for generating PDF: {} MB", memoryUsed);

        if (memoryUsed <= 0) {
            log.info("Memory usage is unexpectedly low. Forcing garbage collection again.");
            System.gc();
            memoryAfter = getUsedMemory();
            memoryUsed = (memoryAfter - memoryBefore) / (1024 * 1024);
            log.info("Memory after second garbage collection: {} MB", memoryAfter / (1024 * 1024));
        }
        return pdfBase64;
    }
    private static long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }


    public static String generateRandomHtmlContent(int numberOfLines) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\">")
                .append("<head><title>Random HTML Test</title></head>")
                .append("<body>");

        for (int i = 0; i < numberOfLines; i++) {
            content.append("<p>This is random line ").append(i).append("</p>\n");
        }

        content.append("</body>")
                .append("</html>");

        return content.toString();
    }

    public static String generateRandomHtmlContent2(int numberOfLines) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\">")
                .append("<head><title>Random HTML Test</title></head>")
                .append("<body>");

        for (int i = 0; i < numberOfLines; i++) {
            html.append(generateRandomHtmlElement());
        }

        html.append("</body></html>");
        return html.toString();
    }

    private static String generateRandomHtmlElement() {
        String tag = HTML_TAGS[RANDOM.nextInt(HTML_TAGS.length)];

        String randomAlphabets = RandomStringUtils.randomAlphabetic(10, 20);
        String randomNumeric = RandomStringUtils.randomNumeric(5, 10);
        String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(10, 15);
        String randomAscii = RandomStringUtils.randomAscii(10, 30);

        String safeContent = String.format("%s %s %s %s",
                escapeHtml(randomAlphabets),
                escapeHtml(randomNumeric),
                escapeHtml(randomAlphanumeric),
                escapeHtml(randomAscii)
        );

        return String.format("<%s>%s</%s>\n", tag, safeContent, tag);
    }

    private static String escapeHtml(String content) {
        return content
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


    public static void savePdfToFile(String htmlContent, String filePath) {
        System.gc();

        long memoryBefore = getUsedMemory();
        log.info("Memory before PDF generation: {} MB", memoryBefore / (1024 * 1024));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                fileOutputStream.write(outputStream.toByteArray());
                log.info("PDF saved successfully to: {}", filePath);
            }

            File pdfFile = new File(filePath);
            long fileSizeInBytes = pdfFile.length();
            double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

            log.info("Size of the generated PDF file: {} MB", String.format("%.2f", fileSizeInMB));

        } catch (IOException e) {
            log.error("Error saving PDF to file: {}", e.getMessage());
        }

        long memoryAfter = getUsedMemory();
        log.info("Memory after PDF generation: {} MB", memoryAfter / (1024 * 1024));

        double memoryUsed = (memoryAfter - memoryBefore) / (1024.0 * 1024.0);
        log.info("Memory used for generating PDF: {} MB", String.format("%.2f", memoryUsed));
    }



}