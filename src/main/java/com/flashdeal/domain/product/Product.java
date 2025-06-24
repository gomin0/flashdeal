package com.flashdeal.domain.product;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.common.exception.BusinessException;
import com.flashdeal.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_sale_time", columnList = "saleStartTime, saleEndTime"),
    @Index(name = "idx_products_status", columnList = "productStatus")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;
    
    @Column(nullable = false)
    private Integer totalStock;
    
    @Column(nullable = false)
    private Integer remainingStock;
    
    @Column(nullable = false)
    private LocalDateTime saleStartTime;
    
    @Column(nullable = false)
    private LocalDateTime saleEndTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus productStatus;
    
    @Column(nullable = false)
    private Integer maxQuantityPerUser;
    
    @Builder
    public Product(String name, BigDecimal originalPrice, BigDecimal salePrice, 
                   Integer totalStock, LocalDateTime saleStartTime, LocalDateTime saleEndTime,
                   Integer maxQuantityPerUser) {
        this.name = name;
        this.originalPrice = originalPrice;
        this.salePrice = salePrice;
        this.totalStock = totalStock;
        this.remainingStock = totalStock;
        this.saleStartTime = saleStartTime;
        this.saleEndTime = saleEndTime;
        this.maxQuantityPerUser = maxQuantityPerUser;
        this.productStatus = ProductStatus.UPCOMING;
    }
    
    /**
     * 현재 판매 가능한지 확인
     */
    public boolean isAvailableForSale() {
        LocalDateTime now = LocalDateTime.now();
        return productStatus == ProductStatus.ON_SALE && 
               now.isAfter(saleStartTime) && 
               now.isBefore(saleEndTime) &&
               remainingStock > 0;
    }
    
    /**
     * 재고 차감 (동시성 이슈 핵심 메서드)
     */
    public void decreaseStock(int quantity) {
        if (!isAvailableForSale()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE);
        }
        
        if (remainingStock < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        
        this.remainingStock -= quantity;
        
        if (this.remainingStock == 0) {
            this.productStatus = ProductStatus.SOLD_OUT;
        }
    }
    
    /**
     * 재고 복구 (결제 타임아웃 시)
     */
    public void restoreStock(int quantity) {
        this.remainingStock += quantity;
        
        // 품절 상태였다면 다시 판매중으로 변경
        if (this.productStatus == ProductStatus.SOLD_OUT && this.remainingStock > 0) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(saleStartTime) && now.isBefore(saleEndTime)) {
                this.productStatus = ProductStatus.ON_SALE;
            }
        }
    }
    
    /**
     * 사용자별 최대 구매 수량 체크
     */
    public void validateQuantity(int requestedQuantity) {
        if (requestedQuantity > maxQuantityPerUser) {
            throw new BusinessException(ErrorCode.EXCEEDED_MAX_QUANTITY);
        }
    }
    
    /**
     * 플래시 세일 시작
     */
    public void startSale() {
        this.productStatus = ProductStatus.ON_SALE;
    }
    
    /**
     * 플래시 세일 종료
     */
    public void endSale() {
        this.productStatus = ProductStatus.ENDED;
    }
    
    /**
     * 할인율 계산
     */
    public BigDecimal getDiscountRate() {
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = originalPrice.subtract(salePrice);
        return discount.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP)
                      .multiply(BigDecimal.valueOf(100));
    }
}
