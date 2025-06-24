package com.flashdeal.domain.product;

import com.flashdeal.common.entity.BaseEntity;
import com.flashdeal.domain.order.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockHistory extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockType stockType;
    
    @Column(nullable = false)
    private Integer quantityChanged;
    
    @Column(nullable = false)
    private Integer stockBefore;
    
    @Column(nullable = false)
    private Integer stockAfter;
    
    @Column(length = 255)
    private String reason;
    
    @Builder
    public StockHistory(Product product, Order order, StockType stockType,
                       Integer quantityChanged, Integer stockBefore, Integer stockAfter,
                       String reason) {
        this.product = product;
        this.order = order;
        this.stockType = stockType;
        this.quantityChanged = quantityChanged;
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
        this.reason = reason;
    }
    
    /**
     * 재고 차감 이력 생성
     */
    public static StockHistory createDecrease(Product product, Order order, 
                                            Integer quantity, Integer stockBefore) {
        return StockHistory.builder()
                .product(product)
                .order(order)
                .stockType(StockType.DECREASE)
                .quantityChanged(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockBefore - quantity)
                .reason("주문으로 인한 재고 차감")
                .build();
    }
    
    /**
     * 재고 복구 이력 생성
     */
    public static StockHistory createRestore(Product product, Order order, 
                                           Integer quantity, Integer stockBefore) {
        return StockHistory.builder()
                .product(product)
                .order(order)
                .stockType(StockType.RESTORE)
                .quantityChanged(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockBefore + quantity)
                .reason("주문 취소/만료로 인한 재고 복구")
                .build();
    }
}
