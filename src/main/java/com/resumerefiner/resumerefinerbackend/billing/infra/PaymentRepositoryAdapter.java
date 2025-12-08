package com.resumerefiner.resumerefinerbackend.billing.infra;

import com.resumerefiner.resumerefinerbackend.billing.domain.Payment;
import com.resumerefiner.resumerefinerbackend.billing.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpa;

    @Override
    public Payment save(Payment payment) {
        return jpa.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Payment> findByPgTransactionId(String pgTransactionId) {
        return jpa.findByPgTransactionId(pgTransactionId);
    }

    @Override
    public List<Payment> findByMemberId(Long memberId) {
        // 전체 내역 (관리자/유저 상세용)
        return jpa.findByMemberIdOrderByRequestedAtDesc(memberId);
    }

    @Override
    public List<Payment> findRecentByMemberId(Long memberId, int limit) {
        return jpa.findByMemberIdOrderByRequestedAtDesc(
                memberId,
                PageRequest.of(0, limit)
        );
    }
}
