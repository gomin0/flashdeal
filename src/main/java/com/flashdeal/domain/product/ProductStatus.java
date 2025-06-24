package com.flashdeal.domain.product;

public enum ProductStatus {
    UPCOMING("판매 예정"),
    ON_SALE("판매 중"),
    SOLD_OUT("품절"),
    ENDED("판매 종료");
    
    private final String description;
    
    ProductStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
