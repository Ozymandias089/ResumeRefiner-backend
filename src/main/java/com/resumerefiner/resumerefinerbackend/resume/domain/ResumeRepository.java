package com.resumerefiner.resumerefinerbackend.resume.domain;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {

    Resume save(Resume resume);

    Optional<Resume> findById(Long id);

    Optional<Resume> findBySlug(ResumeSlug slug);

    List<Resume> findByMemberId(Long memberId);

    int countByMemberId(Long memberId);
}
