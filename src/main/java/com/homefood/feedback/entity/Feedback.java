package com.homefood.feedback.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "feedbacks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    private String id;

    private UUID userId;
    private UUID orderId;
    private String foodId;
    private UUID sellerId;

    private String feedbackType; // TEXT, AUDIO, VIDEO, IMAGE
    private String textContent;
    private List<String> mediaUrls;  // MinIO URLs
    private String subject;
    private int sentimentScore; // 1-5 (1=very negative, 5=very positive)
    private boolean isAnonymous;
    private String status; // PENDING, REVIEWED, RESOLVED

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String adminResponse;
}
