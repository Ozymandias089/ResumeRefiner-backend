package com.resumerefiner.resumerefinerbackend.media.domain.resume;

import java.util.Optional;

public interface ResumeImageRepository {
    ResumeImage save(ResumeImage image);

    Optional<ResumeImage> findById(Long id);

    Optional<String> findUrlById(Long id);

    Optional<ResumeImage> findByResumeId(Long resumeId);

    void delete(ResumeImage image);

    void deleteByResumeId(Long resumeId);
}
