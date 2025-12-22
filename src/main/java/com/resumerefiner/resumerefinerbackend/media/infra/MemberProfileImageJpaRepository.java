package com.resumerefiner.resumerefinerbackend.media.infra;

import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberProfileImageJpaRepository extends JpaRepository<MemberProfileImage, Long> {
    Optional<MemberProfileImage> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);

    @Query("select r.url from MemberProfileImage r where r.id = :id")
    Optional<String> findUrlById(Long id);
}
