package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.domain.resume.ResumeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResumeImageJpaRepository extends JpaRepository<ResumeImage, Long> {
    Optional<ResumeImage> findByResumeId(Long resumeId);
    void deleteByResumeId(Long resumeId);

    @Query("select r.url from ResumeImage r where r.id = :id")
    Optional<String> findUrlById(Long id);
}
