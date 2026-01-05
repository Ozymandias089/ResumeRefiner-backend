package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void delete(Review review) {
        jpa.delete(review);
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

    @Override
    public int findMaxSequencePerVersion(Long resumeId, Long resumeVersion) {
        return jpa.findMaxSequencePerVersion(resumeId, resumeVersion);
    }

    @Override
    public Page<Review> findPageByMemberId(Long memberId, Pageable pageable) {
        return jpa.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    @Override
    public Page<Review> findPageByMemberIdAndResumeId(Long memberId, Long resumeId, Pageable pageable) {
        return jpa.findByMemberIdAndResumeIdOrderByCreatedAtDesc(memberId, resumeId, pageable);
    }
}
