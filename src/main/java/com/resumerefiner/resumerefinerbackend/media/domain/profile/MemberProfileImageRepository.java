package com.resumerefiner.resumerefinerbackend.media.domain.profile;

import java.util.Optional;

public interface MemberProfileImageRepository {

    MemberProfileImage save(MemberProfileImage image);

    Optional<MemberProfileImage> findById(Long id);

    Optional<String> findUrlById(Long id);

    Optional<MemberProfileImage> findByMemberId(Long memberId);

    void delete(MemberProfileImage image);

    void deleteByMemberId(Long memberId);
}
