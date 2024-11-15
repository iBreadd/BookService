package com.example.BookService.CombinedBody.entity;

import com.example.BookService.CombinedBody.enums.InformationStatus;
import com.example.BookService.CombinedBody.enums.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer_information")
public class CustomerInformation {

    @Id
    @GeneratedValue
    private UUID id;

    private String customerId;
    private String applicationId;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
    private Integer offsetValue;

    @Enumerated(EnumType.STRING)
    private InformationStatus status;
    @Schema(name = "failed_status_reason", nullable = true)
    private String failedStatusReason;
    private String data;
    private Instant lastRequested;
}
