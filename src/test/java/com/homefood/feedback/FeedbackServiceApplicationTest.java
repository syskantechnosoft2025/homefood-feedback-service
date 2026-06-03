package com.homefood.feedback;

import com.homefood.feedback.service.FeedbackService;
import com.homefood.feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceApplicationTest {

    @Mock
    FeedbackRepository feedbackRepository;

    @Test
    void contextLoads() {
        assertThat(feedbackRepository).isNotNull();
    }
}
