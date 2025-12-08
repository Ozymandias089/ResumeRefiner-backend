package com.resumerefiner.resumerefinerbackend.billing.domain;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    List<Payment> findByMemberId(Long memberId);

    List<Payment> findRecentByMemberId(Long memberId, int limit);
}
