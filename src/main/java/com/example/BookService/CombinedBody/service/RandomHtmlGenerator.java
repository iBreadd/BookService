package com.example.BookService.CombinedBody.service;

import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class RandomHtmlGenerator {
    private static final Random random = new Random();

    public static String generateRandomHtml(int sizeInMb) {
        int targetSize = sizeInMb * 1024 * 1024; // Размер в байтове
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<section><h2>Random HTML Content</h2>");

        while (htmlBuilder.length() < targetSize) {
            htmlBuilder.append("<p>")
                    .append(generateRandomString(1024)) // Добавяме 1KB текст
                    .append("</p>");
        }

        htmlBuilder.append("</section>");
        return htmlBuilder.toString();
    }

    private static String generateRandomString(int length) {
        return random.ints(97, 123) // Генерира букви от 'a' до 'z'
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

