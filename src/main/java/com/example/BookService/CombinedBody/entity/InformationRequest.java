package com.example.BookService.CombinedBody.entity;

import com.example.BookService.CombinedBody.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "information_request")
public class InformationRequest {

    @Id
    @GeneratedValue
    private UUID id;

    private String customerId;
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    private String salesforceCaseId;
    private String reweId;
    private String email;
    @CreationTimestamp
    private Instant createdAt;
    @Transient
    private Map<String, String> idReferenceMap;
    private boolean isCustomerNotified;
    private Instant finishedAt;

    @PostLoad
    public void populateIdReferenceMap() {
        idReferenceMap = new HashMap<>();
        idReferenceMap.put("reweId", reweId);
        idReferenceMap.put("customerUuid", customerId);
        idReferenceMap.put("emailAddress", email);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InformationRequest{");
        sb.append("customerId='").append(customerId).append('\'');
        sb.append(", requestType=").append(requestType);
        sb.append(", salesforceCaseId='").append(salesforceCaseId).append('\'');
        sb.append(", reweId='").append(reweId).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public InformationRequest(String customerId, RequestType requestType) {
        this.customerId = customerId;
        this.requestType = requestType;
    }
}
