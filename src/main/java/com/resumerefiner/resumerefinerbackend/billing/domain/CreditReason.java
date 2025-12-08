package com.resumerefiner.resumerefinerbackend.billing.domain;

public enum CreditReason {
    PAYMENT,           // 결제 후 충전
    REVIEW_CONSUME,    // 리뷰 생성에 따른 차감
    ADMIN_ADJUST       // 관리자가 조정
}
