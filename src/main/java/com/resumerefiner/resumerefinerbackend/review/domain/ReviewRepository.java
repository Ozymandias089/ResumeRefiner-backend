package com.resumerefiner.resumerefinerbackend.review.domain;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Optional<Review> findById(Long id);

    List<Review> findByResumeId(Long resumeId);

    int countByMemberId(Long memberId);
    List<Review> findAllByMemberId(Long memberId);

    Optional<Review> findLatestByResumeIdAndMemberId(Long resumeId, Long memberId);
}
