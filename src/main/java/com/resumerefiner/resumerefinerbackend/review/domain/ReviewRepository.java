package com.resumerefiner.resumerefinerbackend.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Optional<Review> findById(Long id);

    List<Review> findByResumeId(Long resumeId);

    int countByMemberId(Long memberId);
    List<Review> findAllByMemberId(Long memberId);

    Optional<Review> findLatestByResumeIdAndMemberId(Long resumeId, Long memberId);

    int findMaxSequencePerVersion(Long resumeId, Long resumeVersion);

    Page<Review> findPageByMemberId(Long memberId, Pageable pageable);

    Page<Review> findPageByMemberIdAndResumeId(Long memberId, Long resumeId, Pageable pageable);
}
