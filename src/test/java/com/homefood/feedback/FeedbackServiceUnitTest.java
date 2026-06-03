package com.homefood.feedback;

import com.homefood.feedback.entity.Feedback;
import com.homefood.feedback.repository.FeedbackRepository;
import com.homefood.feedback.service.FeedbackService;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceUnitTest {

    @Mock FeedbackRepository feedbackRepository;
    @Mock MinioClient minioClient;
    @Mock KafkaTemplate<String, String> kafkaTemplate;
    @InjectMocks FeedbackService feedbackService;

    @Test
    void submitTextFeedback_returnsFeedback() {
        ReflectionTestUtils.setField(feedbackService, "bucketName", "test-bucket");
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();

        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        Feedback saved = Feedback.builder()
            .userId(userId)
            .orderId(orderId)
            .foodId("food_001")
            .sellerId(sellerId)
            .feedbackType("TEXT")
            .textContent("Excellent food!")
            .sentimentScore(5)
            .status("PENDING")
            .build();

        when(feedbackRepository.save(any())).thenReturn(saved);

        Feedback result = feedbackService.submitTextFeedback(userId, orderId, "food_001", sellerId,
            "Excellent food!", "Food Quality", 5, false);

        assertThat(result.getFeedbackType()).isEqualTo("TEXT");
    }

    @Test
    void getUserFeedbacks_returnsPage() {
        UUID userId = UUID.randomUUID();
        Page<Feedback> page = new PageImpl<>(List.of(new Feedback()));
        when(feedbackRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any())).thenReturn(page);

        Page<Feedback> result = feedbackService.getUserFeedbacks(userId, 0, 10);
        assertThat(result.getContent()).hasSize(1);
    }
}
