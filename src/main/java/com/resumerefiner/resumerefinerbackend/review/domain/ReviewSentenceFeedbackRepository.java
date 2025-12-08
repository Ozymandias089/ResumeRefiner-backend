package com.resumerefiner.resumerefinerbackend.review.domain;

import java.util.List;

public interface ReviewSentenceFeedbackRepository {

    ReviewSentenceFeedback save(ReviewSentenceFeedback feedback);

    List<ReviewSentenceFeedback> findByReviewId(Long reviewId);

    void deleteByReviewId(Long reviewId);
}
