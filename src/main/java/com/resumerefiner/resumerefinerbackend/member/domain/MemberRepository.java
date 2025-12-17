package com.resumerefiner.resumerefinerbackend.member.domain;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(Email email);

    Optional<Member> findByHandle(Handle handle);

    boolean existsByEmail(Email email);

    boolean existsByHandle(Handle handle);

    Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
