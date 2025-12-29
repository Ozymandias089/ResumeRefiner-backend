package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    List<Review> findByResumeId(Long resumeId);

    int countByMemberId(Long memberId);

    List<Review> findAllByMemberId(Long memberId);

    Optional<Review> findTopByResumeIdAndMemberIdOrderByCreatedAtDesc(Long resumeId, Long memberId);
}
