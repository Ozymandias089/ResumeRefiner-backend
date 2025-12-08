package com.resumerefiner.resumerefinerbackend.billing.infra;

import com.resumerefiner.resumerefinerbackend.billing.domain.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    List<Payment> findByMemberIdOrderByRequestedAtDesc(Long memberId, Pageable pageable);

    List<Payment> findByMemberIdOrderByRequestedAtDesc(Long memberId);
}
