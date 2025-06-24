package com.flashdeal.domain.order;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.common.exception.BusinessException;
import com.flashdeal.common.exception.ErrorCode;
import com.flashdeal.domain.product.Product;
import com.flashdeal.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_status_created", columnList = "orderStatus, createdAt"),
    @Index(name = "idx_orders_payment_deadline", columnList = "paymentDeadline")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
    
    @Column(nullable = false)
    private LocalDateTime paymentDeadline;
    
    @Builder
    public Order(User user, Product product, Integer quantity, BigDecimal unitPrice, 
                 Integer paymentTimeoutMinutes) {
        this.orderNumber = generateOrderNumber();
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.orderStatus = OrderStatus.PENDING;
        this.paymentDeadline = LocalDateTime.now().plusMinutes(
            paymentTimeoutMinutes != null ? paymentTimeoutMinutes : 3
        );
    }
    
    /**
     * 주문번호 생성
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 결제 대기 상태로 변경
     */
    public void waitForPayment() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "결제 대기 상태로 변경할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }
    
    /**
     * 결제 완료 처리
     */
    public void completePayment() {
        if (orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "결제 완료 처리할 수 없는 상태입니다.");
        }
        this.orderStatus = OrderStatus.PAID;
    }
    
    /**
     * 주문 취소
     */
    public void cancel() {
        if (orderStatus == OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "결제 완료된 주문은 취소할 수 없습니다.");
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }
    
    /**
     * 결제 시간 만료 처리
     */
    public void expire() {
        if (orderStatus == OrderStatus.PAID) {
            return; // 이미 결제 완료된 경우 만료 처리하지 않음
        }
        this.orderStatus = OrderStatus.EXPIRED;
    }
    
    /**
     * 결제 가능 여부 확인
     */
    public boolean canPay() {
        return orderStatus == OrderStatus.PAYMENT_PENDING && 
               LocalDateTime.now().isBefore(paymentDeadline);
    }
    
    /**
     * 결제 시간 만료 여부 확인
     */
    public boolean isPaymentExpired() {
        return LocalDateTime.now().isAfter(paymentDeadline);
    }
    
    /**
     * 취소 가능 여부 확인
     */
    public boolean canCancel() {
        return orderStatus == OrderStatus.PENDING || 
               orderStatus == OrderStatus.PAYMENT_PENDING;
    }
}
