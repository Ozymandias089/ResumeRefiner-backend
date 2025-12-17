package com.resumerefiner.resumerefinerbackend.member.infra;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
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
    public Optional<Member> findByEmail(Email email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<Member> findByHandle(Handle handle) {
        return jpa.findByHandle(handle);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByHandle(Handle handle) {
        return jpa.existsByHandle(handle);
    }

    @Override
    public Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId) {
        return jpa.findByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    public Optional<Long> findMemberIdByHandle(Handle handle) {
        return jpa.findIdByHandle(handle);
    }
}
