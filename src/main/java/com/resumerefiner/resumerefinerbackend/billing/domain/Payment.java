package com.resumerefiner.resumerefinerbackend.billing.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pg_transaction_id", columnNames = "pg_transaction_id")
        },
        indexes = {
                @Index(name = "idx_payment_member_id", columnList = "member_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity implements AggregateRoot {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 결제 요청한 사용자
     */
    @Getter
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "pg_provider", nullable = false, length = 30)
    private PaymentProvider pgProvider;

    @Getter
    @Column(name = "pg_transaction_id", nullable = false, length = 100)
    private String pgTransactionId;

    /**
     * 어떤 상품을 결제했는지
     * (예: CREDIT_10PACK, CREDIT_50PACK 등)
     */
    @Getter
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    /**
     * 금액 (정수)
     */
    @Getter
    @Column(name = "amount", nullable = false)
    private int amount;

    @Getter
    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Getter
    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Getter
    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    private Payment(Long memberId,
                    PaymentProvider pgProvider,
                    String pgTransactionId,
                    String productCode,
                    int amount,
                    String currency,
                    PaymentStatus status,
                    OffsetDateTime requestedAt,
                    OffsetDateTime approvedAt) {

        this.memberId = Objects.requireNonNull(memberId);
        this.pgProvider = Objects.requireNonNull(pgProvider);
        this.pgTransactionId = Objects.requireNonNull(pgTransactionId);
        this.productCode = Objects.requireNonNull(productCode);
        this.amount = amount;
        this.currency = currency != null ? currency : "KRW";
        this.status = Objects.requireNonNull(status);
        this.requestedAt = Objects.requireNonNull(requestedAt);
        this.approvedAt = approvedAt;
    }

    // === Factory ===

    public static Payment createRequested(Long memberId,
                                          PaymentProvider provider,
                                          String pgTransactionId,
                                          String productCode,
                                          int amount,
                                          String currency) {

        return new Payment(
                memberId,
                provider,
                pgTransactionId,
                productCode,
                amount,
                currency,
                PaymentStatus.REQUESTED,
                OffsetDateTime.now(),
                null
        );
    }

    // === Business Methods ===

    public void markApproved() {
        if (this.status != PaymentStatus.REQUESTED) {
            throw new DomainConflictException("Payment must be in REQUESTED state to approve.");
        }
        this.status = PaymentStatus.APPROVED;
        this.approvedAt = OffsetDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
    }
}
