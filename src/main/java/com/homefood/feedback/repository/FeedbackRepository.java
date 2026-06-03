package com.homefood.feedback.repository;

import com.homefood.feedback.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Page<Feedback> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Feedback> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);
    List<Feedback> findByStatusOrderByCreatedAtDesc(String status);
}
