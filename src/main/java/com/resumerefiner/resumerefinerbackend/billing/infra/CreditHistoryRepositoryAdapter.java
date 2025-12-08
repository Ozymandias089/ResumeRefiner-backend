package com.resumerefiner.resumerefinerbackend.billing.infra;

import com.resumerefiner.resumerefinerbackend.billing.domain.CreditHistory;
import com.resumerefiner.resumerefinerbackend.billing.domain.CreditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CreditHistoryRepositoryAdapter implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository jpa;

    @Override
    public CreditHistory save(CreditHistory history) {
        return jpa.save(history);
    }

    @Override
    public List<CreditHistory> findByMemberId(Long memberId) {
        return jpa.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Override
    public List<CreditHistory> findRecentByMemberId(Long memberId, int limit) {
        return jpa.findByMemberIdOrderByCreatedAtDesc(
                memberId,
                PageRequest.of(0, limit)
        );
    }
}
