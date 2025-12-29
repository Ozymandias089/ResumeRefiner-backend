package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

    private final ReviewJpaRepository jpa;

    @Override
    public Review save(Review review) {
        return jpa.save(review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Review> findByResumeId(Long resumeId) {
        return jpa.findByResumeId(resumeId);
    }

    @Override
    public int countByMemberId(Long memberId) {
        return jpa.countByMemberId(memberId);
    }

    @Override
    public List<Review> findAllByMemberId(Long memberId) {
        return jpa.findAllByMemberId(memberId);
    }

    @Override
    public Optional<Review> findLatestByResumeIdAndMemberId(Long resumeId, Long memberId) {
        return jpa.findTopByResumeIdAndMemberIdOrderByCreatedAtDesc(resumeId, memberId);
    }
}
