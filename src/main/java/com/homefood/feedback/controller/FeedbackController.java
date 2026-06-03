package com.homefood.feedback.controller;

import com.homefood.feedback.entity.Feedback;
import com.homefood.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/text")
    public ResponseEntity<Feedback> submitTextFeedback(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, Object> body) {
        Feedback feedback = feedbackService.submitTextFeedback(
                userId,
                UUID.fromString((String) body.get("orderId")),
                (String) body.get("foodId"),
                UUID.fromString((String) body.get("sellerId")),
                (String) body.get("text"),
                (String) body.get("subject"),
                (Integer) body.getOrDefault("sentiment", 3),
                Boolean.parseBoolean(body.getOrDefault("anonymous", "false").toString())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }

    @GetMapping("/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam String contentType) {
        return ResponseEntity.ok(feedbackService.generatePresignedUploadUrl(fileName, contentType));
    }

    @PostMapping("/media")
    public ResponseEntity<Feedback> submitMediaFeedback(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> mediaKeys = (List<String>) body.get("mediaKeys");
        Feedback feedback = feedbackService.submitMediaFeedback(
                userId,
                UUID.fromString((String) body.get("orderId")),
                (String) body.get("foodId"),
                UUID.fromString((String) body.get("sellerId")),
                mediaKeys,
                (String) body.getOrDefault("feedbackType", "IMAGE"),
                (Integer) body.getOrDefault("sentiment", 3)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Feedback>> getMyFeedbacks(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(feedbackService.getUserFeedbacks(userId, page, size));
    }
}
