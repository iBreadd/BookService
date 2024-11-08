package com.example.BookService.utill;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
public final class PdfUtil {
    private static final String TEMPLATE_PATH_CUSTOMER_INFORMATION = "templates/GDPR";
    private static final String TEMPLATE_PATH_DATA_TAKEOUT = "templates/GDPR_takeout";
    private static final int MAX_RETRIES = 3;

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
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        String pdfBase64 = generate(htmlContent);

        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsed = (memoryAfter - memoryBefore) / (1024 * 1024);
        log.info("Memory used for generating PDF: {} MB", memoryUsed);

        return pdfBase64;
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

}
