package com.flashdeal.domain.product;

public enum StockType {
    DECREASE("재고 차감"),
    RESTORE("재고 복구");
    
    private final String description;
    
    StockType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
