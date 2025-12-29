package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSummaryRow;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findBySlug(ResumeSlug slug);

    List<Resume> findByMemberId(Long memberId);

    int countResumeByMemberId(Long memberId);

    @Query("""
    select
        r.slug as slug,
        r.title as title,
        r.createdAt as createdAt,
        r.updatedAt as updatedAt
    from Resume r
    where r.memberId = :memberId
      and (:q is null or :q = '' or lower(r.title) like lower(concat('%', :q, '%')))
""")
    Page<ResumeSummaryRow> findSummaryRowsByMemberIdAndQuery(
            Long memberId,
            String q,
            Pageable pageable
    );

    @Query("""
        select r.slug as slug from Resume r where r.id = :id
    """)
    Optional<ResumeSlug> findSlugById(Long id);
}
