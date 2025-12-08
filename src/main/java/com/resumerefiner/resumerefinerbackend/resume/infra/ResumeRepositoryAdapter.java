package com.resumerefiner.resumerefinerbackend.resume.infra;

import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import lombok.RequiredArgsConstructor;
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
    public Optional<Resume> findBySlug(String slug) {
        return jpa.findBySlug(slug);
    }

    @Override
    public List<Resume> findByMemberId(Long memberId) {
        return jpa.findByMemberId(memberId);
    }
}
