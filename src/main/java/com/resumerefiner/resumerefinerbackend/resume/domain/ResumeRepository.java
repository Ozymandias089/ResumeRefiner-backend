package com.resumerefiner.resumerefinerbackend.resume.domain;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {

    Resume save(Resume resume);

    Optional<Resume> findById(Long id);

    Optional<Resume> findBySlug(String slug);

    List<Resume> findByMemberId(Long memberId);

    int countByMemberId(Long memberId);
}
