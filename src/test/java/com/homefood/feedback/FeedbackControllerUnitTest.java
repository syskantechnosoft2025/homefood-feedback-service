package com.homefood.feedback;

import com.homefood.feedback.controller.FeedbackController;
import com.homefood.feedback.entity.Feedback;
import com.homefood.feedback.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerUnitTest {

    @Mock FeedbackService feedbackService;
    @InjectMocks FeedbackController feedbackController;

    @Test
    void getMyFeedbacks_returns200() {
        UUID userId = UUID.randomUUID();
        Page<Feedback> page = new PageImpl<>(List.of(new Feedback()));
        when(feedbackService.getUserFeedbacks(userId, 0, 10)).thenReturn(page);

        ResponseEntity<Page<Feedback>> result = feedbackController.getMyFeedbacks(userId, 0, 10);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }
}
