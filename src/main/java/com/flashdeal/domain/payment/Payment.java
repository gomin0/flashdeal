package com.flashdeal.domain.payment;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_status", columnList = "paymentStatus"),
    @Index(name = "idx_payments_key", columnList = "paymentKey")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false, unique = true, length = 100)
    private String paymentKey;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 100)
    private String pgTransactionId;
    
    private LocalDateTime paymentApprovedAt;
    
    @Builder
    public Payment(Order order, PaymentMethod paymentMethod, BigDecimal amount) {
        this.order = order;
        this.paymentKey = generatePaymentKey();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PENDING;
        this.amount = amount;
    }
    
    /**
     * 결제 키 생성
     */
    private String generatePaymentKey() {
        return "PAY" + System.currentTimeMillis() + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 결제 성공 처리
     */
    public void succeed(String pgTransactionId) {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.pgTransactionId = pgTransactionId;
        this.paymentApprovedAt = LocalDateTime.now();
    }
    
    /**
     * 결제 실패 처리
     */
    public void fail() {
        this.paymentStatus = PaymentStatus.FAILED;
    }
    
    /**
     * 결제 취소 처리
     */
    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELLED;
    }
    
    /**
     * 결제 성공 여부 확인
     */
    public boolean isSuccess() {
        return paymentStatus == PaymentStatus.SUCCESS;
    }
    
    /**
     * 취소 가능 여부 확인
     */
    public boolean canCancel() {
        return paymentStatus == PaymentStatus.PENDING;
    }
}
