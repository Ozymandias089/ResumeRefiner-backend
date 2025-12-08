package com.resumerefiner.resumerefinerbackend.member.infra;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByHandle(String handle);

    boolean existsByEmail(String email);

    boolean existsByHandle(String handle);

    Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
