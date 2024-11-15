package com.example.BookService.CombinedBody.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "application")
@Builder
public class Application{
    @Id
    private String applicationId;

    @Column(name = "id_types", columnDefinition = "text[]")
    private List<String> idTypes;

    private String requestUrl;

    private String teamEmail;

    private String slackChannel;

    private String microsoftTeamsWebhookUrl;

    public Application(String applicationId) {
        this.applicationId = applicationId;
    }
}
