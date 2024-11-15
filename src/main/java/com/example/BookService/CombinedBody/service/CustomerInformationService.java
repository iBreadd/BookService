package com.example.BookService.CombinedBody.service;

import com.example.BookService.CombinedBody.entity.CustomerInformation;
import com.example.BookService.CombinedBody.enums.RequestType;
import com.example.BookService.CombinedBody.repository.CustomerInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerInformationService {
    private final CustomerInformationRepository repository;

    public List<CustomerInformation> findByCustomerIdAndApplicationIdAndRequestType(String customerId, String applicationId, RequestType requestType) {
        return repository.findCustomerInformationByIdCustomerIdApplicationIdRequestType(customerId, applicationId, requestType);

    }
}
