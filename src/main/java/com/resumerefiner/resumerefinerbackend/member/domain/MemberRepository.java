package com.resumerefiner.resumerefinerbackend.member.domain;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByHandle(String handle);

    boolean existsByEmail(String email);

    boolean existsByHandle(String handle);

    Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
