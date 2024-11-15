package com.example.BookService;

import com.example.BookService.CombinedBody.entity.Application;
import com.example.BookService.CombinedBody.entity.CustomerInformation;
import com.example.BookService.CombinedBody.entity.InformationRequest;
import com.example.BookService.CombinedBody.enums.RequestType;
import com.example.BookService.CombinedBody.repository.CustomerInformationRepository;
import com.example.BookService.CombinedBody.service.CustomerInformationService;
import com.example.BookService.CombinedBody.service.InformationRequestProcessingService;
import com.example.BookService.CombinedBody.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HtmlProcessingServiceTest {
    private InformationRequestProcessingService processingService;
    private CustomerInformationService customerInformationService;
    private CustomerInformationRepository customerInformationRepository;
    private ValidationService validationService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    @BeforeEach
    void setup() {
        customerInformationService = mock(CustomerInformationService.class);
        validationService = mock(ValidationService.class);
        processingService = new InformationRequestProcessingService(
                customerInformationService,
                validationService,
                customerInformationRepository
        );
    }

    // Метод за генериране на произволен HTML низ с определен брой редове
    private String generateRandomHtmlByLines(int lines) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        for (int i = 0; i < lines; i++) {
            sb.append("<p>").append(generateRandomString(100)).append("</p>\n"); // всеки ред е около 100 символа
        }
        sb.append("</html>");
        return sb.toString();
    }

    // Метод за генериране на произволен текст
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // Метод за изчисляване на размера на низ в MB
    private int calculateSizeInMB(String content) {
        return content.getBytes(StandardCharsets.UTF_8).length / (1024 * 1024);
    }

    // Метод за разделяне на HTML съдържание на части с определен размер (в MB)
    private List<String> splitHtmlContentIntoChunks(String content, int maxSizeMB) {
        List<String> parts = new ArrayList<>();
        int maxSizeBytes = maxSizeMB * 1024 * 1024; // Преобразуване в байтове
        int startIndex = 0;

        while (startIndex < content.length()) {
            int endIndex = Math.min(startIndex + maxSizeBytes, content.length());
            String part = content.substring(startIndex, endIndex);
            parts.add(part);
            startIndex = endIndex;
        }
        return parts;
    }
    @Test
    void testWithMultipleCustomerInformationRecordsByLines() {
        // Създаваме 30 приложения с random HTML с различен брой редове
        List<CustomerInformation> customerInfoList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            int lines = RANDOM.nextInt(5000) + 1000; // Брой редове между 1000 и 6000
            CustomerInformation info = new CustomerInformation();
            info.setData(generateRandomHtmlByLines(lines));
            customerInfoList.add(info);
        }

        when(customerInformationService.findByCustomerIdAndApplicationIdAndRequestType(any(), any(), any()))
                .thenReturn(customerInfoList);

        Application application = new Application("app-123");
        InformationRequest request = new InformationRequest("customer-123", RequestType.RIGHT_OF_ACCESS);
        String newBody = generateRandomHtmlByLines(2000); // Добавяме допълнителен HTML с 2000 реда

        String combinedBody = processingService.getCombinedBody(application, request, newBody);

        int combinedHtmlSizeMB = calculateSizeInMB(combinedBody);
        System.out.println("Combined HTML Size: " + combinedHtmlSizeMB + " MB");

        // Проверяваме дали обединеното съдържание е разделено на 20MB части
        int parts = (int) Math.ceil((double) combinedHtmlSizeMB / 20);
        assertTrue(parts <= 30, "Обединеното съдържание надвишава лимита от 30 части");
    }

    @Test
    void testWithOneApplicationAndTwoLargeRecordsByLines() {
        // Създаваме 1 приложение с 2 записа, всеки със специфичен брой редове
        List<CustomerInformation> customerInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CustomerInformation info = new CustomerInformation();
            info.setData(generateRandomHtmlByLines(150000)); // Всеки запис е около 150000 реда
            customerInfoList.add(info);
        }

        when(customerInformationService.findByCustomerIdAndApplicationIdAndRequestType(any(), any(), any()))
                .thenReturn(customerInfoList);

        Application application = new Application("app-456");
        InformationRequest request = new InformationRequest("customer-456", RequestType.RIGHT_OF_ACCESS);

        String combinedBody = processingService.getCombinedBody(application, request, "");
        int combinedHtmlSizeMB = calculateSizeInMB(combinedBody);
        System.out.println("Combined HTML Size: " + combinedHtmlSizeMB + " MB");

        assertTrue(combinedHtmlSizeMB >= 20 && combinedHtmlSizeMB <= 40, "Обединеното съдържание трябва да бъде между 20MB и 40MB");
    }
    // Метод за генериране на HTML съдържание с определен размер в MB
    private String generateRandomHtmlBySize(int sizeInMB) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        int totalBytes = sizeInMB * 1024 * 1024; // MB в байтове
        int lineSize = 100000; // 1000 символа на ред, вместо 100
        while (sb.toString().getBytes(StandardCharsets.UTF_8).length < totalBytes) {
            sb.append("<p>").append(generateRandomString(lineSize)).append("</p>\n");
        }
        sb.append("</html>");
        return sb.toString();
    }


    @Test
    void testWithOneApplicationWith65MbData() {
        // Създаваме 1 приложение с 1 запис, който съдържа данни с общ размер от 65 MB
        List<CustomerInformation> customerInfoList = new ArrayList<>();
        CustomerInformation info = new CustomerInformation();
        info.setData(generateRandomHtmlBySize(65)); // Създаваме запис с размер от 65 MB
        customerInfoList.add(info);

        when(customerInformationService.findByCustomerIdAndApplicationIdAndRequestType(any(), any(), any()))
                .thenReturn(customerInfoList);

        Application application = new Application("app-789");
        InformationRequest request = new InformationRequest("customer-789", RequestType.RIGHT_OF_ACCESS);

        // Генерираме комбинираното съдържание
        String combinedBody = processingService.getCombinedBody(application, request, "");
        int combinedHtmlSizeMB = calculateSizeInMB(combinedBody);
        System.out.println("Combined HTML Size: " + combinedHtmlSizeMB + " MB");

        // Проверяваме дали комбинираното съдържание е разделено правилно на части от по 20MB
        List<String> parts = splitHtmlContentIntoChunks(combinedBody, 20);
        System.out.println("Number of parts: " + parts.size());

        // Очакваме 4 части (3 по 20MB и 1 с 5MB)
        assertTrue(parts.size() == 4, "Съдържанието трябва да бъде разделено на 4 части");
        assertTrue(calculateSizeInMB(parts.get(0)) == 20, "Първата част трябва да е 20MB");
        assertTrue(calculateSizeInMB(parts.get(1)) == 20, "Втората част трябва да е 20MB");
        assertTrue(calculateSizeInMB(parts.get(2)) == 20, "Третата част трябва да е 5MB");
        assertTrue(calculateSizeInMB(parts.get(3)) == 5, "Четвъртата част трябва да е 5MB");
    }
}
