package com.resumerefiner.resumerefinerbackend.billing.domain;

import java.util.List;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory history);

    List<CreditHistory> findByMemberId(Long memberId);

    List<CreditHistory> findRecentByMemberId(Long memberId, int limit);
}
