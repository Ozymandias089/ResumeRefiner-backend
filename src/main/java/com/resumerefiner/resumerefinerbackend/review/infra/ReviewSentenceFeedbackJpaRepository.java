package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewSentenceFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewSentenceFeedbackJpaRepository extends JpaRepository<ReviewSentenceFeedback, Long> {

    List<ReviewSentenceFeedback> findByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}
