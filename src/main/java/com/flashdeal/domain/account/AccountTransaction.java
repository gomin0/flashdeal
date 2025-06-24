package com.flashdeal.domain.account;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "account_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceBefore;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    @Builder
    public AccountTransaction(Account account, Order order, TransactionType transactionType,
                             BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter) {
        this.account = account;
        this.order = order;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
    }
    
    /**
     * 출금 거래 생성
     */
    public static AccountTransaction createWithdrawal(Account account, Order order, 
                                                     BigDecimal amount, BigDecimal balanceBefore) {
        return AccountTransaction.builder()
                .account(account)
                .order(order)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceBefore.subtract(amount))
                .build();
    }
    
    /**
     * 환불 거래 생성
     */
    public static AccountTransaction createRefund(Account account, Order order, 
                                                 BigDecimal amount, BigDecimal balanceBefore) {
        return AccountTransaction.builder()
                .account(account)
                .order(order)
                .transactionType(TransactionType.REFUND)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceBefore.add(amount))
                .build();
    }
}
