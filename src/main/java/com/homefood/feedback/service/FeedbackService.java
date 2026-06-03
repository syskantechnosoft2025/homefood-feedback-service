package com.homefood.feedback.service;

import com.homefood.feedback.entity.Feedback;
import com.homefood.feedback.repository.FeedbackRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MinioClient minioClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${minio.bucket:homefood-feedback}")
    private String bucketName;

    public Feedback submitTextFeedback(UUID userId, UUID orderId, String foodId,
                                        UUID sellerId, String text, String subject, int sentiment, boolean anonymous) {
        Feedback feedback = Feedback.builder()
                .userId(userId)
                .orderId(orderId)
                .foodId(foodId)
                .sellerId(sellerId)
                .feedbackType("TEXT")
                .textContent(text)
                .subject(subject)
                .sentimentScore(sentiment)
                .isAnonymous(anonymous)
                .status("PENDING")
                .build();

        feedback = feedbackRepository.save(feedback);

        kafkaTemplate.send("report.event", feedback.getId(),
                String.format("{\"type\":\"FEEDBACK_SUBMITTED\",\"feedbackId\":\"%s\"}", feedback.getId()));

        return feedback;
    }

    public Map<String, String> generatePresignedUploadUrl(String fileName, String contentType) {
        try {
            String objectKey = "feedback/" + UUID.randomUUID() + "/" + fileName;

            // Generate presigned PUT URL
            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(15, TimeUnit.MINUTES)
                            .build()
            );

            return Map.of("uploadUrl", uploadUrl, "objectKey", objectKey);
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", e.getMessage());
            throw new RuntimeException("Could not generate upload URL", e);
        }
    }

    public Feedback submitMediaFeedback(UUID userId, UUID orderId, String foodId,
                                         UUID sellerId, List<String> mediaKeys, String feedbackType, int sentiment) {
        List<String> mediaUrls = new ArrayList<>();
        for (String key : mediaKeys) {
            try {
                String url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(key)
                                .expiry(7, TimeUnit.DAYS)
                                .build()
                );
                mediaUrls.add(url);
            } catch (Exception e) {
                log.warn("Failed to get media URL for key: {}", key);
                mediaUrls.add(key); // fallback to key
            }
        }

        Feedback feedback = Feedback.builder()
                .userId(userId)
                .orderId(orderId)
                .foodId(foodId)
                .sellerId(sellerId)
                .feedbackType(feedbackType)
                .mediaUrls(mediaUrls)
                .sentimentScore(sentiment)
                .status("PENDING")
                .build();

        return feedbackRepository.save(feedback);
    }

    public Page<Feedback> getUserFeedbacks(UUID userId, int page, int size) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Feedback> getSellerFeedbacks(UUID sellerId, int page, int size) {
        return feedbackRepository.findBySellerIdOrderByCreatedAtDesc(sellerId,
                PageRequest.of(page, size));
    }
}
