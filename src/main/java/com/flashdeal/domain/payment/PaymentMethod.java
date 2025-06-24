package com.flashdeal.domain.payment;

public enum PaymentMethod {
    CARD("카드"),
    ACCOUNT("계좌");
    
    private final String description;
    
    PaymentMethod(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
