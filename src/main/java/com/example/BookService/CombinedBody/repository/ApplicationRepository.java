package com.example.BookService.CombinedBody.repository;

import com.example.BookService.CombinedBody.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Application findByApplicationId(String applicationId);
}

