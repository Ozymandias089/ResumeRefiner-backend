package com.resumerefiner.resumerefinerbackend.billing.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "credit_history",
        indexes = {
                @Index(name = "idx_credit_history_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditHistory extends BaseTimeEntity implements AggregateRoot {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 대상 사용자
     */
    @Getter
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * 증가/감소 (ex: +10, -1)
     */
    @Getter
    @Column(name = "delta", nullable = false)
    private int delta;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 30)
    private CreditReason reason;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", length = 30)
    private ReferenceType refType;

    @Getter
    @Column(name = "ref_id")
    private Long refId;

    private CreditHistory(Long memberId,
                          int delta,
                          CreditReason reason,
                          ReferenceType refType,
                          Long refId) {
        this.memberId = memberId;
        this.delta = delta;
        this.reason = reason;
        this.refType = refType;
        this.refId = refId;
    }

    // === Factory ===

    public static CreditHistory fromPayment(Long memberId, Long paymentId, int delta) {
        return new CreditHistory(
                memberId,
                delta,
                CreditReason.PAYMENT,
                ReferenceType.PAYMENT,
                paymentId
        );
    }

    public static CreditHistory fromReview(Long memberId, Long reviewId, int delta) {
        return new CreditHistory(
                memberId,
                delta,
                CreditReason.REVIEW_CONSUME,
                ReferenceType.REVIEW,
                reviewId
        );
    }

    public static CreditHistory adminAdjust(Long memberId, int delta) {
        return new CreditHistory(
                memberId,
                delta,
                CreditReason.ADMIN_ADJUST,
                ReferenceType.ADMIN,
                null
        );
    }
}
