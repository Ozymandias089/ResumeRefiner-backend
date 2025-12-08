package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.ReviewSentenceFeedback;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewSentenceFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewSentenceFeedbackRepositoryAdapter implements ReviewSentenceFeedbackRepository {

    private final ReviewSentenceFeedbackJpaRepository jpa;

    @Override
    public ReviewSentenceFeedback save(ReviewSentenceFeedback feedback) {
        return jpa.save(feedback);
    }

    @Override
    public List<ReviewSentenceFeedback> findByReviewId(Long reviewId) {
        return jpa.findByReviewId(reviewId);
    }

    @Override
    public void deleteByReviewId(Long reviewId) {
        jpa.deleteByReviewId(reviewId);
    }
}
