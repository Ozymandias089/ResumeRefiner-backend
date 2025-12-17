package com.resumerefiner.resumerefinerbackend.member.infra;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.Provider;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);

    Optional<Member> findByHandle(Handle handle);

    boolean existsByEmail(Email email);

    boolean existsByHandle(Handle handle);

    Optional<Member> findByProviderAndProviderUserId(Provider provider, String providerUserId);

    Optional<Long> findIdByHandle(Handle handle);
}
