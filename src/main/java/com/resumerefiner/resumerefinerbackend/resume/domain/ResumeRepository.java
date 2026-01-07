package com.resumerefiner.resumerefinerbackend.resume.domain;

import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSlugTitleRow;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSummaryProjection;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {

    Resume save(Resume resume);

    void delete(Resume resume);

    Optional<Resume> findById(Long id);

    Optional<Resume> findBySlug(ResumeSlug slug);

    List<Resume> findByMemberId(Long memberId);

    int countByMemberId(Long memberId);

    Page<ResumeSummaryProjection> findResumeSummaries(Long memberId, String q, Pageable pageable);

    Optional<ResumeSlug> findSlugById(Long id);

    Optional<ResumeSlugTitleRow> findSlugTitleById(Long resumeId);
}
