package com.resumerefiner.resumerefinerbackend.media.infra.profile;

import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImage;
import com.resumerefiner.resumerefinerbackend.media.domain.profile.MemberProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberProfileImageRepositoryAdapter implements MemberProfileImageRepository {

    private final MemberProfileImageJpaRepository jpa;


    @Override
    public MemberProfileImage save(MemberProfileImage image) {
        return jpa.save(image);
    }

    @Override
    public Optional<MemberProfileImage> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<String> findUrlById(Long id) {
        return jpa.findUrlById(id);
    }

    @Override
    public Optional<MemberProfileImage> findByMemberId(Long memberId) {
        return jpa.findByMemberId(memberId);
    }

    @Override
    public void delete(MemberProfileImage image) {
        jpa.delete(image);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        jpa.deleteByMemberId(memberId);
    }
}
