package com.flashdeal.domain.account;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.common.exception.BusinessException;
import com.flashdeal.common.exception.ErrorCode;
import com.flashdeal.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Version  // 낙관적 락
    private Integer version;
    
    @Builder
    public Account(User user, BigDecimal balance) {
        this.user = user;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }
    
    /**
     * 잔액 차감 (동시성 이슈 핵심 메서드)
     */
    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
        }
        
        if (balance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(amount);
        
        return balanceBefore;
    }
    
    /**
     * 잔액 충전/환불
     */
    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
        }
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.add(amount);
        
        return balanceBefore;
    }
    
    /**
     * 잔액 충분 여부 확인
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }
}
