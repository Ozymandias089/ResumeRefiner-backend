package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findBySlug(String slug);

    List<Resume> findByMemberId(Long memberId);

    int countResumeByMemberId(Long memberId);
}
