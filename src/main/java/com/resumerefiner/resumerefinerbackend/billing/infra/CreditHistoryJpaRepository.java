package com.resumerefiner.resumerefinerbackend.billing.infra;

import com.resumerefiner.resumerefinerbackend.billing.domain.CreditHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistory, Long> {

    List<CreditHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<CreditHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
