package com.homefood.feedback.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishFeedbackEvent(String feedbackId, String type) {
        String event = String.format("{\"type\":\"%s\",\"feedbackId\":\"%s\"}", type, feedbackId);
        kafkaTemplate.send("report.event", feedbackId, event);
        log.info("Published {} event for feedback: {}", type, feedbackId);
    }
}
