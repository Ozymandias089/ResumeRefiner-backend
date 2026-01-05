package com.resumerefiner.resumerefinerbackend.review.infra;

import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    List<Review> findByResumeId(Long resumeId);

    int countByMemberId(Long memberId);

    List<Review> findAllByMemberId(Long memberId);

    Optional<Review> findTopByResumeIdAndMemberIdOrderByCreatedAtDesc(Long resumeId, Long memberId);

    @Query("""
        select coalesce(max(r.sequencePerVersion), 0)
        from Review r
        where r.resumeId = :resumeId and r.resumeVersion = :resumeVersion
    """)
    int findMaxSequencePerVersion(@Param("resumeId") Long resumeId,
                                  @Param("resumeVersion") Long resumeVersion);

    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Page<Review> findByMemberIdAndResumeIdOrderByCreatedAtDesc(Long memberId, Long resumeId, Pageable pageable);
}
