package com.example.BookService.CombinedBody.service;

import com.example.BookService.CombinedBody.entity.Application;
import com.example.BookService.CombinedBody.repository.ApplicationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    @Transactional
    public Application findByApplicationId(String applicationId) {
        return applicationRepository.findByApplicationId(applicationId);
    }
}
