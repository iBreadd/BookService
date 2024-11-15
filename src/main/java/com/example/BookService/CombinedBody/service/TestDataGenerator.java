package com.example.BookService.CombinedBody.service;

import com.example.BookService.CombinedBody.entity.Application;
import com.example.BookService.CombinedBody.entity.CustomerInformation;
import com.example.BookService.CombinedBody.enums.InformationStatus;
import com.example.BookService.CombinedBody.enums.RequestType;
import com.example.BookService.CombinedBody.repository.ApplicationRepository;
import com.example.BookService.CombinedBody.repository.CustomerInformationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TestDataGenerator {
    private final CustomerInformationRepository customerInformationRepository;
    private final ApplicationRepository applicationRepository;

    public TestDataGenerator(CustomerInformationRepository customerInformationRepository,
                             ApplicationRepository applicationRepository) {
        this.customerInformationRepository = customerInformationRepository;
        this.applicationRepository = applicationRepository;
    }

    public void createTestApplications() {
        for (int i = 0; i < 30; i++) {
            String applicationId = UUID.randomUUID().toString();
            String customerId = UUID.randomUUID().toString();

            // Създаване на Application обект
            Application application = new Application(applicationId, List.of("type1", "type2"), "http://example.com", "team@example.com", "slack-channel", "webhook-url");
            applicationRepository.save(application);

            for (int j = 0; j < 2; j++) {
                CustomerInformation info = CustomerInformation.builder()
                        .customerId(customerId)
                        .applicationId(applicationId)
                        .requestType(RequestType.RIGHT_OF_ACCESS)
                        .offsetValue(j)
                        .status(InformationStatus.Requested)
                        .data(RandomHtmlGenerator.generateRandomHtml(20)) // Генерира 20MB HTML
                        .lastRequested(Instant.now())
                        .build();

                customerInformationRepository.save(info);
            }
        }
    }

}

