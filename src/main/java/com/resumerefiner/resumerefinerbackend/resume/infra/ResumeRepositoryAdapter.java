package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSummaryProjection;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryAdapter implements ResumeRepository {

    private final ResumeJpaRepository jpa;

    @Override
    public Resume save(Resume resume) {
        return jpa.save(resume);
    }

    @Override
    public Optional<Resume> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Resume> findBySlug(ResumeSlug slug) {
        return jpa.findBySlug(slug);
    }

    @Override
    public List<Resume> findByMemberId(Long memberId) {
        return jpa.findByMemberId(memberId);
    }

    @Override
    public int countByMemberId(Long memberId) {
        return jpa.countResumeByMemberId(memberId);
    }

    @Override
    public Page<ResumeSummaryProjection> findResumeSummaries(Long memberId, String q, Pageable pageable) {
        return jpa.findSummaryRowsByMemberIdAndQuery(memberId, q, pageable)
                .map(row -> ResumeSummaryProjection.builder()
                        .slug(row.getSlug())
                        .title(row.getTitle())
                        .createdAt(row.getCreatedAt())
                        .updatedAt(row.getUpdatedAt())
                        .reviewCount(0L)
                        .build()
                );
    }
}
