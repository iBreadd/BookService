package com.example.BookService.CombinedBody.service;

import com.example.BookService.CombinedBody.entity.Application;
import com.example.BookService.CombinedBody.entity.CustomerInformation;
import com.example.BookService.CombinedBody.entity.InformationRequest;
import com.example.BookService.CombinedBody.enums.InformationStatus;
import com.example.BookService.CombinedBody.exception.InvalidInputException;
import com.example.BookService.CombinedBody.repository.CustomerInformationRepository;
import com.example.BookService.utill.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InformationRequestProcessingService {
    private final CustomerInformationService customerInformationService;
    private final ValidationService validationService;
    private final CustomerInformationRepository customerInformationRepository;

    public Pair<InformationStatus, String> parseAndValidateResponse(String body, Application application, InformationRequest request, int offset) {
        log.info("Starting parseAndValidateResponse with offset={}, applicationId={}, customerId={}",
                offset, application.getApplicationId(), request.getCustomerId());

        // Логваме входното тяло
        if (body.isBlank()) {
            log.warn("Received blank body with offset={}", offset);
        } else {
            log.debug("Received body: {}", body.length() > 100 ? body.substring(0, 100) + "..." : body);
        }

        if (body.isBlank() && offset == 0) {
            log.info("Returning CompletedNoData status due to blank body and offset=0");
            return Pair.of(InformationStatus.CompletedNoData, "");
        }

        String combinedBody = body;
        if (offset > 0) {
            combinedBody = getCombinedBody(application, request, body);
            log.info("Combined body length after getCombinedBody: {}", combinedBody.length());
        }

        try {
            log.info("Validating combined body with length: {}", combinedBody.length());
            validationService.validateHtmlSnippet(combinedBody);
        } catch (InvalidInputException exception) {
            log.error("Validation failed for HTML snippet: {}", exception.getMessage());
            return Pair.of(InformationStatus.ServiceFailed, "Invalid html:\n" + body);
        }

        log.info("Validation succeeded, returning Completed status");
        return Pair.of(InformationStatus.Completed, body);
    }

    public String getCombinedBody(Application application, InformationRequest request, String body) {
        log.info("Fetching customer information for customerId={}, applicationId={}, requestType={}",
                request.getCustomerId(), application.getApplicationId(), request.getRequestType());

        List<CustomerInformation> customerInformationList = customerInformationService.findByCustomerIdAndApplicationIdAndRequestType(
                request.getCustomerId(), application.getApplicationId(), request.getRequestType());

        log.info("Fetched {} customer information records", customerInformationList.size());

        if (customerInformationList.isEmpty()) {
            log.warn("No customer information records found!");
        }

        StringBuilder combinedBody = new StringBuilder();
        for (CustomerInformation ci : customerInformationList) {
            log.debug("Appending customer information data with length: {}", ci.getData().length());
            combinedBody.append(ci.getData());
        }

        // Добавяме входното тяло (body)
        log.debug("Appending initial body data with length: {}", body.length());
        combinedBody.append(body);

        log.info("Final combined body length: {}", combinedBody.length());

        // Запазваме комбинираното HTML съдържание във файл
        String fileName = "combined_body_" + application.getApplicationId() + ".html";
        FileUtils.saveHtmlToFile(combinedBody.toString(), fileName);

        return combinedBody.toString();
    }



}
