package com.resumerefiner.resumerefinerbackend.member.infra;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {

    private final MemberJpaRepository jpa;

    @Override
    public Member save(Member member) {
        return jpa.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<Member> findByHandle(String handle) {
        return jpa.findByHandle(handle);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByHandle(String handle) {
        return jpa.existsByHandle(handle);
    }

    @Override
    public Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId) {
        return jpa.findByProviderAndProviderUserId(provider, providerUserId);
    }
}
