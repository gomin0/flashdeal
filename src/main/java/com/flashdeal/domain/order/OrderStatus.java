package com.flashdeal.domain.order;

public enum OrderStatus {
    PENDING("주문 생성"),
    PAYMENT_PENDING("결제 대기"),
    PAID("결제 완료"),
    CANCELLED("주문 취소"),
    EXPIRED("결제 시간 만료");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
