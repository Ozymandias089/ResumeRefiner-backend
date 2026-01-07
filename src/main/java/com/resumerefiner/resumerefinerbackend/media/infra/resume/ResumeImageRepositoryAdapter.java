package com.resumerefiner.resumerefinerbackend.media.infra.resume;

import com.resumerefiner.resumerefinerbackend.media.domain.resume.ResumeImage;
import com.resumerefiner.resumerefinerbackend.media.domain.resume.ResumeImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResumeImageRepositoryAdapter implements ResumeImageRepository {
    private final ResumeImageJpaRepository jpa;
    @Override
    public ResumeImage save(ResumeImage image) {
        return jpa.save(image);
    }

    @Override
    public Optional<ResumeImage> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<String> findUrlById(Long id) {
        return jpa.findUrlById(id);
    }

    @Override
    public Optional<ResumeImage> findByResumeId(Long resumeId) {
        return jpa.findByResumeId(resumeId);
    }

    @Override
    public void delete(ResumeImage image) {
        jpa.delete(image);
    }

    @Override
    public void deleteByResumeId(Long resumeId) {
        jpa.deleteByResumeId(resumeId);
    }
}
